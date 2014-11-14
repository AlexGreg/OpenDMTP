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
// Description:
//  GPS event information container
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//     -Initial release
//  2006/06/26  Martin D. Flynn
//     -Changed 'lat'/'lon' to 'latitude'/'longitude'
//  2006/06/30  Martin D. Flynn
//     -Moved to "org.opendmtp.server.base"
//  2007/01/25  Martin D. Flynn
//     -Added 'getter' for FLD_tempLO, FLD_tempHI, and FLD_tempAV fields.
//     -Added fields FLD_entity, and various FLD_obcXXXXX fields
//     -Combined Latitude/Longitude into a single 'GeoPoint' field.
//     -'toString()' now prints returns the value of all fields.
//  2007/02/11  Martin D. Flynn
//     -Added FLD_obcCoolantLevel, FLD_obcOilPressure
//     -Changed FLD_obcEngineTemp to FLD_obcCoolantTemp
//  2007/02/18  Martin D. Flynn
//     -Changed FLD_obcFault to FLD_obcJ1708Fault
//  2007/02/25  Martin D. Flynn
//     -Added FLD_odometerKM
//  2007/07/13  Martin D. Flynn
//     -Added support for getSensorLow/getSensorHigh
//  2007/09/16  Martin D. Flynn
//     -Added methods getGpsAge(), getHorizontalAccuracy(), getVerticalAccuracy(),
//      getNumberOfSatellites(), getPDOP(), getHDOP(), getVDOP(), getInputId(), 
//      getInputState(), getOutputId(), getOutputState(), getElapsedTime(), getCounter(),
//      getIndex(), getString(), getBinary(), getObcDistanceKM(), getObcEngineHours(),
//      getObcEngineRPM(), getObcOilLevel(), getObcValue(), getObcGeneric()
//  2007/12/04  Martin D. Flynn
//     -Added methods getBrakeGForce().
//  2008/05/14  Martin D. Flynn
//     -Updated 'OrderedMap' method 'keys()' to 'keyIterator()'
// ----------------------------------------------------------------------------
package org.opendmtp.server.base;

import java.util.Iterator;

import org.opengts.util.*;
import org.opendmtp.codes.StatusCodes;

public class DMTPGeoEvent
{

    // ------------------------------------------------------------------------

    public static final double  GALLONS_PER_LITER       = 0.264172052;

    // ------------------------------------------------------------------------

    private static final byte  EMPTY_BYTE_ARRAY[]       = new byte[0];

    // ------------------------------------------------------------------------

    public static final String FLD_ipAddress            = "IPAddress";      // String
    public static final String FLD_dataSource           = "DataSource";     // String
    public static final String FLD_rawData              = "RawData";        // String

    public static final String FLD_statusCode           = "StatusCode";     // Long
    public static final String FLD_timestamp            = "Timestamp";      // Long
    public static final String FLD_geoPoint             = "GeoPoint";       // GeoPoint
    public static final String FLD_speedKPH             = "SpeedKPH";       // Double
    public static final String FLD_heading              = "Heading";        // Double
    public static final String FLD_altitude             = "AltitudeM";      // Double
    public static final String FLD_distanceKM           = "DistanceKM";     // Double
    public static final String FLD_odometerKM           = "OdometerKM";     // Double

    public static final String FLD_sequence             = "Sequence";       // Long
    public static final String FLD_sequenceLength       = "SeqLen";         // Long
    
    public static final String FLD_geofenceID           = "Geofence";       // Long [array]
    public static final String FLD_topSpeedKPH          = "TopSpeedKPH";    // Double
    public static final String FLD_brakeGForce          = "BrakeGForce";    // Double

    public static final String FLD_index                = "Index";          // Long

    public static final String FLD_inputID              = "InputID";        // Long
    public static final String FLD_inputState           = "InputState";     // Long
    public static final String FLD_outputID             = "OutputID";       // Long
    public static final String FLD_outputState          = "OutputState";    // Long
    public static final String FLD_elapsedTime          = "ElapsedTime";    // Long [array]
    public static final String FLD_counter              = "Counter";        // Long [array]

