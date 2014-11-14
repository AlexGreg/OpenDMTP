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
//  2006/04/02  Martin D. Flynn
//     -Minor changes to 'toString()'
//  2007/01/25  Martin D. Flynn
//     -Added custom fields FIELD_ENTITY, FIELD_ENTITY_PAD, FIELD_STRING_PAD, 
//      and various FILED_OBJ_xxxx fields
//     -Limit temperature ranges to -126/126 Low-Res, -3276.6/3276.6 High-Res
//  2007/02/11  Martin D. Flynn
//     -Added FIELD_OBC_COOLANT_LEVEL, FIELD_OBC_OIL_PRESSURE
//     -Changed FIELD_OBC_ENGINE_TEMP to FIELD_OBC_COOLANT_TEMP and added hiRes mode
//  2007/02/18  Martin D. Flynn
//     -Changed FIELD_OBC_FAULT_CODE to FIELD_OBC_J1708_FAULT
//  2007/02/25  Martin D. Flynn
//     -Added support for PayloadTemplate.FIELD_ODOMETER
//  2008/02/04  Martin D. Flynn
//     -Added support for PayloadTemplate.[FIELD_OBC_FUEL_TOTAL|FIELD_OBC_FUEL_IDLE]
//  2010/02/17  Martin D. Flynn
//     -Modified to support fixed-length strings FIELD_STRING_PAD and FIELD_ENTITY_PAD
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

public class Event
{
    
    // ------------------------------------------------------------------------

    private static final long INVALID_TEMPERATURE = -99999L;
    
    // ------------------------------------------------------------------------
    // types:
    //      long
    //      double
    //      byte
    //      GeoPoint
    //      String
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------

    /* the fields that made up this object */
    private Packet                packet        = null;
    private PayloadTemplate       custTemplate  = null;
    private int                   custFieldLen  = 0;
    private DMTPGeoEvent          geoEvent      = null;

    // ------------------------------------------------------------------------
    
    public Event(Packet pkt)
        throws PacketParseException
    {
        this(null, pkt);
    }

    public Event(String ipAddr, Packet pkt)
        throws PacketParseException
    {
        super();
        this.packet    = pkt;
        this.geoEvent  = new DMTPGeoEvent();

        /* Validate Packet */
        if (this.packet == null) {
            // no packet specified
            // internal error (this should never happen)
            throw new PacketParseException(ServerErrors.NAK_PACKET_LENGTH, this.packet); // errData ok
        } else
        if (!this.packet.isEventType()) {
            // not an event packet
            // internal error (this should never happen)
            throw new PacketParseException(ServerErrors.NAK_PACKET_TYPE, this.packet); // errData ok
        } else
        if (!this.packet.hasPayload()) {
            // client did not include payload
            throw new PacketParseException(ServerErrors.NAK_PACKET_PAYLOAD, this.packet); // errData ok
        }

        /* get Event Payload Definition? */
        this.custTemplate = this.packet.getPayloadTemplate();
        if (this.custTemplate == null) {
            int hdrType = (this.packet.getPacketHeader() << 8) | this.packet.getPacketType();
            Print.logError("PayloadTemplate not found: 0x" + StringTools.toHexString(hdrType,16));
            throw new PacketParseException(ServerErrors.NAK_FORMAT_NOT_RECOGNIZED, this.packet); // errData ok
        }
        
        /* save ip address */
        this.setEventValue(DMTPGeoEvent.FLD_ipAddress, ((ipAddr!=null)?ipAddr:""));

        /* parse */
        this.custFieldLen = 0;
        this._decodeEvent();

    }

    // ------------------------------------------------------------------------

    public String getIPAddress()
    {
        return this.getGeoEvent().getIPAddress();
    }

    // ------------------------------------------------------------------------

    public Packet getPacket()
    {
        return this.packet;
    }
    
    public DMTPGeoEvent getGeoEvent()
    {
        return this.geoEvent;
    }
    
    public long getSequence()
    {
        return this.getGeoEvent().getSequence();
    }
    
    public int getSequenceLength()
    {
        return this.getGeoEvent().getSequenceLength();
    }

    // ------------------------------------------------------------------------
    
