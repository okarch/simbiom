package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Sample</code> represents a biospecimen sample.
 *
 * Created: Mon Feb  2 21:13:14 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Sample extends AbstractTrackable implements Comparable, Copyable {
    private String sampleid;
    private String samplename;

    private long   typeid;
    private long   stamp;
    private long studyid;

    private Timestamp created;

    private static final String ITEM_TYPE = "sample";

    public Sample() {
	super( ITEM_TYPE );
	this.samplename = "";
	this.sampleid = UUID.randomUUID().toString();
	this.stamp = 0L;
	this.typeid = 1L;
	this.setTrackid( DataHasher.hash( this.sampleid.getBytes() ) );
	this.created = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Creates a sample from the given sample type.
     *
     * @param sType the sample type.
     * @return a <code>Sample</code> object.
     */
    public static Sample getInstance( SampleType sType ) {
	Sample st = new Sample();
	st.setTypeid( sType.getTypeid() );
	st.setSamplename( Stringx.getDefault(sType.getTypename(),"unknown")+
			  " "+Stringx.getDateString( "yyyy-MM-dd hh:mm:ss", st.getCreated() ) );
	return st;
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
     * Get the <code>Samplename</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSamplename() {
	return samplename;
    }

    /**
     * Set the <code>Samplename</code> value.
     *
     * @param samplename The new Samplename value.
     */
    public final void setSamplename(final String samplename) {
	this.samplename = samplename;
    }

    /**
     * Get the <code>Stamp</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getStamp() {
	return stamp;
    }

    /**
     * Set the <code>Stamp</code> value.
     *
     * @param stamp The new Stamp value.
     */
    public final void setStamp(final long stamp) {
	this.stamp = stamp;
    }

    /**
     * Get the <code>Trackid</code> value.
     *
     * @return a <code>long</code> value
     */
    // public final long getTrackid() {
    // 	return super.getTrackid()+stamp;
    // }

    /**
     * Get the Created value.
     * @return the Created value.
     */
    public Timestamp getCreated() {
	return created;
    }

    /**
     * Set the Created value.
     * @param newCreated The new Created value.
     */
    public void setCreated(Timestamp newCreated) {
	this.created = newCreated;
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
	long stmp = getStamp();
	stmp++;
	setStamp( stmp );
	return contId;
    }

    protected long contentId() {
	StringBuilder stb = new StringBuilder();
	stb.append( Stringx.getDefault( getSampleid(), "" ) );
	stb.append( Stringx.getDefault(getSamplename(), "" ) );
	stb.append( String.valueOf( getTypeid() ) );
	stb.append( String.valueOf( getStamp() ) );
	stb.append( String.valueOf( getCreated().getTime() ) );

	// calculate hash from properties

	stb.append( String.valueOf(getPropertyContentId() ) );

	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Sample();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getSamplename(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Sample ) {
	    Sample f = (Sample)obj;
	    return (f.getSampleid().equals( this.getSampleid() ));
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return this.getSampleid().hashCode();
    }

    /**
     * Compares this object with the specified object for order. 
     * Returns a negative integer, zero, or a positive integer as this object is less 
     * than, equal to, or greater than the specified object. 
     *
     */
    public int compareTo( Object o) {
	return getSampleid().compareTo( ((Sample)o).getSampleid() );
    }
}
