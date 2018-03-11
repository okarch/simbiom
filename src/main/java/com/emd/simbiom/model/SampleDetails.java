package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

import com.emd.util.Stringx;

/**
 * <code>SampleDetails</code> holds detailed information about a sample.
 *
 * Created: Thu Feb  1 08:10:45 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class SampleDetails {
    private String sampleid;
    private Sample sample;
    private Timestamp created;
    private String details;
    private String typename;

    private List<DetailsSection> sections;

    // 3 days period for report to expire
    private static final long EXPIRE_DAYS = 3L * 24L * 60L * 60L * 1000L;


    /**
     * Creates a new <code>SampleDetails</code> object.
     */
    public SampleDetails() {
	this.sample = null;
	this.created = new Timestamp(System.currentTimeMillis());
	this.typename = "";
	this.sections = new ArrayList<DetailsSection>();
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
     * Get the <code>Created</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getCreated() {
	return created;
    }

    /**
     * Set the <code>Created</code> value.
     *
     * @param created The new Created value.
     */
    public final void setCreated(final Timestamp created) {
	this.created = created;
    }

    /**
     * Get the <code>Sample</code> value.
     *
     * @return a <code>Sample</code> value
     */
    public final Sample getSample() {
	return sample;
    }

    /**
     * Set the <code>Sample</code> value.
     *
     * @param sample The new Sample value.
     */
    public final void setSample(final Sample sample) {
	this.sample = sample;
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
     * Get the <code>Details</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getDetails() {
	if( details == null )
	    details = renderXml();
	return details;
    }

    /**
     * Set the <code>Details</code> value.
     *
     * @param details The new Details value.
     */
    public final void setDetails(final String details) {
	this.details = details;
    }

    /**
     * Calculates if sample details are expired.
     *
     * @return true if sample details are expired.
     */
    public boolean isExpired() {
	long cTime = created.getTime();
	return ((System.currentTimeMillis() - cTime) > EXPIRE_DAYS);
    }

    /**
     * Creates a section entry and clears the details entry.
     *
     * @param section the section name.
     * @return the newly created <code>DetailsSection</code> object.
     */
    public DetailsSection createSection( String section ) {
	this.details = null;
	for( DetailsSection sect : sections ) {
	    if( sect.getSectionName().equals( section ) )
		return sect;
	}
	DetailsSection det = new DetailsSection( section );
	sections.add( det );
	return det;
    }

    /**
     * Returns an XML representation of the sample details.
     *
     * @return the XML representation.
     */
    public String renderXml() {
	StringBuilder stb = new StringBuilder( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<sample-details>\n" );
	stb.append( "  <sampleid>" );
	stb.append( getSampleid() );
	stb.append( "</sampleid>\n" );
	stb.append( "  <created>" );
	stb.append( getCreated() );	   
	stb.append( "</created>\n" );
	stb.append( "  <sampletype>" );
	stb.append( getTypename() );	   
	stb.append( "</sampletype>\n" );

	for( DetailsSection sect : sections ) {
	    stb.append( sect.toXml() );
	}
	
	stb.append( "</sample-details>\n" );
	return stb.toString();
    }    

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( sample.getSamplename(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof SampleDetails ) {
	    SampleDetails f = (SampleDetails)obj;
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

}
