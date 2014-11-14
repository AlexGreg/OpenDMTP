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
//  2007/12/04  Martin D. Flynn
//     -Initial release
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

public class PacketList
{

    // ------------------------------------------------------------------------

    private String      accountName = null;
    private String      deviceName  = null;
    private Packet      packets[]   = null;
    private long        timestamp   = 0L;
    
    public PacketList(String accountName, String deviceName, Packet pkts[], long timestamp)
    {
        this.accountName = accountName;
        this.deviceName  = deviceName;
        this.packets     = (pkts != null)? pkts : new Packet[0];
        this.timestamp   = timestamp;
    }

    // ------------------------------------------------------------------------

    public String getAccountName()
    {
        return this.accountName;
    }

    public String getDeviceName()
    {
        return this.deviceName;
    }
    // ------------------------------------------------------------------------

    public int getPacketCount()
    {
        return (this.packets != null)? this.packets.length : 0;
    }

    public boolean hasPackets()
    {
        return (this.getPacketCount() > 0);
    }
    
    public Packet[] getPackets()
    {
        return this.packets;
    }
    
    // ------------------------------------------------------------------------

    public long getTimestamp()
    {
        return this.timestamp;
    }

}
