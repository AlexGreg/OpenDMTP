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
//  Read/Write binary fields
// ----------------------------------------------------------------------------
// Change History:
//  2006/03/26  Martin D. Flynn
//     -Initial release
//  2007/02/25  Martin D. Flynn
//     -Made 'decodeLong'/'encodeLong' public.
//     -Moved to 'org.opengts.util'.
//  2007/03/11  Martin D. Flynn
//     -Added check for remaining available read/write bytes
//  2008/02/04  Martin D. Flynn
//     -Added 'encodeDouble'/'decodeDouble' methods for encoding and decoding
//      32-bit and 64-bit IEEE 754 floating-point values.
//     -Added Big/Little-Endian flag
//     -Added 'writeZeroFill' method
//     -Fixed 'writeBytes' to proper blank fill written fields
//  2009/09/23  Martin D. Flynn
//     -Added methods "peekByte", "saveIndex", "restoreIndex"
// ----------------------------------------------------------------------------
package org.opengts.util;

import java.lang.*;
import java.util.*;

/**
*** For reading/writing binary fields
**/

public class Payload
{
    
    // ------------------------------------------------------------------------

    public  static final int        DEFAULT_MAX_PAYLOAD_LENGTH = 255;
    
    public  static final byte       EMPTY_BYTE_ARRAY[] = new byte[0];
    
    private static final boolean    DEFAULT_BIG_ENDIAN = true;

    // ------------------------------------------------------------------------

    private byte        payload[] = null;
    private int         size = 0;

    private int         index = 0;
    private int         indexSnapshot = -1;

    private boolean     bigEndian = DEFAULT_BIG_ENDIAN;

    // ------------------------------------------------------------------------

    /**
    *** Destination Constructor
    **/
    public Payload()
    {
        // DESTINATION: configure for creating a new binary payload
        this(DEFAULT_MAX_PAYLOAD_LENGTH, DEFAULT_BIG_ENDIAN);
    }

    /**
    *** Destination Constructor
    *** @param maxPayloadLen The maximum payload length
    **/
    public Payload(int maxPayloadLen)
    {
        // DESTINATION: configure for creating a new binary payload
        this(maxPayloadLen, DEFAULT_BIG_ENDIAN);
    }

    /**
    *** Destination Constructor
    *** @param maxPayloadLen The maximum payload length
    *** @param bigEndian If the payload uses big-endian byte ordering
    **/
    public Payload(int maxPayloadLen, boolean bigEndian)
    {
        // DESTINATION: configure for creating a new binary payload
        this.payload = new byte[maxPayloadLen];
        this.size    = 0; // no 'size' yet
        this.index   = 0; // start at index '0' for writing
        this.setBigEndian(bigEndian);
    }

    // ------------------------------------------------------------------------

    /**
    *** Source Constuctor
    *** @param b The payload (default big-endian byte ordering)
    **/
    public Payload(byte b[])
    {
        // SOURCE: configure for reading a binary payload
        this(b, DEFAULT_BIG_ENDIAN);
    }

    /**
    *** Source Constuctor
    *** @param b The payload
    *** @param bigEndian If the payload uses big-endian byte ordering
    **/
    public Payload(byte b[], boolean bigEndian)
    {
        // SOURCE: configure for reading a binary payload
        this(b, 0, ((b != null)? b.length : 0), bigEndian);
    }

    /**
    *** Source Constuctor
    *** @param b The byte array to copy the payload from (defalt big-endian byte ordering
    *** @param ofs The offset at which copying of <code>b</code> should begin
    *** @param len The length of the resultant payload
    **/
    public Payload(byte b[], int ofs, int len)
    {
        this(b, ofs, len, DEFAULT_BIG_ENDIAN);
    }
    
