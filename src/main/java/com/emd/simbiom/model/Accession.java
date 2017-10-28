package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Accession</code> represents a sample accession code assigned by lab.
 *
 * Created: Wed Feb 25 08:18:51 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Accession extends AbstractTrackable implements Copyable {
    private String sampleid;
    private String accession;
    private String acctype;

    private long orgid;

    private static final String ITEM_TYPE = "accession";

    /**
     * Creates a new <code>Accession</code> instance.
     */
    public Accession() {
	super( ITEM_TYPE );
	this.acctype = "";
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
     * Get the <code>Accession</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getAccession() {
	return accession;
    }

    /**
     * Set the <code>Accession</code> value.
     *
     * @param accession The new Accession value.
     */
    public final void setAccession(final String accession) {
	this.accession = accession;
    }

    /**
     * Get the <code>Acctype</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getAcctype() {
	return acctype;
    }

    /**
     * Set the <code>Acctype</code> value.
     *
     * @param acctype The new Acctype value.
     */
    public final void setAcctype(final String acctype) {
	this.acctype = acctype;
    }

    /**
     * Get the <code>Orgid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getOrgid() {
	return orgid;
    }

    /**
     * Set the <code>Orgid</code> value.
     *
     * @param orgid The new Orgid value.
     */
    public final void setOrgid(final long orgid) {
	this.orgid = orgid;
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
	stb.append( Stringx.getDefault( getAccession(), "" ) );
	stb.append( Stringx.getDefault( getAcctype(), "" ) );
	stb.append( String.valueOf( getOrgid() ) );
	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Accession();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getAccession(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Accession ) {
	    Accession f = (Accession)obj;
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
