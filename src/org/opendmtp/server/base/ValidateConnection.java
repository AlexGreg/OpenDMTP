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
package org.opendmtp.server.base;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;

import org.opengts.util.*;

public class ValidateConnection
{
    
    // ------------------------------------------------------------------------

    // The size of the counter (in bits) used to count the number of connections in a minute
    private static final int  BITS_PER_MINUTE        = 2;                               // 2, 4, or 8
    private static final byte BITS_PER_MINUTE_MASK   = (1 << BITS_PER_MINUTE) - 1;      // 0x03
    
    private static final int  BITS_PER_UNIT_ADDR     = 3;                               // 2^3 = 8
    private static final int  BITS_PER_UNIT          = (1 << BITS_PER_UNIT_ADDR);       // 2^3 = 8
    private static final int  BITS_PER_UNIT_MASK     = BITS_PER_UNIT - 1;               // 0x07
    
    private static final int  BYTES_PER_UNIT         = BITS_PER_UNIT / 8;               // 1
    private static final int  MINUTES_PER_UNIT       = BITS_PER_UNIT / BITS_PER_MINUTE; // 4
    
    // ------------------------------------------------------------------------

    private int  minuteRange    = 120;
    private int  byteLength     = 0;
    private byte padMask        = 0;
    
    public ValidateConnection(int minuteRange)
    {
        this.minuteRange = (minuteRange >= 0)? minuteRange : 0;
        this.byteLength  = ((this.minuteRange * BITS_PER_MINUTE) + BITS_PER_UNIT - 1) / BITS_PER_UNIT;
        int pad          = (this.byteLength * MINUTES_PER_UNIT) - minuteRange;
        if (pad > 0) {
            // create mask of all minutes we wish to keep in the first unit
            this.padMask = (byte)((1 << ((MINUTES_PER_UNIT - pad) * BITS_PER_MINUTE)) - 1);
        } else {
            // create mask of all minutes in the first unit
            this.padMask = (byte)((this.minuteRange > 0)? ~0 : 0);
        }
    }
    
    public int getByteLength()
    {
        return this.byteLength;
    }
    
    // ------------------------------------------------------------------------

    private byte[] _adjustProfileMask(byte profileMask[])
    {
        if ((profileMask == null) || (profileMask.length != this.byteLength)) {
            byte b[] = new byte[this.byteLength];
            if ((profileMask == null) || (profileMask.length == 0)) {
                // nothing to copy
            } else
            if (profileMask.length <= this.byteLength) {
                int ofs = this.byteLength - profileMask.length;
                System.arraycopy(profileMask, 0, b, ofs, profileMask.length);
            } else {
                int ofs = profileMask.length - this.byteLength;
                System.arraycopy(profileMask, ofs, b, 0, this.byteLength);
            }
            profileMask = b;
        }
        return profileMask;
    }
    
    // ------------------------------------------------------------------------

    public byte[] markConnection(int maxConnections, int maxPerMinute, byte profileMask[], long shiftSec)
    {
        
        /* shift time since last connection */
        long shiftMin = shiftSec / 60L; // count whole minutes only
        profileMask = this.shiftMinutes(shiftMin, profileMask);
        // 'profileMask' is now guaranteed to be 'this.byteLength' in length
        if (profileMask.length == 0) {
            // the device does not care about connection accounting, just return the empty mask
            return profileMask;
        }
        
        /* exceeded max connections? */
        if (maxConnections > 0) {
            int count = this.countConnections(profileMask);
            if ((count + 1) > maxConnections) {
                // exceeded the maximum allowable connections per limit period
                Print.logError("Exceeded maximum allowable connections");
                return null;
            }
        }

        /* mark connection */
        int  ndx = profileMask.length - 1;     // last unit (current)
        byte val = (byte)(profileMask[ndx] & BITS_PER_MINUTE_MASK);    // current value
        if ((maxPerMinute <= 0) || (maxPerMinute > BITS_PER_MINUTE_MASK)) {
            maxPerMinute = BITS_PER_MINUTE_MASK; 
        }
        if ((val < BITS_PER_MINUTE_MASK) && (val < maxPerMinute)) {
            val++;
            profileMask[ndx] = (byte)((profileMask[ndx] & ~BITS_PER_MINUTE_MASK) | (val & BITS_PER_MINUTE_MASK));
            return profileMask;
        } else {
            // exceeded the per-minute maximum
            Print.logError("Exceeded the per-minute maximum");
            return null;
        }
        
    }
    
