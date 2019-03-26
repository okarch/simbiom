package com.emd.simbiom.util;

import java.math.BigInteger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.emd.util.Stringx;

/**
 * <code>DataHasher</code> used to produce 64 bit hash codes.
 *
 * Created: Mon Feb  2 18:41:03 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class DataHasher {
  private static final long[] byteTable = createLookupTable();
  private static final long HSTART = 0xBB40E64DA205B064L;
  private static final long HMULT = 7664345821815920749L;

    private DataHasher() { }

    private static final long[] createLookupTable() {
	long[] bTable = new long[256];
	long h = 0x544B2FBACAAF1684L;
	for (int i = 0; i < 256; i++) {
	    for (int j = 0; j < 31; j++) {
		h = (h >>> 7) ^ h;
		h = (h << 11) ^ h;
		h = (h >>> 10) ^ h;
	    }
	    bTable[i] = h;
	}
	return bTable;
    }

    /**
     * Returns a 64 bit hash code.
     *
     * @return a 64 bit hash code. 
     */
    public static long hash(byte[] data) {
	long h = HSTART;
	final long hmult = HMULT;
	final long[] ht = byteTable;
	for (int len = data.length, i = 0; i < len; i++) {
	    h = (h * hmult) ^ ht[data[i] & 0xff];
	}
	return h;
    }

    /**
     * Calculates a formatted string representing an md5sum checksum.
     *
     * @param cont the content for which checksum should be calculated.
     *
     * @return a formatted md5sum (or empty string in case of error).
     */
    public static String calculateMd5sum( String cont ) {
	try {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    byte[] md5sum = md.digest(Stringx.getDefault(cont,"").trim().getBytes());
	    return String.format("%032X", new BigInteger(1, md5sum));
	}
	catch( NoSuchAlgorithmException nae ) {
	    // do nothing
	}
	return "";
    }

}