    /**
    *** Source Constuctor
    *** @param b The byte array to copy the payload from (defalt big-endian byte ordering
    *** @param ofs The offset at which copying of <code>b</code> should begin
    *** @param len The length of the resultant payload
    *** @param bigEndian If the payload uses big-endian byte ordering
    **/
    public Payload(byte b[], int ofs, int len, boolean bigEndian)
    {
        // SOURCE: configure for reading a binary payload
        this();
        if ((b == null) || (ofs >= b.length)) {
            this.payload = new byte[0];
            this.size    = 0;
            this.index   = 0;
        } else
        if ((ofs == 0) && (b.length == len)) {
            this.payload = b;
            this.size    = b.length;
            this.index   = 0;
        } else {
            if (len > (b.length - ofs)) { len = b.length - ofs; }
            this.payload = new byte[len];
            System.arraycopy(b, ofs, this.payload, 0, len);
            this.size    = len;
            this.index   = 0;
        }
        this.setBigEndian(bigEndian);
    }
    
    // ------------------------------------------------------------------------

    /**
    *** Sets the byte ordering of the payload
    *** @param bigEndFirst True for big-endian, false for little-endian numeric encoding
    **/
    public void setBigEndian(boolean bigEndFirst)
    {
        this.bigEndian = bigEndFirst;
    }
    
    /**
    *** Gets the byte ordering of the payload
    *** @return True if big-endian, false if little-endian
    **/
    public boolean getBigEndian()
    {
        return this.bigEndian;
    }

    /**
    *** Returns true if the payload is big-endian
    *** @return True if big-endian, false if little-endian
    **/
    public boolean isBigEndian()
    {
        return this.bigEndian;
    }
    
    // ------------------------------------------------------------------------

    /**
    *** For an output/write Payload, returns the number of bytes written.
    *** For an input/read Payload, return the total number of bytes contained in this Payload.
    *** @return The current size of the payload
    **/
    public int getSize()
    {
        return this.size;
    }
    
    /**
    *** Resets the payload to an empty state
    **/
    public void clear()
    {
        this.size  = 0;
        this.index = 0;
    }
    
    // ------------------------------------------------------------------------

    /**
    *** Return the backing byte array (as-is)
    *** @return The backing byte array
    **/
    private byte[] _getBytes()
    {
        return this.payload;
    }
    
    /**
    *** Return a byte array representing the data currently in the payload (may be a copy)
    *** @return The byte array currently in the payload (as-is)
    **/
    public byte[] getBytes()
    {
        // return the full payload (regardless of the state of 'this.index')
        byte b[] = this._getBytes();
        if (this.size == b.length) {
            return b;
        } else {
            byte n[] = new byte[this.size];
            System.arraycopy(b, 0, n, 0, this.size);
            return n;
        }
    }
    
    // ------------------------------------------------------------------------

    /**
    *** Gets the current read/write index
    *** return@ The index
    **/
    public int getIndex()
    {
        return this.index;
    }
    
    /**
    *** Resets the read/write index to '<code>0</code>'
    **/
    public void resetIndex()
    {
        // this makes Payload a data source
        this.resetIndex(0);
    }

    /**
    *** Resets the read/write index to the specified value
    *** @param ndx The value to set the index
    **/
    public void resetIndex(int ndx)
    {
        this.index = (ndx <= 0)? 0 : ndx;
    }

    /**
    *** Saves the current index.
    *** @return True if the operation was successful
    **/
    public boolean saveIndex()
    {
        this.indexSnapshot = this.getIndex();
        return true;
    }
    
    /**
    *** Restores the current index.
    *** @return True if the operation was successful
    **/
    public boolean restoreIndex()
    {
        if (this.indexSnapshot >= 0) {
            this.resetIndex(this.indexSnapshot);
            return true;
        } else {
            return false;
        }
    }

    // ------------------------------------------------------------------------

    /**
    *** Gets the number of remaining available read to read
    *** @return The number of availible bytes to read
    **/
    public int getAvailableReadLength()
    {
        return (this.size - this.index);
    }

