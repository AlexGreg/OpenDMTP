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
//  2007/02/25  Martin D. Flynn
//     -Initial release
//  2008/02/04  Martin D. Flynn
//     -Added description to property attributes
//  2008/05/12  Martin D. Flynn
//     -Updated to Java 5.
// ----------------------------------------------------------------------------
package org.opendmtp.codes;

import java.util.HashMap;

import org.opengts.util.*;

public class PropCodes
{

    // ------------------------------------------------------------------------
    // Geozone admin property commands (see PROP_CMD_GEOF_ADMIN[F542])

    public static final int GEOF_CMD_ADD_STD_2              = 0x10;
    public static final int GEOF_CMD_ADD_HIGH_2             = 0x11;
    public static final int GEOF_CMD_ADD_STD_N              = 0x1E;
    public static final int GEOF_CMD_ADD_HIGH_N             = 0x1F;
    public static final int GEOF_CMD_REMOVE                 = 0x20;
    public static final int GEOF_CMD_SAVE                   = 0x30;

    // ------------------------------------------------------------------------
    // Property attribute types

    public static final int TYPE_TYPE_MASK                  = 0xF000;
    public static final int TYPE_SIZE_MASK                  = 0x000F;

    public static final int TYPE_COMMAND                    = 0x1000;
    public static final int TYPE_STRING                     = 0x2000;
    public static final int TYPE_BINARY                     = 0x3000;
    public static final int TYPE_GPS                        = 0x4000;
    public static final int TYPE_BOOLEAN                    = 0x5000;
    public static final int TYPE_NUMERIC                    = 0x6000;
    
    public static final int TYPE_DEC                        = 0x0100;
    public static final int TYPE_SIGNED                     = 0x0800;

    public static final int TYPE_UINT8                      = TYPE_NUMERIC | 0x0001;
    public static final int TYPE_UINT16                     = TYPE_NUMERIC | 0x0002;
    public static final int TYPE_INT16                      = TYPE_NUMERIC | 0x0002 | TYPE_SIGNED;
    public static final int TYPE_UINT32                     = TYPE_NUMERIC | 0x0004;
    public static final int TYPE_UDEC16                     = TYPE_NUMERIC | 0x0002 | TYPE_DEC;
    public static final int TYPE_DEC16                      = TYPE_NUMERIC | 0x0002 | TYPE_DEC | TYPE_SIGNED;

    // ------------------------------------------------------------------------
    // OpenDMTP properties

    // --- Transport media port config
    public static final int PROP_CFG_XPORT_PORT             = 0xEF11;
    public static final int PROP_CFG_XPORT_BPS              = 0xEF12;
    public static final int PROP_CFG_XPORT_DEBUG            = 0xEF1D;
   
    // --- GPS port config
    public static final int PROP_CFG_GPS_PORT               = 0xEF21;
    public static final int PROP_CFG_GPS_BPS                = 0xEF22;
    public static final int PROP_CFG_GPS_MODEL              = 0xEF2A;  // was 0xEF22
    public static final int PROP_CFG_GPS_DEBUG              = 0xEF2D;
    
    // --- General serial port 0 config
    public static final int PROP_CFG_SERIAL0_PORT           = 0xEF31;
    public static final int PROP_CFG_SERIAL0_BPS            = 0xEF32;
    public static final int PROP_CFG_SERIAL0_DEBUG          = 0xEF3D;
    
    // --- General serial port 1 config
    public static final int PROP_CFG_SERIAL1_PORT           = 0xEF41;
    public static final int PROP_CFG_SERIAL1_BPS            = 0xEF42;
    public static final int PROP_CFG_SERIAL1_DEBUG          = 0xEF4D;
    
    // --- General serial port 2 config
    public static final int PROP_CFG_SERIAL2_PORT           = 0xEF51;
    public static final int PROP_CFG_SERIAL2_BPS            = 0xEF52;
    public static final int PROP_CFG_SERIAL2_DEBUG          = 0xEF5D;
    
    // --- General serial port 3 config
    public static final int PROP_CFG_SERIAL3_PORT           = 0xEF61;
    public static final int PROP_CFG_SERIAL3_BPS            = 0xEF62;
    public static final int PROP_CFG_SERIAL3_DEBUG          = 0xEF6D;

    // --- Command properties
    public static final int PROP_CMD_SAVE_PROPS             = 0xF000;
    public static final int PROP_CMD_AUTHORIZE              = 0xF002;
    public static final int PROP_CMD_STATUS_EVENT           = 0xF011;
    public static final int PROP_CMD_SET_OUTPUT             = 0xF031;
    public static final int PROP_CMD_RESET                  = 0xF0FF;

    // --- State properties
    public static final int PROP_STATE_PROTOCOL             = 0xF100;
    public static final int PROP_STATE_FIRMWARE             = 0xF101;
    public static final int PROP_STATE_COPYRIGHT            = 0xF107;
    public static final int PROP_STATE_SERIAL               = 0xF110;
    public static final int PROP_STATE_UNIQUE_ID            = 0xF112;
    public static final int PROP_STATE_ACCOUNT_ID           = 0xF114;
    public static final int PROP_STATE_DEVICE_ID            = 0xF115;
    public static final int PROP_STATE_USER_ID              = 0xF117;
    public static final int PROP_STATE_USER_TIME            = 0xF118;
    public static final int PROP_STATE_TIME                 = 0xF121;
    public static final int PROP_STATE_GPS                  = 0xF123;
    public static final int PROP_STATE_GPS_DIAGNOSTIC       = 0xF124;
    public static final int PROP_STATE_QUEUED_EVENTS        = 0xF131;
    public static final int PROP_STATE_DEV_DIAGNOSTIC       = 0xF141;

    // --- Communication properties
    public static final int PROP_COMM_SPEAK_FIRST           = 0xF303;
    public static final int PROP_COMM_FIRST_BRIEF           = 0xF305;
    public static final int PROP_COMM_FAILURE_DELAY         = 0xF309;
    public static final int PROP_COMM_MAX_CONNECTIONS       = 0xF311;
    public static final int PROP_COMM_MIN_XMIT_DELAY        = 0xF312;
    public static final int PROP_COMM_MIN_XMIT_RATE         = 0xF313;
    public static final int PROP_COMM_MAX_XMIT_RATE         = 0xF315;
    public static final int PROP_COMM_MAX_DUP_EVENTS        = 0xF317;
    public static final int PROP_COMM_MAX_SIM_EVENTS        = 0xF318;

    // --- Communication connection properties:
    public static final int PROP_COMM_SETTINGS              = 0xF3A0;
    public static final int PROP_COMM_DMTP_HOST             = 0xF3A1;
    public static final int PROP_COMM_DMTP_PORT             = 0xF3A2;
    public static final int PROP_COMM_DNS_1                 = 0xF3A3;
    public static final int PROP_COMM_DNS_2                 = 0xF3A4;
    public static final int PROP_COMM_CONNECTION            = 0xF3A5;
    public static final int PROP_COMM_APN_NAME              = 0xF3A6;
    public static final int PROP_COMM_APN_SERVER            = 0xF3A7;
    public static final int PROP_COMM_APN_USER              = 0xF3A8;
    public static final int PROP_COMM_APN_PASSWORD          = 0xF3A9;
    public static final int PROP_COMM_APN_PHONE             = 0xF3AA;
    public static final int PROP_COMM_APN_SETTINGS          = 0xF3AC;
    public static final int PROP_COMM_MIN_SIGNAL            = 0xF3AD;
    public static final int PROP_COMM_ACCESS_PIN            = 0xF3AF;

