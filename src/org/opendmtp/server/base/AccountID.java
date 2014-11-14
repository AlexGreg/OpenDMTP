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
//  2006/06/28  Martin D. Flynn
//      Added method to return AccountDB instance
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

public class AccountID
{

    // ------------------------------------------------------------------------
    // - AccountID
    //      - AccountID name [key]
    //      - Owner name
    //      - Contact email
    //      - Event notification email
    //      - Account level of service

    /* identification */
    private AccountDB db = null;
 
    // ------------------------------------------------------------------------

    private static AccountDB GetAccountDB(String acctName)
        throws PacketParseException
    {
        DMTPServer.DBFactory fact = DMTPServer.getDBFactory();
        if (fact != null) {
            AccountDB db = fact.getAccountDB(acctName);
            if (db != null) {
                return db;
            }
        } else {
            Print.logError("No factory registered for AccountID");
        }
        return null;
    }

    // ------------------------------------------------------------------------

    public static AccountID loadAccountID(String acctName)
        throws PacketParseException
    {
        return new AccountID(acctName);
    }

    // ------------------------------------------------------------------------

    private AccountID(String acctName)
        throws PacketParseException
    {
        
        /* null? */
        if ((acctName == null) || acctName.equals("")) {
            Print.logError("Account name is null/empty");
            throw new PacketParseException(ServerErrors.NAK_ACCOUNT_INVALID, null); // errData ok
        }
        
        /* device exists? */
        this.db = GetAccountDB(acctName);
        if (this.db == null) {
            Print.logError("AccountID not found: " + acctName);
            throw new PacketParseException(ServerErrors.NAK_ACCOUNT_INVALID, null); // errData ok
        }
        
    }

    // ------------------------------------------------------------------------

    public AccountDB getAccountDB()
    {
        return this.db;
    }

    // ------------------------------------------------------------------------

    public String getAccountName()
    {
        return this.db.getAccountName();
    }
    
    // ------------------------------------------------------------------------

    public boolean isActive()
    {
        return this.db.isActive();
    }
    
    // ------------------------------------------------------------------------

    public String toString()
    {
        return (this.db != null)? this.db.toString() : "";
    }

    // ------------------------------------------------------------------------
    
}
