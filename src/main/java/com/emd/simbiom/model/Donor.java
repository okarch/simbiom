package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Donor</code> holds a relationship of sample to donor.
 *
 * Created: Tue Mar  3 10:45:45 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Donor extends AbstractTrackable implements Copyable {
    private String sampleid;
    private long donorid;

    private static final String ITEM_TYPE = "donor";

    public Donor() {
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
     * Get the <code>Donorid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getDonorid() {
	return donorid;
    }

    /**
     * Set the <code>Donorid</code> value.
     *
     * @param donorid The new Donorid value.
     */
    public final void setDonorid(final long donorid) {
	this.donorid = donorid;
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
	stb.append( String.valueOf( getDonorid() ) );
	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Donor();
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
	stb.append( String.valueOf( getDonorid() ) );
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
	if( obj instanceof Donor ) {
	    Donor f = (Donor)obj;
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