    private void setEventValue(String fldName, Object val, int ndx) 
    {
        this.getGeoEvent().setEventValue(fldName, val, ndx);
    }
    private void setEventValue(String fldName, Object val) 
    {
        this.getGeoEvent().setEventValue(fldName, val);
    }

    public void setEventValue(String fldName, long val, int ndx) 
    {
        this.getGeoEvent().setEventValue(fldName, val, ndx);
    }
    public void setEventValue(String fldName, long val) 
    {
        this.getGeoEvent().setEventValue(fldName, val);
    }

    public void setEventValue(String fldName, double val, int ndx) 
    {
        this.getGeoEvent().setEventValue(fldName, val, ndx);
    }
    public void setEventValue(String fldName, double val)
    {
        this.getGeoEvent().setEventValue(fldName, val);
    }

    // ------------------------------------------------------------------------

    public byte[] getByteValue(String fldName, byte[] dft)
    {
        return this.getGeoEvent().getByteValue(fldName, dft);
    }
    public  byte[] getByteValue(String fldName,  byte[] dft, int ndx)
    {
        return this.getGeoEvent().getByteValue(fldName, dft, ndx);
    }

    public String getStringValue(String fldName, String dft)
    {
        return this.getGeoEvent().getStringValue(fldName, dft);
    }
    public String getStringValue(String fldName, String dft, int ndx)
    {
        return this.getGeoEvent().getStringValue(fldName, dft, ndx);
    }

    public long getLongValue(String fldName, long dft)
    {
        return this.getGeoEvent().getLongValue(fldName, dft);
    }
    public long getLongValue(String fldName, long dft, int ndx)
    {
        return this.getGeoEvent().getLongValue(fldName, dft, ndx);
    }
    
    public double getDoubleValue(String fldName, double dft)
    {
        return this.getGeoEvent().getDoubleValue(fldName, dft);
    }
    public double getDoubleValue(String fldName, double dft, int ndx)
    {
        return this.getGeoEvent().getDoubleValue(fldName, dft, ndx);
    }
    
    public GeoPoint getGeoPointValue(String fldName, GeoPoint dft)
    {
        return this.getGeoEvent().getGeoPointValue(fldName, dft);
    }
    public GeoPoint getGeoPointValue(String fldName, GeoPoint dft, int ndx)
    {
        return this.getGeoEvent().getGeoPointValue(fldName, dft, ndx);
    }

    // ------------------------------------------------------------------------

