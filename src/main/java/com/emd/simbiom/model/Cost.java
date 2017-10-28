package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Cost</code> holds a cost for sample storage.
 *
 * Created: Sat Jul  9 13:50:45 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Cost implements Copyable {
    private long costid;

    private String currency;
    private String frequency;
    private String region;
    private String servicegroup;
    private String serviceitem;
    private String unit;

    private int rank;

    private float price;

    public Cost() {
	this.setCostid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
    }

    private Cost( Cost cs ) {
	this.setCostid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setRegion( cs.getRegion() );
	this.setServicegroup( cs.getServicegroup() );
	this.setServiceitem( cs.getServiceitem() );
	this.setUnit( cs.getUnit() );
	this.setFrequency( cs.getFrequency() );
	this.setPrice( cs.getPrice() );
	this.setCurrency( cs.getCurrency() );
    }

    /**
     * Get the <code>Costid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getCostid() {
	return costid;
    }

    /**
     * Set the <code>Costid</code> value.
     *
     * @param costid The new Costid value.
     */
    public final void setCostid(final long costid) {
	this.costid = costid;
    }

    /**
     * Get the <code>Region</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getRegion() {
	return region;
    }

    /**
     * Set the <code>Region</code> value.
     *
     * @param region The new Region value.
     */
    public final void setRegion(final String region) {
	this.region = region;
    }

    /**
     * Get the <code>Servicegroup</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getServicegroup() {
	return servicegroup;
    }

    /**
     * Set the <code>Servicegroup</code> value.
     *
     * @param servicegroup The new Servicegroup value.
     */
    public final void setServicegroup(final String servicegroup) {
	this.servicegroup = servicegroup;
    }

    /**
     * Get the <code>Serviceitem</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getServiceitem() {
	return serviceitem;
    }

    /**
     * Set the <code>Serviceitem</code> value.
     *
     * @param serviceitem The new Serviceitem value.
     */
    public final void setServiceitem(final String serviceitem) {
	this.serviceitem = serviceitem;
    }

    /**
     * Get the <code>Unit</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getUnit() {
	return unit;
    }

    /**
     * Set the <code>Unit</code> value.
     *
     * @param unit The new Unit value.
     */
    public final void setUnit(final String unit) {
	this.unit = unit;
    }

    /**
     * Get the <code>Currency</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getCurrency() {
	return currency;
    }

    /**
     * Set the <code>Currency</code> value.
     *
     * @param currency The new Currency value.
     */
    public final void setCurrency(final String currency) {
	this.currency = currency;
    }

    /**
     * Get the <code>Frequency</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getFrequency() {
	return frequency;
    }

    /**
     * Set the <code>Frequency</code> value.
     *
     * @param frequency The new Frequency value.
     */
    public final void setFrequency(final String frequency) {
	this.frequency = frequency;
    }

    /**
     * Get the <code>Price</code> value.
     *
     * @return a <code>float</code> value
     */
    public final float getPrice() {
	return price;
    }

    /**
     * Set the <code>Price</code> value.
     *
     * @param price The new Price value.
     */
    public final void setPrice(final float price) {
	this.price = price;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Cost(this);
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getServiceitem(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Cost ) {
	    Cost f = (Cost)obj;
	    return (f.getCostid() == this.getCostid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getCostid()).hashCode();
    }

}
