package com.emd.simbiom.model;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>SampleSummary</code> holds sample counts per category.
 *
 * Created: Tue Aug  4 18:17:23 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class SampleSummary implements Copyable{
    private long lastUpdate;
    private long samplecount;

    private String groupname;
    private String categoryPath;
    private String term;

    public SampleSummary() {
	this.lastUpdate = System.currentTimeMillis();
	this.samplecount = 0L;
	this.groupname = "";
	this.term = "";
	this.categoryPath = "";
    }

    /**
     * Get the <code>LastUpdate</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getLastUpdate() {
	return lastUpdate;
    }

    /**
     * Set the <code>LastUpdate</code> value.
     *
     * @param lastUpdate The new LastUpdate value.
     */
    public final void setLastUpdate(final long lastUpdate) {
	this.lastUpdate = lastUpdate;
    }

    /**
     * Get the <code>Samplecount</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getSamplecount() {
	return samplecount;
    }

    /**
     * Set the <code>Samplecount</code> value.
     *
     * @param samplecount The new Samplecount value.
     */
    public final void setSamplecount(final long samplecount) {
	this.samplecount = samplecount;
    }

    /**
     * Get the <code>Groupname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getGroupname() {
	return groupname;
    }

    /**
     * Set the <code>Groupname</code> value.
     *
     * @param groupname The new Groupname value.
     */
    public final void setGroupname(final String groupname) {
	this.groupname = groupname;
    }

    /**
     * Get the <code>Term</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTerm() {
	return term;
    }

    /**
     * Set the <code>Term</code> value.
     *
     * @param term The new Term value.
     */
    public final void setTerm(final String term) {
	this.term = term;
    }

    /**
     * Get the <code>CategoryPath</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getCategoryPath() {
	return categoryPath;
    }

    /**
     * Set the <code>CategoryPath</code> value.
     *
     * @param categoryPath The new CategoryPath value.
     */
    public final void setCategoryPath(final String categoryPath) {
	this.categoryPath = categoryPath;
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getGroupname(), "" );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new SampleSummary();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof SampleSummary ) {
	    SampleSummary f = (SampleSummary)obj;
	    return (f.getCategoryPath().equals( this.getCategoryPath() ));
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return this.getCategoryPath().hashCode();
    }

}
