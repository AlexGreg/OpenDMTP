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
//  2007/01/25  Martin D. Flynn
//     -Added custom fields FIELD_ENTITY, FIELD_ENTITY_PAD, FIELD_STRING_PAD, 
//      and various FILED_OBC_xxxx fields
//     -Limit temperature ranges to -126/126 Low-Res, -3276.6/3276.6 High-Res
//  2007/02/11  Martin D. Flynn
//     -Added FIELD_OBC_COOLANT_LEVEL, FIELD_OBC_OIL_PRESSURE
//     -Changed FIELD_OBC_ENGINE_TEMP to FIELD_OBC_COOLANT_TEMP and added hiRes mode
//  2007/02/25  Martin D. Flynn
//     -Added FIELD_ODOMETER
//  2008/02/04  Martin D. Flynn
//     -Added FIELD_OBC_FUEL_TOTAL, FIELD_OBC_FUEL_IDLE
//  2010/04/27  Martin D. Flynn
// ----------------------------------------------------------------------------
package org.opendmtp.server.db;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;

import org.opengts.util.*;

public class PayloadTemplate
{

    // ------------------------------------------------------------------------

    public  static final int MAX_FIELD_COUNT            = 128; // arbitrary valie
    
    // ------------------------------------------------------------------------

    public  static final int PRIMITIVE_MASK             = 0x00F0;
    public  static final int PRIMITIVE_LONG             = 0x0010;
    public  static final int PRIMITIVE_GPS              = 0x0030;
    public  static final int PRIMITIVE_STRING           = 0x0040;
    public  static final int PRIMITIVE_BINARY           = 0x0050;

    // ------------------------------------------------------------------------
 
    public  static final int FIELD_STATUS_CODE          = 0x01;
    public  static final int FIELD_TIMESTAMP            = 0x02;
    public  static final int FIELD_INDEX                = 0x03;
    
    public  static final int FIELD_SEQUENCE             = 0x04;

    public  static final int FIELD_GPS_POINT            = 0x06;
    public  static final int FIELD_GPS_AGE              = 0x07; // %2u 0 to 65535 sec
    public  static final int FIELD_SPEED                = 0x08;
    public  static final int FIELD_HEADING              = 0x09;
    public  static final int FIELD_ALTITUDE             = 0x0A;
    public  static final int FIELD_DISTANCE             = 0x0B;
    public  static final int FIELD_ODOMETER             = 0x0C;

 // Misc fields                                                 // Low                          High
    public  static final int FIELD_GEOFENCE_ID          = 0x0E; // %4u 0x00000000 to 0xFFFFFFFF
    public  static final int FIELD_TOP_SPEED            = 0x0F; // %1u 0 to 255 kph             %2u 0.0 to 655.3 kph
    public  static final int FIELD_BRAKE_G_FORCE        = 0x10; // %1u 0.0 to 25.5 G            %2u 0.0 to 655.3 G

    public  static final int FIELD_STRING               = 0x11; // %*s may contain only 'A'..'Z', 'a'..'z, '0'..'9', '-', '.'
    public  static final int FIELD_STRING_PAD           = 0x12; // %*s may contain only 'A'..'Z', 'a'..'z, '0'..'9', '-', '.'
    public  static final int FIELD_ENTITY               = 0x15; // %*s may contain only 'A'..'Z', 'a'..'z, '0'..'9', '-', '.' 
    public  static final int FIELD_ENTITY_PAD           = 0x16; // %*s may contain only 'A'..'Z', 'a'..'z, '0'..'9', '-', '.' 

    public  static final int FIELD_BINARY               = 0x1A; // %*b  

