package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>User</code> holds user information.
 *
 * Created: Fri Feb 20 12:30:29 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class User extends AbstractTrackable implements Copyable {
    private long userid;
    private long roles;

    private String muid;
    private String username;
    private String apikey;
    private String email;

    private boolean active;

    private Timestamp created;

    private static final String ITEM_TYPE = "user";

    /**
     * Creates a new <code>User</code> instance.
     */
    public User() {
	super( ITEM_TYPE );
	this.userid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.muid="";
	this.username = "";
	this.setTrackid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.created = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Get the <code>Userid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getUserid() {
	return userid;
    }

    /**
     * Set the <code>Userid</code> value.
     *
     * @param userid The new Userid value.
     */

    /**
     * Get the <code>Roles</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getRoles() {
	return roles;
    }

    /**
     * Set the <code>Roles</code> value.
     *
     * @param roles The new Roles value.
     */
    public final void setRoles(final long roles) {
	this.roles = roles;
    }

    public final void setUserid(final long userid) {
	this.userid = userid;
    }

    /**
     * Get the <code>Muid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getMuid() {
	return muid;
    }

    /**
     * Set the <code>Muid</code> value.
     *
     * @param muid The new Muid value.
     */
    public final void setMuid(final String muid) {
	this.muid = muid;
    }

    /**
     * Get the <code>Username</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getUsername() {
	return username;
    }

    /**
     * Set the <code>Username</code> value.
     *
     * @param username The new Username value.
     */
    public final void setUsername(final String username) {
	this.username = username;
    }

    /**
     * Get the <code>Apikey</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getApikey() {
	return apikey;
    }

    /**
     * Set the <code>Apikey</code> value.
     *
     * @param apikey The new Apikey value.
     */
    public final void setApikey(final String apikey) {
	this.apikey = apikey;
    }

    /**
     * Get the <code>Email</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getEmail() {
	return email;
    }

    /**
     * Set the <code>Email</code> value.
     *
     * @param email The new Email value.
     */
    public final void setEmail(final String email) {
	this.email = email;
    }

    /**
     * Get the <code>Active</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isActive() {
	return active;
    }

    /**
     * Set the <code>Active</code> value.
     *
     * @param active The new Active value.
     */
    public final void setActive(final boolean active) {
	this.active = active;
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
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new User();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getUsername(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof User ) {
	    User f = (User)obj;
	    return (f.getUserid() == this.getUserid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getUserid()).hashCode();
    }

    /**
     * Tests if the user has the required role.
     *
     * @param role The role required.
     * @return true if the user owns the role, false otherwise.
     */
    public boolean hasRole( long reqRole ) {
	return ( ((this.getRoles() & Roles.SUPER_USER) == 1L) || 
		 ((this.getRoles() & reqRole) == reqRole) );
    }
   
}
