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
//  2006/05/26  Martin D. Flynn
//      'getId()' now returns a byte array
// ----------------------------------------------------------------------------
package org.opendmtp.server.base;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;

import org.opengts.util.*;

public class UniqueID
{
    
    // ------------------------------------------------------------------------

    private byte id[] = new byte[0];
    
    public UniqueID(byte id[]) 
    {
        this.id = (id != null)? id : new byte[0];
    }
    
    // ------------------------------------------------------------------------
        
    public byte[] getId()
    {
        return this.id;
    }
    
    public int getLength()
    {
        return this.getId().length;
    }

    public boolean isValid()
    {
        return (this.getLength() > 0);
    }

    // ------------------------------------------------------------------------

    public boolean equals(Object other)
    {
        if (other instanceof UniqueID) {
            byte id1[] = ((UniqueID)other).getId();
            byte id2[] = this.getId();
            if (id1.length != id2.length) {
                return false;
            } else {
                for (int i = 0; i < id1.length; i++) {
                    if (id1[i] != id2[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    // ------------------------------------------------------------------------

    public String toString() 
    {
        if (this.isValid()) {
            return "0x" + StringTools.toHexString(this.getId());
        } else {
            return "";
        }
    }
    
    // ------------------------------------------------------------------------

}