    // --- Packet/Data format properties:
    public static final int PROP_COMM_CUSTOM_FORMATS        = 0xF3C0;
    public static final int PROP_COMM_ENCODINGS             = 0xF3C1;
    public static final int PROP_COMM_BYTES_READ            = 0xF3F1;
    public static final int PROP_COMM_BYTES_WRITTEN         = 0xF3F2;
    
    // --- GPS config properties:
    public static final int PROP_GPS_SAMPLE_RATE            = 0xF511;
    public static final int PROP_GPS_ACQUIRE_WAIT           = 0xF512;
    public static final int PROP_GPS_EXPIRATION             = 0xF513;
    public static final int PROP_GPS_CLOCK_DELTA            = 0xF515;
    public static final int PROP_GPS_ACCURACY               = 0xF521;
    public static final int PROP_GPS_MIN_SPEED              = 0xF522;
    public static final int PROP_GPS_DISTANCE_DELTA         = 0xF531;

    // --- Geofence properties:
    public static final int PROP_CMD_GEOF_ADMIN             = 0xF542;
    public static final int PROP_GEOF_COUNT                 = 0xF547;
    public static final int PROP_GEOF_VERSION               = 0xF548;
    public static final int PROP_GEOF_ARRIVE_DELAY          = 0xF54A;
    public static final int PROP_GEOF_DEPART_DELAY          = 0xF54D;
    public static final int PROP_GEOF_CURRENT               = 0xF551;

    // --- GeoCorr properties:
    public static final int PROP_CMD_GEOC_ADMIN             = 0xF562;
    public static final int PROP_GEOC_ACTIVE_ID             = 0xF567;
    public static final int PROP_GEOC_VIOLATION_INTRVL      = 0xF56A;
    public static final int PROP_GEOC_VIOLATION_COUNT       = 0xF56D;

    // --- Motion properties:
    public static final int PROP_MOTION_START_TYPE          = 0xF711;
    public static final int PROP_MOTION_START               = 0xF712;
    public static final int PROP_MOTION_IN_MOTION           = 0xF713;
    public static final int PROP_MOTION_STOP                = 0xF714;
    public static final int PROP_MOTION_STOP_TYPE           = 0xF715;
    public static final int PROP_MOTION_DORMANT_INTRVL      = 0xF716;
    public static final int PROP_MOTION_DORMANT_COUNT       = 0xF717;
    public static final int PROP_MOTION_EXCESS_SPEED        = 0xF721;  // Excess speed (0.1 kph)
    public static final int PROP_MOTION_MOVING_INTRVL       = 0xF725;
    
    // --- Odometer properties:
    public static final int PROP_ODOMETER_0_VALUE           = 0xF770;
    public static final int PROP_ODOMETER_1_VALUE           = 0xF771;
    public static final int PROP_ODOMETER_2_VALUE           = 0xF772;
    public static final int PROP_ODOMETER_3_VALUE           = 0xF773;
    public static final int PROP_ODOMETER_4_VALUE           = 0xF774;
    public static final int PROP_ODOMETER_5_VALUE           = 0xF775;
    public static final int PROP_ODOMETER_6_VALUE           = 0xF776;
    public static final int PROP_ODOMETER_7_VALUE           = 0xF777;
    public static final int PROP_ODOMETER_0_LIMIT           = 0xF780;
    public static final int PROP_ODOMETER_1_LIMIT           = 0xF781;
    public static final int PROP_ODOMETER_2_LIMIT           = 0xF782;
    public static final int PROP_ODOMETER_3_LIMIT           = 0xF783;
    public static final int PROP_ODOMETER_4_LIMIT           = 0xF784;
    public static final int PROP_ODOMETER_5_LIMIT           = 0xF785;
    public static final int PROP_ODOMETER_6_LIMIT           = 0xF786;
    public static final int PROP_ODOMETER_7_LIMIT           = 0xF787;
    public static final int PROP_ODOMETER_0_GPS             = 0xF790;
    public static final int PROP_ODOMETER_1_GPS             = 0xF791;
    public static final int PROP_ODOMETER_2_GPS             = 0xF792;
    public static final int PROP_ODOMETER_3_GPS             = 0xF793;
    public static final int PROP_ODOMETER_4_GPS             = 0xF794;
    public static final int PROP_ODOMETER_5_GPS             = 0xF795;
    public static final int PROP_ODOMETER_6_GPS             = 0xF796;
    public static final int PROP_ODOMETER_7_GPS             = 0xF797;
    
    // --- Digital input properties:
    public static final int PROP_INPUT_STATE                = 0xF901;
    public static final int PROP_INPUT_CONFIG_0             = 0xF910;
    public static final int PROP_INPUT_CONFIG_1             = 0xF911;
    public static final int PROP_INPUT_CONFIG_2             = 0xF912;
    public static final int PROP_INPUT_CONFIG_3             = 0xF913;
    public static final int PROP_INPUT_CONFIG_4             = 0xF914;
    public static final int PROP_INPUT_CONFIG_5             = 0xF915;
    public static final int PROP_INPUT_CONFIG_6             = 0xF916;
    public static final int PROP_INPUT_CONFIG_7             = 0xF917;
    public static final int PROP_INPUT_CONFIG_8             = 0xF918;
    public static final int PROP_INPUT_CONFIG_9             = 0xF919;
    public static final int PROP_INPUT_CONFIG_A             = 0xF91A;
    public static final int PROP_INPUT_CONFIG_B             = 0xF91B;
    public static final int PROP_INPUT_CONFIG_C             = 0xF91C;
    public static final int PROP_INPUT_CONFIG_D             = 0xF91D;
    public static final int PROP_INPUT_CONFIG_E             = 0xF91E;
    public static final int PROP_INPUT_CONFIG_F             = 0xF91F;
    
    // --- Digital output properties:
    public static final int PROP_OUTPUT_CONFIG_0            = 0xF930;
    public static final int PROP_OUTPUT_CONFIG_1            = 0xF931;
    public static final int PROP_OUTPUT_CONFIG_2            = 0xF932;
    public static final int PROP_OUTPUT_CONFIG_3            = 0xF933;
    public static final int PROP_OUTPUT_CONFIG_4            = 0xF934;
    public static final int PROP_OUTPUT_CONFIG_5            = 0xF935;
    public static final int PROP_OUTPUT_CONFIG_6            = 0xF936;
    public static final int PROP_OUTPUT_CONFIG_7            = 0xF937;
    