    public static final String FLD_sensor32LO           = "Sens32LO";       // Long [array]
    public static final String FLD_sensor32HI           = "Sens32HI";       // Long [array]
    public static final String FLD_sensor32AV           = "Sens32AV";       // Long [array]
    
    public static final String FLD_tempLO               = "TempLO";         // Double [array]
    public static final String FLD_tempHI               = "TempHI";         // Double [array]
    public static final String FLD_tempAV               = "TempAV";         // Double [array]
    
    public static final String FLD_entity               = "Entity";         // String [array]
    public static final String FLD_string               = "String";         // String [array]
    public static final String FLD_binary               = "Binary";         // Byte[] [array]

    public static final String FLD_gpsAge               = "GPSAge";         // Long
    public static final String FLD_gpsDgpsUpdate        = "GPSDgpsUpd";     // Long
    public static final String FLD_gpsHorzAccuracy      = "GPSHorzAcc";     // Double
    public static final String FLD_gpsVertAccuracy      = "GPSVertAcc";     // Double
    public static final String FLD_gpsSatellites        = "GPSSats";        // Long
    public static final String FLD_gpsMagVariation      = "GPSMagVar";      // Double
    public static final String FLD_gpsQuality           = "GPSQuality";     // Long
    public static final String FLD_gps2D3D              = "GPS2D3D";        // Long
    public static final String FLD_gpsGeoidHeight       = "GPSGeoidHt";     // Double
    public static final String FLD_gpsPDOP              = "GPSPDOP";        // Double
    public static final String FLD_gpsHDOP              = "GPSHDOP";        // Double
    public static final String FLD_gpsVDOP              = "GPSVDOP";        // Double

    public static final String FLD_obcValue             = "OBCValue";       // Byte[]
    public static final String FLD_obcGeneric           = "OBCGeneric";     // Long
    public static final String FLD_obcJ1708Fault        = "OBCJ1708Fault";  // Long
    public static final String FLD_obcDistanceKM        = "OBCDistance";    // Double
    public static final String FLD_obcEngineHours       = "OBCEngHours";    // Double
    public static final String FLD_obcEngineRPM         = "OBCEngRPM";      // Long
    public static final String FLD_obcCoolantTemp       = "OBCCoolantTemp"; // Double
    public static final String FLD_obcCoolantLevel      = "OBCCoolantLevel";// Double
    public static final String FLD_obcOilLevel          = "OBCOilLevel";    // Double
    public static final String FLD_obcOilPressure       = "OBCOilPressure"; // Double
    public static final String FLD_obcFuelLevel         = "OBCFuelLevel";   // Double
    public static final String FLD_obcFuelEconomy       = "OBCFuelEcon";    // Double
    public static final String FLD_obcFuelTotal         = "OBCFuelTotal";   // Double
    public static final String FLD_obcFuelIdle          = "OBCFuelIdle";    // Double

    // ------------------------------------------------------------------------

    private OrderedMap<String,Object> fieldMap = null;
    
    public DMTPGeoEvent()
    {
        this.fieldMap = new OrderedMap<String,Object>();
    }
    
    // ------------------------------------------------------------------------

    public Iterator keyIterator()
    {
        return this.fieldMap.keyIterator();
    }
    
    // ------------------------------------------------------------------------
    
    public void setEventValue(String fldName, Object newVal, int ndx) 
    {
        if (ndx <= 0) {
            this.fieldMap.put(fldName, newVal);
        } else {
            this.fieldMap.put(fldName + "." + ndx, newVal);
        }
    }
    public void setEventValue(String fldName, Object newVal) 
    {
        this.setEventValue(fldName, newVal, -1);
    }
    
    public void setEventValue(String fldName, long val, int ndx) 
    {
        this.setEventValue(fldName, new Long(val), ndx);
    }
    public void setEventValue(String fldName, long val) 
    {
        this.setEventValue(fldName, new Long(val), -1);
    }

    public void setEventValue(String fldName, double val, int ndx) 
    {
        this.setEventValue(fldName, new Double(val), ndx);
    }
    public void setEventValue(String fldName, double val) 
    {
        this.setEventValue(fldName, new Double(val), -1);
    }

