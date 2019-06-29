package com.emd.simbiom.model;

import java.util.Map;
import java.util.Set;

/**
 * <code>PropertyHolder</code> interface to retrieve properties.
 *
 * Created: Fri Jun 14 19:01:32 2019
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface PropertyHolder {

    /**
     * Get the <code>Propertyid</code> value.
     *
     * @return a <code>long</code> value
     */
    public long getPropertyid();

    /**
     * Set the <code>Propertyid</code> value.
     *
     * @param propertyid The new Propertyid value.
     */
    public void setPropertyid( long propertyid);

    /**
     * Adds a property to the property set.
     *
     * @param idx the index of the property to be set.
     * @param prop the <code>Property</code> object to be set.
     */
    public void setAttribute( String attrName, Object value );

    /**
     * Returns the set of attributes.
     *
     * @return a Set of <code>Map.Entry</code> objects.
     */
    public Set<Map.Entry<String,Object>> getAttributes();

    /**
     * Adds a property to the property set.
     *
     * @param prop the <code>Property</code> object to be added.
     */
    public void addProperty( Property prop );

    /**
     * Adds a property to the property set.
     *
     * @param idx the index of the property to be set.
     * @param prop the <code>Property</code> object to be set.
     */
    public void setProperty( int idx, Property prop );

    /**
     * Returns a property by name.
     *
     * @return the <code>Property</code> found (or null otherwise).
     */
    public Property getProperty( String name );

    /**
     * Returns an array of properties which are members of this <code>PropertySet</code> object.
     *
     * @return an (potentially empty) array of <code>Property</code> objects
     */
    public Property[] getProperties();

    /**
     * Returns the list of properties matching the given name (label and / or name).
     *
     * @param item the item to search.
     * 
     * @return an (potentially empty) array of <code>Property</code> objects representing the item.
     */
    public Property[] getProperties( final String item );

    /**
     * Empties the list of properties.
     *
     */
    public void clearProperties();

}