    // --- Elapsed time properties:
    public static final int PROP_ELAPSED_0_VALUE            = 0xF970;
    public static final int PROP_ELAPSED_1_VALUE            = 0xF971;
    public static final int PROP_ELAPSED_2_VALUE            = 0xF972;
    public static final int PROP_ELAPSED_3_VALUE            = 0xF973;
    public static final int PROP_ELAPSED_4_VALUE            = 0xF974;
    public static final int PROP_ELAPSED_5_VALUE            = 0xF975;
    public static final int PROP_ELAPSED_6_VALUE            = 0xF976;
    public static final int PROP_ELAPSED_7_VALUE            = 0xF977;
    public static final int PROP_ELAPSED_0_LIMIT            = 0xF980;
    public static final int PROP_ELAPSED_1_LIMIT            = 0xF981;
    public static final int PROP_ELAPSED_2_LIMIT            = 0xF982;
    public static final int PROP_ELAPSED_3_LIMIT            = 0xF983;
    public static final int PROP_ELAPSED_4_LIMIT            = 0xF984;
    public static final int PROP_ELAPSED_5_LIMIT            = 0xF985;
    public static final int PROP_ELAPSED_6_LIMIT            = 0xF986;
    public static final int PROP_ELAPSED_7_LIMIT            = 0xF987;
    
    // --- Sensor configuration properties:
    public static final int PROP_UNDERVOLTAGE_LIMIT         = 0xFB01;
    public static final int PROP_SENSOR_CONFIG_0            = 0xFB10;
    public static final int PROP_SENSOR_CONFIG_1            = 0xFB11;
    public static final int PROP_SENSOR_CONFIG_2            = 0xFB12;
    public static final int PROP_SENSOR_CONFIG_3            = 0xFB13;
    public static final int PROP_SENSOR_CONFIG_4            = 0xFB14;
    public static final int PROP_SENSOR_CONFIG_5            = 0xFB15;
    public static final int PROP_SENSOR_CONFIG_6            = 0xFB16;
    public static final int PROP_SENSOR_CONFIG_7            = 0xFB17;
    public static final int PROP_SENSOR_RANGE_0             = 0xFB20;
    public static final int PROP_SENSOR_RANGE_1             = 0xFB21;
    public static final int PROP_SENSOR_RANGE_2             = 0xFB22;
    public static final int PROP_SENSOR_RANGE_3             = 0xFB23;
    public static final int PROP_SENSOR_RANGE_4             = 0xFB24;
    public static final int PROP_SENSOR_RANGE_5             = 0xFB25;
    public static final int PROP_SENSOR_RANGE_6             = 0xFB26;
    public static final int PROP_SENSOR_RANGE_7             = 0xFB27;
    
    // --- Temperature configuration:
    public static final int PROP_TEMP_SAMPLE_INTRVL         = 0xFB60;
    public static final int PROP_TEMP_REPORT_INTRVL         = 0xFB63;
    public static final int PROP_TEMP_CONFIG_0              = 0xFB70;
    public static final int PROP_TEMP_CONFIG_1              = 0xFB71;
    public static final int PROP_TEMP_CONFIG_2              = 0xFB72;
    public static final int PROP_TEMP_CONFIG_3              = 0xFB73;
    public static final int PROP_TEMP_RANGE_0               = 0xFB80;
    public static final int PROP_TEMP_RANGE_1               = 0xFB81;
    public static final int PROP_TEMP_RANGE_2               = 0xFB82;
    public static final int PROP_TEMP_RANGE_3               = 0xFB83;

    // --- Accelerometer configuration:
    public static final int PROP_MAX_BRAKE_G_FORCE          = 0xFBA0;

    // --- OBC properties (EXPERIMENTAL - not yet part of the protocol)
    public static final int PROP_OBC_J1708_CONFIG           = 0xFC01;
    public static final int PROP_OBC_ODOM_OFFSET            = 0xFC02;
    public static final int PROP_OBC_J1708_VALUE            = 0xFC11;

    // ------------------------------------------------------------------------
    // Property attribute table

