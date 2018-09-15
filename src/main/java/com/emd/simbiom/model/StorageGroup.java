package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>StorageGroup</code> specifies a storage group.
 *
 * Created: Fri Sep  7 19:15:02 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class StorageGroup implements Copyable {
    private long groupid;
    private long projectid;
    private String groupname;
    private String groupref;

    public StorageGroup() {
	this.setGroupid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setProjectid( -1L );
    }

    /**
     * Get the <code>Groupid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getGroupid() {
	return groupid;
    }

    /**
     * Set the <code>Groupid</code> value.
     *
     * @param groupid The new Groupid value.
     */
    public final void setGroupid(final long groupid) {
	this.groupid = groupid;
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
     * Get the <code>Groupref</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getGroupref() {
	return groupref;
    }

    /**
     * Set the <code>Groupref</code> value.
     *
     * @param groupref The new Groupref value.
     */
    public final void setGroupref(final String groupref) {
	this.groupref = groupref;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new StorageGroup();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getGroupname(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof StorageGroup ) {
	    StorageGroup f = (StorageGroup)obj;
	    return (f.getGroupid() == this.getGroupid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getGroupid()).hashCode();
    }

}