    /**
    *** Gets the nubmer of remaining availible bytes to write
    *** @return The remaining available bytes to write
    **/
    public int getAvailableWriteLength()
    {
        byte b[] = this._getBytes();
        return (b.length - this.index);
    }

    /**
    *** Returns true if there are at least <code>length</code> bytes that 
    *** can be read from the payload
    *** @return True if there at least <code>length</code> readable bytes
    ***         in the payload
    **/
    public boolean isValidReadLength(int length)
    {
        return ((this.index + length) <= this.size);
    }
    
    /**
    *** Returns true if there are at least <code>length</code> bytes that 
    *** can be writen to the payload
    *** @return True if there at least <code>length</code> writeable bytes
    ***         in the payload
    **/
    public boolean isValidWriteLength(int length)
    {
        byte b[] = this._getBytes();
        return ((this.index + length) <= b.length);
    }

    /**
    *** Returns true if there are bytes available for reading
    *** @return True if there are bytes availible for reading
    **/
    public boolean hasAvailableRead()
    {
        return (this.getAvailableReadLength() > 0);
    }

    /**
    *** Returns true if there are bytes available for writing
    **/
    public boolean hasAvailableWrite()
    {
        return (this.getAvailableWriteLength() > 0);
    }

    // ------------------------------------------------------------------------

    /**
    *** Skip a specified number of bytes
    *** @param length The number of bytes to skip
    **/
    public void readSkip(int length)
    {
        int maxLen = ((this.index + length) <= this.size)? length : (this.size - this.index);
        if (maxLen <= 0) {
            // nothing to skip
            return;
        } else {
            this.index += maxLen;
            return;
        }
    }
    
    /**
    *** Skip a specified number of bytes
    *** @param length The number of bytes to skip
    **/
    public void skip(int length)
    {
        this.readSkip(length);
    }
    
    // ------------------------------------------------------------------------

    /**
    *** Read <code>length</code< of bytes from the payload
    *** @param length The number fo bytes to read from the payload
    **/
    public byte[] readBytes(int length)
    {
        // This will read 'length' bytes, or the remaining bytes, whichever is less
        int maxLen = ((length >= 0) && ((this.index + length) <= this.size))? length : (this.size - this.index);
        if (maxLen <= 0) {
            // no room left
            return new byte[0];
        } else {
            byte n[] = new byte[maxLen];
            System.arraycopy(this._getBytes(), this.index, n, 0, maxLen);
            this.index += maxLen;
            return n;
        }
    }
    
    // ------------------------------------------------------------------------

    /**
    *** Read the next byte without moving the read pointer
    *** @return The next byte (-1 if no bytes are available)
    **/
    public int peekByte()
    {
        if (this.index < this.size) {
            return (int)this._getBytes()[this.index] & 0xFF;
        } else {
            return -1;
        }
    }

    // ------------------------------------------------------------------------

    /**
    *** Decodes a <code>long</code> value from bytes
    *** @param data The byte array to decode the value from
    *** @param ofs The offset into <code>data</code> to start decoding from
    *** @param len The number of bytes to decode the value from
    *** @param bigEndian True if the bytes are big-endian ordered, false if little-endian
    *** @param signed If the encoded bytes represent a signed value
    *** @param dft The default value if a value cannot be decoded
    *** @return The decoded value, or the default
    **/
    public static long decodeLong(byte data[], int ofs, int len, boolean bigEndian, boolean signed, long dft)
    {
        if ((data != null) && (data.length >= (ofs + len))) {
            if (bigEndian) {
                // Big-Endian order
                // { 0x01, 0x02, 0x03 } -> 0x010203
                long n = (signed && ((data[ofs] & 0x80) != 0))? -1L : 0L;
                for (int i = ofs; i < ofs + len; i++) {
                    n = (n << 8) | ((long)data[i] & 0xFF); 
                }
                return n;
            } else {
                // Little-Endian order
                // { 0x01, 0x02, 0x03 } -> 0x030201
                long n = (signed && ((data[ofs + len - 1] & 0x80) != 0))? -1L : 0L;
                for (int i = ofs + len - 1; i >= ofs; i--) {
                    n = (n << 8) | ((long)data[i] & 0xFF); 
                }
                return n;
            }
        } else {
            return dft;
        }
    }