    private static Attr propAttr[] = {
        //       Key                          Type          Array  Description               
        
        // --- Transport media port config
        new Attr(PROP_CFG_XPORT_PORT        , TYPE_STRING   ,  1, "Transport ComPort"               ),
        new Attr(PROP_CFG_XPORT_BPS         , TYPE_UINT32   ,  1, "Transport BPS"                   ),
        new Attr(PROP_CFG_XPORT_DEBUG       , TYPE_BOOLEAN  ,  1, "Transport Debug"                 ),
       
        // --- GPS port config
        new Attr(PROP_CFG_GPS_PORT          , TYPE_STRING   ,  1, "GPS Receiver ComPort"            ),
        new Attr(PROP_CFG_GPS_BPS           , TYPE_UINT32   ,  1, "GPS Receiver BPS"                ),
        new Attr(PROP_CFG_GPS_MODEL         , TYPE_STRING   ,  1, "GPS Receiver Model"              ),
        new Attr(PROP_CFG_GPS_DEBUG         , TYPE_BOOLEAN  ,  1, "GPS Receiver Debug"              ),
        
        // --- General serial port 0 config
        new Attr(PROP_CFG_SERIAL0_PORT      , TYPE_STRING   ,  1, "Serial-0 ComPort"                ),
        new Attr(PROP_CFG_SERIAL0_BPS       , TYPE_UINT32   ,  1, "Serial-0 BPS"                    ),
        new Attr(PROP_CFG_SERIAL0_DEBUG     , TYPE_BOOLEAN  ,  1, "Serial-0 Debug"                  ),
        
        // --- General serial port 1 config
        new Attr(PROP_CFG_SERIAL1_PORT      , TYPE_STRING   ,  1, "Serial-1 ComPort"                ),
        new Attr(PROP_CFG_SERIAL1_BPS       , TYPE_UINT32   ,  1, "Serial-1 BPS"                    ),
        new Attr(PROP_CFG_SERIAL1_DEBUG     , TYPE_BOOLEAN  ,  1, "Serial-1 Debug"                  ),
        
        // --- General serial port 2 config
        new Attr(PROP_CFG_SERIAL2_PORT      , TYPE_STRING   ,  1, "Serial-2 ComPort"                ),
        new Attr(PROP_CFG_SERIAL2_BPS       , TYPE_UINT32   ,  1, "Serial-2 BPS"                    ),
        new Attr(PROP_CFG_SERIAL2_DEBUG     , TYPE_BOOLEAN  ,  1, "Serial-2 Debug"                  ),
        
        // --- General serial port 3 config
        new Attr(PROP_CFG_SERIAL3_PORT      , TYPE_STRING   ,  1, "Serial-3 ComPort"                ),
        new Attr(PROP_CFG_SERIAL3_BPS       , TYPE_UINT32   ,  1, "Serial-3 BPS"                    ),
        new Attr(PROP_CFG_SERIAL3_DEBUG     , TYPE_BOOLEAN  ,  1, "Serial-3 Debug"                  ),
        
        // --- Command properties
        new Attr(PROP_CMD_SAVE_PROPS        , TYPE_COMMAND  ,  1, "Command Save Properties"         ),
        new Attr(PROP_CMD_AUTHORIZE         , TYPE_COMMAND  ,  1, "Command Authorize"               ),
        new Attr(PROP_CMD_STATUS_EVENT      , TYPE_COMMAND  ,  1, "Command Status Event"            ),
        new Attr(PROP_CMD_SET_OUTPUT        , TYPE_COMMAND  ,  1, "Command Set Output"              ),
        new Attr(PROP_CMD_RESET             , TYPE_COMMAND  ,  1, "Command Reset/Reboot"            ),
    
        // --- State properties
        new Attr(PROP_STATE_PROTOCOL        , TYPE_UINT8    ,  3, "Protocol Version"                ),
        new Attr(PROP_STATE_FIRMWARE        , TYPE_STRING   ,  1, "Firmware Version"                ),
        new Attr(PROP_STATE_COPYRIGHT       , TYPE_STRING   ,  1, "Copyright"                       ),
        new Attr(PROP_STATE_SERIAL          , TYPE_STRING   ,  1, "Device Serial Number"            ),
        new Attr(PROP_STATE_UNIQUE_ID       , TYPE_BINARY   ,  1, "Unique ID"                       ),
        new Attr(PROP_STATE_ACCOUNT_ID      , TYPE_STRING   ,  1, "Account ID"                      ),
        new Attr(PROP_STATE_DEVICE_ID       , TYPE_STRING   ,  1, "Device ID"                       ),
        new Attr(PROP_STATE_USER_ID         , TYPE_STRING   ,  1, "User ID"                         ),
        new Attr(PROP_STATE_USER_TIME       , TYPE_UINT32   ,  1, "User Login Time"                 ),
        new Attr(PROP_STATE_TIME            , TYPE_UINT32   ,  1, "Current Time"                    ),
        new Attr(PROP_STATE_GPS             , TYPE_GPS      ,  1, "Current GPS Fix"                 ), 
        new Attr(PROP_STATE_GPS_DIAGNOSTIC  , TYPE_UINT32   ,  5, "GPS Diagnostics"                 ), 
        new Attr(PROP_STATE_QUEUED_EVENTS   , TYPE_UINT32   ,  2, "Number of Queued Events"         ), 
        new Attr(PROP_STATE_DEV_DIAGNOSTIC  , TYPE_UINT32   ,  5, "Device Diagnostics"              ), 

        // --- Communication protocol properties
        new Attr(PROP_COMM_SPEAK_FIRST      , TYPE_BOOLEAN  ,  1, "Speak First"                     ),
        new Attr(PROP_COMM_FIRST_BRIEF      , TYPE_BOOLEAN  ,  1, "Speak First Brief"               ),
        new Attr(PROP_COMM_FAILURE_DELAY    , TYPE_UINT32   ,  2, "Connection failure delay"        ), // min/max
        new Attr(PROP_COMM_MAX_CONNECTIONS  , TYPE_UINT8    ,  3, "Max Connections"                 ), // total/duplex/minutes
        new Attr(PROP_COMM_MIN_XMIT_DELAY   , TYPE_UINT16   ,  1, "Min Transmit Delay"              ),
        new Attr(PROP_COMM_MIN_XMIT_RATE    , TYPE_UINT32   ,  1, "Min Transmit Rate"               ),
        new Attr(PROP_COMM_MAX_XMIT_RATE    , TYPE_UINT32   ,  1, "Max Transmit Rate"               ),
        new Attr(PROP_COMM_MAX_DUP_EVENTS   , TYPE_UINT8    ,  1, "Max Duplex Events"               ),
        new Attr(PROP_COMM_MAX_SIM_EVENTS   , TYPE_UINT8    ,  1, "Max Simplex Events"              ),

        // --- Communication connection properties
        new Attr(PROP_COMM_SETTINGS         , TYPE_STRING   ,  1, "Comm Settings"                   ),
        new Attr(PROP_COMM_DMTP_HOST        , TYPE_STRING   ,  1, "Comm Host"                       ),
        new Attr(PROP_COMM_DMTP_PORT        , TYPE_UINT16   ,  1, "Comm Port"                       ),
        new Attr(PROP_COMM_DNS_1            , TYPE_STRING   ,  1, "DNS-1"                           ),
        new Attr(PROP_COMM_DNS_2            , TYPE_STRING   ,  1, "DNS-2"                           ),
        new Attr(PROP_COMM_CONNECTION       , TYPE_STRING   ,  1, "Connection Name"                 ),
        new Attr(PROP_COMM_APN_NAME         , TYPE_STRING   ,  1, "APN Name"                        ),
        new Attr(PROP_COMM_APN_SERVER       , TYPE_STRING   ,  1, "APN Server"                      ),
        new Attr(PROP_COMM_APN_USER         , TYPE_STRING   ,  1, "APN Username"                    ),
        new Attr(PROP_COMM_APN_PASSWORD     , TYPE_STRING   ,  1, "APN Password"                    ),
        new Attr(PROP_COMM_APN_PHONE        , TYPE_STRING   ,  1, "APN Phone Number"                ),
        new Attr(PROP_COMM_APN_SETTINGS     , TYPE_STRING   ,  1, "APN Settings"                    ),
        new Attr(PROP_COMM_MIN_SIGNAL       , TYPE_INT16    ,  1, "Minimum Signal"                  ),
        new Attr(PROP_COMM_ACCESS_PIN       , TYPE_BINARY   ,  1, "Access PIN"                      ),

        // --- Packet/Data format properties
        new Attr(PROP_COMM_CUSTOM_FORMATS   , TYPE_UINT8    ,  1, "Custom Formats"                  ),
        new Attr(PROP_COMM_ENCODINGS        , TYPE_UINT8    ,  1, "Supported Encodings"             ),
        new Attr(PROP_COMM_BYTES_READ       , TYPE_UINT32   ,  1, "Bytes Read"                      ),
        new Attr(PROP_COMM_BYTES_WRITTEN    , TYPE_UINT32   ,  1, "Bytes Written"                   ),

        // --- GPS properties
        new Attr(PROP_GPS_SAMPLE_RATE       , TYPE_UINT16   ,  1, "GPS Sample Rate"                 ),
        new Attr(PROP_GPS_ACQUIRE_WAIT      , TYPE_UINT16   ,  1, "GPS Acquire Wait"                ),
        new Attr(PROP_GPS_EXPIRATION        , TYPE_UINT16   ,  1, "GPS Expiration Time"             ),
        new Attr(PROP_GPS_CLOCK_DELTA       , TYPE_BOOLEAN  ,  1, "GPS Clock Delta"                 ),
        new Attr(PROP_GPS_ACCURACY          , TYPE_UINT16   ,  1, "GPS Accuracy"                    ),
        new Attr(PROP_GPS_MIN_SPEED         , TYPE_UDEC16   ,  1, "GPS Minimum Speed"               ),
        new Attr(PROP_GPS_DISTANCE_DELTA    , TYPE_UINT32   ,  1, "GPS Distance Delta"              ),

        // --- GeoZone properties
        new Attr(PROP_CMD_GEOF_ADMIN        , TYPE_COMMAND  ,  1, "Command Geofence Admin"          ),
        new Attr(PROP_GEOF_COUNT            , TYPE_UINT16   ,  1, "Geofence Count"                  ),
        new Attr(PROP_GEOF_VERSION          , TYPE_STRING   ,  1, "Geofence Version"                ),
        new Attr(PROP_GEOF_ARRIVE_DELAY     , TYPE_UINT32   ,  1, "Geofence Arrive Delay"           ), 
        new Attr(PROP_GEOF_DEPART_DELAY     , TYPE_UINT32   ,  1, "Geofence Depart Delay"           ), 
        new Attr(PROP_GEOF_CURRENT          , TYPE_UINT32   ,  1, "Geofence Current"                ), 

        // --- GeoCorr properties
        new Attr(PROP_CMD_GEOC_ADMIN        , TYPE_COMMAND  ,  1, "Command Geocorridor Admin"       ),
        new Attr(PROP_GEOC_ACTIVE_ID        , TYPE_UINT32   ,  1, "Geocorridor Active ID"           ), 
        new Attr(PROP_GEOC_VIOLATION_INTRVL , TYPE_UINT16   ,  1, "Geocorridor Violation Interval"  ), 
        new Attr(PROP_GEOC_VIOLATION_COUNT  , TYPE_UINT16   ,  1, "Geocorridor Violation Count"     ), 

        // --- Motion properties
        new Attr(PROP_MOTION_START_TYPE     , TYPE_UINT8    ,  1, "Motion Start Type"               ),
        new Attr(PROP_MOTION_START          , TYPE_UDEC16   ,  1, "Motion Start Definition"         ),
        new Attr(PROP_MOTION_IN_MOTION      , TYPE_UINT16   ,  1, "Motion In-Motion Interval"       ),
        new Attr(PROP_MOTION_STOP           , TYPE_UINT16   ,  1, "Motion Stop Delay"               ),
        new Attr(PROP_MOTION_STOP_TYPE      , TYPE_UINT8    ,  1, "Motion Stop Type"                ),
        new Attr(PROP_MOTION_DORMANT_INTRVL , TYPE_UINT32   ,  1, "Motion Dormant Interval"         ),
        new Attr(PROP_MOTION_DORMANT_COUNT  , TYPE_UINT16   ,  1, "Motion Dormant Count"            ),
        new Attr(PROP_MOTION_EXCESS_SPEED   , TYPE_UDEC16   ,  1, "Motion Excess Speed"             ),
        new Attr(PROP_MOTION_MOVING_INTRVL  , TYPE_UINT16   ,  1, "Moving Interval"                 ),

        // --- Odometer properties
        new Attr(PROP_ODOMETER_0_VALUE      , TYPE_UINT32   ,  1, "Odometer-0 Value"                ),
        new Attr(PROP_ODOMETER_1_VALUE      , TYPE_UINT32   ,  1, "Odometer-1 Value"                ),
        new Attr(PROP_ODOMETER_2_VALUE      , TYPE_UINT32   ,  1, "Odometer-2 Value"                ),
        new Attr(PROP_ODOMETER_3_VALUE      , TYPE_UINT32   ,  1, "Odometer-3 Value"                ),
        new Attr(PROP_ODOMETER_4_VALUE      , TYPE_UINT32   ,  1, "Odometer-4 Value"                ),
        new Attr(PROP_ODOMETER_5_VALUE      , TYPE_UINT32   ,  1, "Odometer-5 Value"                ),
        new Attr(PROP_ODOMETER_6_VALUE      , TYPE_UINT32   ,  1, "Odometer-6 Value"                ),
        new Attr(PROP_ODOMETER_7_VALUE      , TYPE_UINT32   ,  1, "Odometer-7 Value"                ),
        new Attr(PROP_ODOMETER_0_LIMIT      , TYPE_UINT32   ,  1, "Odometer-0 Limit"                ),
        new Attr(PROP_ODOMETER_1_LIMIT      , TYPE_UINT32   ,  1, "Odometer-1 Limit"                ),
        new Attr(PROP_ODOMETER_2_LIMIT      , TYPE_UINT32   ,  1, "Odometer-2 Limit"                ),
        new Attr(PROP_ODOMETER_3_LIMIT      , TYPE_UINT32   ,  1, "Odometer-3 Limit"                ),
        new Attr(PROP_ODOMETER_4_LIMIT      , TYPE_UINT32   ,  1, "Odometer-4 Limit"                ),
        new Attr(PROP_ODOMETER_5_LIMIT      , TYPE_UINT32   ,  1, "Odometer-5 Limit"                ),
        new Attr(PROP_ODOMETER_6_LIMIT      , TYPE_UINT32   ,  1, "Odometer-6 Limit"                ),
        new Attr(PROP_ODOMETER_7_LIMIT      , TYPE_UINT32   ,  1, "Odometer-7 Limit"                ),
        new Attr(PROP_ODOMETER_0_GPS        , TYPE_GPS      ,  1, "Odometer-0 GPS"                  ),
        new Attr(PROP_ODOMETER_1_GPS        , TYPE_GPS      ,  1, "Odometer-1 GPS"                  ),
        new Attr(PROP_ODOMETER_2_GPS        , TYPE_GPS      ,  1, "Odometer-2 GPS"                  ),
        new Attr(PROP_ODOMETER_3_GPS        , TYPE_GPS      ,  1, "Odometer-3 GPS"                  ),
        new Attr(PROP_ODOMETER_4_GPS        , TYPE_GPS      ,  1, "Odometer-4 GPS"                  ),
        new Attr(PROP_ODOMETER_5_GPS        , TYPE_GPS      ,  1, "Odometer-5 GPS"                  ),
        new Attr(PROP_ODOMETER_6_GPS        , TYPE_GPS      ,  1, "Odometer-6 GPS"                  ),
        new Attr(PROP_ODOMETER_7_GPS        , TYPE_GPS      ,  1, "Odometer-7 GPS"                  ),

        // --- Digital input properties
        new Attr(PROP_INPUT_STATE           , TYPE_UINT32   ,  1, "Input State Mask"                ),
        new Attr(PROP_INPUT_CONFIG_0        , TYPE_UINT32   ,  2, "Input-0 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_1        , TYPE_UINT32   ,  2, "Input-1 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_2        , TYPE_UINT32   ,  2, "Input-2 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_3        , TYPE_UINT32   ,  2, "Input-3 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_4        , TYPE_UINT32   ,  2, "Input-4 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_5        , TYPE_UINT32   ,  2, "Input-5 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_6        , TYPE_UINT32   ,  2, "Input-6 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_7        , TYPE_UINT32   ,  2, "Input-7 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_8        , TYPE_UINT32   ,  2, "Input-8 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_9        , TYPE_UINT32   ,  2, "Input-9 Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_A        , TYPE_UINT32   ,  2, "Input-A Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_B        , TYPE_UINT32   ,  2, "Input-B Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_C        , TYPE_UINT32   ,  2, "Input-C Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_D        , TYPE_UINT32   ,  2, "Input-D Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_E        , TYPE_UINT32   ,  2, "Input-E Configuration"           ),
        new Attr(PROP_INPUT_CONFIG_F        , TYPE_UINT32   ,  2, "Input-F Configuration"           ),

        // --- Digitaloutput properties
        new Attr(PROP_OUTPUT_CONFIG_0       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_OUTPUT_CONFIG_1       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_OUTPUT_CONFIG_2       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_OUTPUT_CONFIG_3       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_OUTPUT_CONFIG_4       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_OUTPUT_CONFIG_5       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_OUTPUT_CONFIG_6       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_OUTPUT_CONFIG_7       , TYPE_UINT32   ,  2, "" ),

        // --- Elapsed time properties
        new Attr(PROP_ELAPSED_0_VALUE       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_1_VALUE       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_2_VALUE       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_3_VALUE       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_4_VALUE       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_5_VALUE       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_6_VALUE       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_7_VALUE       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_0_LIMIT       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_1_LIMIT       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_2_LIMIT       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_3_LIMIT       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_4_LIMIT       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_5_LIMIT       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_6_LIMIT       , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_ELAPSED_7_LIMIT       , TYPE_UINT32   ,  1, "" ),

        // --- Generic sensor properties
        new Attr(PROP_UNDERVOLTAGE_LIMIT    , TYPE_UINT32   ,  1, "" ),
        new Attr(PROP_SENSOR_CONFIG_0       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_CONFIG_1       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_CONFIG_2       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_CONFIG_3       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_CONFIG_4       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_CONFIG_5       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_CONFIG_6       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_CONFIG_7       , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_RANGE_0        , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_RANGE_1        , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_RANGE_2        , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_RANGE_3        , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_RANGE_4        , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_RANGE_5        , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_RANGE_6        , TYPE_UINT32   ,  2, "" ),
        new Attr(PROP_SENSOR_RANGE_7        , TYPE_UINT32   ,  2, "" ),

        // --- Temperature properties
        new Attr(PROP_TEMP_SAMPLE_INTRVL    , TYPE_UINT32   ,  2, "Temperature Sample Interval"     ),
        new Attr(PROP_TEMP_REPORT_INTRVL    , TYPE_UINT32   ,  2, "Temperature Report Interval"     ),
        new Attr(PROP_TEMP_CONFIG_0         , TYPE_INT16    ,  2, "Temperature-0 Configuration"     ),
        new Attr(PROP_TEMP_CONFIG_1         , TYPE_INT16    ,  2, "Temperature-1 Configuration"     ),
        new Attr(PROP_TEMP_CONFIG_2         , TYPE_INT16    ,  2, "Temperature-2 Configuration"     ),
        new Attr(PROP_TEMP_CONFIG_3         , TYPE_INT16    ,  2, "Temperature-3 Configuration"     ),
        new Attr(PROP_TEMP_RANGE_0          , TYPE_DEC16    ,  2, "Temperature-0 Range"             ),
        new Attr(PROP_TEMP_RANGE_1          , TYPE_DEC16    ,  2, "Temperature-1 Range"             ),
        new Attr(PROP_TEMP_RANGE_2          , TYPE_DEC16    ,  2, "Temperature-2 Range"             ),
        new Attr(PROP_TEMP_RANGE_3          , TYPE_DEC16    ,  2, "Temperature-3 Range"             ),

        // --- Accelerometer configuration:
        new Attr(PROP_MAX_BRAKE_G_FORCE     , TYPE_DEC16    ,  1, "Max Brake G-Force"               ),

        // --- OBC properties (EXPERIMENTAL - not yet part of the protocol)
        new Attr(PROP_OBC_J1708_CONFIG      , TYPE_UINT32   ,  1, "J1708 Configuration"             ),
        new Attr(PROP_OBC_ODOM_OFFSET       , TYPE_UINT32   ,  1, "J1708 Odometer Offset"           ),
        new Attr(PROP_OBC_J1708_VALUE       , TYPE_BINARY   ,  1, "J1708 Value"                     ),

    };
    