 // I/O fields                                                  // Low                          High
    public  static final int FIELD_INPUT_ID             = 0x21; // %4u 0x00000000 to 0xFFFFFFFF
    public  static final int FIELD_INPUT_STATE          = 0x22; // %4u 0x00000000 to 0xFFFFFFFF
    public  static final int FIELD_OUTPUT_ID            = 0x24; // %4u 0x00000000 to 0xFFFFFFFF
    public  static final int FIELD_OUTPUT_STATE         = 0x25; // %4u 0x00000000 to 0xFFFFFFFF
    public  static final int FIELD_ELAPSED_TIME         = 0x27; // %3u 0 to 16777216 sec        %4u 0.000 to 4294967.295 sec
    public  static final int FIELD_COUNTER              = 0x28; // %4u 0 to 4294967295
    
    public  static final int FIELD_SENSOR32_LOW         = 0x31; // %4u 0x00000000 to 0xFFFFFFFF
    public  static final int FIELD_SENSOR32_HIGH        = 0x32; // %4u 0x00000000 to 0xFFFFFFFF
    public  static final int FIELD_SENSOR32_AVER        = 0x33; // %4u 0x00000000 to 0xFFFFFFFF
    
    public  static final int FIELD_TEMP_LOW             = 0x3A; // %1i -126 to +126 C           %2i -3276.6 to +3276.6 C
    public  static final int FIELD_TEMP_HIGH            = 0x3B; // %1i -126 to +126 C           %2i -3276.6 to +3276.6 C
    public  static final int FIELD_TEMP_AVER            = 0x3C; // %1i -126 to +126 C           %2i -3276.6 to +3276.6 C

 // GPS quality fields                                          // Low                          High
    public  static final int FIELD_GPS_DGPS_UPDATE      = 0x41; // %2u 0 to 65535 sec
    public  static final int FIELD_GPS_HORZ_ACCURACY    = 0x42; // %1u 0 to 255 m               %2u 0.0 to 6553.5 m
    public  static final int FIELD_GPS_VERT_ACCURACY    = 0x43; // %1u 0 to 255 m               %2u 0.0 to 6553.5 m
    public  static final int FIELD_GPS_SATELLITES       = 0x44; // %1u 0 to 12
    public  static final int FIELD_GPS_MAG_VARIATION    = 0x45; // %2i -180.00 to 180.00 deg
    public  static final int FIELD_GPS_QUALITY          = 0x46; // %1u (0=None, 1=GPS, 2=DGPS, ...)
    public  static final int FIELD_GPS_TYPE             = 0x47; // %1u (1=None, 2=2D, 3=3D, ...)
    public  static final int FIELD_GPS_GEOID_HEIGHT     = 0x48; // %1i -128 to +127 m           %2i -3276.7 to +3276.7 m
    public  static final int FIELD_GPS_PDOP             = 0x49; // %1u 0.0 to 25.5              %2u 0.0 to 99.9
    public  static final int FIELD_GPS_HDOP             = 0x4A; // %1u 0.0 to 25.5              %2u 0.0 to 99.9
    public  static final int FIELD_GPS_VDOP             = 0x4B; // %1u 0.0 to 25.5              %2u 0.0 to 99.9

