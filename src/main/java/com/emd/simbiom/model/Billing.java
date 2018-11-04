package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Billing</code> holds information about how a project is billed.
 *
 * Created: Sat Sep 22 17:38:57 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class Billing implements Copyable {
    private long billid;
    private long projectid;

    private String purchase;
    private String projectcode;
    private String currency;

    private float total;


    public Billing() {
	this.setBillid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setProjectid( -1L );
	this.setPurchase( "" );
	this.setProjectcode( "" );
	this.setCurrency( "EUR" );
    }

    /**
     * Get the <code>Projectid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getProjectid() {
	return projectid;
    }

    /**
     * Set the <code>Projectid</code> value.
     *
     * @param projectid The new Projectid value.
     */
    public final void setProjectid(final long projectid) {
	this.projectid = projectid;
    }

    /**
     * Get the <code>Billid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getBillid() {
	return billid;
    }

    /**
     * Set the <code>Billid</code> value.
     *
     * @param billid The new Billid value.
     */
    public final void setBillid(final long billid) {
	this.billid = billid;
    }

 // currency varchar(3)\, total float )

    /**
     * Get the <code>Purchase</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getPurchase() {
	return purchase;
    }

    /**
     * Set the <code>Purchase</code> value.
     *
     * @param purchase The new Purchase value.
     */
    public final void setPurchase(final String purchase) {
	this.purchase = purchase;
    }

    /**
     * Get the <code>Projectcode</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getProjectcode() {
	return projectcode;
    }

    /**
     * Set the <code>Projectcode</code> value.
     *
     * @param projectcode The new Projectcode value.
     */
    public final void setProjectcode(final String projectcode) {
	this.projectcode = projectcode;
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
     * Get the <code>Total</code> value.
     *
     * @return a <code>float</code> value
     */
    public final float getTotal() {
	return total;
    }

    /**
     * Set the <code>Total</code> value.
     *
     * @param total The new Total value.
     */
    public final void setTotal(final float total) {
	this.total = total;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Billing();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getPurchase(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Billing ) {
	    Billing f = (Billing)obj;
	    return (f.getBillid() == this.getBillid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getBillid()).hashCode();
    }

}