    /**
    *** Read a <code>long</code> value from payload (with default)
    *** @param length The number of bytes to decode the value from
    *** @param dft The default value if a value could not be decoded
    *** @return The decoded value, or the default value
    **/
    public long readLong(int length, long dft)
    {
        int maxLen = ((this.index + length) <= this.size)? length : (this.size - this.index);
        if (maxLen <= 0) {
            // nothing to read
            return dft;
        } else {
            byte b[] = this._getBytes();
            long val = Payload.decodeLong(b, this.index, maxLen, this.bigEndian, true, dft);
            this.index += maxLen;
            return val;
        }
    }

    /**
    *** Read a <code>long</code> value from payload
    *** @param length The number of bytes to decode the value from
    *** @return The decoded value
    **/
    public long readLong(int length)
    {
        return this.readLong(length, 0L);
    }
    
    // ------------------------------------------------------------------------

    /**
    *** Read an unsigned <code>long</code> value from payload (with default)
    *** @param length The number of bytes to decode the value from
    *** @param dft The default value if a value could not be decoded
    *** @return The decoded value, or the default value
    **/
    public long readULong(int length, long dft)
    {
        int maxLen = ((this.index + length) <= this.size)? length : (this.size - this.index);
        if (maxLen <= 0) {
            // nothing to read
            return dft;
        } else {
            byte b[] = this._getBytes();
            long val = Payload.decodeLong(b, this.index, maxLen, this.bigEndian, false, dft);
            this.index += maxLen;
            return val;
        }
    }

    /**
    *** Read an unsigned <code>long</code> value from payload
    *** @param length The number of bytes to decode the value from
    *** @return The decoded value
    **/
    public long readULong(int length)
    {
        return this.readULong(length, 0L);
    }

    // ------------------------------------------------------------------------
    
    /**
    *** Decodes a <code>double</code> value from bytes, using IEEE 754 format
    *** @param data The byte array from which to decode the <code>double</code> value
    *** @param ofs The offset into <code>data</code> to start decoding
    *** @param len The number of bytes from which the value is decoded
    *** @param bigEndian True if the bytes are in big-endian order, false if little-endian
    *** @param dft The default value if a value cannot be decoded
    *** @return The decoded value, or the default
    **/
    public static double decodeDouble(byte data[], int ofs, int len, boolean bigEndian, double dft)
    {
        // 'len' must be at lest 4
        if ((data != null) && (len >= 4) && (data.length >= (ofs + len))) {
            int flen = (len >= 8)? 8 : 4;
            long n = 0L;
            if (bigEndian) {
                // Big-Endian order
                // { 0x01, 0x02, 0x03, 0x04 } -> 0x01020304
                for (int i = ofs; i < ofs + flen; i++) {
                    n = (n << 8) | ((long)data[i] & 0xFF);
                }
            } else {
                // Little-Endian order
                // { 0x01, 0x02, 0x03, 0x04 } -> 0x04030201
                for (int i = ofs + flen - 1; i >= ofs; i--) {
                    n = (n << 8) | ((long)data[i] & 0xFF);
                }
            }
            if (flen == 8) {
                //Print.logInfo("Decoding 64-bit float " + n);
                return Double.longBitsToDouble(n);
            } else {
                //Print.logInfo("Decoding 32-bit float " + n);
                return (double)Float.intBitsToFloat((int)n);
            }
        } else {
            return dft;
        }
    }

