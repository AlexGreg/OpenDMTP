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
//  2007/09/16  Martin D. Flynn
//     -Added handler methods for errors, diagnostics, and properties.
//  2007/12/04  Martin D. Flynn
//     -'getPendingPackets' now returns a 'PacketList' object
// ----------------------------------------------------------------------------
package org.opendmtp.server.db;

import java.lang.*;
import java.util.*;

import org.opengts.util.*;

import org.opendmtp.server.base.Packet;
import org.opendmtp.server.base.PacketList;
import org.opendmtp.server.base.DMTPGeoEvent;

public interface DeviceDB
{
    
    // Record keys
    public String getAccountName();
    public String getDeviceName();

    // Displayed description of this device
    public String getDescription();

    // true, if this device is active
    public boolean isActive();

    // check incoming IP address
    public boolean isValidIpAddress(String ipAddr);

    // time period (in minutes) over which the following limits apply
    public int getLimitTimeIntervalMinutes();

    // maximum allowed events per 'LimitTimeInterval'
    public int getMaxAllowedEvents();
    
    // return the current number of events within the specified time interval
    public long getEventCount(long timeStart, long timeEnd);
    
    // maximum allowed 'total' connections (simplex/duplex) per 'LimitTimeInterval'
    public int getMaxTotalConnections();
    
    // maximum allowed 'total' connections (simplex/duplex) per minute (used for connection profile)
    public int getMaxTotalConnectionsPerMinute();
    
    // get/set the 'total' connection profile
    public byte[] getTotalConnectionProfile();
    public void setTotalConnectionProfile(byte[] profile);
    
    // get/set the last simplex/duplex connection time
    public long getLastTotalConnectionTime();
    public void setLastTotalConnectionTime(long connectTime);
    
    // maximum allowed 'duplex' connections per 'LimitTimeInterval'
    public int getMaxDuplexConnections();
    
    // maximum allowed 'duplex' connections per minute (used for connection profile)
    public int getMaxDuplexConnectionsPerMinute();

    // get/set the 'duplex' connection profile
    public byte[] getDuplexConnectionProfile();
    public void setDuplexConnectionProfile(byte[] profile);

    // get/set the last duplex connection time
    public long getLastDuplexConnectionTime();
    public void setLastDuplexConnectionTime(long connectTime);
    
    // set/get supported packet encodings
    public boolean supportsEncoding(int encoding);
    public void removeEncoding(int encoding);
    
    // set/get custom event packet template
    public boolean addClientPayloadTemplate(PayloadTemplate template);
    public PayloadTemplate getClientPayloadTemplate(int custType);
    
    // insert event into datastore
    public int insertEvent(DMTPGeoEvent event);
    
    // save session statistics
    public void sessionStatistics(long startTime, String ipAddr, boolean isDuplex, long bytesRead, long bytesWritten, long evtsRecv);
    
    // get list of pending packets to send to device
    public PacketList getPendingPackets();
    public void clearPendingPackets(PacketList pktList);

    // save any changes to the datastore
    public int saveChanges();

    // handle errors from client
    public void handleError(int errCode, byte errData[]);

    // handle diagnostics from client
    public void handleDiagnostic(int diagCode, byte diagData[]);

    // handle properties from client
    public void handleProperty(int propKey, byte propVal[]);

}
