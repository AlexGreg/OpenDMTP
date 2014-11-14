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
//  2008/01/10  Martin D. Flynn
//     -Initial release
//  2008/04/04  Martin D. Flynn
//     -Moved description methods to OpenGTS 
// ----------------------------------------------------------------------------
package org.opendmtp.codes;

import org.opengts.util.*;

public interface ClientDiagnostics
{

// ----------------------------------------------------------------------------
// File upload acknowledgement:

    public static final int DIAG_UPLOAD_ACK                     = 0xF001;
    // Description:
    //      Acknowledge upload record
    // Payload:
    //      0:2 - This diagnostic code
    //      2:1 - The upload record type (see "upload.h" for valid values)

// ----------------------------------------------------------------------------
// OBC/J1708 queried data:

    public static final int DIAG_OBC_J1708_VALUE                = 0xFC11;
    // Description:
    //      Returned queried J1708 value
    // Payload:
    //      0:2 - This diagnostic code
    //      2:2 - MID(128)
    //      4:2 - PID
    //      6:X - J1708 data

// ----------------------------------------------------------------------------
// Internal diagnostics/errors (data provides specifics):
    
    public static final int DIAG_INTERNAL_DIAG_E000             = 0xE000;
// ...
    public static final int DIAG_INTERNAL_DIAG_FFFF             = 0xFFFF;
    // Description:
    //      Internal diagnostics, as defined by client device
    // Payload:
    //      0:2 - This diagnostic code
    //      2:X - payload format is defined by the client. [optional]
    // Notes:
    //      These diagnostic codes are for use by the client to allow general
    //      diagnostic information to be sent to the server for analysis.

// ----------------------------------------------------------------------------

}
