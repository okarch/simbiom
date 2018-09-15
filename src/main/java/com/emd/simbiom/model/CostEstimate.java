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
 * <code>CostEstimate</code> holds a cost estimate for sample storage.
 *
 * Created: Sat Jul  9 13:45:45 2016
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class CostEstimate implements Copyable {
    private long estimateid;
    private String projectname;
    private Timestamp created;
    private int duration;
    private float total;
    private String region;

    private List<CostItem> costs;

    public static final String DEFAULT_REGION = "EU";

    public CostEstimate() {
	this.setEstimateid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.costs = new ArrayList<CostItem>();
	this.region = DEFAULT_REGION;
	this.setCreated( new Timestamp( (new Date()).getTime() ));
    }

    private CostEstimate( CostEstimate cs ) {
	this.setEstimateid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.costs = new ArrayList<CostItem>();
	this.costs.addAll( cs.costs );
	this.setCreated( new Timestamp( (new Date()).getTime() ));
	this.setProjectname( cs.getProjectname() );
	this.setRegion( cs.getRegion() );
	this.setDuration( cs.getDuration() );
	this.setTotal( cs.getTotal() );
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
     * Get the <code>Projectname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getProjectname() {
	return projectname;
    }

    /**
     * Set the <code>Projectname</code> value.
     *
     * @param projectname The new Projectname value.
     */
    public final void setProjectname(final String projectname) {
	this.projectname = projectname;
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
     * Get the <code>Duration</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getDuration() {
	return duration;
    }

    /**
     * Set the <code>Duration</code> value.
     *
     * @param duration The new Duration value.
     */
    public final void setDuration(final int duration) {
	this.duration = duration;
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
     * Adds an certain amount to the <code>Total</code> value.
     *
     * @param amount The amount to be added.
     */
    public final void addTotal(final float amount) {
	this.total+=amount;
    }

    /**
     * Decrease <code>Total</code> value by a certain amount.
     *
     * @param amount The amount to be decreased.
     */
    public final void decreaseTotal(final float amount) {
	this.total-=amount;
    }

    /**
     * Adds a <code>CostItem</code> value.
     *
     * @param cost The new CostItem value.
     */
    public final void addCost(final CostItem cost) {
	costs.add( cost );
    }

    /**
     * Clears all <code>CostItem</code> values.
     *
     */
    public final void clearCosts() {
	costs.clear();
	this.setTotal( 0f );
    }

    /**
     * Returns a list of costs associated with this estimate.
     *
     * @return an array of costs.
     */
    public final CostItem[] getCosts() {
	CostItem[] aCosts = new CostItem[ costs.size() ];
	return (CostItem[])costs.toArray( aCosts );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new CostEstimate(this);
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	StringBuilder stb = new StringBuilder();
	stb.append( Stringx.getDefault( getProjectname(), "") );
	stb.append( " " );
	stb.append( String.valueOf(getTotal()) );
	return stb.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof CostEstimate ) {
	    CostEstimate f = (CostEstimate)obj;
	    return (f.getEstimateid() == this.getEstimateid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getEstimateid()).hashCode();
    }

    /**
     * Adds a cost item to this estimate.
     *
     * @param cs the cost per sample.
     * @param numItems the number of items.
     * @return this cost estimate.
     */       
    public CostItem addCostItem( CostSample cs, long numItems ) {
	CostItem ci = new CostItem();
	ci.setEstimateid( this.getEstimateid() );
	ci.setCostid( cs.getCostid() );
	ci.setItemtype( cs.toItemName() );
	ci.setItemcount( numItems );
	this.addTotal( (float)numItems * cs.getPrice() );
	this.addCost( ci );
	return ci;
    }

    /**
     * Removes the given sample cost item using the cost id.
     *
     * @param cs the cost per sample.
     * @return this cost estimate.
     */
    public void removeCostItem( CostSample cs ) {
	CostItem[] items = this.getCosts();
	for( int i = 0; i < items.length; i++ ) {
	    if( cs.getCostid() == items[i].getCostid() ) {
		// this.decreaseTotal(items[i].getPrice()*(float)items[i].getItemcount());
		costs.remove( i );
	    }
	}
    }

}
