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
//  2006/05/10  Martin D. Flynn
//     -Added 'getPendingPackets()' method
//  2006/05/26  Martin D. Flynn
//     -Incorporated <UniqueID>.getId() return of a byte array.
//  2006/06/28  Martin D. Flynn
//     -Change constructor to require AccountID instance, rather than just the 
//      account name String.
//  2007/12/04  Martin D. Flynn
//     -'getPendingPackets' now returns a 'PacketList' object
//  2008/01/10  Martin D. Flynn
//     -Added 'saveSessionStatistics' method for recording device connection information.
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

public class DeviceID
{

    // ------------------------------------------------------------------------
    // - DeviceID
    //      - AccountID name [key]
    //      - DeviceID name [key]
    //      - UniqueID code [altKey]
    //      - Event notification email
    //      - isActive
    //      - Supported encodings
    //      - Time of last connection
    //      - Time interval over which below limits apply
    //      - Max events per unit time
    //      - Total connection profile mask
    //      - Max total connections per unit time
    //      - Max total connections per minute (0..3)
    //      - Duplex connection profile mask
    //      - Max duplex connections per unit time
    //      - Max duplex connections per minute (0..3)
    // - DeviceIDErrors (logged errors)
    //      - AccountID name [key]
    //      - DeviceID name [key]
    //      - ArrivalTime [key]
    //      - Index [key] (uniquifier)
    //      - Error packet
    // - CustomTemplate
    //      - AccountID name [key]
    //      - DeviceID name [key]
    //      - Packet header [key]
    //      - Custom template type [key]
    //      - Custom template definition    

    /* device ID */
    private DeviceDB            db = null;
    private AccountID           accountId = null;
    private ValidateConnection  connectionValidator = null;
 
    // ------------------------------------------------------------------------
    
    private static DeviceDB GetDeviceDB(UniqueID uniqId)
        throws PacketParseException
    {
        DMTPServer.DBFactory fact = DMTPServer.getDBFactory();
        if (fact != null) {
            byte id[] = uniqId.getId();
            DeviceDB db = fact.getDeviceDB(id);
            if (db != null) {
                return db;
            } else {
                Print.logError("Device not found: UniqueID " + StringTools.toHexString(id));
                // fall through
            }
        }
        return null;
    }

    private static DeviceDB GetDeviceDB(AccountDB acctDB, String devName)
        throws PacketParseException
    {
        DMTPServer.DBFactory fact = DMTPServer.getDBFactory();
        if (fact != null) {
            DeviceDB db = fact.getDeviceDB(acctDB, devName);
            if (db != null) {
                return db;
            } else {
                String acctName = (acctDB != null)? acctDB.getAccountName() : "null";
                Print.logError("Device not found: Acct/Dev " + acctName + "/" + devName);
                // fall through
            }
        } 
        return null;
    }

    // ------------------------------------------------------------------------
    
    public static DeviceID loadDeviceID(UniqueID uniqId)
        throws PacketParseException
    {
        return new DeviceID(uniqId);
    }

    public static DeviceID loadDeviceID(AccountID acctId, String devName)
        throws PacketParseException
    {
        return new DeviceID(acctId, devName);
    }
    
    // ------------------------------------------------------------------------
    
    private DeviceID(UniqueID uniqId)
        throws PacketParseException
    {
        
        /* validate arguments */
        if ((uniqId == null) || !uniqId.isValid()) {
            Print.logError("UniqueID is invalid: " + uniqId);
            throw new PacketParseException(ServerErrors.NAK_ID_INVALID, null); // errData ok
        }
        
        /* device exists? */
        this.db = GetDeviceDB(uniqId);
        if (this.db == null) {
            Print.logError("UniqueID not found: " + uniqId);
            throw new PacketParseException(ServerErrors.NAK_ID_INVALID, null); // errData ok
        }

        /* account */
        this.accountId = AccountID.loadAccountID(this.db.getAccountName());
        Print.logInfo("[" + (new DateTime()) + "] Loaded device: " + this.db.getAccountName() + "/" + this.db.getDeviceName());
        
    }

    private DeviceID(AccountID acctId, String devName)
        throws PacketParseException
    {
        
        /* validate arguments */
        if ((devName == null) || devName.equals("")) {
            Print.logError("Device name is null/empty");
            throw new PacketParseException(ServerErrors.NAK_DEVICE_INVALID, null); // errData ok
        } else
        if (acctId == null) {
            Print.logError("AccountID is null");
            throw new PacketParseException(ServerErrors.NAK_ACCOUNT_INVALID, null); // errData ok
        }
        
        /* device exists? */
        AccountDB acctDB = acctId.getAccountDB();
        this.db = GetDeviceDB(acctDB, devName);
        if (this.db == null) {
            String acctName = acctId.getAccountName();
            Print.logError("Device not found: " + acctName + "/" + devName);
            throw new PacketParseException(ServerErrors.NAK_DEVICE_INVALID, null); // errData ok
        }
        
        /* account */
        this.accountId = acctId;
        Print.logInfo("[" + (new DateTime()) + "] Loaded device: " + this.db.getAccountName() + "/" + this.db.getDeviceName());

    }

    // ------------------------------------------------------------------------
    // Device ID
    
    public AccountID getAccountID()
    {
        return this.accountId;
    }