    // ------------------------------------------------------------------------
    
    /* Property attribute table */
    private static HashMap<Integer,Attr> propsTable = new HashMap<Integer,Attr>();
    
    /* put new attribute into table */
    private static void putAttr(PropCodes.Attr attr) 
    {
        propsTable.put(new Integer(attr.getKey()), attr);
    }
    
    /* return attribute for specified property key (code) */
    public static PropCodes.Attr getAttr(int key)
    {
        return (PropCodes.Attr)propsTable.get(new Integer(key));
    }
    
    /* static initializer */
    static {
        for (int i = 0; i < propAttr.length; i++) {
            PropCodes.putAttr(propAttr[i]);
        }
    }

    // ------------------------------------------------------------------------
    
    /* property attributes */
    private static class Attr
    {
        private int     key        = 0;     // property code
        private long    type       = 0L;    // bitmask
        private int     arrayCount = 0;
        private String  descript   = null;
        public Attr(int key, long type, int array, String desc) {
            this.key        = key;
            this.type       = type;
            this.arrayCount = (array > 0)? array : 1;
            this.descript   = desc;
        }
        
        /* return attribute key (property code) */
        public int getKey() {
            return this.key;
        }
        
        /* return property type */
        public long getType() {
            return this.type;
        }
        
        /* return true if this property is a command */
        public boolean isCommand() {
            return ((this.type & TYPE_TYPE_MASK) == TYPE_COMMAND);
        }
        
