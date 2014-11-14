// ----------------------------------------------------------------------------
// Copyright 2006-2010, GeoTelematic Solutions, Inc.
// All rights reserved
// ----------------------------------------------------------------------------
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//     -Initial release
//  2006/05/26  Martin D. Flynn
//     -Now accepts a PKT_CLIENT_UNIQUE_ID payload of up to 20 bytes.
//  2007/02/25  Martin D. Flynn
//     -Changed to respond with server EOB (rather than EOT) if any pending packets
//      were sent to the client.  Also, pending packets will continue to be
//      sent until there are no more packets to send.  This allows the Device DB
//      implementation to optimize the storage and retrieval of pending packets.
//  2007/12/04  Martin D. Flynn
//     -Hold on to PendingPackets if an error occurred during the session
//  2008/01/10  Martin D. Flynn
//     -Added ability to record connection information/statistics
//  2008/04/04  Martin D. Flynn
//     -Changed error messages for unrecognized custom event packet types.
//     -Allow client to respond with an event template during the same session 
//      where the server has responded with a NAK_FORMAT_NOT_RECOGNIZED error.
// ----------------------------------------------------------------------------
package org.opendmtp.server.base;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;

import org.opengts.util.*;

import org.opendmtp.codes.*;
import org.opendmtp.server.db.*;