    /**
    *** Read a <code>double</code> value from payload (with default), using IEEE 754 format
    *** @param length The number of bytes from which the value is decoded
    *** @param dft The default value if a value could not be decoded
    *** @return The decoded value, or the default value
    **/
    public double readDouble(int length, double dft)
    {
        // 'length' must be at least 4
        int maxLen = ((this.index + length) <= this.size)? length : (this.size - this.index);
        if (maxLen <= 0) {
            // nothing to read
            return dft;
        } else {
            byte b[] = this._getBytes();
            double val = Payload.decodeDouble(b, this.index, maxLen, this.bigEndian, dft);
            this.index += maxLen;
            return val;
        }
    }

    /**
    *** Read a <code>double</code> value from payload, using IEEE 754 format
    *** @param length The number of bytes from which the value is decoded
    *** @return The decoded value
    **/
    public double readDouble(int length)
    {
        // 'length' must be at least 4
        return this.readDouble(length, 0.0);
    }

    // ------------------------------------------------------------------------

    /**
    *** Reads a variable length string from the payload
    *** @param length The maximum length of the string to read
    *** @return The read String
    *** @see #readString(int length, boolean varLength)
    **/
    public String readString(int length)
    {
        return this.readString(length, true);
    }
    
    /**
    *** Read a string from the payload.
    *** The string is read until (whichever comes first):
    *** <ol><li><code>length</code> bytes have been read</li>
    ***     <li>a null (0x00) byte is found (if <code>varLength==true</code>)</li>
    ***     <li>end of data is reached</li></ol>
    *** @param length The maximum length to read
    *** @param varLength If the string can be variable in length (stop on a null)
    *** @return The read String
    **/
    public String readString(int length, boolean varLength)
    {
        // Read until (whichever comes first):
        //  1) length bytes have been read
        //  2) a null (0x00) byte is found (if 'varLength==true')
        //  3) end of data is reached
        int maxLen = ((this.index + length) <= this.size)? length : (this.size - this.index);
        if (maxLen <= 0) {
            // no room left
            return "";
        } else {
            int m;
            byte b[] = this._getBytes();
            if (varLength) {
                // look for the end-of-data, or a terminating null (0x00)
                for (m = 0; (m < maxLen) && ((this.index + m) < this.size) && (b[this.index + m] != 0); m++);
            } else {
                // look for end of data only
                m = ((this.index + maxLen) < this.size)? maxLen : (this.size - this.index);
            }
            String s = StringTools.toStringValue(b, this.index, m);
            this.index += m;
            if (m < maxLen) { this.index++; }
            return s;
        }
    }
    
    // ------------------------------------------------------------------------

    /**
    *** Reads an encoded GPS point (latitude,longitude) from the payload
    *** @param length The number of bytes to decode the GeoPoint from
    *** @return The decoded GeoPoint
    *** @see GeoPoint#decodeGeoPoint
    *** @see GeoPoint#encodeGeoPoint
    **/
    public GeoPoint readGPS(int length)
    {
        int maxLen = ((this.index + length) <= this.size)? length : (this.size - this.index);
        if (maxLen < 6) {
            // not enough bytes to decode GeoPoint
            GeoPoint gp = new GeoPoint();
            if (maxLen > 0) { this.index += maxLen; }
            return gp;
        } else
        if (length < 8) {
            // 6 <= len < 8
            GeoPoint gp = GeoPoint.decodeGeoPoint(this._getBytes(), this.index, length);
            this.index += maxLen; // 6
            return gp;
        } else {
            // 8 <= len
            GeoPoint gp = GeoPoint.decodeGeoPoint(this._getBytes(), this.index, length);
            this.index += maxLen; // 8
            return gp;
        }
    }

    // ------------------------------------------------------------------------