    private void _decodeEvent()
        throws PacketParseException
    {
        Payload payload = this.packet.getPayload(true);
        
        /* raw data */
        this.setEventValue(DMTPGeoEvent.FLD_rawData   , this.packet.toString());

        /* defaults */
        this.setEventValue(DMTPGeoEvent.FLD_statusCode, StatusCodes.STATUS_NONE);
        this.setEventValue(DMTPGeoEvent.FLD_timestamp , DateTime.getCurrentTimeSec());
        
        /* parse payload */
        boolean hasStatusCode = false;
        boolean hasGeoPoint = false;
        payload.resetIndex();
        for (this.custFieldLen = 0; payload.hasAvailableRead(); this.custFieldLen++) {
            
            /* safety net */
            if (this.custFieldLen >= PayloadTemplate.MAX_FIELD_COUNT) {
                Print.logError("Invalid number of fields: " + this.custFieldLen);
                Payload p = new Payload();
                p.writeULong(this.packet.getPacketType(), 1);
                byte errData[] = p.getBytes();
                throw new PacketParseException(ServerErrors.NAK_FORMAT_DEFINITION_INVALID, this.packet, errData); // formatType, fieldIndex
            }
            
            /* get field */
            PayloadTemplate.Field field = this.custTemplate.getField(this.custFieldLen);
            if (field == null) { break; }
            int type      = field.getType();
            boolean hiRes = field.isHiRes();
            int ndx       = field.getIndex();
            int length    = field.getLength();
            if (length == 0) {
                Print.logError("Invalid Field length: " + StringTools.toHexString(type,8));
                Payload p = new Payload();
                p.writeULong(this.packet.getPacketType(), 1);
                byte errData[] = p.getBytes();
                throw new PacketParseException(ServerErrors.NAK_FORMAT_DEFINITION_INVALID, this.packet, errData); // formatType, fieldIndex
            }
            
            /* parse file type */
            long longVal  = 0L;
            GeoPoint gp   = null;
            switch (type) {
                
                case PayloadTemplate.FIELD_STATUS_CODE      : // %2u
                    this.setEventValue(DMTPGeoEvent.FLD_statusCode, payload.readULong(length, 0L));
                    hasStatusCode = true;
                    break;
                case PayloadTemplate.FIELD_TIMESTAMP        : // %4u
                    this.setEventValue(DMTPGeoEvent.FLD_timestamp, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_INDEX            : // %4u 0 to 4294967295
                    this.setEventValue(DMTPGeoEvent.FLD_index, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_GPS_POINT        : // %6g                          %8g
                    this.setEventValue(DMTPGeoEvent.FLD_geoPoint, payload.readGPS(length));
                    hasGeoPoint = true;
                    break;
                case PayloadTemplate.FIELD_SPEED            : // %1u 0 to 255 kph             %2u 0.0 to 655.3 kph
                    this.setEventValue(DMTPGeoEvent.FLD_speedKPH, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)));
                    break;
                case PayloadTemplate.FIELD_HEADING          : // %1u 1.412 deg un.            %2u 0.00 to 360.00 deg
                    this.setEventValue(DMTPGeoEvent.FLD_heading, hiRes?
                        ((double)payload.readULong(length, 0L) / 100.0) :
                        ((double)payload.readULong(length, 0L) * 360.0/255.0));
                    break;
                case PayloadTemplate.FIELD_ALTITUDE         : // %2i -32767 to +32767 m       %3i -838860.7 to +838860.7 m
                    this.setEventValue(DMTPGeoEvent.FLD_altitude, hiRes?
                        ((double)payload.readLong(length, 0L) / 10.0) :
                        ((double)payload.readLong(length, 0L)));
                    break;
                case PayloadTemplate.FIELD_DISTANCE         : // %3u 0 to 16777216 km         %3u 0.0 to 1677721.6 km
                    this.setEventValue(DMTPGeoEvent.FLD_distanceKM, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)));
                    break;
                case PayloadTemplate.FIELD_ODOMETER        : // %3u 0 to 16777216 km         %3u 0.0 to 1677721.6 km
                    this.setEventValue(DMTPGeoEvent.FLD_odometerKM, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)));
                    break;
                case PayloadTemplate.FIELD_SEQUENCE         : // %1u 0 to 255
                    this.setEventValue(DMTPGeoEvent.FLD_sequence, payload.readULong(length, -1L));
                    this.setEventValue(DMTPGeoEvent.FLD_sequenceLength, length);
                    break;

                case PayloadTemplate.FIELD_INPUT_ID         : // %4u 0x00000000 to 0xFFFFFFFF
                    this.setEventValue(DMTPGeoEvent.FLD_inputID, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_INPUT_STATE      : // %4u 0x00000000 to 0xFFFFFFFF
                    this.setEventValue(DMTPGeoEvent.FLD_inputState, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_OUTPUT_ID        : // %4u 0x00000000 to 0xFFFFFFFF
                    this.setEventValue(DMTPGeoEvent.FLD_outputID, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_OUTPUT_STATE     : // %4u 0x00000000 to 0xFFFFFFFF
                    this.setEventValue(DMTPGeoEvent.FLD_outputState, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_ELAPSED_TIME     : // %3u 0 to 16777216 sec        %4u 0.000 to 4294967.295 sec
                    this.setEventValue(DMTPGeoEvent.FLD_elapsedTime, hiRes?
                        (payload.readULong(length, 0L)) :
                        (payload.readULong(length, 0L) * 1000L), ndx);
                    break;
                case PayloadTemplate.FIELD_COUNTER          : // %4u 0 to 4294967295
                    this.setEventValue(DMTPGeoEvent.FLD_counter, payload.readULong(length, 0L), ndx);
                    break;
                case PayloadTemplate.FIELD_SENSOR32_LOW     : // %4u 0x00000000 to 0xFFFFFFFF
                    this.setEventValue(DMTPGeoEvent.FLD_sensor32LO, payload.readULong(length, 0L), ndx);
                    break;
                case PayloadTemplate.FIELD_SENSOR32_HIGH    : // %4u 0x00000000 to 0xFFFFFFFF
                    this.setEventValue(DMTPGeoEvent.FLD_sensor32HI, payload.readULong(length, 0L), ndx);
                    break;
                case PayloadTemplate.FIELD_SENSOR32_AVER    : // %4u 0x00000000 to 0xFFFFFFFF
                    this.setEventValue(DMTPGeoEvent.FLD_sensor32AV, payload.readULong(length, 0L), ndx);
                    break;
                case PayloadTemplate.FIELD_TEMP_LOW         : // %1i -126 to +126 C           %2i -3276.6 to +3276.6 C
                    longVal = payload.readLong(length, INVALID_TEMPERATURE);
                    if ((length == 1) && (Math.abs(longVal) > 126)) { longVal = INVALID_TEMPERATURE; }
                    this.setEventValue(DMTPGeoEvent.FLD_tempLO, hiRes?
                        ((double)longVal / 10.0) :
                        ((double)longVal), ndx);
                    break;
                case PayloadTemplate.FIELD_TEMP_HIGH        : // %1i -126 to +126 C           %2i -3276.6 to +3276.6 C
                    longVal = payload.readLong(length, INVALID_TEMPERATURE);
                    if ((length == 1) && (Math.abs(longVal) > 126)) { longVal = INVALID_TEMPERATURE; }
                    this.setEventValue(DMTPGeoEvent.FLD_tempHI, hiRes?
                        ((double)longVal / 10.0) :
                        ((double)longVal), ndx);
                    break;
                case PayloadTemplate.FIELD_TEMP_AVER        : // %1i -126 to +126 C           %2i -3276.6 to +3276.6 C
                    longVal = payload.readLong(length, INVALID_TEMPERATURE);
                    if ((length == 1) && (Math.abs(longVal) > 126)) { longVal = INVALID_TEMPERATURE; }
                    this.setEventValue(DMTPGeoEvent.FLD_tempAV, hiRes?
                        ((double)longVal / 10.0) :
                        ((double)longVal), ndx);
                    break;

                case PayloadTemplate.FIELD_GEOFENCE_ID      : // %4u 0x00000000 to 0xFFFFFFFF
                    this.setEventValue(DMTPGeoEvent.FLD_geofenceID, payload.readULong(length, 0L), ndx);
                    break;
                case PayloadTemplate.FIELD_TOP_SPEED        : // %1u 0 to 255 kph             %2u 0.0 to 655.3 kph
                    this.setEventValue(DMTPGeoEvent.FLD_topSpeedKPH, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)       )  , ndx);
                    break;
                case PayloadTemplate.FIELD_BRAKE_G_FORCE    : // %1u 0.0 to 25.5              %2u 0.0 to 655.3
                    this.setEventValue(DMTPGeoEvent.FLD_brakeGForce, ((double)payload.readLong(length, 0L) / 10.0));
                    break;

                case PayloadTemplate.FIELD_STRING           : // %*s may contain only 'A'..'Z', 'a'..'z, '0'..'9', '-', '.'
                    this.setEventValue(DMTPGeoEvent.FLD_string, payload.readString(length,true) , ndx);
                    break;
                case PayloadTemplate.FIELD_STRING_PAD       : // %*s may contain only 'A'..'Z', 'a'..'z, '0'..'9', '-', '.'
                    this.setEventValue(DMTPGeoEvent.FLD_string, payload.readString(length,false), ndx);
                    break;
                case PayloadTemplate.FIELD_ENTITY           : // %*s may contain only 'A'..'Z', 'a'..'z, '0'..'9', '-', '.'
                    this.setEventValue(DMTPGeoEvent.FLD_entity, payload.readString(length,true) , ndx);
                    break;
                case PayloadTemplate.FIELD_ENTITY_PAD       : // %*s may contain only 'A'..'Z', 'a'..'z, '0'..'9', '-', '.'
                    this.setEventValue(DMTPGeoEvent.FLD_entity, payload.readString(length,false), ndx);
                    break;
                case PayloadTemplate.FIELD_BINARY           : // %*b
                    this.setEventValue(DMTPGeoEvent.FLD_binary, payload.readBytes(length), ndx);
                    break;

                case PayloadTemplate.FIELD_GPS_AGE          : // %2u 0 to 65535 sec
                    this.setEventValue(DMTPGeoEvent.FLD_gpsAge, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_GPS_DGPS_UPDATE  : // %2u 0 to 65535 sec
                    this.setEventValue(DMTPGeoEvent.FLD_gpsDgpsUpdate, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_GPS_HORZ_ACCURACY: // %1u 0 to 255 m               %2u 0.0 to 6553.5 m
                    this.setEventValue(DMTPGeoEvent.FLD_gpsHorzAccuracy, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)));
                    break;
                case PayloadTemplate.FIELD_GPS_VERT_ACCURACY: // %1u 0 to 255 m               %2u 0.0 to 6553.5 m
                    this.setEventValue(DMTPGeoEvent.FLD_gpsVertAccuracy, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)));
                    break;
                case PayloadTemplate.FIELD_GPS_SATELLITES   : // %1u 0 to 12
                    this.setEventValue(DMTPGeoEvent.FLD_gpsSatellites, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_GPS_MAG_VARIATION: // %2i -180.00 to 180.00 deg
                    this.setEventValue(DMTPGeoEvent.FLD_gpsMagVariation, (double)payload.readLong(length, 0L) / 100.0);
                    break;
                case PayloadTemplate.FIELD_GPS_QUALITY      : // %1u (0=None, 1=GPS, 2=DGPS, ...)
                    this.setEventValue(DMTPGeoEvent.FLD_gpsQuality, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_GPS_TYPE         : // %1u (1=None, 2=2D, 3=3D, ...)
                    this.setEventValue(DMTPGeoEvent.FLD_gps2D3D, payload.readULong(length, 0L));
                    break;
                case PayloadTemplate.FIELD_GPS_GEOID_HEIGHT : // %1i -128 to +127 m           %2i -3276.7 to +3276.7 m
                    this.setEventValue(DMTPGeoEvent.FLD_gpsGeoidHeight, hiRes?
                        ((double)payload.readLong(length, 0L) / 10.0) :
                        ((double)payload.readLong(length, 0L)));
                    break;
                case PayloadTemplate.FIELD_GPS_PDOP         : // %1u 0.0 to 25.5              %2u 0.0 to 99.9
                    this.setEventValue(DMTPGeoEvent.FLD_gpsPDOP, ((double)payload.readLong(length, 0L) / 10.0));
                    break;
                case PayloadTemplate.FIELD_GPS_HDOP         : // %1u 0.0 to 25.5              %2u 0.0 to 99.9
                    this.setEventValue(DMTPGeoEvent.FLD_gpsHDOP, ((double)payload.readLong(length, 0L) / 10.0));
                    break;
                case PayloadTemplate.FIELD_GPS_VDOP         : // %1u 0.0 to 25.5              %2u 0.0 to 99.9
                    this.setEventValue(DMTPGeoEvent.FLD_gpsVDOP, ((double)payload.readLong(length, 0L) / 10.0));
                    break;
                    
                case PayloadTemplate.FIELD_OBC_VALUE        : // %*b
                    this.setEventValue(DMTPGeoEvent.FLD_obcValue, payload.readBytes(length), ndx);
                    break;
                case PayloadTemplate.FIELD_OBC_GENERIC      : // %4u
                    this.setEventValue(DMTPGeoEvent.FLD_obcGeneric, payload.readULong(length, 0L), ndx);
                    break;
                case PayloadTemplate.FIELD_OBC_J1708_FAULT  : // %4u
                    this.setEventValue(DMTPGeoEvent.FLD_obcJ1708Fault, payload.readULong(length, 0L), ndx);
                    break;
                case PayloadTemplate.FIELD_OBC_DISTANCE     : // %3u 0 to 16777216 km         %3u 0.0 to 1677721.6 km
                    this.setEventValue(DMTPGeoEvent.FLD_obcDistanceKM, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)));
                    break;
                case PayloadTemplate.FIELD_OBC_ENGINE_HOURS : // %3u 0 to 1677721.6 hours
                    this.setEventValue(DMTPGeoEvent.FLD_obcEngineHours, (double)payload.readULong(length, 0L) / 10.0);
                    break;
                case PayloadTemplate.FIELD_OBC_ENGINE_RPM   : // %2u 0 to 65535 rpm
                    this.setEventValue(DMTPGeoEvent.FLD_obcEngineRPM, 
                        payload.readULong(length, 0L) );
                    break;
                case PayloadTemplate.FIELD_OBC_COOLANT_TEMP : // %1i -126 to +126 C           %2i -3276.6 to +3276.6 C
                    longVal = payload.readLong(length, INVALID_TEMPERATURE);
                    if ((length == 1) && (Math.abs(longVal) > 126)) { longVal = INVALID_TEMPERATURE; }
                    this.setEventValue(DMTPGeoEvent.FLD_obcCoolantTemp, hiRes?
                        ((double)longVal / 10.0) :
                        ((double)longVal       )  );
                    break;
                case PayloadTemplate.FIELD_OBC_COOLANT_LEVEL: // %1u 0% to 100% percent       %2u 0.0% to 100.0% percent
                    this.setEventValue(DMTPGeoEvent.FLD_obcCoolantLevel, hiRes?
                        ((double)payload.readULong(length, 0L) / 1000.0) :
                        ((double)payload.readULong(length, 0L) /  100.0)  );
                    break;
                case PayloadTemplate.FIELD_OBC_OIL_LEVEL    : // %1u 0% to 100% percent       %2u 0.0% to 100.0% percent
                    this.setEventValue(DMTPGeoEvent.FLD_obcOilLevel, hiRes?
                        ((double)payload.readULong(length, 0L) / 1000.0) :
                        ((double)payload.readULong(length, 0L) /  100.0)  );
                    break;
                case PayloadTemplate.FIELD_OBC_OIL_PRESSURE  : // %1u 0 to 255 kPa            %2u 0.0 to 6553.5 kPa
                    this.setEventValue(DMTPGeoEvent.FLD_obcOilPressure, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)       )  );
                    break;
                case PayloadTemplate.FIELD_OBC_FUEL_LEVEL    : // %1u 0% to 100% percent       %2u 0.0% to 100.0% percent
                    this.setEventValue(DMTPGeoEvent.FLD_obcFuelLevel, hiRes?
                        ((double)payload.readULong(length, 0L) / 1000.0) :
                        ((double)payload.readULong(length, 0L) /  100.0)  );
                    break;
                case PayloadTemplate.FIELD_OBC_FUEL_ECONOMY  : // %2u 0.0 to 6553.5 kpg
                    this.setEventValue(DMTPGeoEvent.FLD_obcFuelEconomy, 
                        ((double)payload.readULong(length, 0L) / 10.0) );
                    break;
                case PayloadTemplate.FIELD_OBC_FUEL_TOTAL    : // %3u 0 to 16777216 liters     %4u 0.0 to 429496729.5 liters
                    this.setEventValue(DMTPGeoEvent.FLD_obcFuelTotal, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)       ) );
                    break;
                case PayloadTemplate.FIELD_OBC_FUEL_IDLE     : // %3u 0 to 16777216 liters     %4u 0.0 to 429496729.5 liters
                    this.setEventValue(DMTPGeoEvent.FLD_obcFuelIdle, hiRes?
                        ((double)payload.readULong(length, 0L) / 10.0) :
                        ((double)payload.readULong(length, 0L)       ) );
                    break;

                default:
                    // internal error (this should not occur here - formats should be pre-validated)
                    Print.logError("Field not defined: " + StringTools.toHexString(type,8));
                    Payload p = new Payload();
                    p.writeULong(this.packet.getPacketType(), 1);
                    byte errData[] = p.getBytes();
                    throw new PacketParseException(ServerErrors.NAK_FORMAT_DEFINITION_INVALID, this.packet, errData); // formatType, fieldIndex
            }

        }
        
        /* set status code if not specified in packet */
        if (!hasStatusCode) {
            this.setEventValue(DMTPGeoEvent.FLD_statusCode, hasGeoPoint? StatusCodes.STATUS_LOCATION : StatusCodes.STATUS_NONE);
        }
        
    }

    // ------------------------------------------------------------------------
    
    /* Debug purposes: display contents of Event */
    public String toString()
    {
        return this.getGeoEvent().toString();
    }

    // ------------------------------------------------------------------------

}