 // OBC/J1708 fields                                            // Low                          High
    public  static final int FIELD_OBC_VALUE            = 0x50; // %*b (at least 4 bytes, includes mid/pid)
    public  static final int FIELD_OBC_GENERIC          = 0x51; // %4u
    public  static final int FIELD_OBC_J1708_FAULT      = 0x52; // %4u
    public  static final int FIELD_OBC_DISTANCE         = 0x54; // %3u 0 to 16777216 km         %4u 0.0 to 429496729.5 km
    public  static final int FIELD_OBC_ENGINE_HOURS     = 0x57; // %3u 0 to 1677721.6 hours
    public  static final int FIELD_OBC_ENGINE_RPM       = 0x58; // %2u 0 to 65535 rpm
    public  static final int FIELD_OBC_COOLANT_TEMP     = 0x59; // %1i -126 to +126 C           %2i -3276.7 to +3276.7 C
    public  static final int FIELD_OBC_COOLANT_LEVEL    = 0x5A; // %1u 0% to 100% percent       %2u 0.0% to 100.0% percent
    public  static final int FIELD_OBC_OIL_LEVEL        = 0x5B; // %1u 0% to 100% percent       %2u 0.0% to 100.0% percent
    public  static final int FIELD_OBC_OIL_PRESSURE     = 0x5C; // %1u 0 to 255 kPa             %2u 0.0 to 6553.5 kPa
    public  static final int FIELD_OBC_FUEL_LEVEL       = 0x5D; // %1u 0% to 100% percent       %2u 0.0% to 100.0% percent
    public  static final int FIELD_OBC_FUEL_ECONOMY     = 0x5E; // %2u 0.0 to 6553.5 kpg
    public  static final int FIELD_OBC_FUEL_TOTAL       = 0x5F; // %3u 0 to 16777216 liters     %4u 0.0 to 429496729.5 liters
    public  static final int FIELD_OBC_FUEL_IDLE        = 0x60; // %3u 0 to 16777216 liters     %4u 0.0 to 429496729.5 liters
    // 0x60..0x6F reserved for additional OBC/J1708 fields as needed

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    protected static Field[] parseFields(String fldStr[])
    {
        java.util.List<Field> pfl = new Vector<Field>();
        for (int i = 0; i < fldStr.length; i++) {
            Field pf = new Field(fldStr[i]);
            if (pf.isValidType()) {
                pfl.add(pf);
            } else {
                Print.logError("Invalid field format [ignored]: " + fldStr[i]);
            }
        }
        return pfl.toArray(new Field[pfl.size()]);
    }
    
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    private int     customType  = -1; // undefined
    private Field   fields[]    = null;
    private boolean repeatLast  = false;
    
    public PayloadTemplate(int type, Field flds[])
    {
        this.customType = type;
        this.fields     = flds;
        this.repeatLast = false;
    }
        
    public PayloadTemplate(int type, Field flds[], boolean repeatLast)
    {
        this.customType = type;
        this.fields     = flds;
        this.repeatLast = repeatLast;
    }
    
    public PayloadTemplate(int type, String flds[])
    {
        this(type, PayloadTemplate.parseFields(flds));
    }
        
    public PayloadTemplate(int type, String flds[], boolean repeatLast)
    {
        this(type, PayloadTemplate.parseFields(flds), repeatLast);
    }

    // ------------------------------------------------------------------------

    public int getPacketType()
    {
        return this.customType;
    }
    
    public Field getField(int ndx)
    {
        if ((ndx >= 0) && (this.fields != null) && (this.fields.length > 0)) {
            if (ndx < this.fields.length) {
                return this.fields[ndx];
            } else
            if (this.repeatLast) {
                return this.fields[this.fields.length - 1];
            }
        }
        return null;
    }
    
    public Field[] getFields()
    {
        if (this.fields == null) { this.fields = new Field[0]; }
        return this.fields;
    }
    