    /**
    *** Encodes a <code>long</code> value into bytes
    *** @param data The byte array to encode the value to
    *** @param ofs The offset into <code>data</code> to start encoding to
    *** @param len The number of bytes to encode the value to
    *** @param bigEndian True if the bytes are to be big-endian ordered, 
    ***        false if little-endian
    *** @param val The value to encode
    *** @return The number of bytes writen to <code>data</data>. 0 or 
    ***         <code>len</code>
    **/
    public static int encodeLong(byte data[], int ofs, int len, boolean bigEndian, long val)
    {
        if ((data != null) && (data.length >= (ofs + len))) {
            long n = val;
            if (bigEndian) {
                // Big-Endian order
                for (int i = (ofs + len - 1); i >= ofs; i--) {
                    data[i] = (byte)(n & 0xFF);
                    n >>>= 8;
                }
            } else {
                // Little-Endian order
                for (int i = ofs; i < ofs + len; i++) {
                    data[i] = (byte)(n & 0xFF);
                    n >>>= 8;
                }
            }
            return len;
        } else {
            return 0;
        }
    }

    /**
    *** Write a <code>long</code> value to the payload
    *** @param val The value to write
    *** @param wrtLen The number of bytes to write the value into
    *** @return The number of bytes written
    **/
    public int writeLong(long val, int wrtLen)
    {

        /* check for nothing to write */
        if (wrtLen <= 0) {
            // nothing to write
            return 0;
        }

        /* write float/double */
        byte b[] = this._getBytes();
        int maxLen = ((this.index + wrtLen) <= b.length)? wrtLen : (b.length - this.index);
        if (maxLen < wrtLen) {
            // not enough bytes to encode long
            return 0;
        }

        /* write long */
        Payload.encodeLong(b, this.index, maxLen, this.bigEndian, val);
        this.index += maxLen;
        if (this.size < this.index) { this.size = this.index; }
        return maxLen;
        
    }

    /**
    *** Write a unsigned <code>long</code> value to the payload.
    *** Same as writeLong()
    *** @param val The value to write
    *** @param length The number of bytes to write the value into
    *** @return The number of bytes written
    *** @see #writeLong
    **/
    public int writeULong(long val, int length)
    {
        return this.writeLong(val, length);
    }

    // ------------------------------------------------------------------------

    /**
    *** Encodes a <code>double</code> value into bytes
    *** @param data The byte array to encode the value to
    *** @param ofs The offset into <code>data</code> to start encoding to
    *** @param len The number of bytes to encode the value to
    *** @param bigEndian True if the bytes are to be big-endian ordered, 
    ***        false if little-endian
    *** @param val The value to encode
    *** @return The number of bytes writen to <code>data</data>. 0 or 
    ***         <code>len</code>
    **/
    public static int encodeDouble(byte data[], int ofs, int len, boolean bigEndian, double val)
    {
        // 'len' must be at least 4
        if ((data != null) && (len >= 4) && (data.length >= (ofs + len))) {
            int flen = (len >= 8)? 8 : 4;
            long n = (flen == 8)? Double.doubleToRawLongBits(val) : (long)Float.floatToRawIntBits((float)val);
            if (bigEndian) {
                // Big-Endian order
                for (int i = (ofs + flen - 1); i >= ofs; i--) {
                    data[i] = (byte)(n & 0xFF);
                    n >>>= 8;
                }
            } else {
                // Little-Endian order
                for (int i = ofs; i < ofs + flen; i++) {
                    data[i] = (byte)(n & 0xFF);
                    n >>>= 8;
                }
            }
            return len;
        } else {
            return 0;
        }
    }

    /**
    *** Write a <code>double</code> value to the payload
    *** @param val The value to write
    *** @param wrtLen The number of bytes to write the value into
    *** @return The number of bytes written
    **/
    public int writeDouble(double val, int wrtLen)
    {
        // 'wrtLen' should be either 4 or 8

        /* check for nothing to write */
        if (wrtLen <= 0) {
            // nothing to write
            return 0;
        }

        /* write float/double */
        byte b[] = this._getBytes();
        int maxLen = ((this.index + wrtLen) <= b.length)? wrtLen : (b.length - this.index);
        if (maxLen < 4) {
            // not enough bytes to encode float/double
            return 0;
        }

        /* write float/double */
        if (wrtLen < 8) {
            // 4 <= wrtLen < 8  [float]
            int len = Payload.encodeDouble(b, this.index, 4, this.bigEndian, val);
            this.index += 4;
            if (this.size < this.index) { this.size = this.index; }
            return 4;
        } else {
            // 8 <= wrtLen      [double]
            int len = Payload.encodeDouble(b, this.index, 8, this.bigEndian, val);
            this.index += 8;
            if (this.size < this.index) { this.size = this.index; }
            return 8;
        }
        
    }

