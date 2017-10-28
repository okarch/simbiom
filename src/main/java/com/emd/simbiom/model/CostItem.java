package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>CostItem</code> holds a cost item for sample storage.
 *
 * Created: Mon Jul 11 17:45:45 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class CostItem implements Copyable {
    private long costitemid;
    private long estimateid;
    private long costid;
    private long itemcount;

    private String itemtype;


    public CostItem() {
	this.setCostitemid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );	
    }

    private CostItem( CostItem cs ) {
	this.setCostitemid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setEstimateid( cs.getEstimateid() );
	this.setCostid( cs.getCostid() );
	this.setItemtype( cs.getItemtype() );
	this.setItemcount( cs.getItemcount() );
    }

    /**
     * Get the <code>Costitemid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getCostitemid() {
	return costitemid;
    }

    /**
     * Set the <code>Costitemid</code> value.
     *
     * @param costitemid The new Costitemid value.
     */
    public final void setCostitemid(final long costitemid) {
	this.costitemid = costitemid;
    }

    /**
     * Get the <code>Estimateid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getEstimateid() {
	return estimateid;
    }

    /**
     * Set the <code>Estimateid</code> value.
     *
     * @param estimateid The new Estimateid value.
     */
    public final void setEstimateid(final long estimateid) {
	this.estimateid = estimateid;
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
     * Get the <code>Itemtype</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getItemtype() {
	return itemtype;
    }

    /**
     * Set the <code>Itemtype</code> value.
     *
     * @param itemtype The new Itemtype value.
     */
    public final void setItemtype(final String itemtype) {
	this.itemtype = itemtype;
    }

    /**
     * Get the <code>Itemcount</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getItemcount() {
	return itemcount;
    }

    /**
     * Set the <code>Itemcount</code> value.
     *
     * @param itemcount The new Itemcount value.
     */
    public final void setItemcount(final long itemcount) {
	this.itemcount = itemcount;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new CostItem(this);
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getItemtype(), "");
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof CostItem ) {
	    CostItem f = (CostItem)obj;
	    return (f.getCostitemid() == this.getCostitemid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getCostitemid()).hashCode();
    }

}
