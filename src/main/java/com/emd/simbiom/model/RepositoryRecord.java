package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>RepositoryRecord</code> holds a raw entry from the storage repository.
 *
 * Created: Thu Jan 30 15:58:57 2020
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class RepositoryRecord implements Copyable {
    private long recordid;
    private long typeid;
    private long groupid;

    private Timestamp modified;
    private Timestamp registered;
    private Timestamp shipped;
    private Timestamp disposed;
    private Timestamp latestRegistered;
    private Timestamp latestShipped;
    private Timestamp latestDisposed;

    private String study;
    private String project;
    private String storagegroup;
    private String status;
    private String division;
    private String originid;
    private String registrationid;
    private String paramname;
    private String paramvalue;
    private String typename;

// Feb 27 2018  3:19PM

    private static final String[] dtPatterns = new String[] {
        "MMM d yyyy h:mma",
        "MMM dd yyyy h:mma",
        "MMM dd yyyy HH:mm",
        "yyyy-MM-dd HH:mm:ss",
        "MMM dd yyyy"
    };


    public RepositoryRecord() {
	this.setRecordid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setModified( new Timestamp( (new Date()).getTime() ));
    }

    /**
     * Get the <code>Recordid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getRecordid() {
	return recordid;
    }

    /**
     * Set the <code>Recordid</code> value.
     *
     * @param recordid The new Recordid value.
     */
    public final void setRecordid(final long recordid) {
	this.recordid = recordid;
    }

    /**
     * Get the <code>Modified</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getModified() {
	return modified;
    }

    /**
     * Set the <code>Modified</code> value.
     *
     * @param modified The new Modified value.
     */
    public final void setModified(final Timestamp modified) {
	this.modified = modified;
    }

    /**
     * Get the <code>Study</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getStudy() {
	return study;
    }

    /**
     * Set the <code>Study</code> value.
     *
     * @param study The new Study value.
     */
    public final void setStudy(final String study) {
	this.study = study;
    }

    /**
     * Get the <code>Project</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getProject() {
	return project;
    }

    /**
     * Set the <code>Project</code> value.
     *
     * @param project The new Project value.
     */
    public final void setProject(final String project) {
	this.project = project;
    }

    /**
     * Get the <code>Projectid</code> value.
     *
     * @return a <code>long</code> value
     */
    // public final long getProjectid() {
    // 	return projectid;
    // }

    /**
     * Set the <code>Projectid</code> value.
     *
     * @param projectid The new Projectid value.
     */
    // public final void setProjectid(final long projectid) {
    // 	this.projectid = projectid;
    // }

    /**
     * Get the <code>Storagegroup</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getStoragegroup() {
	return storagegroup;
    }

    /**
     * Set the <code>Storagegroup</code> value.
     *
     * @param storagegroup The new Storagegroup value.
     */
    public final void setStoragegroup(final String storagegroup) {
	this.storagegroup = storagegroup;
    }

    /**
     * Get the <code>Status</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getStatus() {
	return status;
    }

    /**
     * Set the <code>Status</code> value.
     *
     * @param status The new Status value.
     */
    public final void setStatus(final String status) {
	this.status = status;
    }

    /**
     * Get the <code>Division</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getDivision() {
	return division;
    }

    /**
     * Set the <code>Division</code> value.
     *
     * @param division The new Division value.
     */
    public final void setDivision(final String division) {
	this.division = division;
    }

    /**
     * Get the <code>Originid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getOriginid() {
	return originid;
    }

    /**
     * Set the <code>Originid</code> value.
     *
     * @param originid The new Originid value.
     */
    public final void setOriginid(final String originid) {
	this.originid = originid;
    }

    /**
     * Get the <code>Registrationid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getRegistrationid() {
	return registrationid;
    }

    /**
     * Set the <code>Registrationid</code> value.
     *
     * @param registrationid The new Registrationid value.
     */
    public final void setRegistrationid(final String registrationid) {
	this.registrationid = registrationid;
    }

    private Timestamp parseDateTime( String dtString, Timestamp dtDefault ) {
	Date dtStamp = null;
	int patternIndex = -1;
	for( int i = 0; i < dtPatterns.length; i++ ) {
	    SimpleDateFormat formatter = new SimpleDateFormat( dtPatterns[i] );
	    try {
		// log.debug( "Applying "+periodPatterns[i]+" to "+nPeriod );
		dtStamp = formatter.parse( dtString );
		if( dtStamp != null ) {
		    patternIndex = i;
		    break;
		}
	    }
	    catch( ParseException pex ) {
		// log.warn( "Parse exception: "+pex );
		dtStamp = null;
	    }
	}
	
	Timestamp dtResult = dtDefault;
	if( dtStamp != null )
	    dtResult = new Timestamp( dtStamp.getTime() );
	    
	return dtResult;
    }

    /**
     * Get the <code>Registered</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getRegistered() {
	return registered;
    }

    /**
     * Set the <code>Registered</code> value.
     *
     * @param registered The new Registered value.
     */
    public final void setRegistered(final Timestamp registered) {
	this.registered = registered;
    }

    /**
     * Parses the datetime string to set the <code>Registered</code> value.
     *
     * @param stDatetime The datetime string to be parsed.
     */
    public final void parseRegistered(final String stDatetime ) {
	Timestamp dt = parseDateTime( stDatetime, new Timestamp(1000L) );
	this.registered = dt;
    }

    /**
     * Get the <code>Shipped</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getShipped() {
	return shipped;
    }

    /**
     * Set the <code>Shipped</code> value.
     *
     * @param shipped The new Shipped value.
     */
    public final void setShipped(final Timestamp shipped) {
	this.shipped = shipped;
    }

    /**
     * Parses the datetime string to set the <code>Shipped</code> value.
     *
     * @param stDatetime The datetime string to be parsed.
     */
    public final void parseShipped(final String stDatetime ) {
	Timestamp dt = parseDateTime( stDatetime, new Timestamp(1000L) );
	this.shipped = dt;
    }

    /**
     * Get the <code>Disposed</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getDisposed() {
	return disposed;
    }

    /**
     * Set the <code>Disposed</code> value.
     *
     * @param disposed The new Disposed value.
     */
    public final void setDisposed(final Timestamp disposed) {
	this.disposed = disposed;
    }

    /**
     * Parses the datetime string to set the <code>Disposed</code> value.
     *
     * @param stDatetime The datetime string to be parsed.
     */
    public final void parseDisposed(final String stDatetime ) {
	Timestamp dt = parseDateTime( stDatetime, new Timestamp(1000L) );
	this.disposed = dt;
    }

    /**
     * Get the <code>Paramname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getParamname() {
	return paramname;
    }

    /**
     * Set the <code>Paramname</code> value.
     *
     * @param paramname The new Paramname value.
     */
    public final void setParamname(final String paramname) {
	this.paramname = paramname;
    }

    /**
     * Get the <code>Paramvalue</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getParamvalue() {
	return paramvalue;
    }

    /**
     * Set the <code>Paramvalue</code> value.
     *
     * @param paramvalue The new Paramvalue value.
     */
    public final void setParamvalue(final String paramvalue) {
	this.paramvalue = paramvalue;
    }

    /**
     * Get the <code>Typeid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTypeid() {
	return typeid;
    }

    /**
     * Set the <code>Typeid</code> value.
     *
     * @param typeid The new Typeid value.
     */
    public final void setTypeid(final long typeid) {
	this.typeid = typeid;
    }

    /**
     * Get the <code>Typename</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTypename() {
	return typename;
    }

    /**
     * Set the <code>Typename</code> value.
     *
     * @param typename The new Typename value.
     */
    public final void setTypename(final String typename) {
	this.typename = typename;
    }

    /**
     * Get the <code>Groupid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getGroupid() {
	return groupid;
    }

    /**
     * Set the <code>Groupid</code> value.
     *
     * @param groupid The new Groupid value.
     */
    public final void setGroupid(final long groupid) {
	this.groupid = groupid;
    }

    /**
     * Get the <code>LatestRegistered</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getLatestRegistered() {
	return latestRegistered;
    }

    /**
     * Set the <code>LatestRegistered</code> value.
     *
     * @param latestRegistered The new LatestRegistered value.
     */
    public final void setLatestRegistered(final Timestamp latestRegistered) {
	this.latestRegistered = latestRegistered;
    }

    /**
     * Get the <code>LatestShipped</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getLatestShipped() {
	return latestShipped;
    }

    /**
     * Set the <code>LatestShipped</code> value.
     *
     * @param latestShipped The new LatestShipped value.
     */
    public final void setLatestShipped(final Timestamp latestShipped) {
	this.latestShipped = latestShipped;
    }

    /**
     * Get the <code>LatestDisposed</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getLatestDisposed() {
	return latestDisposed;
    }

    /**
     * Set the <code>LatestDisposed</code> value.
     *
     * @param latestDisposed The new LatestDisposed value.
     */
    public final void setLatestDisposed(final Timestamp latestDisposed) {
	this.latestDisposed = latestDisposed;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new RepositoryRecord();
    }

    /**
     * Returns whether this record is valid.
     *
     * @return true if it is valid, false otherwise.
     */
    public boolean isValid() {
	return ( (Stringx.getDefault(this.getRegistrationid(),"").trim().length() > 0) &&
		 (Stringx.getDefault(this.getStudy(),"").trim().length() > 0) &&
		 (Stringx.getDefault(this.getProject(),"").trim().length() > 0) &&
		 (Stringx.getDefault(this.getStoragegroup(),"").trim().length() > 0) &&
		 (Stringx.getDefault(this.getParamname(),"").trim().length() > 0) &&
		 (Stringx.getDefault(this.getOriginid(),"").trim().length() > 0) );
    }

    /**
     * Returns the human readable explanation on validity of this object.
     *
     * @return a human readble error message.
     */
    public String checkValidity() {
	StringBuilder stb = new StringBuilder();
	if( Stringx.getDefault(this.getRegistrationid(),"").trim().length() <= 0 )
	    stb.append( "registration id is empty," );
	if( Stringx.getDefault(this.getStudy(),"").trim().length() <= 0 )
	    stb.append( " study is empty," );
	if( Stringx.getDefault(this.getProject(),"").trim().length() <= 0 )
	    stb.append( " project is empty," );
	if( Stringx.getDefault(this.getStoragegroup(),"").trim().length() <= 0 )
	    stb.append( " storage group is empty," );
	if( Stringx.getDefault(this.getParamname(),"").trim().length() <= 0 )
	    stb.append( " parameter name is empty," );
	if( Stringx.getDefault(this.getOriginid(),"").trim().length() <= 0 )
	    stb.append( " origin id is empty," );
	if( stb.length() > 0 )
	    stb.setLength( stb.length()-1 );
	return stb.toString().trim();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	StringBuilder stb = new StringBuilder();
	stb.append( Stringx.getDefault( getRegistrationid(), "Unknown" ));
	stb.append( " " );
	stb.append( Stringx.getDefault( getParamname(), "Unknown" ) );
	stb.append( ": " );
	stb.append( Stringx.getDefault( getParamvalue(), "" ) );
	return stb.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof RepositoryRecord ) {
	    RepositoryRecord f = (RepositoryRecord)obj;
	    return (f.getRecordid() == this.getRecordid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getRecordid()).hashCode();
    }

}
