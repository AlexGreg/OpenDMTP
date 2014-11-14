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
//     -Increases allowed UniqueID size to 20 bytes (however, the client doesn't
//      need to used that many bytes to represent a unique id)
//  2007/02/08  Martin D. Flynn
//     -When encoding a packet and the preferred encoding is not specified, or
//      is invalid, use ENCODING_HEX as the default encoding.
//  2007/02/25  Martin D. Flynn
//     -Removed custom defined payload template 'ClientCustomEvent_5F' (it wasn't
//      part of the standard DMTP protocol definition, and should only be used
//      in custom platform implementations).
//     -Fixed case where server error packets were sometimes created as client
//      error packets.
//     -Fixed "static int getPacketLength(byte[],int)" to return proper length of
//      binary encoded packets.
//     -Added 'createServerSetPropertyPacket' methods.
//  2007/01/10  Martin D. Flynn
//     -Added 'createServerGetFilePacket' method
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

public class Packet
{

    // ------------------------------------------------------------------------
    
    public static final int     MIN_HEADER_LENGTH           = 3;
    public static final int     MAX_PAYLOAD_LENGTH          = 255;

    public static final int     HEADER_BASIC                = 0xE0;
    // The value of 'HEADER_BASIC' must NOT be one of the following values:
    //    0x0A - This is the newline character and may be used to separate ASCII packets
    //    0x0D - This is the carriage-return character used to separate ASCII packets
    //    0x23 - This is reserved for encrypted ASCII encoded packets.
    //    0x24 - This is the start of an ASCII encoded packed ("$")
    // In general 0x20 through 0x7F are reserved for ASCII packet use and should not be used.

    // ------------------------------------------------------------------------
    // Client originated packets 
    
    // dialog packets
    public static final int     PKT_CLIENT_EOB_DONE         = 0x00;    // End of block/transmission, "no more to say"
    public static final int     PKT_CLIENT_EOB_MORE         = 0x01;    // End of block/transmission, "I have more to say"
    
    // identification packets
    public static final int     PKT_CLIENT_UNIQUE_ID        = 0x11;    // Unique identifier
    public static final int     PKT_CLIENT_ACCOUNT_ID       = 0x12;    // Account identifier
    public static final int     PKT_CLIENT_DEVICE_ID        = 0x13;    // Device identifier

    // standard fixed format event packets
    public static final int     PKT_CLIENT_FIXED_FMT_STD    = 0x30;    // Standard GPS
    public static final int     PKT_CLIENT_FIXED_FMT_HIGH   = 0x31;    // High Resolution GPS

    // DMTP service provider format event packets
    public static final int     PKT_CLIENT_DMTSP_FMT_0      = 0x50;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_1      = 0x51;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_2      = 0x52;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_3      = 0x53;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_4      = 0x54;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_5      = 0x55;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_6      = 0x56;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_7      = 0x57;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_8      = 0x58;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_9      = 0x59;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_A      = 0x5A;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_B      = 0x5B;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_C      = 0x5C;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_D      = 0x5D;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_E      = 0x5E;    //
    public static final int     PKT_CLIENT_DMTSP_FMT_F      = 0x5F;    //

    // custom format event packets
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_0  = 0x70;    // Custom format data #0
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_1  = 0x71;    // Custom format data #1
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_2  = 0x72;    // Custom format data #2
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_3  = 0x73;    // Custom format data #3
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_4  = 0x74;    // Custom format data #4
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_5  = 0x75;    // Custom format data #5
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_6  = 0x76;    // Custom format data #6
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_7  = 0x77;    // Custom format data #7
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_8  = 0x78;    // Custom format data #8
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_9  = 0x79;    // Custom format data #9
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_A  = 0x7A;    // Custom format data #A
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_B  = 0x7B;    // Custom format data #B
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_C  = 0x7C;    // Custom format data #C
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_D  = 0x7D;    // Custom format data #D
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_E  = 0x7E;    // Custom format data #E
    public static final int     PKT_CLIENT_CUSTOM_FORMAT_F  = 0x7F;    // Custom format data #F

    // Property packet
    public static final int     PKT_CLIENT_PROPERTY_VALUE   = 0xB0;    // Property value
    
    // Custom format packet
    public static final int     PKT_CLIENT_FORMAT_DEF_24    = 0xCF;    // Custom format definition (24 bit field def)

    // Diagnostic/Error packets
    public static final int     PKT_CLIENT_DIAGNOSTIC       = 0xD0;    // Diagnostic codes
    public static final int     PKT_CLIENT_ERROR            = 0xE0;    // Error codes

    // ------------------------------------------------------------------------
    // Server originated packets 
    
