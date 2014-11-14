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
//      Initial release
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

public class PacketParseException
    extends Exception
{
    
    // ------------------------------------------------------------------------

    /* Server error codes (see "org.opendmtp.codes.ServerErrors") */
    private int     errorCode   = 0x0000;
    
    /* error data */
    private Packet  errPacket    = null;
    private byte    errorData[] = null;
    
    /* if true, this indicates to the client/server session that it should terminate immediately */
    private boolean terminate   = false;

    // ------------------------------------------------------------------------

    public PacketParseException(int servErrCode) 
    {
        this(servErrCode, null, null);
    }

    public PacketParseException(int servErrCode, Packet packet) 
    {
        this(servErrCode, packet, null);
    }
    
    public PacketParseException(int servErrCode, Packet packet, byte errData[]) 
    {
        super();
        this.errorCode = servErrCode;
        this.errPacket = packet;
        this.errorData = errData;
    }
    
    // ------------------------------------------------------------------------

    public Packet getPacket()
    {
        return this.errPacket;
    }

    public void setPacket(Packet pkt)
    {
        this.errPacket = pkt;
    }

    // ------------------------------------------------------------------------

    public int getErrorCode()
    {
        return this.errorCode;
    }
    
    public byte[] getErrorData() 
    {
        return this.errorData;
    }
    
    public Packet createServerErrorPacket()
    {
        int errCode    = this.getErrorCode();
        Packet cause   = this.getPacket();
        byte errData[] = this.getErrorData();
        Packet errPkt  = Packet.createServerErrorPacket(errCode, cause);
        if (errData != null) {
            // DO NOT RESET PAYLOAD INDEX!!!
            errPkt.getPayload(false).writeBytes(errData, errData.length);
        }
        return errPkt;
    }
    
    // ------------------------------------------------------------------------

    public void setTerminate()
    {
        this.terminate = true;
    }
    
    public boolean terminateSession()
    {
        return this.terminate;
    }
    
    // ------------------------------------------------------------------------

    public String toString() 
    {
        Packet errPkt = this.getPacket();
        int errCode = this.getErrorCode();
        StringBuffer sb = new StringBuffer();
        sb.append(ServerErrors.getErrorDescription(errCode));
        sb.append(" [ServerError=");
        sb.append(StringTools.toHexString(errCode,16));
        if (errPkt != null) {
            int hdrType = (errPkt.getPacketHeader() << 8) | errPkt.getPacketType();
            sb.append(", Packet=");
            sb.append(StringTools.toHexString(hdrType,16));
        }
        sb.append("] ");
        return sb.toString();
    }
    
    // ------------------------------------------------------------------------

}
