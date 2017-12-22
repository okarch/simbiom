package com.emd.simbiom.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Country</code> holds country information.
 *
 * Created: Wed Dec 20 13:30:46 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Country implements Copyable {
    private int id;
    private int utc;

    private String name;
    private String capital;
    private String continent;
    private String ioc;
    private String tld;
    private String currency;
    private String phone;
    private String wiki;

    public Country() {
    }

    /**
     * Get the <code>Id</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getId() {
	return id;
    }

    /**
     * Set the <code>Id</code> value.
     *
     * @param id The new Id value.
     */
    public final void setId(final int id) {
	this.id = id;
    }

    /**
     * Get the <code>Continent</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getContinent() {
	return continent;
    }

    /**
     * Set the <code>Continent</code> value.
     *
     * @param continent The new Continent value.
     */
    public final void setContinent(final String continent) {
	this.continent = continent;
    }

    /**
     * Get the <code>Name</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getName() {
	return name;
    }

    /**
     * Set the <code>Name</code> value.
     *
     * @param name The new Name value.
     */
    public final void setName(final String name) {
	this.name = name;
    }

    /**
     * Get the <code>Capital</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getCapital() {
	return capital;
    }

    /**
     * Set the <code>Capital</code> value.
     *
     * @param capital The new Capital value.
     */
    public final void setCapital(final String capital) {
	this.capital = capital;
    }

    /**
     * Get the <code>Ioc</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getIoc() {
	return ioc;
    }

    /**
     * Set the <code>Ioc</code> value.
     *
     * @param ioc The new Ioc value.
     */
    public final void setIoc(final String ioc) {
	this.ioc = ioc;
    }

    /**
     * Get the <code>Tld</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTld() {
	return tld;
    }

    /**
     * Set the <code>Tld</code> value.
     *
     * @param tld The new Tld value.
     */
    public final void setTld(final String tld) {
	this.tld = tld;
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
     * Get the <code>Phone</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getPhone() {
	return phone;
    }

    /**
     * Set the <code>Phone</code> value.
     *
     * @param phone The new Phone value.
     */
    public final void setPhone(final String phone) {
	this.phone = phone;
    }

    /**
     * Get the <code>Utc</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getUtc() {
	return utc;
    }

    /**
     * Set the <code>Utc</code> value.
     *
     * @param utc The new Utc value.
     */
    public final void setUtc(final int utc) {
	this.utc = utc;
    }

    /**
     * Get the <code>Wiki</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getWiki() {
	return wiki;
    }

    /**
     * Set the <code>Wiki</code> value.
     *
     * @param wiki The new Wiki value.
     */
    public final void setWiki(final String wiki) {
	this.wiki = wiki;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Country();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getName(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Country ) {
	    Country f = (Country)obj;
	    return (f.getId() == this.getId() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return id;
    }

}
