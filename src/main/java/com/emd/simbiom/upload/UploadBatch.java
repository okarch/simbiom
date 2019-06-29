package com.emd.simbiom.upload;

import java.math.BigInteger;

import java.io.IOException;
import java.io.StringReader;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import java.text.ParseException;

import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>UploadBatch</code> describes the content of an upload.
 *
 * Created: Mon Feb  9 20:02:49 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class UploadBatch implements Copyable {
    private long uploadid;
    private long templateid;
    private long userid;

    private String upload;
    private String md5sum;

    private int nummsg;

    private Timestamp uploaded;
    private Timestamp logstamp;

    private List<String> uploadHeader;

    private static Log log = LogFactory.getLog(UploadBatch.class);

    private static final String[] DATE_PATTERNS = new String[] {
	"ddMMMyyyy",
	"yyyyMMMdd",
	"dd-MMM-yyyy",
	"yyyy-MMM-dd",
	"dd-MM-yyyy",
	"yyyy-MM-dd",
        "dd.MM.yyyy",
        "MM/dd/yyyy",
        "MMM dd,yyyy"
    };

    public UploadBatch() {
	this.uploadid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.upload = "";
	this.uploadHeader = new ArrayList<String>();
    }

    /**
     * Get the <code>Uploadid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getUploadid() {
	return uploadid;
    }

    /**
     * Set the <code>Uploadid</code> value.
     *
     * @param uploadid The new Uploadid value.
     */
    public final void setUploadid(final long uploadid) {
	this.uploadid = uploadid;
    }

    /**
     * Get the <code>Templateid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTemplateid() {
	return templateid;
    }

    /**
     * Set the <code>Templateid</code> value.
     *
     * @param templateid The new Templateid value.
     */
    public final void setTemplateid(final long templateid) {
	this.templateid = templateid;
    }

    /**
     * Get the <code>Uploaded</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getUploaded() {
	return uploaded;
    }

    /**
     * Set the <code>Uploaded</code> value.
     *
     * @param uploaded The new Uploaded value.
     */
    public final void setUploaded(final Timestamp uploaded) {
	this.uploaded = uploaded;
    }

    /**
     * Get the <code>Userid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getUserid() {
	return userid;
    }

    /**
     * Set the <code>Userid</code> value.
     *
     * @param userid The new Userid value.
     */
    public final void setUserid(final long userid) {
	this.userid = userid;
    }

    /**
     * Get the <code>Upload</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getUpload() {
	return upload;
    }

    private void initMd5( String cont ) {
	try {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    byte[] md5sum = md.digest(Stringx.getDefault(cont,"").trim().getBytes());
	    this.setMd5sum( String.format("%032X", new BigInteger(1, md5sum)) );
	}
	catch( NoSuchAlgorithmException nae ) {
	    // do nothing
	}
    }

    /**
     * Set the <code>Upload</code> value.
     *
     * @param upload The new Upload value.
     */
    public final void setUpload(final String upload) {
	this.upload = Stringx.getDefault(upload,"").trim();
	initMd5( this.upload );
    }

    /**
     * Get the <code>Valid</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isValid() {
	return (uploaded != null);
    }

    /**
     * Get the <code>Md5sum</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getMd5sum() {
	return this.md5sum;
    }

    /**
     * Set the <code>Md5sum</code> value.
     *
     * @param md5sum The new Md5sum value.
     */
    public final void setMd5sum(final String md5sum) {
     	this.md5sum = md5sum;
    }

    /**
     * Get the <code>Nummsg</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getNummsg() {
	return nummsg;
    }

    /**
     * Set the <code>Nummsg</code> value.
     *
     * @param nummsg The new Nummsg value.
     */
    public final void setNummsg(final int nummsg) {
	this.nummsg = nummsg;
    }

    /**
     * Get the <code>Logstamp</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getLogstamp() {
	return logstamp;
    }

    /**
     * Set the <code>Logstamp</code> value.
     *
     * @param logstamp The new Logstamp value.
     */
    public final void setLogstamp(final Timestamp logstamp) {
	this.logstamp = logstamp;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new UploadBatch();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return String.valueOf(getUploadid());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof UploadBatch ) {
	    UploadBatch f = (UploadBatch)obj;
	    return (f.getUploadid() == this.getUploadid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(getUploadid()).hashCode();
    }

    private void addUploadColumn( String colName ) {
	uploadHeader.add( colName );
    }

    private void parseHeader( String hLine, String delim ) {
	String[] headerLines = null;
	if( delim != null )
	    headerLines = hLine.split( delim );
	else
	    headerLines = hLine.split( "[|,]" );
	for( int i = 0; i < headerLines.length; i++ ) 
	    addUploadColumn( headerLines[i] );
    }

    private void parseHeader( String hLine ) {
	parseHeader( hLine, null );
    }

    /**
     * Returns the column name at the given position.
     *
     * @param index the column index.
     * @return the column name (can be empty string if not existing).
     */
    public String getColumn( int index ) {
	if( (index < 0) || (index > uploadHeader.size()) )
	    return "";
	return uploadHeader.get( index );
    }

    /**
     * Returns the column index.
     *
     * @param colName the column name.
     * @return the column index (or -1 if not existing).
     */
    public int getColumnIndex( String colName ) {
	if( colName != null ) {
	    for( int i = 0; i < uploadHeader.size(); i++ ) {
		if( colName.equals( (String)uploadHeader.get(i) ) )
		    return i;
	    }
	}
	return -1;
    }

    /**
     * Returns the column names if any detected.
     *
     * @return a list of column names.
     */
    public List<String> readColumns() {
	return uploadHeader;
    }

    /**
     * Reads through the lines of the upload content and returns a list of lines.
     *
     * @return a list of lines.
     */
    public List<String> readLines( String delim ) {
	String cont = Stringx.getDefault(getUpload(),"");
	StringReader sr = new StringReader( cont );
	uploadHeader.clear();
	try {
	    List<String> lines = IOUtils.readLines( sr );
	    if( lines.size() > 0 )
		parseHeader( lines.get(0), delim );
	    return lines;
	}
	catch( IOException ioe ) {
	    log.error( ioe );
	}
	List<String> el = Collections.emptyList();
	return el;
    }

    /**
     * Reads through the lines of the upload content and returns a list of lines.
     *
     * @return a list of lines.
     */
    public List<String> readLines() {
	return readLines( null );
    }

    /**
     * Normalizes a study name.
     *
     * @param study the unformatted study name.
     * @return a formatted study name
     */
    public String formatStudy( String study ) {
	if( (study == null) || (study.trim().length() <= 0) )
	    return "Unknown";
	String sName = study.trim().toUpperCase();
	sName = sName.replace(" ","").replace( "_", "-" );
	if( (sName.indexOf( "-" ) < 0) && (sName.length() > 3) ) 
	    sName = sName.substring(0, sName.length()-3 )+"-"+sName.substring(sName.length()-3);
	return sName;	
    }

    /**
     * Formats the site id.
     *
     * @param site the site id.
     * @return a site id
     */
    public String formatSiteName( String site ) {
	if( site == null )
	    return "999";
	String sName = site.trim();
	if( sName.length() <= 0 )
	    return "999";
	if( sName.length() < 3 ) {
	    String sNum = Stringx.trimZero( sName );
	    int num = Stringx.toInt( sNum, -1 );
	    if( num < 0 )
		return sName;
	    sName = StringUtils.leftPad( sName, 3, "0" );
	}
	return sName;
    }

    /**
     * Extracts the site id.
     *
     * @param subject the subject id.
     * @return a site id
     */
    public String formatSite( String subject ) {
	if( subject == null )
	    return "999";
	String sName = subject.trim().replace(" ","").replace( "-", "" );
	if( sName.length() <= 3 )
	    return "999";
	sName = StringUtils.leftPad( sName, 7, "0" );
	return sName.substring( 0, 3 );
    }

    /**
     * Extracts the site id.
     *
     * @param subject the subject id.
     * @return a site id
     */
    public String formatSubject( String subject ) {
	if( subject == null )
	    return "0000000";
	String sName = subject.trim().replace(" ","").replace( "-", "" );
	return StringUtils.leftPad( sName, 7, "0" );
    }

    /**
     * Formats the subject id. 
     *
     * @param site the site.
     * @param subject the subject id (potentially incl. site).
     * @return a subject id w/o site.
     */
    public String formatSubjectName( String site, String subject ) {
	String subj = Stringx.getDefault( subject, "" ).trim();
	String sit = Stringx.getDefault( site, "" ).trim();
	if( subj.length() <= 0 )
	    return "9999";

	if( subj.length() <= sit.length() ) {
	    String sNum = Stringx.trimZero( subj );
	    int num = Stringx.toInt( sNum, -1 );
	    if( num > 0 )
		return StringUtils.leftPad( subj, 4, "0" );
	}

	String[] toks = StringUtils.split( subj, " -.:+#/,;" );
	if( toks.length > 1 ) {
	    if( sit.endsWith( toks[0] ) ) {
		subj = StringUtils.substringAfter( subj, toks[0] );
		if( subj.length() > 1 )
		    subj = subj.substring(1);
	    }
	}
	else if( ((subj.length() - sit.length()) >= 4) && (subj.startsWith( sit )) ) {
	    subj = StringUtils.substringAfter( subj, sit );
	}
	    
	String sNum = Stringx.trimZero( subj );
	int num = Stringx.toInt( sNum, -1 );
	if( num < 0 )
	    return subj;

	return StringUtils.leftPad( subj, 4, "0" );
    }

    /**
     * Parses the date.Formats the subject id. 
     *
     * @param site the site.
     * @param subject the subject id (potentially incl. site).
     * @return a subject id w/o site.
     */
    public Date parseDate( String dtString ) {
	Date date = null;
	try {
	    date = DateUtils.parseDate( dtString, DATE_PATTERNS );
	}
	catch( ParseException pex ) {
	    date = null;
	}
	if( date == null )
	    date = new Date( 1000L );
	return date;
    }


}
