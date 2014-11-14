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
//  2006/04/23  Martin D. Flynn
//      Integrated logging changes made to Print
//  2006/06/28  Martin D. Flynn
//      Changed DBFactory.getDeviceDB to requrie AccountDB instance
// ----------------------------------------------------------------------------
package org.opendmtp.server_file;

import java.lang.*;
import java.util.*;
import java.math.*;
import java.io.*;
import java.sql.*;

import org.opengts.util.*;

import org.opendmtp.server.base.*;
import org.opendmtp.server.db.*;

public class DBConfig
{

    // ------------------------------------------------------------------------

    private static class DMTP_DBFactory
        implements DMTPServer.DBFactory
    {
        public AccountDB getAccountDB(String acctName) {
            return new AccountDBImpl(acctName);
        }
        public DeviceDB getDeviceDB(byte uniqId[]) {
            return null;
        }
        public DeviceDB getDeviceDB(AccountDB acctDB, String devName) {
            return new DeviceDBImpl(acctDB, devName);
        }
    }

    // ------------------------------------------------------------------------
    
    public static void init(String argv[], boolean interactive)
    {

        /* command line options */
        RTConfig.setCommandLineArgs(argv);
        if (interactive) {
            RTConfig.setFile(RTKey.LOG_FILE,null);      // no log file
            Print.setLogHeaderLevel(Print.LOG_WARN);    // include log header on WARN/ERROR/FATAL
            RTConfig.setBoolean(RTKey.LOG_INCL_DATE, false);        // exclude date
            RTConfig.setBoolean(RTKey.LOG_INCL_STACKFRAME, true);   // include stackframe
        } else {
            //RTConfig.setBoolean(RTKey.LOG_INCL_DATE, true);
            //RTConfig.setBoolean(RTKey.LOG_INCL_STACKFRAME, false);
        }

        /* set the data-store directory for the received events */
        File storeDir = RTConfig.getFile(Main.ARG_STOREDIR, null);
        DeviceDBImpl.setDataStoreDirectory(storeDir);
        Print.logInfo("Account/Device Events will be stored in directory '" + 
            DeviceDBImpl.getDataStoreDirectory() + "'");
        
        /* register OpenDMTP protocol DB interface */
        DMTPServer.setDBFactory(new DBConfig.DMTP_DBFactory());
        
    }
    
    // ------------------------------------------------------------------------
    
}
