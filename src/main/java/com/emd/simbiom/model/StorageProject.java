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
 * Describe class StorageProject here.
 *
 *
 * Created: Fri Aug 10 18:58:57 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class StorageProject implements Copyable {
    private long projectid;
    private Timestamp created;
    private String title;
    private List<StorageGroup> groups;

    public StorageProject() {
	this.setProjectid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setCreated( new Timestamp( (new Date()).getTime() ));
	this.groups = new ArrayList<StorageGroup>();
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
     * Get the <code>Title</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTitle() {
	return title;
    }

    /**
     * Set the <code>Title</code> value.
     *
     * @param title The new Title value.
     */
    public final void setTitle(final String title) {
	this.title = title;
    }

    /**
     * Adds a new storage group.
     *
     * @param the group name
     * @return the newly created group.
     */
    public StorageGroup addStorageGroup( String groupName ) {
	StorageGroup sg = new StorageGroup();
	sg.setGroupname( groupName );
	sg.setProjectid( getProjectid() );
	this.groups.add( sg );
	return sg;
    }

    /**
     * Adds a new storage group.
     *
     * @param the storage group.
     * @return the newly created group.
     */
    public StorageGroup addStorageGroup( StorageGroup sg ) {
	sg.setProjectid( getProjectid() );
	this.groups.add( sg );
	return sg;
    }

    /**
     * Returns the current list of storage groups.
     * @return a (potentially empty) array of storage groups assigned.
     */
    public StorageGroup[] getStorageGroups() {
	StorageGroup[] grps = new StorageGroup[ this.groups.size() ];
	return (StorageGroup[])this.groups.toArray( grps );
    }

    /**
     * Returns the first storage group matching the name.
     * @param the group name.
     * @return the storage group.
     */
    public StorageGroup findStorageGroup( String groupName ) {
	StorageGroup[] grps = getStorageGroups();
	for( int i = 0; i < grps.length; i++ ) {
	    if( grps[i].getGroupname().equalsIgnoreCase( groupName ) )
		return grps[i];
	}
	return null;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new StorageProject();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getTitle(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof StorageProject ) {
	    StorageProject f = (StorageProject)obj;
	    return (f.getProjectid() == this.getProjectid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getProjectid()).hashCode();
    }

}