    // ------------------------------------------------------------------------

    /* count connections */
    private int countConnections(byte mask[])
    {
        int count = 0;
        if ((mask != null) && (mask.length > 0)) {
            // only the right-most 'byteLength' bytes are examined
            if (mask.length < this.byteLength) {
                Print.logStackTrace("Invalid mask length: " + mask.length + " [" + this.byteLength);
            }
            int ofs = (mask.length <= this.byteLength)? 0 : (mask.length - this.byteLength);
            int len = (mask.length <= this.byteLength)? mask.length : this.byteLength;
            for (int i = 0; i < len; i++) {
                long n = (long)mask[i + ofs] & 0xFF;
                while (n != 0L) {
                    count += (int)(n & BITS_PER_MINUTE_MASK) & 0xFF;
                    n >>>= BITS_PER_MINUTE;
                }
            }
        }
        return count;
    }

    // ------------------------------------------------------------------------
    
    private byte[] shiftMinutes(long minutes, byte mask[])
    {
        // does not return null
        
        /* create/adjust mask if necessary */
        mask = this._adjustProfileMask(mask);

        /* bits to shift */
        long n = minutes * BITS_PER_MINUTE;
        if (n <= 0) {
            // nothing to shift
            return mask;
        }

        /* offset into mask */
        int ofs = mask.length - this.byteLength;
        // 'ofs' will alsway be '0' here ('mask' was already adjusted to size above)

        /* calculate the number of 'Units' to be shifted */
        // since we are using a byte array, a 'Unit' is 1 byte.
        long nUnits  = n >>> BITS_PER_UNIT_ADDR;    // 2^3 == 8
        // nUnits = (n / 8); [or (n >>> 3)]
        // if 'nUnits' is '0', then we're shift less than a full byte
        
        /* new mask containing shifted bits */
        byte newMask[] = new byte[this.byteLength]; // assume already zeroed out

        /* already shifted beyond the scope of the mask? */
        if (nUnits >= (long)newMask.length) {
            // We've already shifted off the scale, return an empty array
            return newMask;
        }
        
        /* see if the number of bits we need to shift is divisible by our unit size */
        // since we're using a byte array, see if it's divisible by 8
        int nBits = (int)n & BITS_PER_UNIT_MASK;      // (2^3) - 1 == 0x07
        // if the last 3 bits are '0', then it is divisble by 8
        
        /* start shifting */
        if (nBits == 0) {
            // shifting by full units
            // if the number of shifted bit is divisible by 8/16/24/32/etc, then we can
            // safely just move the bytes down by the number of specified units.
            for (int i = 0; i < (newMask.length - (int)nUnits); i++) {
                int fromNdx = i + (int)nUnits + ofs;
                newMask[i] = mask[fromNdx];
            }
        } else {
            // shifting by fractional units
            int nBits2 = BITS_PER_UNIT - nBits; // remaining bits
            // shift all but the last unit
            int i = 0;
            for (; i < (newMask.length - (int)nUnits - 1); i++) {
                int fromNdx = i + (int)nUnits + ofs;
                newMask[i] = (byte)((mask[fromNdx] << nBits) | (mask[fromNdx + 1] >>> nBits2));
            }
            // shift the final unit
            int fromNdx = i + (int)nUnits + ofs;
            newMask[i] = (byte)(mask[fromNdx] << nBits);
            newMask[0] &= this.padMask;
        }
        
        return newMask;

    }

    // ------------------------------------------------------------------------

    public static void main(String argv[])
    {
        RTConfig.setCommandLineArgs(argv);
        ValidateConnection a = new ValidateConnection(120);
        byte b[] = new byte[a.getByteLength()];
        a.markConnection(9, -1, b, 0L);
        a.markConnection(9, -1, b, 0L);
        for (int i = 0; i < 120; i++) {
            //b = a.shiftMinutes(i, b);
            byte x[] = a.markConnection(5, -1, b, 60L); 
            Print.logInfo("Mask: " + StringTools.toHexString(x));
        }
    }
    
}
