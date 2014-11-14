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
//  2006/06/05  v1.2.2 [Initial release]
//  2007/02/08  v1.2.3
//  2007/02/18  v1.2.4
//  2007/02/25  v1.2.5 ['VERSION' and 'COPYRIGHT' made private]
//  2007/02/25  v1.2.6
//  2007/03/11  v1.2.7
//  2007/03/16  v1.2.8
//  2007/07/13  v1.2.9
//  2007/09/16  v1.2.10
//  2007/12/04  v1.2.11
//  2008/01/10  v1.2.12
//  2008/02/04  v1.2.13
//  2008/04/04  v1.2.14
//  2008/05/14  v1.3.0
//  2008/08/15  v1.3.1
//  2010/04/27  v1.3.2
// ----------------------------------------------------------------------------
package org.opendmtp.server;

public class Version
{

    // ------------------------------------------------------------------------

    private static final String COPYRIGHT = "Copyright 2006-2010, GeoTelematic Solutions, Inc.";

    // ------------------------------------------------------------------------

    // This string may be parsed via 'grep' & 'sed' scripts and thus ONLY the version
    // value specified within the quotes should change.
    private static final String VERSION = "1.3.2";

    // ------------------------------------------------------------------------

    public static String getCopyright()
    {
        return COPYRIGHT;
    }

    public static String getVersion()
    {
        String ver = VERSION;
        return ver;
    }
    
    public static void main(String argv[])
    {
        System.out.println(getVersion());
    }

    // ------------------------------------------------------------------------

}
