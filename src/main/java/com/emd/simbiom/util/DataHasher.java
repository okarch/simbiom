package com.emd.simbiom.util;

import java.math.BigInteger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.emd.util.HexOutputStream;
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


    public static String encode( byte[] buf ) {
 	Deflater compressor = new Deflater( 9, true );
	compressor.setInput( buf );
	compressor.finish();

	byte[] output = new byte[buf.length];
	int len = compressor.deflate( output );
	
	StringBuffer sb = new StringBuffer();
	for( int i=0; i < len; i++ ) {
	    int b = output[i];
	    if( b < 0 ) 
		b += 256;
	    String s = Integer.toHexString(b);
	    if( s.length() == 1 ) 
		sb.append("0");
			//System.out.println(s);
	    sb.append(s);
	}
	return sb.toString();
    }

    /**
     * Encodes a binary content into hexadecimal text content.
     *
     * @param ins input stream.
     * @param outs output stream.
     * @return total number of bytes read from the input stream.
     */
    public static long encodeTo( InputStream ins, OutputStream outs ) 
	throws IOException {

 	Deflater compressor = new Deflater( 9, true );

	DeflaterOutputStream defOut = new DeflaterOutputStream
	    ( new HexOutputStream(outs), compressor );

	byte[] output = new byte[ 16384 ];
	int bRead = 0;
	long nRead = 0L;
	do {
	    bRead = ins.read( output );
	    if( bRead > 0 ) {
		defOut.write( output, 0, bRead );
		nRead+=bRead;
	    }
	}
	while( bRead > 0 );
	defOut.finish();
	defOut.flush();
	return nRead;
    }

    public static byte[] decode( String bs ) throws DataFormatException {
	byte[] out = new byte[(bs.length()/2)+1];
	for( int i=0; i < bs.length()/2; i++ ) {
	    String n = bs.substring(i*2, (i*2)+2);
	    int ii = Integer.parseInt(n, 16);
	    byte b = new Integer(ii).byteValue();
	    out[i] = b;
	}

	Inflater decompressor = new Inflater( true );
	decompressor.setInput( out );
	byte[] result = new byte[512];
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	int len = 0;
	do {
	    len = decompressor.inflate( result );
	    if( len > 0 )
		bos.write( result, 0, len );
// 	    System.out.println( "......need input: "+decompressor.needsInput()+
// 				" -- "+decompressor.getRemaining()+" -- "+
// 				len );
	}
	while( (decompressor.getRemaining() > 0) && (len > 0) );
	decompressor.end();
	return bos.toByteArray();
    }


}