    public DeviceDB getDeviceDB()
    {
        return this.db;
    }

    public String getAccountName()
    {
        return this.db.getAccountName();
    }

    public String getDeviceName()
    {
        return this.db.getDeviceName();
    }
    
    // ------------------------------------------------------------------------
    // Accounting: device active

    public boolean isActive()
    {
        // check to see if this device is still active in the database
        return this.db.isActive();
    }

    // ------------------------------------------------------------------------
    // IPAddress: IP address valid

    public boolean isValidIpAddress(String ipAddr)
    {
        return this.db.isValidIpAddress(ipAddr);
    }

    // ------------------------------------------------------------------------
    // Connection validation

    public ValidateConnection getConnectionValidator()
    {
        if (this.connectionValidator == null) {
            this.connectionValidator = new ValidateConnection(this.db.getLimitTimeIntervalMinutes());
        }
        return this.connectionValidator;
    }
    
    public boolean markAndValidateConnection(boolean isDuplex)
    {
        long nowTime = DateTime.getCurrentTimeSec();

        /* test total connections */
        byte totalConn[] = this.getConnectionValidator().markConnection(
            this.db.getMaxTotalConnections(),
            this.db.getMaxTotalConnectionsPerMinute(), 
            this.db.getTotalConnectionProfile(),
            (nowTime - this.db.getLastTotalConnectionTime()));
        if (totalConn == null) {
            // exceed total allowable connections per time period
            Print.logError("Exceeded total allowable conections");
            return false;
        }
        this.db.setTotalConnectionProfile(totalConn); // may be redundant
        this.db.setLastTotalConnectionTime(nowTime);

        /* test duplex connections */
        if (isDuplex) {
            byte duplexConn[] = this.getConnectionValidator().markConnection(
                this.db.getMaxDuplexConnections(),
                this.db.getMaxDuplexConnectionsPerMinute(), 
                this.db.getDuplexConnectionProfile(),
                (nowTime - this.db.getLastDuplexConnectionTime()));
            if (duplexConn == null) {
                // exceed allowable duplex connections per time period
                Print.logError("Exceeded allowable duplex conections");
                return false;
            }
            this.db.setDuplexConnectionProfile(duplexConn); // may be redundant
            this.db.setLastDuplexConnectionTime(nowTime);
        }
        
        /* save total/duplex connections mask */
        return true;
        
    }

    // ------------------------------------------------------------------------
    // Encoding

    public boolean supportsEncoding(int encoding)
    {
        return this.db.supportsEncoding(encoding);
    }
    
    public void removeEncoding(int encoding)
    {
        this.db.removeEncoding(encoding);
    }
    
    // ------------------------------------------------------------------------
    
    public boolean addClientPayloadTemplate(PayloadTemplate template)
    {
        return this.db.addClientPayloadTemplate(template);
    }
    
    public PayloadTemplate getClientPayloadTemplate(int custType)
    {
        return this.db.getClientPayloadTemplate(custType);
    }
    
    // ------------------------------------------------------------------------
        
    public int saveEvent(Event event)
    {
        Packet packet = event.getPacket();
        
        long timeEnd   = DateTime.getCurrentTimeSec();
        long timeStart = timeEnd - DateTime.MinuteSeconds(this.db.getLimitTimeIntervalMinutes());
        if ((this.db.getMaxAllowedEvents() > 0) &&
            (this.db.getEventCount(timeStart, timeEnd) >= this.db.getMaxAllowedEvents())) {
            
            /* excessive events */
            Print.logError("Excessive events");
            return ServerErrors.NAK_EXCESSIVE_EVENTS;
            
        } else {
            
            /* insert event */
            int err = this.db.insertEvent(event.getGeoEvent());
            // ServerErrors.NAK_DUPLICATE_EVENT
            // ServerErrors.NAK_EVENT_ERROR
            // ServerErrors.NAK_OK
            return err;

        }
    }

    // ------------------------------------------------------------------------

    public void saveSessionStatistics(long startTime, String ipAddr, boolean isDuplex, 
        long bytesRead, long bytesWritten, long evtsRecv)
    {
        this.db.sessionStatistics(startTime, ipAddr, isDuplex, bytesRead, bytesWritten, evtsRecv);
    }

    // ------------------------------------------------------------------------

    public PacketList getPendingPackets()
    {
        return this.db.getPendingPackets();
    }

    public void clearPendingPackets(PacketList pktList)
    {
        this.db.clearPendingPackets(pktList);
    }
    // ------------------------------------------------------------------------

    public int saveChanges()
        throws PacketParseException
    {
        return this.db.saveChanges();
    }

    // ------------------------------------------------------------------------

    /* log/handle client device errors */
    public void handleError(int errCode, byte errData[])
    {
        this.db.handleError(errCode, errData);
    }

    /* log/handle client device diagnostic values */
    public void handleDiagnostic(int diagCode, byte diagData[])
    {
        this.db.handleDiagnostic(diagCode, diagData);
    }

    /* log/handle client device property values */
    public void handleProperty(int propKey, byte propVal[])
    {
        this.db.handleProperty(propKey, propVal);
    }

    // ------------------------------------------------------------------------

    public String toString()
    {
        return (this.db != null)? this.db.toString() : "";
    }
    
    // ------------------------------------------------------------------------

}
