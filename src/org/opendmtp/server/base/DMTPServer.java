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
//     -DBFactory updated to specify a byte array for 'getDeviceDB'
//  2006/06/28  Martin D. Flynn
//     -Changed DBFactory.getDeviceDB(...) to require AccountDB instance
//  2006/09/29  Martin D. Flynn
//     -Added ability to externally set TCP/UDP timeouts
//  2007/02/18  Martin D. Flynn
//     -Added initial support for listening on more than one port
//  2007/05/01  David Cowan
//     -Added support for gracefully shutting down the server
//  2008/05/14  Martin D. Flynn
//     -Added support allowing/disabling first session custom event packet negotiation
// ----------------------------------------------------------------------------
package org.opendmtp.server.base;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;

import org.opengts.util.*;

import org.opendmtp.server.db.*;

public class DMTPServer
{
    
    // ------------------------------------------------------------------------

    //public static final int DEFAULT_PORT    = 31000;
    public static final int MAX_PORTS       = 4;
    
    // ------------------------------------------------------------------------
    // DMTPServer is a singleton
    
    private static DMTPServer trackTcpInstance = null;
    
    public static DMTPServer createTrackSocketHandler(int port)
        throws Throwable
    {
        return DMTPServer.createTrackSocketHandler(new int[] { port });
    }
        
    public static DMTPServer createTrackSocketHandler(int port[])
        throws Throwable
    {
        if (trackTcpInstance == null) {
            trackTcpInstance = new DMTPServer(port);
        }
        return trackTcpInstance;
    }

    // ------------------------------------------------------------------------

    private static boolean allowFirstSessionNegotiation = true;
    public static void setAllowFirstSessionNegotiation(boolean state)
    {
        DMTPServer.allowFirstSessionNegotiation = state;
    }
    public static boolean getAllowFirstSessionNegotiation()
    {
        return DMTPServer.allowFirstSessionNegotiation;
    }
    
    // ------------------------------------------------------------------------

    private static long tcpTimeout_idle     = 10000L;
    public static void setTcpIdleTimeout(long timeout)
    {
        DMTPServer.tcpTimeout_idle = timeout;
    }
    public static long getTcpIdleTimeout()
    {
        return DMTPServer.tcpTimeout_idle;
    }
    
    private static long tcpTimeout_packet   = 4000L;
    public static void setTcpPacketTimeout(long timeout)
    {
        DMTPServer.tcpTimeout_packet = timeout;
    }
    public static long getTcpPacketTimeout()
    {
        return DMTPServer.tcpTimeout_packet;
    }

    private static long tcpTimeout_session  = 15000L;
    public static void setTcpSessionTimeout(long timeout)
    {
        DMTPServer.tcpTimeout_session = timeout;
    }
    public static long getTcpSessionTimeout()
    {
        return DMTPServer.tcpTimeout_session;
    }

    // ------------------------------------------------------------------------

    private static long udpTimeout_idle     = 5000L;
    public static void setUdpIdleTimeout(long timeout)
    {
        DMTPServer.udpTimeout_idle = timeout;
    }
    public static long getUdpIdleTimeout()
    {
        return DMTPServer.udpTimeout_idle;
    }

    private static long udpTimeout_packet   = 4000L;
    public static void setUdpPacketTimeout(long timeout)
    {
        DMTPServer.udpTimeout_packet = timeout;
    }
    public static long getUdpPacketTimeout()
    {
        return DMTPServer.udpTimeout_packet;
    }

    private static long udpTimeout_session  = 60000L;
    public static void setUdpSessionTimeout(long timeout)
    {
        DMTPServer.udpTimeout_session = timeout;
    }
    public static long getUdpSessionTimeout()
    {
        return DMTPServer.udpTimeout_session;
    }

    // ------------------------------------------------------------------------

