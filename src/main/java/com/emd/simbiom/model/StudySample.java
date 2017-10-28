package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>StudySample</code> holds a relationship of study and sample.
 *
 * Created: Sat Feb 28 18:45:45 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class StudySample extends AbstractTrackable implements Copyable {
    private String sampleid;
    private long studyid;

    private static final String ITEM_TYPE = "studymember";

    public StudySample() {
	super( ITEM_TYPE );
	this.setTrackid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
    }

    /**
     * Get the <code>Sampleid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSampleid() {
	return sampleid;
    }

    /**
     * Set the <code>Sampleid</code> value.
     *
     * @param sampleid The new Sampleid value.
     */
    public final void setSampleid(final String sampleid) {
	this.sampleid = sampleid;
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
     * Updates the trackid to reflect the current content.
     *
     * @return the updated trackid
     */
    public long updateTrackid() {
	long contId = contentId();
	setTrackid( contId );
	return contId;
    }

    protected long contentId() {
	StringBuilder stb = new StringBuilder();
	stb.append( Stringx.getDefault( getSampleid(), "" ) );
	stb.append( String.valueOf( getStudyid() ) );
	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new StudySample();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	StringBuilder stb = new StringBuilder();
	stb.append( "(" );
	stb.append( Stringx.getDefault( getSampleid(), "" ) );
	stb.append( "," );
	stb.append( String.valueOf( getStudyid() ) );
	stb.append( ")" );
	return stb.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof StudySample ) {
	    StudySample f = (StudySample)obj;
	    return (f.contentId() == this.contentId() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.contentId()).hashCode();
    }

}