        /* return true if this property is numeric */
        public boolean isNumeric() {
            return ((this.type & TYPE_TYPE_MASK) == TYPE_NUMERIC);
        }
        
        /* return true if this property is a decimal numeric */
        public boolean isDecimal() {
            return this.isNumeric() && ((this.type & TYPE_DEC) != 0);
        }
        
        /* return true if this property is a string */
        public boolean isString() {
            return ((this.type & TYPE_TYPE_MASK) == TYPE_STRING);
        }
        
        /* return true if this property is a binary value */
        public boolean isBinary() {
            return ((this.type & TYPE_TYPE_MASK) == TYPE_BINARY);
        }
        
        /* return true if this property is a boolean */
        public boolean isBoolean() {
            return ((this.type & TYPE_TYPE_MASK) == TYPE_BOOLEAN);
        }
        
        /* return true if this property is a GPS value */
        public boolean isGPS() {
            return ((this.type & TYPE_TYPE_MASK) == TYPE_GPS);
        }
        
        /* return the number of bytes per element (numeric only, all else returns '1') */
        public int getTypeLength() {
            int t = (int)(this.type & TYPE_TYPE_MASK);
            switch (t) {
                case TYPE_COMMAND:
                    return -1;  // invalid
                case TYPE_STRING:
                    return 1;   // '1' null-terminated String
                case TYPE_BINARY:
                    return 1;   // '1' byte array
                case TYPE_GPS:
                    return 1;   // '1' GPS fix
                case TYPE_BOOLEAN:
                    return 1;   // '1' byte
                case TYPE_NUMERIC:
                    return (int)(this.type & TYPE_SIZE_MASK);
            }
            return -1;
        }
        
