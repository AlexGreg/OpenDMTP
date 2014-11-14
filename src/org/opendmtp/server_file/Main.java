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
//  2006/04/23  Martin D. Flynn
//     -Integrated logging changes made to Print
// ----------------------------------------------------------------------------
package org.opendmtp.server_file;

import java.lang.*;
import java.util.*;
import java.io.*;

import org.opengts.util.*;

import org.opendmtp.server.base.*;

public class Main
{

    // ----------------------------------------------------------------------------

    public  static final String COPYRIGHT       = org.opendmtp.server.Version.getCopyright();

    // ----------------------------------------------------------------------------

    private static final String DMTP_NAME       = "OpenDMTP";
    private static final String DMTP_TYPE       = "Server(File)";
    public static String getVersion()
    {
        return DMTP_NAME + "_" + DMTP_TYPE + "." + org.opendmtp.server.Version.getVersion();
    }

    // ------------------------------------------------------------------------
    
    public static final String DMTP_PORT        = "dmtp.port";

    // ------------------------------------------------------------------------
    
    public  static final String ARG_START       = "start";
    public  static final String ARG_PORT        = "port";
    public  static final String ARG_STOREDIR    = "storedir";

    // ------------------------------------------------------------------------
    
    private static final int DEFAULT_DATA_PORT  = 31000;
    
    private static int _serverPort()
    {
        int port = RTConfig.getInt(ARG_PORT, -1);
        if (port <= 0) { 
            int p = RTConfig.getInt(Main.DMTP_PORT);
            port = (p > 0)? p : DEFAULT_DATA_PORT;
        }
        return port;
    }

    // ------------------------------------------------------------------------
    
    private static void usage()
    {
        Print.logInfo("");
        Print.logInfo("Usage:");
        Print.logInfo("  java ... " + Main.class.getName() + " {options}");
        Print.logInfo("Options:");
        Print.logInfo("  [-port]            Server port to listen for TCP/UDP connections [default="+_serverPort()+"]");
        Print.logInfo("  [-storedir=<dir>]  Filestore directory [default='./data']");
        Print.logInfo("  -start             Start server on specified port");
        Print.logInfo("");
        System.exit(1);
    }

    public static void main(String argv[])
    {
        
        /* runtime default properties */
        RTKey.addRuntimeEntry(new RTKey.Entry(DMTP_PORT, DEFAULT_DATA_PORT, "DMTP service port"));

        /* configure server for File data store */
        DBConfig.init(argv,false);

        /* header */
        Print.logInfo("OpenDMTP Java Server Reference Implementation.");
        Print.logInfo("Version: " + Main.getVersion());
        Print.logInfo(COPYRIGHT);

        /* start server */
        if (RTConfig.getBoolean(ARG_START,false)) {
            try {
                DMTPServer.createTrackSocketHandler(Main._serverPort());
            } catch (Throwable t) { // trap any server exception
                Print.logError("Error: " + t);
            }
            /* wait here forever while the server is running in a thread */
            while (true) { try { Thread.sleep(60L * 60L * 1000L); } catch (Throwable t) {} }
        }
        
        /* display usage */
        usage();
        
    }

}
