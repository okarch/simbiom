package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Property;
import com.emd.simbiom.model.PropertyHolder;
import com.emd.simbiom.model.PropertySet;
import com.emd.simbiom.model.PropertyType;


/**
 * <code>PropertySets</code> specifies the API to manage properties and property sets.
 *
 * Created: Tue Aug 21 21:37:19 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface PropertySets {

    /**
     * Returns a specific <code>PropertyType</code> by name.
     *
     * @param typeName The type name.
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType findTypeByName( String typeName ) throws SQLException;

    /**
     * Returns a specific <code>PropertyType</code> by id.
     *
     * @param tid The type id.
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType findTypeById( long tid ) throws SQLException;

    /**
     * Returns a newly created <code>PropertyType</code> object.
     *
     * @param typeName The type name.
     * @param label The type label (can be null).
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType createType( String typeName, String label ) throws SQLException;

    /**
     * Returns property information by id.
     *
     * @param propertyid The property id.
     * @return the <code>Property</code> object.
     */
    public Property findPropertyById( long propertyid ) throws SQLException;

    /**
     * Returns property information by name (and type).
     *
     * @param pName The property name.
     * @param pType The property type (can be null).
     * @return a (potentially empty) list of matching <code>Property</code> objects.
     */
    public Property[] findPropertyByName( String pName, String pType ) throws SQLException;

    /**
     * Returns property information by name (and type).
     *
     * @param pName The property name.
     * @param pType The property type (can be null).
     * @return a (potentially empty) list of matching <code>Property</code> objects.
     */
    public Property[] findPropertyByName( String pName ) throws SQLException;

    /**
     * Removes property with the given id.
     *
     * @param propertyid The property id.
     * @return the property removed.
     */
    public Property deleteProperty( long propertyid ) throws SQLException;

    /**
     * Assigns a value to a property
     *
     * @param prop the property.
     * @param numVal the numeric value.
     *
     * @return the updated numeric value.
     */
    public Property assignPropertyValue( Property prop, double numVal )
	throws SQLException;

    /**
     * Assigns a value to a property
     *
     * @param prop the property.
     * @param numVal the numeric value.
     *
     * @return the updated numeric value.
     */
    public Property assignPropertyValue( Property prop, String charVal )
	throws SQLException;

    /**
     * Retrieves the a value of a property
     *
     * @param prop the property.
     *
     * @return the updated numeric value.
     */
    public Property retrievePropertyValue( Property prop ) 
	throws SQLException;

    /**
     * Stores a property.
     *
     * @param prop The property.
     * @return the stored property.
     */
    public Property storeProperty( long userId, Property prop ) throws SQLException;

    /**
     * Returns an property set by id.
     *
     * @param setId the id of the property set.
     *
     * @return the <code>PropertySet</code> object matching the query.
     */
    public PropertySet findPropertySetById( long setId ) throws SQLException;

    /**
     * Returns an property set by name.
     *
     * @param setName the name of the property set.
     *
     * @return the <code>PropertySet</code> objects matching the query.
     */
    public PropertySet[] findPropertySetByName( String setName ) throws SQLException;

    /**
     * Create a property set.
     *
     * @param setName the property set name.
     * @param setType the type of the property set.
     *
     * @return the newly allocated property set.
     */
    public PropertySet createPropertySet( String setName, String setType ) throws SQLException;

    /**
     * Returns an property set by id.
     *
     * @param setId the id of the property set.
     *
     * @return the <code>PropertySet</code> object matching the query.
     */
    public PropertySet deletePropertySet( long setId ) throws SQLException;

    /**
     * Store a property set.
     *
     * @param userId the user id.
     * @param pSet the property set.
     *
     * @return the updated property set.
     */
    public PropertySet storePropertySet( long userId, PropertySet pSet ) throws SQLException;

    /**
     * Returns a newly created <code>PropertyType</code> object.
     *
     * @param typeName The type name.
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType createType( String typeName ) throws SQLException;

    /**
     * Store a <code>PropertyHolder</code> object.
     *
     * @param userId the user id.
     * @param pHolderId the property holder object id.
     * @param pHolder the property holder.
     * @param insertHolder the insert statement for new properties.
     *
     * @return the updated property set.
     */
    public PropertyHolder storePropertyHolder( long userId, 
					       String pHolderId, 
					       PropertyHolder pHolder, 
					       String insertHolder ) 
	throws SQLException;

}