    // ------------------------------------------------------------------------

    /**
    *** Write a zero fill to the payload
    *** @param wrtLen The number of bytes to write
    *** @return The number of bytes written
    **/
    public int writeZeroFill(int wrtLen)
    {

        /* check for nothing to write */
        if (wrtLen <= 0) {
            // nothing to write
            return 0;
        }

        /* check for available space to write the data */
        byte b[] = this._getBytes();
        int maxLen = ((this.index + wrtLen) <= b.length)? wrtLen : (b.length - this.index);
        if (maxLen <= 0) {
            // no room left
            return 0;
        }

        /* fill field bytes with '0's, and adjust pointers */
        for (int m = 0; m < maxLen; m++) { b[this.index + m] = 0; }
        this.index += maxLen;
        if (this.size < this.index) { this.size = this.index; }

        /* return number of bytes written */
        return maxLen;

    }

    // ------------------------------------------------------------------------

    /**
    *** Write an array of bytes to the payload
    *** @param n The bytes to write to the payload
    *** @param nOfs The offset into <code>n</code> to start reading from
    *** @param nLen The number of bytes to write from <code>n</code>
    *** @param wrtLen The total number of bytes to write. (remaining bytes
    ***        filled with '0' if greater than <code>nLen</code>
    *** @return The number of bytes writen
    **/
    public int writeBytes(byte n[], int nOfs, int nLen, int wrtLen)
    {

        /* check for nothing to write */
        if (wrtLen <= 0) {
            // nothing to write
            return 0;
        }

        /* adjust nOfs/nLen to fit within the byte array */
        if ((nOfs < 0) || (nLen <= 0) || (n == null)) {
            // invalid offset/length, or byte array
            return this.writeZeroFill(wrtLen);
        } else
        if (nOfs >= n.length) {
            // 'nOfs' is outside the array, nothing to write
            return this.writeZeroFill(wrtLen);
        } else
        if ((nOfs + nLen) > n.length) {
            // 'nLen' would extend beyond the end of the array
            nLen = n.length - nOfs; // nLen will be > 0
        }

        /* check for available space to write the data */
        byte b[] = this._getBytes();
        int maxLen = ((this.index + wrtLen) <= b.length)? wrtLen : (b.length - this.index);
        if (maxLen <= 0) {
            // no room left
            return 0;
        }

        /* write byte field */
        // copy 'm' bytes to buffer at current index
        int m = (nLen < maxLen)? nLen : maxLen;
        System.arraycopy(n, nOfs, b, this.index, m);

        /* fill remaining field bytes with '0's, and adjust pointers */
        for (;m < maxLen; m++) { b[this.index + m] = 0; }
        this.index += maxLen;
        if (this.size < this.index) { this.size = this.index; }

        /* return number of bytes written */
        return maxLen;

    }

    /**
    *** Write an array of bytes to the payload
    *** @param n The bytes to write to the payload
    *** @param wrtLen The total number of bytes to write. (remaining bytes
    ***        filled with '0' if greater than <code>n.length</code>
    *** @return The number of bytes writen
    **/
    public int writeBytes(byte n[], int wrtLen)
    {
        return (n == null)? 
            this.writeZeroFill(wrtLen) :
            this.writeBytes(n, 0, n.length, wrtLen);
    }