public class DMTPClientPacketHandler
    extends AbstractClientPacketHandler
{

    // ------------------------------------------------------------------------

    // Set this to 'true' for production.
    // When 'false' this prevents the device table from being updated with marked connection
    // information, allowing unlimited connections to occur in a short period of time.
    // This mode is useful for debugging purposes, but should not be used in production.
    private static boolean SAVE_MARKED_CONNECTION_INFO      = true;

    // ------------------------------------------------------------------------

    // Set to 'true' to return UDP response to client
    // if 'true', the client should be configured to listen for returning ACK packets.
    private static boolean UDP_RETURN_RESPONSE              = false;
    public static void setUdpReturnResponse(boolean state)
    {
        UDP_RETURN_RESPONSE = state;
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    //private int             dataNdx               = 0;
    private boolean         terminate               = false;
    
    /* duplex/simplex */
    private boolean         isDuplex                = true;
    
    /* session start time */
    private long            sessionStartTime        = 0L;
    
    /* session IP address */
    private InetAddress     inetAddress             = null;
    private String          ipAddress               = null;

    /* packet */
    private int             encoding                = Encoding.ENCODING_UNKNOWN;
    
    /* Fletcher checksum */
    private FletcherChecksum fletcher               = null;
    
    /* identification */
    private AccountID       accountId               = null;
    private DeviceID        deviceId                = null;
    
    /* event accounting */
    private int             eventTotalCount         = 0;
    private int             eventBlockCount         = 0;
    private Event           lastValidEvent          = null;
    
    private Packet          eventErrorPacket        = null;
    
    private int             formatErrorCount        = 0;
    private int             formatErrorType         = 0;
    private int             formatRecvTemplate      = 0; // number of event template packets received
    private boolean         expectEventTemplate     = false;

    /* pending packets */
    private boolean         sendPending             = true;
    private PacketList      pendingPackets          = null;

    public DMTPClientPacketHandler() 
    {
        super();
        this.fletcher = new FletcherChecksum();
    }
    
    // ------------------------------------------------------------------------

    public void sessionStarted(InetAddress inetAddr, boolean isTCP, boolean isText)
    {
        super.sessionStarted(inetAddr, isTCP, isText);
        
        /* init */
        this.sessionStartTime = DateTime.getCurrentTimeSec();
        this.inetAddress      = inetAddr;
        this.ipAddress        = (inetAddr != null)? inetAddr.getHostAddress() : null;
        this.isDuplex         = isTCP;
        this.eventTotalCount  = 0;
        this.formatErrorCount = 0;
        this.formatErrorType  = 0;
        
        /* debug message */
        if (this.isDuplex) {
            Print.logInfo("Begin Duplex communication: " + this.ipAddress);
        } else {
            Print.logInfo("Begin Simplex communication: " + this.ipAddress);
        }
    }
    
    public void sessionTerminated(Throwable err, long readCount, long writeCount)
    {
        // called before the socket is closed
        boolean hasError = (err != null);
        
        /* clear any pending packets if no errors have occurred */
        if (hasError) {
            if (this.pendingPackets != null) {
                Print.logWarn("**** Session terminating with errors (PendingPackets remain intact)");
            } else {
                Print.logWarn("Session terminating with errors");
            }
        } else {
            try {
                this._clearPendingPackets();
            } catch (PacketParseException ppe) { 
                // ignore any error at this point
            }
        } 
        
        /* save session statistics */
        if (this.deviceId != null) {
            this.deviceId.saveSessionStatistics(this.sessionStartTime,this.ipAddress,this.isDuplex,readCount,writeCount,this.eventTotalCount);
        }

        /* log session termination */
        if (this.isDuplex()) {
            Print.logInfo("End Duplex communication: " + this.ipAddress);
            // short pause to 'help' make sure the pending outbound data is transmitted
            try { Thread.sleep(75L); } catch (Throwable t) {}
        } else {
            Print.logInfo("End Simplex communication: " + this.ipAddress);
        }
        
    }
    
    // ------------------------------------------------------------------------

    public boolean isDuplex()
    {
        return this.isDuplex;
    }

    // ------------------------------------------------------------------------

    public int getActualPacketLength(byte packet[], int packetLen)
    {
        if ((packetLen >= 1) && (packet[0] == Encoding.AsciiEncodingChar)) {
            // look for line terminator
            return -1;
        } else
        if (packetLen >= Packet.MIN_HEADER_LENGTH) {
            int payloadLen = (int)packet[2] & 0xFF;
            return Packet.MIN_HEADER_LENGTH + payloadLen;
        } else {
            // this should not occur, since minimum length has been set above
            return 0;
        }
    }
        
    // ------------------------------------------------------------------------

    public byte[] getHandlePacket(byte pktBytes[]) 
    {
        String ipAddr = this.getHostAddress();
        //Print.logInfo("handlePacket: IP = " + ipAddr);
        Packet resp[] = this._parsePacket(ipAddr, pktBytes);
        
        /* null/empty response */
        if ((resp == null) || (resp.length == 0)) {
            //Print.logWarn("<-- null (no response)");
            return null;
        }

        /* Duplex/TCP response */
        if (this.isDuplex()) {
            if (resp.length == 1) {
                Print.logDebug("==> " + resp[0].toString(this.encoding));
                return resp[0].encode(this.encoding);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int i = 0; i < resp.length; i++) {
                    Print.logDebug("==> " + resp[i].toString(this.encoding));
                    byte b[] = resp[i].encode(this.encoding);
                    baos.write(b, 0, b.length);
                }
                return baos.toByteArray();
            }
        }

        /* Discard Simplex/UDP response */
        if (!UDP_RETURN_RESPONSE) {
            Print.logError("UDP Response discarded.");
            return null;
        }

        /* Return Simplex/UDP response */
        if (resp.length == 1) {
            Print.logDebug("==> " + resp[0].toString(this.encoding));
            return resp[0].encode(this.encoding);
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i = 0; i < resp.length; i++) {
                Print.logDebug("==> " + resp[i].toString(this.encoding));
                byte b[] = resp[i].encode(this.encoding);
                baos.write(b, 0, b.length);
            }
            return baos.toByteArray();
        }

    }
            
    // ------------------------------------------------------------------------

    /* indicate that the session should terminate */
    public boolean terminateSession()
    {
        return this.terminate;
    }
    
    /* set terminate session */
    private void _setTerminateSession()
    {
        this.terminate = true;
    }

    // ------------------------------------------------------------------------

    /* load account/device from unique id */
    private void loadUniqueID(String ipAddr, byte id[])
        throws PacketParseException
    {

        /* device already defined? */
        if (this.deviceId != null) {
            Print.logError("Device ID already defined");
            throw new PacketParseException(ServerErrors.NAK_PROTOCOL_ERROR, null); // errData ok
        }
    
        /* invalid id specified? */
        UniqueID uniqId = new UniqueID(id);
        if (!uniqId.isValid()) {
            throw new PacketParseException(ServerErrors.NAK_ID_INVALID, null); // errData ok
        }

        /* load device */
        DeviceID devId = DeviceID.loadDeviceID(uniqId);
        // will throw PacketParseException if named DeviceID does not exist
        
        /* set account/device */
        this._setAccountId(ipAddr, devId.getAccountID());
        this._setDeviceId(ipAddr, devId);
        
    }

    // ------------------------------------------------------------------------

    private void loadAccountId(String ipAddr, String acctName)
        throws PacketParseException
    {

        /* account already defined? */
        if (this.accountId != null) {
            Print.logError("Account ID already defined");
            throw new PacketParseException(ServerErrors.NAK_PROTOCOL_ERROR, null); // errData ok
        } else
        if ((acctName == null) || acctName.equals("")) {
            Print.logError("Account name is null/empty");
            throw new PacketParseException(ServerErrors.NAK_ACCOUNT_INVALID, null); // errData ok
        }

        /* load account */
        AccountID acctId = AccountID.loadAccountID(acctName.toLowerCase());
        // will throw PacketParseException if named AccountID does not exist

        /* set account */
        this._setAccountId(ipAddr, acctId);

    }
    
    private void _setAccountId(String ipAddr, AccountID acctId)
        throws PacketParseException
    {
        
        /* null? */
        if (acctId == null) {
            throw new PacketParseException(ServerErrors.NAK_ACCOUNT_INVALID, null); // errData ok
        }
        
        /* validate account */
        if (!acctId.isActive()) {
            throw new PacketParseException(ServerErrors.NAK_ACCOUNT_INACTIVE, null); // errData ok
        }
        this.accountId = acctId;

    }
            
    private AccountID getAccountId()
        throws PacketParseException
    {
        if (this.accountId == null) {
            // throw termination error if the account hasn't been defined
            PacketParseException ppe = new PacketParseException(ServerErrors.NAK_ACCOUNT_INVALID, null); // errData ok
            ppe.setTerminate();
            throw ppe;
        }
        return this.accountId;
    }

    // ------------------------------------------------------------------------

    /* load device id */
    private void loadDeviceId(String ipAddr, String devName)
        throws PacketParseException
    {

        /* device already defined? */
        if (this.deviceId != null) {
            Print.logError("Device ID already defined");
            throw new PacketParseException(ServerErrors.NAK_PROTOCOL_ERROR, null); // errData ok
        } else
        if ((devName == null) || devName.equals("")) {
            Print.logError("Device name is null/empty");
            throw new PacketParseException(ServerErrors.NAK_DEVICE_INVALID, null); // errData ok
        }

        /* load device */
        DeviceID devId = DeviceID.loadDeviceID(this.getAccountId(), devName.toLowerCase());
        // will throw PacketParseException if named DeviceID does not exist

        /* set device */
        this._setDeviceId(ipAddr, devId);

    }
    
    private void _setDeviceId(String ipAddr, DeviceID devId)
        throws PacketParseException
    {
        
        /* null? */
        if (devId == null) {
            Print.logError("Device ID is null");
            throw new PacketParseException(ServerErrors.NAK_DEVICE_INVALID, null); // errData ok
        }

        /* validate IP address */
        if (!devId.isValidIpAddress(ipAddr)) {
            // invalid IP address
            Print.logError("Invalid IP address: " + ipAddr);
            throw new PacketParseException(ServerErrors.NAK_DEVICE_ERROR, null); // errData ok
        }

        /* validate device */
        if (!devId.isActive()) {
            // device is not active
            Print.logError("Device is inactive: " + devId.getDeviceName());
            throw new PacketParseException(ServerErrors.NAK_DEVICE_INACTIVE, null); // errData ok
        } else
        if (!devId.markAndValidateConnection(this.isDuplex())) {
            // DMT service provider refuses connection
            Print.logError("Excessive connections: " + devId.getDeviceName());
            throw new PacketParseException(ServerErrors.NAK_EXCESSIVE_CONNECTIONS, null); // errData ok
        }
        
        /* save marked connection info */
        if (SAVE_MARKED_CONNECTION_INFO) {
            int servErr = devId.saveChanges();
            if (servErr != ServerErrors.NAK_OK) {
                throw new PacketParseException(servErr, null); // errData ok
            }
        }
        
        /**/
        this.deviceId = devId;

    }
    
    private DeviceID getDeviceId()
        throws PacketParseException
    {
        if (this.deviceId == null) {
            // throw termination error if the device hasn't been defined
            PacketParseException ppe = new PacketParseException(ServerErrors.NAK_DEVICE_INVALID, null); // errData ok
            ppe.setTerminate();
            throw ppe;
        }
        return this.deviceId;
    }

    // ------------------------------------------------------------------------

    private Packet[] _handleError(Packet packet)
        throws PacketParseException
    {
        Payload payload = packet.getPayload(true);
        int errCode = (int)payload.readULong(2, 0L);
        
        /* handle specific error */
        switch (errCode) {
            
            case ClientErrors.ERROR_PACKET_HEADER:
            case ClientErrors.ERROR_PACKET_TYPE:
            case ClientErrors.ERROR_PACKET_LENGTH: {
                // we must have sent the client an invalid packet
                return null;
            }
            
            case ClientErrors.ERROR_PACKET_ENCODING: {
                // only occurs if client does not support CSV encoding
                this.getDeviceId().removeEncoding(this.encoding);
                if (Encoding.IsEncodingAscii(this.encoding)) {
                    this.encoding = Encoding.IsEncodingChecksum(this.encoding)? 
                        Encoding.ENCODING_BASE64_CKSUM : 
                        Encoding.ENCODING_BASE64;
                } else {
                    // ignore this
                }
                return null;
            }
                
            case ClientErrors.ERROR_PACKET_PAYLOAD: {
                // we must have sent the client an invalid packet
                return null;
            }
                
            case ClientErrors.ERROR_PACKET_CHECKSUM: {
                // should try again?
                return null;
            }
                
            case ClientErrors.ERROR_PACKET_ACK: {
                // we must have sent the client an ack for a sequence it doesn't have
                return null;
            }

            case ClientErrors.ERROR_PROTOCOL_ERROR: {
                // we must have sent the client an invalid packet
                return null;
            }

            case ClientErrors.ERROR_PROPERTY_READ_ONLY: {
                // just note this somewhere
                return null;
            }

            case ClientErrors.ERROR_PROPERTY_WRITE_ONLY: {
                // just note this somewhere
                return null;
            }

            case ClientErrors.ERROR_PROPERTY_INVALID_ID: {
                // just note this somewhere
                return null;
            }

            case ClientErrors.ERROR_PROPERTY_INVALID_VALUE: {
                // just note this somewhere
                return null;
            }

            case ClientErrors.ERROR_PROPERTY_UNKNOWN_ERROR: {
                // ignore this, since we don't know what to do about this anyway
                return null;
            }

            case ClientErrors.ERROR_COMMAND_INVALID:
            case ClientErrors.ERROR_COMMAND_ERROR: {
                // ignore this, since we don't know what to do about this anyway
                return null;
            }

            case ClientErrors.ERROR_UPLOAD_TYPE:
            case ClientErrors.ERROR_UPLOAD_LENGTH:
            case ClientErrors.ERROR_UPLOAD_OFFSET_OVERLAP:
            case ClientErrors.ERROR_UPLOAD_OFFSET_GAP:
            case ClientErrors.ERROR_UPLOAD_OFFSET_OVERFLOW:
            case ClientErrors.ERROR_UPLOAD_FILE_NAME:
            case ClientErrors.ERROR_UPLOAD_CHECKSUM:
            case ClientErrors.ERROR_UPLOAD_SAVE: {
                // we should abort the upload here
                return null;
            }

            case ClientErrors.ERROR_GPS_EXPIRED:
            case ClientErrors.ERROR_GPS_FAILURE: {
                // pass these along to account owner
                return null;
            }

            default: {
                // pass these along to account owner
                return null;
            }
                
        }
        
    }

    // ------------------------------------------------------------------------

    private int _handleEvent(Event event)
        throws PacketParseException
    {
        DeviceID devId = this.getDeviceId();
        if (Print.isDebugLoggingLevel()) {
            //opendmtp/mobile Event:
            //  IPAddress     : 127.0.0.1
            //  RawData       : $E073:F112<...>75
            //  StatusCode    : [0xF112] InMotion
            //  Timestamp     : [1173601234] Sun Mar 11 14:25:17 CDT 2007
            //  GPSAge        : [0x0004] 4
            //  GeoPoint      : 36.01234/-142.12345
            //  SpeedKPH      : [103.9 kph] 64.6 mph
            //  Heading       : 126.72
            //  AltitudeM     : [1319.0 meters] 4327.4 feet
            //  Sequence      : [0x0075] 117
            //  SeqLen        : [0x0001] 1
            DMTPGeoEvent gev = event.getGeoEvent();
            StringBuffer sb = new StringBuffer();
            sb.append("\n");
            sb.append(devId.getAccountName()).append("/").append(devId.getDeviceName());
            sb.append(" ");
            sb.append(event.toString());
            Print.logDebug(sb.toString());
        }
        return devId.saveEvent(event);
    }
   
    // ------------------------------------------------------------------------

    private void _clearPendingPackets()
        throws PacketParseException
    {
        if (this.pendingPackets != null) {
            Print.logInfo("Deleting sent PendingPackets ...");
            this.getDeviceId().clearPendingPackets(this.pendingPackets);
            this.pendingPackets = null;
        }
    }
    
    private Packet[] _handlePacket(String ipAddr, Packet packet)
        throws PacketParseException
    {

        /* make sure we have a defined device */
        if (!packet.isIdentType()) {
            // we must have a Device ID for anything other that an identification
            // type packet.  The following attempts to get the device id and throws
            // an exception if the device has not yet been defined.
            this.getDeviceId();
        }

        /* handle event packets separately */
        if (packet.isEventType()) {
            
            /* create Event */
            Event evData = null;
            try {
                evData = new Event(ipAddr, packet);
                this.eventTotalCount++; // count total events
                this.eventBlockCount++; // count event in this block
            } catch (PacketParseException ppe) {
                // NAK_FORMAT_NOT_RECOGNIZED?
                throw ppe;
            }
            
            /* check for errors */
            if (this.eventErrorPacket == null) {
                // no errors received during this block, so far
                int err = this._handleEvent(evData);
                if (err == ServerErrors.NAK_OK) {
                    // this event insertion was successful
                    this.lastValidEvent = evData;
                } else
                if (err == ServerErrors.NAK_DUPLICATE_EVENT) {
                    // this record already exists (not a critical error)
                    // duplicate events are quietly ignored
                    this.lastValidEvent = evData;
                } else {
                    // A critical error occurred inserting this event. 
                    // One of the following:
                    //    ServerErrors.NAK_EXCESSIVE_EVENTS
                    //    ServerErrors.NAK_EVENT_ERROR
                    Print.logError("Event insertion [" + StringTools.toHexString(err,16) + "] " + ServerErrors.getErrorDescription(err));
                    long seq    = evData.getSequence();
                    int  seqLen = evData.getSequenceLength();
                    PacketParseException ppe = null;
                    if ((seq >= 0L) && (seqLen > 0)) {
                        Payload p = new Payload();
                        p.writeULong(seq, seqLen);
                        byte errData[] = p.getBytes();
                        ppe = new PacketParseException(err, packet, errData); // sequence
                    } else {
                        ppe = new PacketParseException(err, packet); // errData ok
                    }
                    this.eventErrorPacket = ppe.createServerErrorPacket();
                }
                
            } else {
                // ignore this event
            }
            
            return null;
        }

        /* handle specific packet type */
        Payload payload = packet.getPayload(true);
        switch (packet.getPacketType()) {
            
            /* client is done talking and is waiting for server response */
            case Packet.PKT_CLIENT_EOB_DONE:
            case Packet.PKT_CLIENT_EOB_MORE: {
                // send responses, or close
                boolean clientHasMore = (packet.getPacketType() == Packet.PKT_CLIENT_EOB_MORE);
                java.util.List<Packet> resp = new Vector<Packet>();
                // check checksum (if binary encoding)
                if (this.encoding == Encoding.ENCODING_BINARY) {
                    if (packet.getPayloadLength() == 0) {
                        // checksum not specified 
                        //Print.logInfo("Client checksum not specified");
                    } else
                    if (packet.getPayloadLength() == 2) {
                        if (!this.fletcher.isValid()) {
                            Print.logError("Fletcher checksum is INVALID!!");
                            throw new PacketParseException(ServerErrors.NAK_BLOCK_CHECKSUM, packet); // errData ok
                        }
                    } else {
                        throw new PacketParseException(ServerErrors.NAK_PACKET_PAYLOAD, packet); // errData ok
                    }
                }
                this.fletcher.reset();
                // acknowledge sent events
                if (this.lastValidEvent != null) {
                    // at least 1 event has been received
                    Packet ackPkt = Packet.createServerPacket(Packet.PKT_SERVER_ACK);
                    int seqLen = this.lastValidEvent.getSequenceLength();
                    if (seqLen > 0) {
                        long seq = this.lastValidEvent.getSequence();
                        ackPkt.getPayload(true).writeLong(seq, seqLen);
                    }
                    resp.add(ackPkt);
                    this.eventBlockCount = 0;
                    this.lastValidEvent  = null;
                }
                // send any event parsing error packet
                if (this.eventErrorPacket != null) {
                    resp.add(this.eventErrorPacket);
                    this.eventErrorPacket = null;
                }
                // clear any previously transmitted PendingPackets
                this._clearPendingPackets();
                // send any pending configuration packets
                // TODO: do we want to skip sending pending packets if we have an error?
                boolean serverSentPending = false;
                if (this.sendPending) { // until we know that we have no more pending packets
                    this.pendingPackets = this.getDeviceId().getPendingPackets();
                    if (this.pendingPackets != null) {
                        Packet p[] = this.pendingPackets.getPackets(); // will not be null
                        Print.logInfo("Adding PendingPacket's to response [cnt=" + p.length + "]");
                        ListTools.toList(p, resp);
                        serverSentPending = true;
                        // TODO: Need to record that pending-packets are being (or have been) sent.
                        //this._clearPendingPackets();
                    } else {
                        // no more pending packets to send
                        //Comment this line to contually check for PendingPackets
                        //this.sendPending = false;
                    }
                }
                // end-of-block / end-of-transmission
                if (clientHasMore || serverSentPending || this.expectEventTemplate) {
                    // If 'serverSentPending' is true, we need to allow the client to respond
                    Packet eobPkt = Packet.createServerPacket(Packet.PKT_SERVER_EOB_DONE);
                    resp.add(eobPkt);
                    if (this.expectEventTemplate) { 
                        // The client is given one opportunity for providing the event template
                        // (this section will be exectued at-most once per session, and only if duplex)
                        this.expectEventTemplate = false; 
                    }
                } else {
                    Packet eotPkt = Packet.createServerPacket(Packet.PKT_SERVER_EOT);
                    resp.add(eotPkt);
                    this._setTerminateSession(); // success
                    // we're done communicating with this client
                }
                return (Packet[])ListTools.toArray(resp, Packet.class);
            }

            /* client sent Unique-ID */
            case Packet.PKT_CLIENT_UNIQUE_ID: {
                // lookup unique id
                try {
                    // typically this should be 6 bytes, but we attempt to read 20 in case
                    // the client wishes to provide additional information.
                    byte uniqId[] = payload.readBytes(20);
                    this.loadUniqueID(ipAddr, uniqId);
                } catch (PacketParseException ppe) {
                    ppe.setTerminate();
                    throw ppe;
                }
                break;
            }

            /* client sent Account-ID */
            case Packet.PKT_CLIENT_ACCOUNT_ID: {
                // lookup account
                try {
                    String acctName = payload.readString(20);
                    this.loadAccountId(ipAddr, acctName);
                } catch (PacketParseException ppe) {
                    ppe.setTerminate();
                    throw ppe;
                }
                break;
            }

            /* client sent Device-ID */
            case Packet.PKT_CLIENT_DEVICE_ID: {
                // lookup device
                try {
                    String devName = payload.readString(20);
                    if (this.accountId != null) {
                        // normal Account/Device name lookup
                        this.loadDeviceId(ipAddr, devName);
                    } else  {
                        // an Account was not specified, try 'devName' as a Unique ID
                        this.loadUniqueID(ipAddr, StringTools.getBytes(devName));
                    }
                } catch (PacketParseException ppe) {
                    ppe.setTerminate();
                    throw ppe;
                }
                break;
            }

            /* client sent property value */
            case Packet.PKT_CLIENT_PROPERTY_VALUE: {
                int  propKey   = (int)payload.readULong(2);
                byte propVal[] = payload.readBytes(255);
                // save property value sent by client, for later analysis
                this.getDeviceId().handleProperty(propKey, propVal);
                break;
            }

            /* client sent format definition template */
            case Packet.PKT_CLIENT_FORMAT_DEF_24: {
                this.formatRecvTemplate++; // number of templates received
                // validate type
                int custType = (int)payload.readULong(1);
                Payload p = new Payload();
                p.writeULong(custType, 1);
                if (!Packet.isCustomEventType(custType)) {
                    byte errData[] = p.getBytes();
                    throw new PacketParseException(ServerErrors.NAK_FORMAT_DEFINITION_INVALID, packet, errData); // formatType
                }
                // validate that the payload size can accomodate the specified number of fields
                int numFlds = (int)payload.readULong(1);
                if (!payload.isValidReadLength(numFlds * 3)) {
                    // not enough data to fill all specified field templates
                    byte errData[] = p.getBytes();
                    throw new PacketParseException(ServerErrors.NAK_FORMAT_DEFINITION_INVALID, packet, errData); // formatType
                }
                // parse field templates
                PayloadTemplate.Field field[] = new PayloadTemplate.Field[numFlds];
                int accumLen = 0;
                for (int i = 0; i < field.length; i++) {
                    long fldMask = payload.readULong(3);
                    field[i] = new PayloadTemplate.Field(fldMask);
                    if (!field[i].isValidType()) {
                        byte errData[] = p.getBytes();
                        throw new PacketParseException(ServerErrors.NAK_FORMAT_DEFINITION_INVALID, packet, errData); // formatType
                    }
                    accumLen += field[i].getLength();
                    if (accumLen > Packet.MAX_PAYLOAD_LENGTH) {
                        byte errData[] = p.getBytes();
                        throw new PacketParseException(ServerErrors.NAK_FORMAT_DEFINITION_INVALID, packet, errData); // formatType
                    }
                }
                PayloadTemplate payloadTemp = new PayloadTemplate(custType, field);
                this.getDeviceId().addClientPayloadTemplate(payloadTemp);
                break;
            }

            /* client sent diagnostic information */
            case Packet.PKT_CLIENT_DIAGNOSTIC: {
                // log diagnostic
                int  diagCode   = (int)payload.readULong(2);
                byte diagData[] = payload.readBytes(255);
                this.getDeviceId().handleDiagnostic(diagCode, diagData);
                break;
            }

            /* client sent error code */
            case Packet.PKT_CLIENT_ERROR: {
                // handle error
                int  errCode   = (int)payload.readULong(2);
                byte errData[] = payload.readBytes(255);
                this.getDeviceId().handleError(errCode, errData);
                return this._handleError(packet);
            }
            
            /* unknown/unsupported client packet */
            default: {
                // generate error
                throw new PacketParseException(ServerErrors.NAK_PACKET_TYPE, packet);  // errData ok
            }
                
        }

        return null;
    }
    
    // ------------------------------------------------------------------------

    private Packet[] _parsePacket(String ipAddr, byte pkt[])
    {
        // 'pkt' always represents a single packet
        
        /* Running Fletcher checksum */
        this.fletcher.runningChecksum(pkt);

        /* print packet */
        if ((pkt != null) && (pkt.length > 0)) {
            if (pkt[0] == Encoding.AsciiEncodingChar) {
                int len = (pkt[pkt.length - 1] == Encoding.AsciiEndOfLineChar)? (pkt.length - 1) : pkt.length;
                Print.logDebug("<== " + StringTools.toStringValue(pkt, 0, len));
            } else {
                String encPkt = StringTools.toHexString(pkt);
                Print.logDebug("<== 0x" + encPkt);
            }
        }
        
        /* parse packet */
        Packet packet = null;
        try {
            
            /* parse packet */
            // Note: 'this.deviceId' may be null here (eg. before device is defined)
            // The device id is only needed for custom payload templates when parsing
            // custom events.
            packet = new Packet(this.deviceId, true, pkt); // client packet
            if (this.encoding == Encoding.ENCODING_UNKNOWN) {
                // The first received packet establishes the encoding
                this.encoding = packet.getEncoding();
            }

            /* handle client packet, return response packets */
            Packet p[] = this._handlePacket(ipAddr, packet);
            return p;

        } catch (PacketParseException ppe) {

            /* set packet if null */
            Packet errPkt = ppe.getPacket();
            if (errPkt == null) {
                ppe.setPacket(packet);
                errPkt = packet;
            }
            
            /* terminate? */
            if (ppe.terminateSession()) {
                this._setTerminateSession(); // error
            }

            /* avoid duplicate NAK_FORMAT_NOT_RECOGNIZED error codes */
            int errCode = ppe.getErrorCode();
            if (errCode == ServerErrors.NAK_FORMAT_NOT_RECOGNIZED) {
                if (++this.formatErrorCount == 1) {
                    // This section will be executed at-most once during a session
                    if (errPkt != null) {
                        // save first custom packet type found
                        this.formatErrorType = errPkt.getPacketType();
                        if (this.isDuplex()) {
                            // This is only a warning for Duplex communication
                            int hdrType = (errPkt.getPacketHeader() << 8) | errPkt.getPacketType();
                            String hdrTypeStr = StringTools.toHexString(hdrType,16);
                            Print.logWarn("Unrecognized event packet type: 0x" + hdrTypeStr + " (client will be notified)");
                            // we're now expecting an event template response from the client
                            if (DMTPServer.getAllowFirstSessionNegotiation()) {
                                this.expectEventTemplate = true;
                            }
                        } else {
                            // This is an error for Simplex communication
                            Print.logException("Unrecognized event packet type during Simplex transport!", ppe);
                        }
                    } else {
                        // will not occur
                        this.formatErrorType = 0;
                    }
                } else
                if ((errPkt != null) && (errPkt.getPacketType() == this.formatErrorType)) {
                    // we've already received a format error for this packet type,
                    // do not send duplicates.
                    return null;
                }
            } else {
                Print.logException("Error: ", ppe);
            }

            /* return error packet */
            return new Packet[] { ppe.createServerErrorPacket() };
            
        }
        
    }

}