        /* return the number of array elements this property holds */
        public int getArrayLength() {
            return this.arrayCount;
        }
        
        /* return the description */
        public String getDescription() {
            return (this.descript != null)? this.descript : "";
        }
        
        /* parse the specified string value into this properties element types */
        public Object[] parseString(String value) {
            if ((value == null) || this.isCommand()) {
                Print.logError("Invalid value or property: 0x" + StringTools.toHexString(this.getKey()));
                return null;
            }
            int arrayLen = this.getArrayLength();
            String val[] = null;
            if (arrayLen == 1) {
                val = new String[] { value };
            } else {
                val = StringTools.parseArray(value);
                if (val.length != arrayLen) {
                    Print.logError("Invalid array length [expected " + arrayLen + ", found=" + val.length + "]");
                    return null;
                }
            }
            Object obj[] = null;
            switch ((int)(this.getType() & TYPE_TYPE_MASK)) {
                case TYPE_STRING:
                    obj = new Object[] { val[0] };
                    break;
                case TYPE_BINARY:
                    if (StringTools.isHex(val[0],true)) {
                        obj = new Object[] { StringTools.parseHex(val[0],new byte[0]) };
                    }
                    break;
                case TYPE_GPS:
                    if (val[0].indexOf(GeoPoint.PointSeparator) > 0) { // <-- not a definitive test
                        obj = new Object[] { new GeoPoint(val[0]) };
                    }
                    break;
                case TYPE_BOOLEAN:
                    obj = new Object[arrayLen];
                    for (int i = 0; i < arrayLen; i++) {
                        String v = val[i].trim();
                        if (StringTools.isBoolean(v,true)) {
                            obj[i] = new Boolean(StringTools.parseBoolean(v,false));
                        } else {
                            Print.logError("Invalid boolean value: " + v);
                            obj = null;
                            break;
                        }
                        //Print.logInfo("Parsed " + obj[i]);
                    }
                    break;
                case TYPE_NUMERIC:
                    obj = new Object[arrayLen];
                    for (int i = 0; i < arrayLen; i++) {
                        String v = val[i].trim();
                        if (this.isDecimal()) {
                            if (StringTools.isDouble(v,true)) {
                                obj[i] = new Double(StringTools.parseDouble(v,0.0));
                            } else {
                                Print.logError("Invalid decimal value: " + v);
                                obj = null;
                                break;
                            }
                        } else {
                            if (StringTools.isLong(v,true)) {
                                obj[i] = new Long(StringTools.parseLong(v,0L));
                            } else {
                                Print.logError("Invalid integer value: " + v);
                                obj = null;
                                break;
                            }
                        }
                        //Print.logInfo("Parsed " + obj[i]);
                    }
                    break;
                case TYPE_COMMAND:
                    //??
                    break;
            }
            return obj;
        }
        