    // ------------------------------------------------------------------------

    public Object getEventValue(String fldName, int ndx)
    {
        String fn = (ndx <= 0)? fldName : (fldName + "." + ndx);
        return this.fieldMap.get(fn); // may return null
    }
    public Object getEventValue(String fldName)
    {
        return this.fieldMap.get(fldName); // may return null
    }

    // ------------------------------------------------------------------------

    public String getStringValue(String fldName, String dft, int ndx)
    {
        Object val = this.getEventValue(fldName, ndx);
        if (val instanceof byte[]) {
            return "0x" + StringTools.toHexString((byte[])val);
        } else
        if (val != null) {
            return val.toString();
        } else {
            return dft;
        }
    }
    public String getStringValue(String fldName, String dft)
    {
        return this.getStringValue(fldName, dft, -1);
    }

    public byte[] getByteValue(String fldName, byte[] dft, int ndx)
    {
        Object val = this.getEventValue(fldName, ndx);
        if (val instanceof byte[]) {
            return (byte[])val;
        } else {
            return dft;
        }
    }
    public byte[] getByteValue(String fldName, byte[] dft)
    {
        return this.getByteValue(fldName, dft, -1);
    }

    public long getLongValue(String fldName, long dft, int ndx)
    {
        Object val = this.getEventValue(fldName, ndx);
        if (val instanceof Number) {
            return ((Number)val).longValue();
        } else {
            return dft;
        }
    }
    public long getLongValue(String fldName, long dft)
    {
        return this.getLongValue(fldName, dft, -1);
    }
    
    public double getDoubleValue(String fldName, double dft, int ndx)
    {
        Object val = this.getEventValue(fldName, ndx);
        if (val instanceof Number) {
            return ((Number)val).doubleValue();
        } else {
            return dft;
        }
    }
    public double getDoubleValue(String fldName, double dft)
    {
        return this.getDoubleValue(fldName, dft, -1);
    }
    
    // ------------------------------------------------------------------------

    public GeoPoint getGeoPointValue(String fldName, GeoPoint dft, int ndx)
    {
        Object val = this.getEventValue(fldName, ndx);
        if (val instanceof GeoPoint) {
            return (GeoPoint)val;
        } else {
            return dft;
        }
    }
    public GeoPoint getGeoPointValue(String fldName, GeoPoint dft)
    {
        return this.getGeoPointValue(fldName, dft, -1);
    }
    public GeoPoint getGeoPoint(int ndx)
    {
        return this.getGeoPointValue(FLD_geoPoint, null, ndx);
    }
    
    public double getLatitude(int ndx)
    {
        GeoPoint gp = this.getGeoPoint(ndx);
        return (gp != null)? gp.getLatitude() : 0.0;
    }
    