    /* End-Of-Block packets */
    public static final int     PKT_SERVER_EOB_DONE         = 0x00;    // ""       : End of transmission, query response
    public static final int     PKT_SERVER_EOB_SPEAK_FREELY = 0x01;    // ""       : End of transmission, speak freely

    // Acknowledge packet
    public static final int     PKT_SERVER_ACK              = 0xA0;    // "%*u"    : Acknowledge

    // Property packets
    public static final int     PKT_SERVER_GET_PROPERTY     = 0xB0;    // "%2u"    : Get property
    public static final int     PKT_SERVER_SET_PROPERTY     = 0xB1;    // "%2u%*b" : Set property

    // File upload packet
    public static final int     PKT_SERVER_FILE_UPLOAD      = 0xC0;    // "%1x%3u%*b" : File upload

    // Error packets
    public static final int     PKT_SERVER_ERROR            = 0xE0;    // "%2u"    : NAK/Error codes
    
    // End-Of-Transmission
    public static final int     PKT_SERVER_EOT              = 0xFF;    // ""       : End transmission (socket will be closed)

    // ------------------------------------------------------------------------
    // custom event payload templates
    
    /* standard fixed low-resolution event format */
    private static PayloadTemplate ClientCustomEvent_30 = new PayloadTemplate(
        Packet.PKT_CLIENT_FIXED_FMT_STD,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  2),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_TIMESTAMP   , false, 0,  4),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_GPS_POINT   , false, 0,  6),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_SPEED       , false, 0,  1),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_HEADING     , false, 0,  1),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_ALTITUDE    , false, 0,  2),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_DISTANCE    , false, 0,  3),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_SEQUENCE    , false, 0,  1)
        }
    );

    /* standard fixed high-resolution event format */
    private static PayloadTemplate ClientCustomEvent_31 = new PayloadTemplate(
        Packet.PKT_CLIENT_FIXED_FMT_HIGH,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , true , 0,  2),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_TIMESTAMP   , true , 0,  4),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_GPS_POINT   , true , 0,  8),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_SPEED       , true , 0,  2),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_HEADING     , true , 0,  2),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_ALTITUDE    , true , 0,  3),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_DISTANCE    , true , 0,  3),
            new PayloadTemplate.Field(PayloadTemplate.FIELD_SEQUENCE    , true , 0,  1)
        }
    );

    // ------------------------------------------------------------------------
    // packet payload templates
    // overloaded types:
    //  FIELD_STATUS_CODE - numeric hex
    //  FIELD_INDEX       - numeric dec
    //  FIELD_BINARY      - binary (byte[])
    //  FIELD_STRING      - string
    //  FIELD_ENTITY      - string

    private static PayloadTemplate ClientTemplate_EndOfBlock_Done = new PayloadTemplate(
        Packet.PKT_CLIENT_EOB_DONE,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  2), // checksum
        }
    );

    private static PayloadTemplate ClientTemplate_EndOfBlock_More = new PayloadTemplate(
        Packet.PKT_CLIENT_EOB_MORE,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  2), // checksum
        }
    );

    private static PayloadTemplate ClientTemplate_Unique_ID = new PayloadTemplate(
        Packet.PKT_CLIENT_UNIQUE_ID,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_BINARY      , false, 0, 20), // unique-id
        }
    );

    private static PayloadTemplate ClientTemplate_Account_ID = new PayloadTemplate(
        Packet.PKT_CLIENT_ACCOUNT_ID,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STRING      , false, 0, 20), // account-id
        }
    );

    private static PayloadTemplate ClientTemplate_Device_ID = new PayloadTemplate(
        Packet.PKT_CLIENT_DEVICE_ID,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STRING      , false, 0, 20), // device-id
        }
    );

    private static PayloadTemplate ClientTemplate_ProvertyValue = new PayloadTemplate(
        Packet.PKT_CLIENT_PROPERTY_VALUE,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  2), // key
            new PayloadTemplate.Field(PayloadTemplate.FIELD_BINARY      , false, 0,253), // device-id
        }
    );

    private static PayloadTemplate ClientTemplate_CustomDef = new PayloadTemplate(
        Packet.PKT_CLIENT_FORMAT_DEF_24,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  1), // key
            new PayloadTemplate.Field(PayloadTemplate.FIELD_INDEX       , false, 0,  1), // count
            new PayloadTemplate.Field(PayloadTemplate.FIELD_BINARY      , false, 0,  3), // field def
        },
        true
    );

    private static PayloadTemplate ClientTemplate_Diagnostic = new PayloadTemplate(
        Packet.PKT_CLIENT_DIAGNOSTIC,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  2), // code
            new PayloadTemplate.Field(PayloadTemplate.FIELD_BINARY      , false, 0,253), // data
        }
    );

    private static PayloadTemplate ClientTemplate_Error = new PayloadTemplate(
        Packet.PKT_CLIENT_ERROR,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  2), // code
            new PayloadTemplate.Field(PayloadTemplate.FIELD_BINARY      , false, 0,253), // data
        }
    );

    // ------------------------------------------------------------------------
    // Client Payload template table

    private static PayloadTemplate ClientEventPayloadTemplate_table[] = {
        ClientCustomEvent_30,
        ClientCustomEvent_31,
    };

    private static PayloadTemplate ClientStandardPayloadTemplate_table[] = {
        ClientTemplate_EndOfBlock_Done,
        ClientTemplate_EndOfBlock_More,
        ClientTemplate_Unique_ID,
        ClientTemplate_Account_ID,
        ClientTemplate_Device_ID,
        ClientTemplate_ProvertyValue,
        ClientTemplate_CustomDef,
        ClientTemplate_Diagnostic,
        ClientTemplate_Error
    };

    public static PayloadTemplate GetClientPayloadTemplate(int type)
    {
        // These need to be in a hash table
        // first try events
        for (int i = 0; i < ClientEventPayloadTemplate_table.length; i++) {
            if (type == ClientEventPayloadTemplate_table[i].getPacketType()) {
                return ClientEventPayloadTemplate_table[i];
            }
        }
        // then try the others
        for (int i = 0; i < ClientStandardPayloadTemplate_table.length; i++) {
            if (type == ClientStandardPayloadTemplate_table[i].getPacketType()) {
                return ClientStandardPayloadTemplate_table[i];
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Server Payload template table

    private static PayloadTemplate ServerTemplate_EndOfBlock_Done = new PayloadTemplate(
        Packet.PKT_SERVER_EOB_DONE,
        new PayloadTemplate.Field[0]
    );

    private static PayloadTemplate ServerTemplate_EndOfBlock_SpeakFreely = new PayloadTemplate(
        Packet.PKT_SERVER_EOB_SPEAK_FREELY,
        new PayloadTemplate.Field[0]
    );

    private static PayloadTemplate ServerTemplate_Ack = new PayloadTemplate(
        Packet.PKT_SERVER_ACK,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  4), // sequence
        }
    );

    private static PayloadTemplate ServerTemplate_GetProperty = new PayloadTemplate(
        Packet.PKT_SERVER_GET_PROPERTY,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  4), // property
        }
    );

    private static PayloadTemplate ServerTemplate_SetProperty = new PayloadTemplate(
        Packet.PKT_SERVER_SET_PROPERTY,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  2), // property
            new PayloadTemplate.Field(PayloadTemplate.FIELD_BINARY      , false, 0,251), // value
        }
    );

    private static PayloadTemplate ServerTemplate_Error = new PayloadTemplate(
        Packet.PKT_SERVER_ERROR,
        new PayloadTemplate.Field[] {
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  2), // error
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  1), // header
            new PayloadTemplate.Field(PayloadTemplate.FIELD_STATUS_CODE , false, 0,  1), // type
            new PayloadTemplate.Field(PayloadTemplate.FIELD_BINARY      , false, 0,251), // extra
        }
    );

    private static PayloadTemplate ServerTemplate_EndOfTransmission = new PayloadTemplate(
        Packet.PKT_SERVER_EOT,
        new PayloadTemplate.Field[0]
    );

    private static PayloadTemplate ServerStandardPayloadTemplate_table[] = {
        ServerTemplate_EndOfBlock_Done,
        ServerTemplate_Ack,
        ServerTemplate_GetProperty,
        ServerTemplate_SetProperty,
        ServerTemplate_Error,
        ServerTemplate_EndOfTransmission
    };

    public static PayloadTemplate GetServerPayloadTemplate(int type)
    {
        // These need to be in a hash table
        for (int i = 0; i < ServerStandardPayloadTemplate_table.length; i++) {
            if (type == ServerStandardPayloadTemplate_table[i].getPacketType()) {
                return ServerStandardPayloadTemplate_table[i];
            }
        }
        return null;
    }

    // ------------------------------------------------------------------------
    // Server to client general packets

    public static Packet createServerPacket(int type)
    {
        return new Packet(null, false, HEADER_BASIC, type);
    }

    public static Packet createServerPacket(int type, Payload payload)
    {
        return new Packet(null, false, HEADER_BASIC, type, payload);
    }

    public static Packet createServerPacket(int type, byte payload[])
    {
        return new Packet(null, false, HEADER_BASIC, type, payload);
    }
    
    // ------------------------------------------------------------------------
    // Server to client error packets

    public static Packet createServerErrorPacket(int errCode, Packet causePkt)
    {
        Payload payload = new Payload();
        payload.writeULong((long)errCode, 2);
        if (causePkt != null) {
            payload.writeULong((long)causePkt.getPacketHeader() , 1);
            payload.writeULong((long)causePkt.getPacketType()   , 1);
        } else {
            payload.writeULong(0L                               , 1);
            payload.writeULong(0L                               , 1);
        }
        // caller may still want to write additional arguments to the specific error payload
        return createServerPacket(PKT_SERVER_ERROR, payload);
    }
    
    public static Packet createServerErrorPacket(int errCode, int header, int type)
    {
        Payload payload = new Payload();
        payload.writeULong((long)errCode, 2);
        payload.writeULong((long)header , 1);
        payload.writeULong((long)type   , 1);
        // caller may still want to write in arguments to the specific error payload
        return createServerPacket(PKT_SERVER_ERROR, payload);
    }
    
    // ------------------------------------------------------------------------
    // Server to client file upload packet
    //   0:1 - Record Type
    //   1:3 - File size
    //   4:X - File name

    /* Tell client to 'Get' file [0x31] */
    public static Packet createServerGetFilePacket(String fileName, long fileSize)
    {
        if (fileName == null) { fileName = ""; }
        Payload payload = new Payload();
        payload.writeULong((long)0x31, 1); // UPLOAD_TYPE_OOB_GETFILE
        payload.writeULong(fileSize, 3);
        payload.writeString(fileName, 64);
        return createServerPacket(PKT_SERVER_FILE_UPLOAD, payload);
    }

    /* Tell client to 'Put' file [0x41] */
    public static Packet createServerPutFilePacket(String fileName)
    {
        if (fileName == null) { fileName = ""; }
        Payload payload = new Payload();
        payload.writeULong((long)0x41, 1); // UPLOAD_TYPE_OOB_PUTFILE
        payload.writeULong(0L, 3);
        payload.writeString(fileName, 64);
        return createServerPacket(PKT_SERVER_FILE_UPLOAD, payload);
    }

    // ------------------------------------------------------------------------
    // Server to client property packets

    public static Packet createServerGetPropertyPacket(int propCode, byte propData[], int propDataLen)
    {
        Payload payload = new Payload();
        payload.writeULong((long)propCode, 2);
        payload.writeBytes(propData, propDataLen);
        return createServerPacket(PKT_SERVER_GET_PROPERTY, payload);
    }

    public static Packet createServerSetPropertyPacket(int propCode, byte propData[], int propDataLen)
    {
        Payload payload = new Payload();
        payload.writeULong((long)propCode, 2);
        payload.writeBytes(propData, propDataLen);
        return createServerPacket(PKT_SERVER_SET_PROPERTY, payload);
    }

    public static Packet createServerSetPropertyPacket(int propCode, long value[])
    {
        byte propData[] = PropCodes.encodePropertyData(propCode, value);
        if (propData != null) {
            return createServerSetPropertyPacket(propCode, propData, propData.length);
        } else {
            return null;
        }
    }

    public static Packet createServerSetPropertyPacket(int propCode, double value[])
    {
        byte propData[] = PropCodes.encodePropertyData(propCode, value);
        if (propData != null) {
            return createServerSetPropertyPacket(propCode, propData, propData.length);
        } else {
            return null;
        }
    }

    public static Packet createServerSetPropertyPacket(int propCode, String value)
    {
        byte propData[] = PropCodes.encodePropertyData(propCode, ((value!=null)?value:""));
        if (propData != null) {
            return createServerSetPropertyPacket(propCode, propData, propData.length);
        } else {
            return null;
        }
    }

    // ------------------------------------------------------------------------

    private static int CalcChecksum(byte b[])
    {
        if (b == null) {
            return -1;
        } else {
            int cksum = 0, s = 0;
            if ((b.length > 0) && (b[0] == Encoding.AsciiEncodingChar)) { s++; }
            for (; s < b.length; s++) {
                if (b[s] == Encoding.AsciiChecksumChar ) { break; }
                if (b[s] == Encoding.AsciiEndOfLineChar) { break; }
                cksum = (cksum ^ b[s]) & 0xFF;
            }
            return cksum;
        }
    }
    
    // ------------------------------------------------------------------------
    
    private int             encoding           = Encoding.ENCODING_BINARY;
    private boolean         hasAsciiChecksum   = false;
    private DeviceID        deviceId           = null; // only used to parse custom device packets
    private PayloadTemplate payloadTemplate    = null;
    private boolean         isClient           = true;
    private int             header             = 0;
    private int             type               = 0;
    private Payload         payload            = null;

    public Packet(PayloadTemplate template, int header, byte payload[])
    {
        this.deviceId        = null; // not needed for server packets
        this.payloadTemplate = template;
        this.isClient        = true;
        this.header          = header;
        this.type            = template.getPacketType();
        this.payload         = new Payload(payload);
    }

    public Packet(DeviceID devId, boolean isClient, int header, int type)
    {
        this(devId, isClient, header, type, new Payload());
    }

    public Packet(DeviceID devId, boolean isClient, int header, int type, byte payload[])
    {
        this(devId, isClient, header, type, new Payload(payload));
    }

    public Packet(DeviceID devId, boolean isClient, int header, int type, Payload payload)
    {
        this.deviceId        = devId; // not needed for server packets
        this.payloadTemplate = null;
        this.isClient        = isClient;
        this.header          = header;
        this.type            = type;
        this.payload         = payload;
    }

    public Packet(DeviceID devId, boolean isClient, String pkt) 
        throws PacketParseException 
    {
        this(devId, isClient, StringTools.getBytes(pkt));
    }

    public Packet(DeviceID devId, boolean isClient, byte pkt[]) 
        throws PacketParseException 
    {
        // 'pkt' always contains only a single packet
        this.deviceId = devId; // (for custom templates) not needed for server packets
        this.isClient = isClient;
        this.encoding = Encoding.ENCODING_UNKNOWN;
        if (pkt.length < 3) {
            
            this.header = (pkt.length > 0)? ((int)pkt[0] & 0xFF) : 0x00;
            this.type   = (pkt.length > 1)? ((int)pkt[1] & 0xFF) : 0x00;
            throw new PacketParseException(ServerErrors.NAK_PACKET_LENGTH, this); // errData ok
            
        } else
        if (pkt[0] == Encoding.AsciiEncodingChar) {
            
            /* checksum */
            int pLen = 1; // start with first character after AsciiEndOfLineChar
            int cksumActual = 0, cksumTest = -1;
            this.hasAsciiChecksum = false;
            for (;(pLen < pkt.length) && (pkt[pLen] != Encoding.AsciiEndOfLineChar); pLen++) {
                if (pkt[pLen] == Encoding.AsciiChecksumChar) {
                    this.hasAsciiChecksum = true;
                    String hexCksum = StringTools.toStringValue(pkt, pLen + 1, 2);
                    cksumTest = StringTools.parseHexInt(hexCksum, -1);
                    break;
                }
                cksumActual = (cksumActual ^ pkt[pLen]) & 0xFF;
            }
            // 'pLen' now represents length of actual packet string.
            
            /* string packet */
            String p = StringTools.toStringValue(pkt, 0, pLen);
            
            /* header */
            this.header = (pLen >= 3)? StringTools.parseHexInt(p.substring(1,3), 0x00) : 0x00;
            this.type   = (pLen >= 5)? StringTools.parseHexInt(p.substring(3,5), 0x00) : 0x00;
            if (this.header != HEADER_BASIC) {
                throw new PacketParseException(ServerErrors.NAK_PACKET_HEADER, this); // errData ok
            }

            /* minimum length */
            if (pLen < 5) { // eg. "$E0D1"
                throw new PacketParseException(ServerErrors.NAK_PACKET_LENGTH, this); // errData ok
            }
            
            /* check checksum */
            // wait until header/type are parsed before testing checksum
            if (cksumTest < 0) {
                // record does not contain a checksum
            } else
            if (cksumTest != cksumActual) {
                // If the checksum fails, then we really can't trust any information contained in the 
                // packet, thus we are unable to accurately let the client know specifically which packet
                // had the problem.
                throw new PacketParseException(ServerErrors.NAK_PACKET_CHECKSUM, this); // errData ok
            } else {
                //Print.logDebug("Checksum is OK!");
            }

            /* payload encoding */
            int ench = (p.length() >= 6)? p.charAt(5) : -1;
            if ((ench == Encoding.AsciiEndOfLineChar) || (ench < 0)) {
                // encoding not known, assign default
                this.encoding = this.hasAsciiChecksum? Encoding.ENCODING_BASE64_CKSUM : Encoding.ENCODING_BASE64;
                this.payload  = new Payload(new byte[0]);
            } else
            if (ench == Encoding.ENCODING_HEX_CHAR) {
                // Hex
                this.encoding = this.hasAsciiChecksum? Encoding.ENCODING_HEX_CKSUM : Encoding.ENCODING_HEX;
                this.payload  = new Payload(StringTools.parseHex(p.substring(6), new byte[0]));
            } else
            if (ench == Encoding.ENCODING_BASE64_CHAR) {
                // Base64
                this.encoding = this.hasAsciiChecksum? Encoding.ENCODING_BASE64_CKSUM : Encoding.ENCODING_BASE64;
                this.payload  = new Payload(Base64.decode(p.substring(6)));
            } else
            if (ench == Encoding.ENCODING_CSV_CHAR) {
                // CSV
                this.encoding = this.hasAsciiChecksum? Encoding.ENCODING_CSV_CKSUM : Encoding.ENCODING_CSV;
                this.payload  = _decodeCSV(this, p.substring(6));
            } else {
                // unrecognized encoding
                throw new PacketParseException(ServerErrors.NAK_PACKET_ENCODING, this); // errData ok
            }
            
        } else
        if (pkt[0] == (byte)HEADER_BASIC) {
            
            /* binary header */
            this.encoding = Encoding.ENCODING_BINARY;
            this.header   = (int)pkt[0] & 0xFF;
            this.type     = (int)pkt[1] & 0xFF;
            
            /* check payload length */
            int len = (int)pkt[2] & 0xFF;
            if (len != pkt.length - 3) {
                throw new PacketParseException(ServerErrors.NAK_PACKET_LENGTH, this); // errData ok
            }
            
            /* payload */
            this.payload  = new Payload(pkt, 3, len);
            
        } else {
            
            this.encoding = Encoding.ENCODING_UNKNOWN;
            this.header   = (int)pkt[0] & 0xFF;
            this.type     = (int)pkt[1] & 0xFF;
            throw new PacketParseException(ServerErrors.NAK_PACKET_HEADER, this); // errData ok

        }
        
    }

    public Packet(byte pkt[]) 
        throws PacketParseException 
    {
        // parsed 'server' packet only
        this(null, false, pkt);
    }
    
    // ------------------------------------------------------------------------

    public void setEncoding(int encoding)
    {
        this.encoding = encoding;
    }
    
    public int getEncoding()
    {
        return this.encoding;
    }
    
    // ------------------------------------------------------------------------

    public int getPacketHeader()
    {
        return this.header;
    }
    
    public boolean isBasicPacketHeader()
    {
        return (this.getPacketHeader() == HEADER_BASIC);
    }
    
    // ------------------------------------------------------------------------

    public int getPacketType()
    {
        return this.type;
    }

    public boolean isIdentType()
    {
        int t = this.getPacketType();
        return (t == PKT_CLIENT_UNIQUE_ID ) || 
               (t == PKT_CLIENT_ACCOUNT_ID) ||
               (t == PKT_CLIENT_DEVICE_ID );
    }

    public boolean isEventType()
    {
        return Packet.isEventType(this.getPacketType());
    }

    public static boolean isEventType(int t)
    {
        if (Packet.isFixedEventType(t) || Packet.isCustomEventType(t)) {
            return true;
        } else
        if ((t >= PKT_CLIENT_DMTSP_FMT_0) && (t <= PKT_CLIENT_DMTSP_FMT_F)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isFixedEventType(int t)
    {
        if ((t == PKT_CLIENT_FIXED_FMT_STD) || (t == PKT_CLIENT_FIXED_FMT_HIGH)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isCustomEventType(int t)
    {
        if ((t >= PKT_CLIENT_CUSTOM_FORMAT_0) && (t <= PKT_CLIENT_CUSTOM_FORMAT_F)) {
            return true;
        } else {
            return false;
        }
    }

    // ------------------------------------------------------------------------

    public boolean hasAsciiChecksum()
    {
        return this.hasAsciiChecksum;
    }
    
    // ------------------------------------------------------------------------

    public int getPacketLength()
    {
        return MIN_HEADER_LENGTH + this.getPayloadLength();
    }

    public int getPayloadLength()
    {
        return this.payload.getSize();
    }
    
    public boolean hasPayload()
    {
        return (this.getPayloadLength() > 0);
    }

    public Payload getPayload(boolean reset)
    {
        if (reset) {
            // make Payload a data source
            this.payload.resetIndex();
        }
        return this.payload;
    }
    
    public PayloadTemplate getPayloadTemplate()
    {
        if (this.isClient) {
            if (this.payloadTemplate != null) {
                return this.payloadTemplate;
            } else {
                PayloadTemplate plt = GetClientPayloadTemplate(this.type);
                if ((plt == null) && (this.deviceId != null)) {
                    plt = this.deviceId.getClientPayloadTemplate(this.type); // may still return null
                    //if (plt == null) {
                    //    Print.logError("PayloadTemplate not found: " + StringTools.toHexString(this.type,8));
                    //}
                }
                this.payloadTemplate = plt;
                return plt;
            }
        } else {
            return GetServerPayloadTemplate(this.type);
        }
    }
    
    // ------------------------------------------------------------------------
    
    public byte[] getPayloadBytes(int ofs, int len)
    {
        byte b[] = this.getPayload(true).getBytes();
        if (ofs >= b.length) {
            return new byte[0];
        } else {
            if (len > (b.length - ofs)) { len = b.length - ofs; }
            byte n[] = new byte[len];
            System.arraycopy(b, ofs, n, 0, len);
            return n;
        }
    }
    
    // ------------------------------------------------------------------------

    public byte[] encode()
    {
        return this.encode(this.getEncoding());
    }
    
    public byte[] encode(int encoding)
    {
        byte payload[] = this.getPayload(true).getBytes();
        if (encoding == Encoding.ENCODING_BINARY) {
            int len = payload.length;
            byte pkt[] = new byte[MIN_HEADER_LENGTH + len];
            pkt[0] = (byte)(this.header & 0xFF);
            pkt[1] = (byte)(this.type & 0xFF);
            pkt[2] = (byte)(len & 0xFF);
            System.arraycopy(payload, 0, pkt, 3, len);
            return pkt;
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(Encoding.AsciiEncodingChar);
            sb.append(StringTools.toHexString((long)this.header & 0xFF, 8));
            sb.append(StringTools.toHexString((long)this.type   & 0xFF, 8));
            if (payload.length > 0) {
                switch (encoding) {
                    case Encoding.ENCODING_CSV_CKSUM   :
                    case Encoding.ENCODING_CSV         : {
                        //Print.logDebug("Encoding CSV ...");
                        sb.append(_encodeCSV(this));
                        break;
                    }
                    case Encoding.ENCODING_BASE64_CKSUM: 
                    case Encoding.ENCODING_BASE64      : {
                        //Print.logDebug("Encoding Base64 ...");
                        sb.append(Encoding.ENCODING_BASE64_CHAR); 
                        sb.append(Base64.encode(payload));
                        break;
                    }
                    case Encoding.ENCODING_HEX_CKSUM   : 
                    case Encoding.ENCODING_HEX         : {
                        //Print.logDebug("Encoding Hex ...");
                        sb.append(Encoding.ENCODING_HEX_CHAR); 
                        StringTools.toHexString(payload, sb);
                        break;
                    }
                    case Encoding.ENCODING_UNKNOWN     :
                    default                            : {
                        Print.logError("Unknown encoding: " + encoding);
                        //sb.append("?unknown_encoding?");
                        encoding = Encoding.ENCODING_HEX;
                        sb.append(Encoding.ENCODING_HEX_CHAR); 
                        StringTools.toHexString(payload, sb);
                        break;
                    }
                }
            } else {
                //sb.append(" <No Payload>");
            }
            
            /* add ASCII checksum */
            if (Encoding.IsEncodingChecksum(encoding)) {
                int cksum = CalcChecksum(StringTools.getBytes(sb.toString()));
                if (cksum >= 0) {
                    sb.append(Encoding.AsciiChecksumChar);
                    sb.append(StringTools.toHexString((long)cksum & 0xFF, 8));
                }
            }
            
            /* end of line */
            sb.append(Encoding.AsciiEndOfLineChar);
            return StringTools.getBytes(sb);
            
        }
    }

    // ------------------------------------------------------------------------

    public String toString(int encoding)
    {
        
        /* validate encoding */
        switch (encoding) {
            case Encoding.ENCODING_CSV_CKSUM   :
            case Encoding.ENCODING_CSV         :
            case Encoding.ENCODING_BASE64_CKSUM: 
            case Encoding.ENCODING_BASE64      :
            case Encoding.ENCODING_HEX_CKSUM   : 
            case Encoding.ENCODING_HEX         :
                // valid encoding
                break;
            case Encoding.ENCODING_UNKNOWN     :
            default                            :
                // invalid encoding, pick a valid one
                encoding = Encoding.ENCODING_HEX;
                break;
        }
        
        /* return encoded packet as a string */
        byte b[] = this.encode(encoding);
        if ((b != null) && (b.length > 0)) {
            if (b[0] == Encoding.AsciiEncodingChar) {
                int len = (b[b.length - 1] == Encoding.AsciiEndOfLineChar)? (b.length - 1) : b.length;
                return StringTools.toStringValue(b, 0, len);
            } else {
                return "0x" + StringTools.toHexString(b);
            }
        } else {
            return "";
        }
        
    }

    public String toString()
    {
        return this.toString(this.getEncoding());
    }

    // ------------------------------------------------------------------------

    private static Payload _decodeCSV(Packet pkt, String csv)
        throws PacketParseException
    {
        //Print.logDebug("Parsing CSV: " + csv);

        /* get Payload template field definitions */
        PayloadTemplate plt = pkt.getPayloadTemplate();
        if (plt == null) {
            if (Packet.isCustomEventType(pkt.getPacketType())) {
                throw new PacketParseException(ServerErrors.NAK_FORMAT_NOT_RECOGNIZED, pkt); // errData ok
            } else {
                throw new PacketParseException(ServerErrors.NAK_PACKET_TYPE, pkt); // errData ok
            }
        }

        /* parse csv into fields */
        String csvFlds[] = StringTools.parseString(csv, ',');
        Payload payload = new Payload();
        for (int p = 0, c = 0; c < csvFlds.length; p++) {
            PayloadTemplate.Field pltFld = plt.getField(p);
            if (pltFld == null) { break; }
            c = pltFld.parseString(csvFlds, c, payload);
        }
        //Print.logDebug("Parsed CSV Payload: " + payload);
        payload.resetIndex();
        return payload;

    }

    private static String _encodeCSV(Packet pkt)
    {
        StringBuffer sb = new StringBuffer();
        Payload payload = pkt.getPayload(true);

        /* get Payload template field definitions */
        PayloadTemplate plt = pkt.getPayloadTemplate();
        if (plt == null) {
            // unable to encode with CSV
            sb.append(Encoding.ENCODING_BASE64_CHAR);
            sb.append(Base64.encode(payload.getBytes()));
            return sb.toString();
        }
        
        /* encode to string */
        payload.resetIndex();
        for (int p = 0; payload.isValidReadLength(0); p++) {
            PayloadTemplate.Field pltFld = plt.getField(p);
            if (pltFld == null) { break; }
            sb.append(Encoding.ENCODING_CSV_CHAR);
            int length = pltFld.getLength();
            switch (pltFld.getPrimitiveType()) {
                case PayloadTemplate.PRIMITIVE_GPS: {
                    GeoPoint gp = payload.readGPS(length);
                    if (pltFld.isHiRes()) {
                        sb.append(StringTools.format(gp.getLatitude() , "###0.00000"));
                        sb.append(Encoding.ENCODING_CSV_CHAR);
                        sb.append(StringTools.format(gp.getLongitude(), "###0.00000"));
                    } else {
                        sb.append(StringTools.format(gp.getLatitude() , "###0.0000"));
                        sb.append(Encoding.ENCODING_CSV_CHAR);
                        sb.append(StringTools.format(gp.getLongitude(), "###0.0000"));
                    }
                    break;
                }
                case PayloadTemplate.PRIMITIVE_STRING: {
                    sb.append(payload.readString(length));
                    break;
                }
                case PayloadTemplate.PRIMITIVE_BINARY: {
                    byte b[] = payload.readBytes(length);
                    sb.append("0x" + StringTools.toHexString(b));
                    break;
                }
                case PayloadTemplate.PRIMITIVE_LONG:
                default: {
                    long val = pltFld.isSigned()?
                        payload.readLong(length) :
                        payload.readULong(length);
                    if (pltFld.isHex()) {
                        sb.append( "0x" + StringTools.toHexString(val, length * 8));
                    } else {
                        sb.append(String.valueOf(val));
                    }
                    break;
                }
            }
        }
        return sb.toString();
        
    }

    // ------------------------------------------------------------------------

    /* return the length of the packet starting at the data offset */
    public static int getPacketLength(byte data[], int dataOfs)
    {
        boolean STRICT_ASCII_EOL = false;
        
        /* validate data */
        if (data == null) {
            //Print.logError("No Packet data specified");
            return -1;
        }
        
        /* validate offset/length */
        int dataLen = data.length;
        if (dataOfs + 3 >= dataLen) {
            //Print.logError("Must have at least 3 bytes in packet");
            return -1;
        }
        
        /* find end of packet */
        if (data[dataOfs] == Encoding.AsciiEncodingChar) {
            //Print.logInfo("Ascii packet ...");
            for (int ofs = dataOfs; ofs < dataLen; ofs++) {
                // strictly speaking, an ASCII encoded packet should end with a '\r', however,
                // since this method may be used to parse packets from an ASCII file (which
                // may have been edited by a human), we may also need to check for '\n' as well.
                if (STRICT_ASCII_EOL) {
                    // strict ASCII packet definition, per protocol specification
                    if (data[ofs] == Encoding.AsciiEndOfLineChar) {
                        ofs++; // count EOL
                        //Print.logInfo("Ascii packet length: " + (ofs - dataOfs));
                        return (ofs - dataOfs);
                    }
                } else
                if ((data[ofs] == '\r') || (data[ofs] == '\n')) {
                    // relaxed ASCII packet EOL
                    while ((ofs < dataLen) && ((data[ofs] == '\r') || (data[ofs] == '\n'))) { ofs++; }
                    //Print.logInfo("Ascii packet length: " + (ofs - dataOfs));
                    return (ofs - dataOfs);
                }
            }
            // EOL not found
            //Print.logError("Ascii EOL not found!");
            return -1;
        } else {
            int len = 3 + ((int)data[dataOfs + 2] & 0xFF);
            if ((dataOfs + len) <= dataLen) {
                return len;
            } else {
                // beyond end of 'data'
                return -1;
            }
        }
        
    }
    
    // ------------------------------------------------------------------------

}
