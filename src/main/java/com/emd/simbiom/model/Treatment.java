package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Treatment</code> represents information about the sample's treatment.
 *
 * Created: Sat Jul 25 09:38:29 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Treatment implements Copyable {
    private long treatid;

    private String treatment;
    private String treatdesc;

    /**
     * Creates a new <code>Treatment</code> instance.
     */
    public Treatment() {
	this.treatid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.treatment = "";
    }

    /**
     * Get the <code>Treatid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTreatid() {
	return treatid;
    }

    /**
     * Set the <code>Treatid</code> value.
     *
     * @param treatid The new Treatid value.
     */
    public final void setTreatid(final long treatid) {
	this.treatid = treatid;
    }

    /**
     * Get the <code>Treatment</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTreatment() {
	return treatment;
    }

    /**
     * Set the <code>Treatment</code> value.
     *
     * @param treatment The new Treatment value.
     */
    public final void setTreatment(final String treatment) {
	this.treatment = treatment;
    }

    /**
     * Get the <code>Treatdesc</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTreatdesc() {
	return treatdesc;
    }

    /**
     * Set the <code>Treatdesc</code> value.
     *
     * @param treatdesc The new Treatdesc value.
     */
    public final void setTreatdesc(final String treatdesc) {
	this.treatdesc = treatdesc;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Treatment();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getTreatment(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Treatment ) {
	    Treatment f = (Treatment)obj;
	    return (f.getTreatid() == this.getTreatid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return (Stringx.getDefault(this.getTreatment(),"")+
		Stringx.getDefault(this.getTreatdesc(),"")).hashCode();
    }

}