    public static void shutdown() 
    {
    	if (trackTcpInstance != null) {
    		try {
	    		for (int i = 0; i < trackTcpInstance.tcpThread.length; i++) {
	    			if (trackTcpInstance.tcpThread[i] != null) {
	    				trackTcpInstance.tcpThread[i].shutdown();
	    			}
	    		}
	    		for (int i = 0; i < trackTcpInstance.udpThread.length; i++) {
	    			if (trackTcpInstance.udpThread[i] != null) {
	    				trackTcpInstance.udpThread[i].shutdown();
	    			}
	    		}
    		} catch (Throwable e) {
    			Print.logException("Error shutting down server", e);
    		}
	    	trackTcpInstance = null;
    	}
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    private static DMTPServer.DBFactory dbFactory = null;

    public interface DBFactory
    {
        AccountDB getAccountDB(String acctName);
        DeviceDB  getDeviceDB(AccountDB acctDB, String devName);
        DeviceDB  getDeviceDB(byte uniqId[]);
    }
    
    public static void setDBFactory(DMTPServer.DBFactory factory)
    {
        DMTPServer.dbFactory = factory;
    }
    
    public static DMTPServer.DBFactory getDBFactory()
    {
        return DMTPServer.dbFactory;
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    private ServerSocketThread tcpThread[] = new ServerSocketThread[MAX_PORTS];
    private int                tcpCount    = 0;

    private ServerSocketThread udpThread[] = new ServerSocketThread[MAX_PORTS];
    private int                udpCount    = 0;

    // ------------------------------------------------------------------------

    private DMTPServer(int port[])
        throws Throwable
    {
        if ((port != null) && (port.length > 0)) {
            for (int i = 0; i < port.length; i++) {
                this.startPortListeners(port[i]);
            }
        } else {
            throw new Exception("No ports specified");
        }
    }

    // ------------------------------------------------------------------------

    private void startPortListeners(int port)
        throws Throwable
    {
        if ((port > 0) && (port <= 65535)) {
            this.startTCP(port);
            this.startUDP(port);
        } else {
            throw new Exception("Invalid port number: " + port);
        }
    }

    private void startTCP(int port)
        throws Throwable
    {
        ServerSocketThread sst = null;
        
        /* too many ports? */
        if (this.tcpCount >= this.tcpThread.length) {
            throw new Exception("Too many TCP connection listeners");
        }
        
        /* create server socket */
        try {
            sst = new ServerSocketThread(port);
        } catch (Throwable t) { // trap any server exception
            Print.logException("ServerSocket error", t);
            throw t;
        }
        
        /* initialize */
        sst.setTextPackets(false);
        sst.setBackspaceChar(null); // no backspaces allowed
        sst.setLineTerminatorChar(new int[] { '\r' });
        sst.setMaximumPacketLength(600);
        sst.setMinimumPacketLength(Packet.MIN_HEADER_LENGTH);
        sst.setIdleTimeout(DMTPServer.tcpTimeout_idle);         // time between packets
        sst.setPacketTimeout(DMTPServer.tcpTimeout_packet);     // time from start of packet to packet completion
        sst.setSessionTimeout(DMTPServer.tcpTimeout_session);   // time for entire session
        sst.setLingerTimeoutSec(5);
        sst.setTerminateOnTimeout(true);
        sst.setClientPacketHandlerClass(DMTPClientPacketHandler.class);

        /* start thread */
        Print.logInfo("DMTP: Starting TCP listener thread on port " + port + " [timeout=" + sst.getSessionTimeout() + "ms] ...");
        sst.start();
        this.tcpThread[this.tcpCount++] = sst;

    }

    private void startUDP(int port)
        throws Throwable
    {
        ServerSocketThread sst = null;
        
        /* too many ports? */
        if (this.udpCount >= this.udpThread.length) {
            throw new Exception("Too many UDP connection listeners");
        }

        /* create server socket */
        try {
            sst = new ServerSocketThread(new DatagramSocket(port));
        } catch (Throwable t) { // trap any server exception
            Print.logException("ServerSocket error", t);
            throw t;
        }
        
        /* initialize */
        sst.setTextPackets(false);
        sst.setBackspaceChar(null); // no backspaces allowed
        sst.setLineTerminatorChar(new int[] { '\r' });
        sst.setMaximumPacketLength(600);
        sst.setMinimumPacketLength(Packet.MIN_HEADER_LENGTH);
        sst.setIdleTimeout(DMTPServer.udpTimeout_idle);
        sst.setPacketTimeout(DMTPServer.udpTimeout_packet);
        sst.setSessionTimeout(DMTPServer.udpTimeout_session);
        sst.setTerminateOnTimeout(true);
        
        /* session timeout */
        // This should be AccountID dependent
        sst.setClientPacketHandlerClass(DMTPClientPacketHandler.class);

        /* start thread */
        Print.logInfo("DMTP: Starting UDP listener thread on port " + port + " [timeout=" + sst.getSessionTimeout() + "ms] ...");
        sst.start();
        this.udpThread[this.udpCount++] = sst;

    }

    // ------------------------------------------------------------------------

}
