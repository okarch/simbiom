package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>CostSample</code> holds default costs per sample.
 *
 * Created: Wed Jul  6 10:45:45 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class CostSample implements Copyable {
    private long preferenceid;
    private long costid;

    private String costtype;
    private String currency;
    private String frequency;
    private String region;
    private String servicegroup;
    private String serviceitem;
    private String typename;
    private String unit;

    private int rank;

    private float price;

    public CostSample() {
	this.setPreferenceid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
    }

    private CostSample( CostSample cs ) {
	this.setPreferenceid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setTypename( cs.getTypename() );
	this.setRank( cs.getRank() );
	this.setCostid( cs.getCostid() );
    }

    /**
     * Get the <code>Preferenceid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getPreferenceid() {
	return preferenceid;
    }

    /**
     * Set the <code>Preferenceid</code> value.
     *
     * @param preferenceid The new Preferenceid value.
     */
    public final void setPreferenceid(final long preferenceid) {
	this.preferenceid = preferenceid;
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
     * Get the <code>Rank</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getRank() {
	return rank;
    }

    /**
     * Set the <code>Rank</code> value.
     *
     * @param rank The new Rank value.
     */
    public final void setRank(final int rank) {
	this.rank = rank;
    }

    /**
     * Get the <code>Costtype</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getCosttype() {
	return costtype;
    }

    /**
     * Set the <code>Costtype</code> value.
     *
     * @param costtype The new Costtype value.
     */
    public final void setCosttype(final String costtype) {
	this.costtype = costtype;
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
	return new CostSample(this);
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	String st = Stringx.getDefault( getServiceitem(), "" ).trim();
	if( st.length() > 0 )
	    return st;
	return Stringx.getDefault( getTypename(), "" );
    }

    /**
     * Returns a human readable item name to be stored as cost item.
     *
     * @return the cost item name
     */
    public String toItemName() {
	StringBuilder stb = new StringBuilder();
	String st = Stringx.getDefault( getServiceitem(), "" ).trim();
	String st2 = Stringx.getDefault( getTypename(), "" );
	if( st.length() > 0 ) 
	    stb.append( st );
	if( st2.length() > 0 ) {
	    if( st.length() > 0 ) {
		stb.append( " (" );
		stb.append( st2 );
		stb.append( ")" );
	    }
	    else
		stb.append( st2 );
	}
	return stb.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof CostSample ) {
	    CostSample f = (CostSample)obj;
	    return (f.getPreferenceid() == this.getPreferenceid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getPreferenceid()).hashCode();
    }

}
