package com.emd.simbiom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>PropertySet</code> serves as a container for properties.
 *
 * Created: Thu Jul 27 09:11:13 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class PropertySet implements Comparable, Copyable {
    private long listid;
    private long typeid;
    private long propertyid;

    private String listname;
    private String typename;

    private List<Property> properties;

    public PropertySet() {
	this.listid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.properties = new ArrayList<Property>();
    }

    /**
     * Get the <code>Listid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getListid() {
	return listid;
    }

    /**
     * Set the <code>Listid</code> value.
     *
     * @param listid The new Listid value.
     */
    public final void setListid(final long listid) {
	this.listid = listid;
    }

    /**
     * Get the <code>Listname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getListname() {
	return listname;
    }

    /**
     * Set the <code>Listname</code> value.
     *
     * @param listname The new Listname value.
     */
    public final void setListname(final String listname) {
	this.listname = listname;
    }

    /**
     * Get the <code>Typeid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTypeid() {
	return typeid;
    }

    /**
     * Set the <code>Typeid</code> value.
     *
     * @param typeid The new Typeid value.
     */
    public final void setTypeid(final long typeid) {
	this.typeid = typeid;
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
     * Get the <code>Propertyid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getPropertyid() {
	return propertyid;
    }

    /**
     * Set the <code>Propertyid</code> value.
     *
     * @param propertyid The new Propertyid value.
     */
    public final void setPropertyid(final long propertyid) {
	this.propertyid = propertyid;
    }

    /**
     * Adds a property to the property set.
     *
     * @param prop the <code>Property</code> object to be added.
     */
    public void addProperty( Property prop ) {
	this.properties.add( prop );
    }

    /**
     * Adds a property to the property set.
     *
     * @param idx the index of the property to be set.
     * @param prop the <code>Property</code> object to be set.
     */
    public void setProperty( int idx, Property prop ) {
	this.properties.set( idx, prop );
    }

    /**
     * Returns an array of properties which are members of this <code>PropertySet</code> object.
     *
     * @return an (potentially empty) array of <code>Property</code> objects
     */
    public Property[] getProperties() {
	Property[] cols = new Property[properties.size()];
	return (Property[])properties.toArray( cols );
    }

    /**
     * Empties the list of properties.
     *
     */
    public void clearProperties() {
	properties.clear();
    }

    /**
     * Returns an array of items determined by the labels of the underlying properties.
     *
     * @return an (potentially empty) array of <code>String</code> objects represented by the underlying properties.
     */
    public String[] getItems() {
	Property[] props = getProperties();
	List<String> items = new ArrayList<String>();
	for( int i = 0; i < props.length; i++ )
	    items.add( props[i].toString() );
	String[] iArr = new String[ items.size() ];
	return (String[])items.toArray( iArr );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	PropertySet pSet = new PropertySet();
	pSet.properties.addAll(this.properties);
	return pSet;
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	String st =  Stringx.getDefault( getListname(), String.valueOf( getListid() ) );
	return st;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof PropertySet ) {
	    PropertySet f = (PropertySet)obj;
	    return (f.getListid() == this.getListid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getListid()).hashCode();
    }

    /**
     * Compares this object with the specified object for order. 
     * Returns a negative integer, zero, or a positive integer as this object is less 
     * than, equal to, or greater than the specified object. 
     *
     */
    public int compareTo( Object o) {
	return getListname().compareTo( ((PropertySet)o).getListname() );
    }

}
