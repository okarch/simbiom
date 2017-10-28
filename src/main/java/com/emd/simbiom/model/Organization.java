package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Organization</code> holds information about clinical sites and other CROs.
 *
 * Created: Tue Feb 24 07:49:12 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Organization implements Copyable {
    private int countryid;

    private long orgid;

    private String orgname;
    private String orgtype;
    private String siteid;

    /**
     * Creates a new <code>Organization</code> instance.
     */
    public Organization() {
	this.orgid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.orgname = "";
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
     * Get the <code>Orgname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getOrgname() {
	return orgname;
    }

    /**
     * Set the <code>Orgname</code> value.
     *
     * @param orgname The new Orgname value.
     */
    public final void setOrgname(final String orgname) {
	this.orgname = orgname;
    }

    /**
     * Get the <code>Orgtype</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getOrgtype() {
	return orgtype;
    }

    /**
     * Set the <code>Orgtype</code> value.
     *
     * @param orgtype The new Orgtype value.
     */
    public final void setOrgtype(final String orgtype) {
	this.orgtype = orgtype;
    }

    /**
     * Get the <code>Siteid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSiteid() {
	return siteid;
    }

    /**
     * Set the <code>Siteid</code> value.
     *
     * @param siteid The new Siteid value.
     */
    public final void setSiteid(final String siteid) {
	this.siteid = siteid;
    }

    /**
     * Get the <code>Countryid</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getCountryid() {
	return countryid;
    }

    /**
     * Set the <code>Countryid</code> value.
     *
     * @param countryid The new Countryid value.
     */
    public final void setCountryid(final int countryid) {
	this.countryid = countryid;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Organization();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getOrgname(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Organization ) {
	    Organization f = (Organization)obj;
	    return (f.getOrgid() == this.getOrgid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getOrgid()).hashCode();
    }

}
