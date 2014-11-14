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

import org.opengts.util.*;

public class CommandErrors
{
    
// ----------------------------------------------------------------------------
// Command success

    public static final int COMMAND_OK                          = 0x0000;
    // Description:
    //      Command execution was successful (no error return to server)

    public static final int COMMAND_OK_ACK                      = 0x0001;
    // Description:
    //      Command execution was successful (Acknowledgement returned to server)

// ----------------------------------------------------------------------------
// Command argument errors
    
    public static final int COMMAND_ARGUMENTS                   = 0xF011;
    // Description:
    //      Insufficient/Invalid command arguments

    public static final int COMMAND_INDEX                       = 0xF012;
    // Description:
    //      An index specified in the command arguments is invalid

    public static final int COMMAND_STATUS                      = 0xF013;
    // Description:
    //      A status code specified in the command arguments is invalid

    public static final int COMMAND_LENGTH                      = 0xF014;
    // Description:
    //      A length specified in the command arguments is invalid

    public static final int COMMAND_NAME                        = 0xF015;
    // Description:
    //      An ID/Filename specified in the command arguments is invalid

    public static final int COMMAND_CHECKSUM                    = 0xF016;
    // Description:
    //      A checksum specified in the command arguments is invalid

    public static final int COMMAND_OFFSET                      = 0xF017;
    // Description:
    //      An offset specified in the command arguments is invalid
    
// ----------------------------------------------------------------------------
// Command execution errors
    
    public static final int COMMAND_EXECUTION                   = 0xF511;
    // Description:
    //      The client has determined that the execution of the command has failed
    //      (no specific reason)

    public static final int COMMAND_HARDWARE_FAILURE            = 0xF521;
    // Description:
    //      The client has determined that the execution of the command has failed
    //      due to hardware failure.
    
// ----------------------------------------------------------------------------
// Generic Command errors
// Create desired aliases for these to define specific custom errors

    public static final int COMMAND_ERROR_00                    = 0xFE00;
    public static final int COMMAND_ERROR_01                    = 0xFE01;
    public static final int COMMAND_ERROR_02                    = 0xFE02;
    public static final int COMMAND_ERROR_03                    = 0xFE03;
    public static final int COMMAND_ERROR_04                    = 0xFE04;
    public static final int COMMAND_ERROR_05                    = 0xFE05;
    public static final int COMMAND_ERROR_06                    = 0xFE06;
    public static final int COMMAND_ERROR_07                    = 0xFE07;

// ----------------------------------------------------------------------------
// Command execution errors

    public static final int COMMAND_FEATURE_NOT_SUPPORTED       = 0xFF01;
    // Description:
    //      A requested command feature is not supported.

// ----------------------------------------------------------------------------

    public static String getErrorDescription(int errCode)
    {
        switch (errCode) {
            case COMMAND_OK:                    return "Successful";
            case COMMAND_ARGUMENTS:             return "Invalid argument";
            case COMMAND_INDEX:                 return "Invalid index";
            case COMMAND_STATUS:                return "Invalid status code";
            case COMMAND_LENGTH:                return "Invalid length";
            case COMMAND_NAME:                  return "Invalid name";
            case COMMAND_CHECKSUM:              return "Invalid checksum";
            case COMMAND_OFFSET:                return "Invalid offset";
            case COMMAND_FEATURE_NOT_SUPPORTED: return "Feature not supported";
            case COMMAND_EXECUTION:             return "Execution error";
            case COMMAND_HARDWARE_FAILURE:      return "Hardware failure";
            default:                            return "Unknown [" + StringTools.toHexString(errCode,16) + "]";
        }
    }

}
