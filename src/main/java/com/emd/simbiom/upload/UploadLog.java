package com.emd.simbiom.upload;

import java.sql.Timestamp;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>UploadLog</code> holds message emmitted while upload.
 *
 * Created: Tue Feb 17 08:08:54 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class UploadLog implements Copyable {
    private long logid;
    private long uploadid;
    private long line;

    private String message;
    private String level;

    private Timestamp logstamp;

    public UploadLog() {
	this.logid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.message = "";
    }

    /**
     * Get the <code>Logid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getLogid() {
	return logid;
    }

    /**
     * Set the <code>Logid</code> value.
     *
     * @param logid The new Logid value.
     */
    public final void setLogid(final long logid) {
	this.logid = logid;
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
     * Get the <code>Message</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getMessage() {
	return message;
    }

    /**
     * Set the <code>Message</code> value.
     *
     * @param message The new Message value.
     */
    public final void setMessage(final String message) {
	this.message = message;
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
     * Get the <code>Level</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getLevel() {
	return level;
    }

    /**
     * Set the <code>Level</code> value.
     *
     * @param level The new Level value.
     */
    public final void setLevel(final String level) {
	this.level = level;
    }

    /**
     * Get the <code>Line</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getLine() {
	return line;
    }

    /**
     * Set the <code>Line</code> value.
     *
     * @param line The new Line value.
     */
    public final void setLine(final long line) {
	this.line = line;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new UploadLog();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getMessage(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof UploadLog ) {
	    UploadLog f = (UploadLog)obj;
	    return (f.getLogid() == this.getLogid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(getLogid()).hashCode();
    }

}