    /**
    *** Write an array of bytes to the payload
    *** @param n The bytes to write to the payload
    *** @return The number of bytes writen
    **/
    public int writeBytes(byte n[])
    {
        return (n == null)?
            0 :
            this.writeBytes(n, 0, n.length, n.length);
    }
    
    // ------------------------------------------------------------------------

    /**
    *** Write a string to the payload. Writes until either <code>wrtLen</code>
    *** bytes are written or the string terminates
    *** @param s The string to write
    *** @param wrtLen The maximum number of bytes to write
    *** @return The number of bytes written
    **/
    public int writeString(String s, int wrtLen)
    {

        /* check for nothing to write */
        if (wrtLen <= 0) {
            // nothing to write
            return 0;
        }

        /* check for available space to write the data */
        byte b[] = this._getBytes();
        int maxLen = ((this.index + wrtLen) <= b.length)? wrtLen : (b.length - this.index);
        if (maxLen <= 0) {
            // no room left
            return 0;
        }
        
        /* empty string ('maxLen' is at least 1) */
        if ((s == null) || s.equals("")) {
            b[this.index++] = (byte)0;  // string terminator
            if (this.size < this.index) { this.size = this.index; }
            return 1;
        }

        /* write string bytes, and adjust pointers */
        byte n[] = StringTools.getBytes(s);
        int m = (n.length < maxLen)? n.length : maxLen;
        System.arraycopy(n, 0, b, this.index, m);
        this.index += m;
        if (m < maxLen) { 
            b[this.index++] = (byte)0; // terminate string
            m++;
        }
        if (this.size < this.index) { this.size = this.index; }

        /* return number of bytes written */
        return m;

    }

    // ------------------------------------------------------------------------

    /**
    *** Encode a new GPS point into the payload
    *** @param lat The latitude of the GPS point
    *** @param lon The longitude of the GPS point
    *** @param length The total number of bytes to write. 
    ***        Defaults to a minimum of 6
    *** @return The total number of bytes written
    *** @see GeoPoint#encodeGeoPoint
    *** @see GeoPoint#decodeGeoPoint
    **/
    public int writeGPS(double lat, double lon, int length)
    {
        return this.writeGPS(new GeoPoint(lat,lon), length);
    }
    
    /**
    *** Encode a GPS point into the payload
    *** @param gp The GPS point to encode
    *** @param wrtLen The total number of bytes to write. 
    ***        Defaults to a minimum of 6
    *** @return The total number of bytes written
    *** @see GeoPoint#encodeGeoPoint
    *** @see GeoPoint#decodeGeoPoint
    **/
    public int writeGPS(GeoPoint gp, int wrtLen)
    {

        /* check for nothing to write */
        if (wrtLen <= 0) {
            // nothing to write
            return 0;
        }

        /* check for available space to write the data */
        byte b[] = this._getBytes();
        int maxLen = ((this.index + wrtLen) <= b.length)? wrtLen : (b.length - this.index);
        if (maxLen < 6) {
            // not enough bytes to encode GeoPoint
            return 0;
        }
        
        /* write GPS point */
        if (wrtLen < 8) {
            // 6 <= wrtLen < 8
            int len = 6;
            GeoPoint.encodeGeoPoint(gp, b, this.index, len);
            this.index += len;
            if (this.size < this.index) { this.size = this.index; }
            // TODO: zero-fill (wrtLen - len) bytes?
            return len;
        } else {
            // 8 <= wrtLen
            int len = 8;
            GeoPoint.encodeGeoPoint(gp, b, this.index, len);
            this.index += len;
            if (this.size < this.index) { this.size = this.index; }
            // TODO: zero-fill (wrtLen - len) bytes?
            return len;
        }
        
    }

    // ------------------------------------------------------------------------

    /**
    *** Returns a hex string representation of the payload
    *** @return A hex string representation
    **/
    public String toString()
    {
        return StringTools.toHexString(this.payload, 0, this.size);
    }
    
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

}