    public double getLongitude(int ndx)
    {
        GeoPoint gp = this.getGeoPoint(ndx);
        return (gp != null)? gp.getLongitude() : 0.0;
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    public String getIPAddress()
    {
        return this.getStringValue(FLD_ipAddress, "");
    }

    public String getDataSource()
    {
        return this.getStringValue(FLD_dataSource, "");
    }

    // ------------------------------------------------------------------------

    public int getStatusCode()
    {
        return (int)this.getLongValue(FLD_statusCode, -1L);
    }

    public long getTimestamp()
    {
        return this.getLongValue(FLD_timestamp, -1L);
    }

    // ------------------------------------------------------------------------
    
    public GeoPoint getGeoPoint()
    {
        return this.getGeoPointValue(FLD_geoPoint, null);
    }

    public double getSpeed()
    {
        return this.getDoubleValue(FLD_speedKPH, 0.0);
    }

    public double getHeading()
    {
        return this.getDoubleValue(FLD_heading, 0.0);
    }

    public double getAltitude()
    {
        return this.getDoubleValue(FLD_altitude, 0.0);
    }

    public double getDistance()
    {
        return this.getDoubleValue(FLD_distanceKM, 0.0);
    }

    public double getOdometer()
    {
        return this.getDoubleValue(FLD_odometerKM, 0.0);
    }

    public double getTopSpeed()
    {
        return this.getDoubleValue(FLD_topSpeedKPH, 0.0);
    }

    public double getBrakeGForce()
    {
        return this.getDoubleValue(FLD_brakeGForce, 0.0);
    }

    public long getGeofence(int ndx)
    {
        return this.getLongValue(FLD_geofenceID, 0L, ndx);
    }

    // ------------------------------------------------------------------------

    public long getGpsAge()
    {
        return this.getLongValue(FLD_gpsAge, 0L);
    }

    public double getHorizontalAccuracy()
    {
        return this.getDoubleValue(FLD_gpsHorzAccuracy, 0.0);
    }

    public double getVerticalAccuracy()
    {
        return this.getDoubleValue(FLD_gpsVertAccuracy, 0.0);
    }

    public long getNumberOfSatellites()
    {
        return this.getLongValue(FLD_gpsSatellites, 0L);
    }

    public double getPDOP()
    {
        return this.getDoubleValue(FLD_gpsPDOP, 0.0);
    }

    public double getHDOP()
    {
        return this.getDoubleValue(FLD_gpsHDOP, 0.0);
    }

    public double getVDOP()
    {
        return this.getDoubleValue(FLD_gpsVDOP, 0.0);
    }

    // ------------------------------------------------------------------------

    public long getIndex()
    {
        return this.getLongValue(FLD_index, 0L);
    }

    // ------------------------------------------------------------------------

    public long getInputId()
    {
        return this.getLongValue(FLD_inputID, 0L);
    }

    public long getInputState()
    {
        return this.getLongValue(FLD_inputState, 0L);
    }

    public long getOutputId()
    {
        return this.getLongValue(FLD_outputID, 0L);
    }

    public long getOutputState()
    {
        return this.getLongValue(FLD_outputState, 0L);
    }

    public long getElapsedTime(int ndx)
    {
        return this.getLongValue(FLD_elapsedTime, 0L, ndx);
    }

    public long getCounter(int ndx)
    {
        return this.getLongValue(FLD_counter, 0L, ndx);
    }

    // ------------------------------------------------------------------------

    public long getSensorLow(int ndx)
    {
        return this.getLongValue(FLD_sensor32LO, 0L, ndx);
    }
    
    public long getSensorHigh(int ndx)
    {
        return this.getLongValue(FLD_sensor32HI, 0L, ndx);
    }

    // ------------------------------------------------------------------------

    public double getTemeratureLow(int ndx)
    {
        return this.getDoubleValue(FLD_tempLO, -9999.0, ndx);
    }
    
    public double getTemeratureHigh(int ndx)
    {
        return this.getDoubleValue(FLD_tempHI, -9999.0, ndx);
    }

    public double getTemeratureAverage(int ndx)
    {
        return this.getDoubleValue(FLD_tempAV, -9999.0, ndx);
    }

    // ------------------------------------------------------------------------

    public String getEntity(int ndx)
    {
        return this.getStringValue(FLD_entity, "", ndx);
    }

    public String getString(int ndx)
    {
        return this.getStringValue(FLD_string, "", ndx);
    }

    public byte[] getBinary(int ndx)
    {
        return this.getByteValue(FLD_binary, EMPTY_BYTE_ARRAY, ndx);
    }

    // ------------------------------------------------------------------------

    public byte[] getObcValue(int ndx)
    {
        return this.getByteValue(FLD_obcValue, EMPTY_BYTE_ARRAY, ndx);
    }

    public long getObcGeneric(int ndx)
    {
        return this.getLongValue(FLD_obcGeneric, 0L, ndx);
    }

    public long getObcJ1708Fault(int ndx)
    {
        return this.getLongValue(FLD_obcJ1708Fault, 0L, ndx);
    }

    public double getObcDistanceKM()
    {
        return this.getDoubleValue(FLD_obcDistanceKM, 0.0);
    }

    public double getObcEngineHours()
    {
        return this.getDoubleValue(FLD_obcEngineHours, 0.0);
    }

    public long getObcEngineRPM()
    {
        return this.getLongValue(FLD_obcEngineRPM, 0L);
    }

    public double getObcCoolantLevel()
    {
        return this.getDoubleValue(FLD_obcCoolantLevel, 0.0);
    }

    public double getObcCoolantTemperature()
    {
        return this.getDoubleValue(FLD_obcCoolantTemp, 0.0);
    }

    public double getObcOilLevel()
    {
        return this.getDoubleValue(FLD_obcOilLevel, 0.0);
    }

    public double getObcOilPressure()
    {
        return this.getDoubleValue(FLD_obcOilPressure, 0.0);
    }

    public double getObcFuelLevel()
    {
        return this.getDoubleValue(FLD_obcFuelLevel, 0.0);
    }

    public double getObcFuelEconomy()
    {
        return this.getDoubleValue(FLD_obcFuelEconomy, 0.0);
    }

    public double getObcFuelTotal()
    {
        return this.getDoubleValue(FLD_obcFuelTotal, 0.0);
    }

    public double getObcFuelIdle()
    {
        return this.getDoubleValue(FLD_obcFuelIdle, 0.0);
    }

    // ------------------------------------------------------------------------

    public long getSequence()
    {
        return this.getLongValue(FLD_sequence, -1L);
    }
    
    public int getSequenceLength()
    {
        return (int)this.getLongValue(FLD_sequenceLength, 0L);
    }

    // ------------------------------------------------------------------------

    public String getRawData()
    {
        return this.getStringValue(FLD_rawData, "");
    }

    // ------------------------------------------------------------------------
    
    private String _getEventValueString(String key, Object val)
    {
        if (key == null) {
            return null;
        } else 
        if (key.startsWith(FLD_statusCode)) {
            if (val instanceof Number) {
                StringBuffer sb = new StringBuffer();
                int n = ((Number)val).intValue();
                String code = StringTools.toHexString(n,16);
                sb.append("[0x").append(code).append("] ");
                sb.append(StatusCodes.GetCodeDescription(n));
                return sb.toString();
            }
        } else 
        if (key.startsWith(FLD_timestamp)) {
            if (val instanceof Number) {
                StringBuffer sb = new StringBuffer();
                long n = ((Number)val).longValue();
                sb.append("[").append(n).append("] ");
                sb.append((new DateTime(n)).toString());
                return sb.toString();
            }
        } else 
        if (key.startsWith(FLD_speedKPH)) {
            if (val instanceof Number) {
                StringBuffer sb = new StringBuffer();
                double n = ((Number)val).doubleValue();
                sb.append("[");
                sb.append(StringTools.format(n,"#0.0"));
                sb.append(" kph] ");
                sb.append(StringTools.format(n*GeoPoint.MILES_PER_KILOMETER,"#0.0"));
                sb.append(" mph");
                return sb.toString();
            }
        } else 
        if (key.startsWith(FLD_altitude)) {
            if (val instanceof Number) {
                StringBuffer sb = new StringBuffer();
                double n = ((Number)val).doubleValue();
                sb.append("[");
                sb.append(StringTools.format(n,"#0.0"));
                sb.append(" meters] ");
                sb.append(StringTools.format(n*GeoPoint.FEET_PER_METER,"#0.0"));
                sb.append(" feet");
                return sb.toString();
            }
        } else 
        if (key.startsWith(FLD_distanceKM)   || 
            key.startsWith(FLD_odometerKM)   || 
            key.startsWith(FLD_obcDistanceKM)  ) {
            if (val instanceof Number) {
                StringBuffer sb = new StringBuffer();
                double n = ((Number)val).doubleValue();
                sb.append("[");
                sb.append(StringTools.format(n,"#0.0"));
                sb.append(" km] ");
                sb.append(StringTools.format(n*GeoPoint.MILES_PER_KILOMETER,"#0.0"));
                sb.append(" miles");
                return sb.toString();
            }
        } else 
        if (key.startsWith(FLD_sensor32LO) ||
            key.startsWith(FLD_sensor32HI)   ) { 
            if (val instanceof Number) {
                StringBuffer sb = new StringBuffer();
                long n = ((Number)val).longValue();
                sb.append("[0x").append(StringTools.toHexString(n,32)).append("] ");
                sb.append(n);
                return sb.toString();
             }
        } else 
        if (key.startsWith(FLD_tempLO) ||
            key.startsWith(FLD_tempHI) ||
            key.startsWith(FLD_tempAV)   ) { 
            if (val instanceof Number) {
                StringBuffer sb = new StringBuffer();
                double n = ((Number)val).doubleValue();
                sb.append("[");
                sb.append(StringTools.format(n,"#0.0"));
                sb.append(" C] ");
                sb.append(StringTools.format((n*(9.0/5.0))+32.0,"#0.0"));
                sb.append(" F");
                return sb.toString();
             }
        } else 
        if (key.startsWith(FLD_obcFuelTotal) ||
            key.startsWith(FLD_obcFuelIdle)) {
            StringBuffer sb = new StringBuffer();
            double liters = ((Number)val).doubleValue();
            sb.append("[");
            sb.append(StringTools.format(liters,"#0.0"));
            sb.append(" liters] ");
            sb.append(StringTools.format(liters * GALLONS_PER_LITER,"#0.0"));
            sb.append(" gallons");
            return sb.toString();
        } else 
        if (key.startsWith(FLD_obcJ1708Fault)) {
            //  0: 8 - MID
            //  9:16 - PID/SID [0x8000==SID, 0x0100==ExtendedPID]
            // 25: 8 - Fault (FMI)
            if (val instanceof Number) {
                // [0x80016201] MID=128 PID=354[92] FMI=1
                StringBuffer sb = new StringBuffer();
                long n = ((Number)val).longValue();
                int mid    = (int)((n >> 24) & 0xFF);
                int pidSid = (int)((n >>  8) & 0xFFFF);
                int fmi    = (int)((n >>  0) & 0xFF);
                sb.append("[0x").append(StringTools.toHexString(n,32)).append("] ");
                if (n != 0L) {
                    sb.append("MID=").append(mid).append(" ");
                    if ((pidSid & 0x8000) != 0) {
                        sb.append("SID=").append(pidSid & 0x0FFF);
                        sb.append(" ");
                    } else {
                        sb.append("PID=").append(pidSid & 0x0FFF);
                        sb.append("[").append(pidSid & 0x00FF).append("]");
                        sb.append(" ");
                    }
                    sb.append("FMI=").append(fmi);
                }
                return sb.toString();
            }
        }
        return null;
    }

    /* Debug purposes: display contents */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Event:\n");
        DMTPGeoEvent gev = this;

        /* list all fields we have */
        Iterator keyIter = gev.keyIterator();
        for (;keyIter.hasNext();) {

            /* include key name */
            String key = (String)keyIter.next();
            sb.append("  ").append(key);
            sb.append(StringTools.replicateString(" ", 14-key.length()));
            sb.append(": ");
            
            /* include key value */
            Object val = gev.getEventValue(key); // 'ndx' is already included in the key
            String valStr = this._getEventValueString(key, val);
            if (valStr != null) {
                sb.append(valStr);
            } else {
                if (val instanceof Long) {
                    long n = ((Number)val).longValue();
                    String ns;
                    if ((n & 0xFFFFFFFF00000000L) != 0L) {
                        ns = StringTools.toHexString(n,64);
                    } else
                    if ((n & 0xFFFFFFFFFFFF0000L) != 0L) {
                        ns = StringTools.toHexString(n,32);
                    } else {
                        ns = StringTools.toHexString(n,16);
                    }
                    sb.append("[0x").append(ns).append("] ");
                    sb.append(n);
                } else
                if (val instanceof Double) {
                    double n = ((Number)val).doubleValue();
                    sb.append(StringTools.format(n,"#0.00"));
                } else 
                if (val instanceof byte[]) {
                    byte b[] = (byte[])val;
                    sb.append("0x");
                    sb.append(StringTools.toHexString(b));
                } else
                if (val instanceof GeoPoint) {
                    sb.append(val.toString());
                } else
                if (val instanceof String) {
                    sb.append((String)val);
                } else {
                    sb.append("? ");
                    sb.append(val.toString());
                }
            }

            /* new line */
            sb.append("\n");

        }

        return sb.toString();
    }

    // ------------------------------------------------------------------------

}