        /* return String representation */
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(this.getDescription());
            sb.append(" [0x").append(StringTools.toHexString(this.getKey(),16)).append("]");
            sb.append(" type=0x").append(StringTools.toHexString(this.getType(),16));
            sb.append(" array=").append(this.getArrayLength());
            return sb.toString();
        }
        
    }
    
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    
    /* return true if the specified code refers to a valid property */
    public static boolean isValidPropertyCode(int key)
    {
        return (PropCodes.getAttr(key) != null);
    }
    
    // ------------------------------------------------------------------------

    /* return the description of the specified property */
    public static String getPropertyDescription(int key)
    {
        Attr attr = PropCodes.getAttr(key);
        return (attr != null)? attr.getDescription() : null;
    }
    
    // ------------------------------------------------------------------------

    /* parse string into specified property types */
    public static Object[] parsePropertyValue(int key, String value)
    {
        
        /* nothing to parse? */
        if (value == null) {
            Print.logError("Null value specified");
            return null;
        }

        /* get property attributes */
        PropCodes.Attr attr = PropCodes.getAttr(key);
        if (attr == null) {
            Print.logError("Property not found: 0x" + StringTools.toHexString(key));
            return null;
        }
        
        /* return parsed type */
        return attr.parseString(value);

    }

    // ------------------------------------------------------------------------

    /* encode set-property longs */
    public static byte[] encodePropertyData(int key, Object value[])
    {
        
        /* nothing to encode? */
        if ((value == null) || (value.length == 0)) {
            Print.logError("Null/empty value specified");
            return null;
        }

        /* get property attributes */
        PropCodes.Attr attr = PropCodes.getAttr(key);
        if (attr == null) {
            Print.logError("Property not found: 0x" + StringTools.toHexString(key));
            return null;
        }
        
        /* proper array length? */
        int arrayLen = attr.getArrayLength();
        if (arrayLen > value.length) {
            Print.logError("Invalid array length [expected " + arrayLen + "]");
            return null;
        }

        /* data buffer */
        byte data[] = null;

        /* encode data */
        if (attr.isString()) {
            if (value[0] instanceof String) {
                byte vs[] = StringTools.getBytes((String)value[0]);
                int vsLen = (vs.length <= 253)? vs.length : 253;
                data = new byte[vsLen];
                System.arraycopy(vs, 0, data, 0, vsLen);
            }
        } else
        if (attr.isBinary()) {
            if (value[0] instanceof byte[]) {
                byte vb[] = (byte[])value[0];
                int vbLen = (vb.length <= 253)? vb.length : 253;
                data = new byte[vbLen];
                System.arraycopy(vb, 0, data, 0, vbLen);
            }
        } else
        if (attr.isGPS()) {
            if (value[0] instanceof GeoPoint) {
                GeoPoint gp = (GeoPoint)value[0];
                data = new byte[GeoPoint.ENCODE_HIRES_LEN];
                GeoPoint.encodeGeoPoint(gp, data, 0, data.length);
            }
        } else
        if (attr.isBoolean()) {
            int elemLen = attr.getTypeLength(), ofs = 0;
            data = new byte[elemLen * arrayLen];
            for (int i = 0; i < arrayLen; i++) {
                long val = 0L;
                if (value[i] instanceof Boolean) {
                    val = ((Boolean)value[i]).booleanValue()? 1L : 0L;
                } else
                if (value[i] instanceof Number) {
                    val = (((Number)value[i]).longValue() != 0L)? 1L : 0L;
                } else {
                    // only boolean/numeric object types are allowed
                    data = null;
                    break;
                }
                int len = Payload.encodeLong(data, ofs, elemLen, true, val);
                if (len != elemLen) {
                    data = null; // should never occur
                    break;
                }
                ofs += elemLen;
            }
        } else
        if (attr.isNumeric()) {
            int elemLen = attr.getTypeLength(), ofs = 0;
            data = new byte[elemLen * arrayLen];
            for (int i = 0; i < arrayLen; i++) {
                long val = 0L;
                if (value[i] instanceof Number) {
                    Number v = (Number)value[i];
                    val = attr.isDecimal()? Math.round(v.doubleValue() * 10L) : v.longValue();
                } else {
                    // only numeric object types are allowed
                    data = null;
                    break;
                }
                int len = Payload.encodeLong(data, ofs, elemLen, true, val);
                if (len != elemLen) {
                    data = null; // should never occur
                    break;
                }
                ofs += elemLen;
            }
        }
        
        /* return bytes */
        return data;

    }

    // ------------------------------------------------------------------------

    /* encode set-property long */
    public static byte[] encodePropertyData(int key, long value)
    {
        return encodePropertyData(key, new long[] { value });
    }

    /* encode set-property longs */
    public static byte[] encodePropertyData(int key, long value[])
    {
        
        /* nothing to encode? */
        if ((value == null) || (value.length == 0)) {
            Print.logError("Null/empty value specified");
            return null;
        }

        /* get property attributes */
        PropCodes.Attr attr = PropCodes.getAttr(key);
        if (attr == null) {
            Print.logError("Property not found: 0x" + StringTools.toHexString(key));
            return null;
        }
        
        /* numeric only */
        if (!attr.isNumeric() && !attr.isBoolean()) {
            Print.logError("Property is not a numeric: 0x" + StringTools.toHexString(key));
            return null;
        }
        
        /* proper array length? */
        int arrayLen = attr.getArrayLength();
        if (arrayLen > value.length) {
            Print.logError("Invalid array length [expected " + arrayLen + "]");
            return null;
        }

        /* data buffer */
        int elemLen = attr.getTypeLength();
        byte data[] = new byte[elemLen * arrayLen];
        int ofs = 0;

        /* encode numeric */
        for (int i = 0; i < arrayLen; i++) {
            long val = 0L;
            if (attr.isBoolean()) {
                val = (value[i] != 0L)? 1L : 0L;
            } else
            if (attr.isDecimal()) {
                val = value[i] * 10L;
            } else {
                val = value[i];
            }
            int n = Payload.encodeLong(data, ofs, elemLen, true, val);
            if (n != elemLen) {
                return null; // should never occur
            }
            ofs += elemLen;
        }
        
        /* return bytes */
        return data;

    }

    // ------------------------------------------------------------------------

    /* encode set-property double */
    public static byte[] encodePropertyData(int key, double value)
    {
        return encodePropertyData(key, new double[] { value });
    }

    /* encode set-property doubles */
    public static byte[] encodePropertyData(int key, double value[])
    {
        
        /* nothing to encode? */
        if ((value == null) || (value.length == 0)) {
            Print.logError("Null/empty value specified");
            return null;
        }

        /* get property attributes */
        PropCodes.Attr attr = PropCodes.getAttr(key);
        if (attr == null) {
            Print.logError("Property not found: 0x" + StringTools.toHexString(key));
            return null;
        }
        
        /* numeric only */
        if (!attr.isNumeric() && !attr.isBoolean()) {
            Print.logError("Property is not numeric: 0x" + StringTools.toHexString(key));
            return null;
        }
        
        /* proper array length? */
        int arrayLen = attr.getArrayLength();
        if (arrayLen > value.length) {
            Print.logError("Invalid array length [expected " + arrayLen + "]");
            return null;
        }

        /* data buffer */
        int elemLen = attr.getTypeLength();
        byte data[] = new byte[elemLen * arrayLen];
        int ofs = 0;

        /* encode numeric */
        for (int i = 0; i < arrayLen; i++) {
            long val = 0L;
            if (attr.isBoolean()) {
                val = (Math.round(value[i]) != 0L)? 1L : 0L;
            } else
            if (attr.isDecimal()) {
                val = Math.round(value[i] * 10.0);
            } else {
                val = Math.round(value[i]);
            }
            int n = Payload.encodeLong(data, ofs, elemLen, true, val);
            if (n != elemLen) {
                return null; // should never occur
            }
            ofs += elemLen;
        }

        /* return bytes */
        return data;

    }

    // ------------------------------------------------------------------------

    /* encode set-property byte array */
    public static byte[] encodePropertyData(int key, byte value[])
    {
        
        /* nothing to encode? */
        if (value == null) {
            Print.logError("Null/empty value specified");
            return null;
        }

        /* get property attributes */
        PropCodes.Attr attr = PropCodes.getAttr(key);
        if (attr == null) {
            Print.logError("Property not found: 0x" + StringTools.toHexString(key));
            return null;
        }
        
        /* Binary only */
        if (!attr.isBinary()) {
            Print.logError("Property is not a byte array: 0x" + StringTools.toHexString(key));
            return null;
        }

        /* data buffer */
        byte vb[] = value;
        int vbLen = (vb.length <= 253)? vb.length : 253;
        byte data[] = new byte[vbLen];
        int ofs = 0;

        /* encode string */
        System.arraycopy(vb, 0, data, ofs, vbLen);

        /* return bytes */
        return data;

    }

    // ------------------------------------------------------------------------

    /* encode set-property String */
    public static byte[] encodePropertyData(int key, String value)
    {

        /* nothing to encode? */
        if (value == null) {
            Print.logError("Null value specified");
            return null;
        }

        /* get property attributes */
        PropCodes.Attr attr = PropCodes.getAttr(key);
        if (attr == null) {
            Print.logError("Property not found: 0x" + StringTools.toHexString(key));
            return null;
        }

        /* String only */
        if (!attr.isString()) {
            Print.logError("Property is not a String: 0x" + StringTools.toHexString(key));
            return null;
        }

        /* data buffer */
        byte vs[] = StringTools.getBytes(value);
        int vsLen = (vs.length <= 253)? vs.length : 253;
        byte data[] = new byte[vsLen];
        int ofs = 0;

        /* encode string */
        System.arraycopy(vs, 0, data, ofs, vsLen);

        /* return bytes */
        return data;

    }

    // ------------------------------------------------------------------------

    /* encode set-property GeoPoint */
    public static byte[] encodePropertyData(int key, GeoPoint value)
    {
        return _encodePropertyData(key, value, true);
    }

    /* encode set-property GeoPoint */
    private static byte[] _encodePropertyData(int key, GeoPoint value, boolean hiRes)
    {

        /* nothing to encode? */
        if (value == null) {
            Print.logError("Null value specified");
            return null;
        }

        /* get property attributes */
        PropCodes.Attr attr = PropCodes.getAttr(key);
        if (attr == null) {
            Print.logError("Property not found: 0x" + StringTools.toHexString(key));
            return null;
        }
        
        /* GeoPoint only */
        if (!attr.isGPS()) {
            Print.logError("Property is not a GPS point: 0x" + StringTools.toHexString(key));
            return null;
        }

        /* data buffer */
        byte data[] = new byte[hiRes? GeoPoint.ENCODE_HIRES_LEN : GeoPoint.ENCODE_LORES_LEN];
        
        /* encode GeoPoint */
        return GeoPoint.encodeGeoPoint(value, data, 0, data.length);

    }

    // ------------------------------------------------------------------------

}