    public boolean getRepeatLast()
    {
        return this.repeatLast;
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    private static final char FIELD_VALUE_SEPARATOR = '|';

    public static class Field
    {
        private boolean hiRes   = false;
        private int     fldType = -1;
        private int     fldNdx  = 0;
        private int     fldLen  = 0;
        
        public Field(int type, boolean hiRes, int index, int length) {
            this.fldType = type;
            this.hiRes   = hiRes;
            this.fldNdx  = index;
            this.fldLen  = length;
        }
        
        public Field(long mask) {
            // 24 bits:
            //   ABBBBBBB CCCCCCCC DDDDDDDD
            //   A - hi/low resolution
            //   B - type
            //   C - index
            //   D - length
            this.fldType = (int)(mask >> 16) & 0x7F;
            this.hiRes   = ((mask & 0x800000) != 0);
            this.fldNdx  = (int)(mask >>  8) & 0xFF;
            this.fldLen  = (int)mask & 0xFF;
        }
        
        public Field(String s) {
            // "<type>|[H|L]|<index>|<length>"
            String f[] = StringTools.parseString(s,FIELD_VALUE_SEPARATOR);
            this.fldType = (f.length > 0)? StringTools.parseInt(f[1],-1) : -1;
            this.hiRes   = (f.length > 1)? f[0].equalsIgnoreCase("H") : false;
            this.fldNdx  = (f.length > 2)? StringTools.parseInt(f[2], 0) :  0;
            this.fldLen  = (f.length > 3)? StringTools.parseInt(f[3], 0) :  0;
        }
        
        public int getType() {
            return this.fldType;
        }
        
        public int getPrimitiveType() {
            switch (this.fldType) {
                case FIELD_GPS_POINT:    return PRIMITIVE_GPS;
                case FIELD_STRING:       return PRIMITIVE_STRING;
                case FIELD_STRING_PAD:   return PRIMITIVE_STRING;
                case FIELD_ENTITY:       return PRIMITIVE_STRING;
                case FIELD_ENTITY_PAD:   return PRIMITIVE_STRING;
                case FIELD_BINARY:       return PRIMITIVE_BINARY;
                default:                 return PRIMITIVE_LONG;
            }
        }
        
        public boolean isValidType() {
            return ((this.fldType >= 0) && (this.fldLen > 0));
        }
        
        public boolean isSigned() {
            switch (this.fldType) {
                case FIELD_GPS_MAG_VARIATION:
                case FIELD_GPS_GEOID_HEIGHT:
                case FIELD_ALTITUDE:
                case FIELD_TEMP_LOW:
                case FIELD_TEMP_HIGH:
                case FIELD_TEMP_AVER:
                    return true;
                default:
                    return false;
            }
        }
        
        public boolean isHex() {
            switch (this.fldType) {
                case FIELD_HEADING:
                    return !this.hiRes;
                case FIELD_STATUS_CODE:
                case FIELD_SEQUENCE:
                case FIELD_INPUT_ID:
                case FIELD_INPUT_STATE:
                case FIELD_OUTPUT_ID:
                case FIELD_OUTPUT_STATE:
                case FIELD_GEOFENCE_ID:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isHiRes() {
            return this.hiRes;
        }
        
        public int getIndex() {
            return this.fldNdx;
        }
        
        public int getLength() {
            return this.fldLen;
        }
        
        public int parseString(String s[], int sndx, Payload payload) {
            // NOTE: This should specifically set the index to the proper payload location!!
            int length = this.getLength();
            switch (this.getPrimitiveType()) {
                case PRIMITIVE_GPS: {
                    double lat = StringTools.parseDouble(s[sndx++], 0.0);
                    double lon = (sndx < s.length)? StringTools.parseDouble(s[sndx++], 0.0) : 0.0;
                    payload.writeGPS(new GeoPoint(lat,lon), length);
                    break;
                }
                case PRIMITIVE_STRING: {
                    payload.writeString(s[sndx++], length);
                    break;
                }
                case PRIMITIVE_BINARY: {
                    byte b[] = StringTools.parseHex(s[sndx++], new byte[0]);
                    payload.writeBytes(b, length);
                    break;
                }
                case PRIMITIVE_LONG:
                default: {
                    long val = s[sndx].startsWith("0x")?
                        StringTools.parseHexLong(s[sndx++], 0L) :
                        StringTools.parseLong(s[sndx++], 0L);
                    if (this.isSigned()) {
                        payload.writeLong(val, length);
                    } else {
                        payload.writeULong(val, length);
                    }
                    break;
                }
            }
            return sndx;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(this.getType());
            sb.append(FIELD_VALUE_SEPARATOR);
            sb.append(this.isHiRes()?"H":"L");
            sb.append(FIELD_VALUE_SEPARATOR);
            sb.append(this.getIndex());
            sb.append(FIELD_VALUE_SEPARATOR);
            sb.append(this.getLength());
            return sb.toString();
        }

        public boolean equals(Object other) {
            if (other instanceof Field) {
                return this.toString().equals(other.toString());
            } else {
                return false;
            }
        }

    }

}

