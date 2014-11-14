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
package org.opendmtp.codes;

public class Encoding
{
    
    // ------------------------------------------------------------------------
    
    public static final char    AsciiEncodingChar           = '$';
    public static final char    AsciiChecksumChar           = '*';
    public static final char    AsciiEndOfLineChar          = '\r';

    // ------------------------------------------------------------------------
    // OpenDMTP Protocol Definition v0.1.0 Conformance:
    // These encoding value constants are defined by the OpenDMTP protocol specification
    // and must remain as specified here.

    public static final int     SUPPORTED_ENCODING_BINARY   = 0x01;
    public static final int     SUPPORTED_ENCODING_BASE64   = 0x02;
    public static final int     SUPPORTED_ENCODING_HEX      = 0x04;
    public static final int     SUPPORTED_ENCODING_CSV      = 0x08;

    // ------------------------------------------------------------------------
    // Packet encoding

    public static final int     ENCODING_UNKNOWN            = -1;     // unknown ASCII encoding
    public static final int     ENCODING_BINARY             =  0;     // server must support
    public static final int     ENCODING_BASE64             = 10;     // server must support
    public static final int     ENCODING_BASE64_CKSUM       = 11;     // server must support
    public static final int     ENCODING_HEX                = 20;     // server must support
    public static final int     ENCODING_HEX_CKSUM          = 21;     // server must support
    public static final int     ENCODING_CSV                = 30;     // server need not support
    public static final int     ENCODING_CSV_CKSUM          = 31;     // server need not support
    public static boolean IsEncodingAscii(int encoding) { 
        return (encoding > 0);
    }
    public static boolean IsEncodingChecksum(int encoding) { 
        return (encoding > 0) && ((encoding % 10) != 0);
    }

    // ------------------------------------------------------------------------

    public static final char    ENCODING_BASE64_CHAR        = '='; 
    public static final char    ENCODING_HEX_CHAR           = ':';
    public static final char    ENCODING_CSV_CHAR           = ',';

    // ------------------------------------------------------------------------

}
