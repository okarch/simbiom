package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Study</code> represents the study for which samples have been generated.
 *
 * Created: Mon Feb 23 15:44:25 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Study implements Copyable {
    private long studyid;

    private String studyname;
    private String status;

    private Timestamp started;
    private Timestamp expire;

    private static final long TEN_YEARS = 10L * 365L * 24L * 60L * 60L * 1000L;

    /**
     * Creates a new <code>Study</code> instance.
     */
    public Study() {
	this.studyid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.studyname = "Unknown";
	this.started = new Timestamp(System.currentTimeMillis());
	this.expire = new Timestamp(System.currentTimeMillis()+TEN_YEARS);
    }

    /**
     * Formats a study name.
     *
     * @param sName the study name to be formatted.
     * @return a formatted study name.
     */
    public static String formatName( String sName ) {
	return StringUtils.replaceChars(sName.trim().toUpperCase(), "_:.= #/+;", "----" );
    }

    /**
     * Get the <code>Studyid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getStudyid() {
	return studyid;
    }

    /**
     * Set the <code>Studyid</code> value.
     *
     * @param studyid The new Studyid value.
     */
    public final void setStudyid(final long studyid) {
	this.studyid = studyid;
    }

    /**
     * Get the <code>Studyname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getStudyname() {
	return studyname;
    }

    /**
     * Set the <code>Studyname</code> value.
     *
     * @param studyname The new Studyname value.
     */
    public final void setStudyname(final String studyname) {
	this.studyname = studyname;
    }

    /**
     * Get the <code>Started</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getStarted() {
	return started;
    }

    /**
     * Set the <code>Started</code> value.
     *
     * @param started The new Started value.
     */
    public final void setStarted(final Timestamp started) {
	this.started = started;
    }

    /**
     * Get the <code>Expire</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getExpire() {
	return expire;
    }

    /**
     * Set the <code>Expire</code> value.
     *
     * @param expire The new Expire value.
     */
    public final void setExpire(final Timestamp expire) {
	this.expire = expire;
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
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Study();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getStudyname(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Study ) {
	    Study f = (Study)obj;
	    return (f.getStudyid() == this.getStudyid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getStudyid()).hashCode();
    }

}
