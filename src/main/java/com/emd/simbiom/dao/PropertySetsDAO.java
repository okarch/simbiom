package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Property;
import com.emd.simbiom.model.PropertySet;
import com.emd.simbiom.model.PropertyType;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Stringx;

/**
 * <code>PropertySetsDAO</code> implements the management of properties and property sets.
 *
 * Created: Tue Aug 21 20:37:19 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class PropertySetsDAO extends BasicDAO implements PropertySets {

    private static Log log = LogFactory.getLog(PropertySetsDAO.class);

    private static final String STMT_COLSPEC_DELETE      = "biobank.column.delete";
    private static final String STMT_COLSPEC_INSERT      = "biobank.column.insert";

    private static final String STMT_PROPERTY_BY_ID      = "biobank.property.findById";
    private static final String STMT_PROPERTY_BY_NAME    = "biobank.property.findByName";
    private static final String STMT_PROPERTY_BY_NAMETYPE= "biobank.property.findByNameType";
    private static final String STMT_PROPERTY_DELETE     = "biobank.property.delete";
    private static final String STMT_PROPERTY_INSERT     = "biobank.property.insert";
    private static final String STMT_PROPERTY_UPDATE     = "biobank.property.update";

    private static final String STMT_PROPERTYSET_BY_NAME = "biobank.propertyset.findByName";
    private static final String STMT_PROPERTYSET_BY_ID   = "biobank.propertyset.findById";
    private static final String STMT_PROPERTYSET_INSERT  = "biobank.propertyset.insert";
    private static final String STMT_PROPERTYSET_DELETE  = "biobank.propertyset.delete";

    private static final String STMT_PROPMEMBER_DELETE   = "biobank.member.delete";
    private static final String STMT_PROPMEMBER_INSERT   = "biobank.member.insert";
    private static final String STMT_PROPMEMBER_PROPERTY = "biobank.member.property";

    private static final String STMT_PROPERTYTYPE_BY_ID  = "biobank.propertytype.findById";
    private static final String STMT_PROPERTYTYPE_BY_NAME= "biobank.propertytype.findByName";
    private static final String STMT_PROPERTYTYPE_INSERT = "biobank.propertytype.insert";

    private static final String STMT_PROPERTYVAL_DELETE  = "biobank.propertyvalue.delete";
    private static final String STMT_PROPERTYVAL_BY_ID   = "biobank.propertyvalue.findById";
    private static final String STMT_PROPERTYVAL_BY_PROPERTY = "biobank.propertyvalue.findByProperty";
    private static final String STMT_PROPERTYVAL_INSERT  = "biobank.propertyvalue.insert";

    private static final String[] entityNames = new String[] {
	"column",
	"member",
	"property",
	"propertytype",
	"propertyset",
	"propertyvalue"
    };

    public PropertySetsDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns a specific <code>PropertyType</code> by name.
     *
     * @param typeName The type name.
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType findTypeByName( String typeName ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_PROPERTYTYPE_BY_NAME );
     	pstmt.setString( 1, typeName );
     	ResultSet res = pstmt.executeQuery();
     	PropertyType prop = null;
     	if( res.next() ) 
     	    prop = (PropertyType)TableUtils.toObject( res, new PropertyType() );
     	res.close();
	popStatement( pstmt );
     	return prop;
    }

    /**
     * Returns a specific <code>PropertyType</code> by id.
     *
     * @param tid The type id.
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType findTypeById( long tid ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_PROPERTYTYPE_BY_ID );
     	pstmt.setLong( 1, tid );
     	ResultSet res = pstmt.executeQuery();
     	PropertyType prop = null;
     	if( res.next() ) 
     	    prop = (PropertyType)TableUtils.toObject( res, new PropertyType() );
     	res.close();
	popStatement( pstmt );
     	return prop;
    }

    /**
     * Returns a newly created <code>PropertyType</code> object.
     *
     * @param typeName The type name.
     * @param label The type label (can be null).
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType createType( String typeName, String label ) throws SQLException {
	if( (typeName == null) || (typeName.trim().length() <= 0) ) 
	    throw new SQLException( "Property type is invalid" );

	PropertyType pType = new PropertyType();
	pType.setTypename( typeName.trim() );
	pType.setLabel( Stringx.getDefault( label, StringUtils.capitalize( typeName.trim() ) ) );

	PreparedStatement pstmt = getStatement( STMT_PROPERTYTYPE_INSERT );

	pstmt.setLong( 1, pType.getTypeid() );
	pstmt.setString( 2, pType.getTypename() );
	pstmt.setString( 3, pType.getLabel() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Property type created: ("+pType.getTypeid()+") "+pType.getTypename() );

	return pType;
    }

    /**
     * Returns property information by id.
     *
     * @param propertyid The property id.
     * @return the <code>Property</code> object.
     */
    public Property findPropertyById( long propertyid ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_PROPERTY_BY_ID );
     	pstmt.setLong( 1, propertyid );
     	ResultSet res = pstmt.executeQuery();
     	Property prop = null;
     	if( res.next() ) 
     	    prop = (Property)TableUtils.toObject( res, new Property() );
     	res.close();
	popStatement( pstmt );
     	return prop;
    }

    /**
     * Returns property information by name (and type).
     *
     * @param pName The property name.
     * @param pType The property type (can be null).
     * @return a (potentially empty) list of matching <code>Property</code> objects.
     */
    public Property[] findPropertyByName( String pName, String pType ) throws SQLException {

 	PreparedStatement pstmt = null;
	if( pType == null )
	    pstmt = getStatement( STMT_PROPERTY_BY_NAME );
	else {
	    pstmt = getStatement( STMT_PROPERTY_BY_NAMETYPE );
	    pstmt.setString( 2, pType );
	}

     	pstmt.setString( 1, pName );

     	ResultSet res = pstmt.executeQuery();
     	List<Property> fl = new ArrayList<Property>();
     	Iterator it = TableUtils.toObjects( res, new Property() );
	while( it.hasNext() ) {
	    fl.add( (Property)it.next() );
	}
	res.close();
	popStatement( pstmt );

	Property[] props = new Property[ fl.size() ];
	return (Property[])fl.toArray( props );
    }

    /**
     * Returns property information by name (and type).
     *
     * @param pName The property name.
     * @param pType The property type (can be null).
     * @return a (potentially empty) list of matching <code>Property</code> objects.
     */
    public Property[] findPropertyByName( String pName ) throws SQLException {
	return findPropertyByName( pName, null );
    }

    /**
     * Removes property with the given id.
     *
     * @param propertyid The property id.
     * @return the property removed.
     */
    public Property deleteProperty( long propertyid ) throws SQLException {
	Property prop = findPropertyById( propertyid );
	if( prop == null ) {
	    log.warn( "Cannot delete property: "+propertyid );
	    return null;
	}

	// delete property

	PreparedStatement pstmt = getStatement( STMT_PROPERTY_DELETE );
	pstmt.setLong( 1, propertyid );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Property deleted: "+prop );

	// delete column spec if existing

	// if( prop.getColumnid() != 0L ) {
	pstmt = getStatement( STMT_COLSPEC_DELETE );
	pstmt.setLong( 1, propertyid );
	pstmt.executeUpdate();
	popStatement( pstmt );
	log.debug( "Column specification deleted: "+prop.getColumnid() );

	pstmt = getStatement( STMT_PROPERTYVAL_DELETE );
	pstmt.setLong( 1, propertyid );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Property value(s) removed: "+prop.getValueid() );

     	return prop;
    }

    /**
     * Assigns a value to a property
     *
     * @param prop the property.
     * @param numVal the numeric value.
     *
     * @return the updated numeric value.
     */
    public Property assignPropertyValue( Property prop, double numVal )
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_PROPERTYVAL_DELETE );
	pstmt.setLong( 1, prop.getPropertyid() );
	pstmt.executeUpdate();
	popStatement( pstmt );
	
	long valId = prop.getValueid();
	if( valId == 0L ) 
	    valId = DataHasher.hash( UUID.randomUUID().toString().getBytes() );

	pstmt = getStatement( STMT_PROPERTYVAL_INSERT );
	pstmt.setLong( 1, valId );
	pstmt.setLong( 2, prop.getPropertyid() );
	pstmt.setString( 3, null );
	pstmt.setDouble( 4, numVal );
	pstmt.setInt( 5, 0 );
	pstmt.executeUpdate();

	prop.setValueid( valId );
	prop.setNumvalue( numVal );

	return prop;
    }

    /**
     * Assigns a value to a property
     *
     * @param prop the property.
     * @param numVal the numeric value.
     *
     * @return the updated numeric value.
     */
    public Property assignPropertyValue( Property prop, String charVal )
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_PROPERTYVAL_DELETE );
	pstmt.setLong( 1, prop.getPropertyid() );
	pstmt.executeUpdate();
	popStatement( pstmt );
	
	long valId = prop.getValueid();
	if( valId == 0L ) 
	    valId = DataHasher.hash( UUID.randomUUID().toString().getBytes() );

	pstmt = getStatement( STMT_PROPERTYVAL_INSERT );
	pstmt.setLong( 1, valId );
	pstmt.setLong( 2, prop.getPropertyid() );
	pstmt.setString( 3, charVal );
	pstmt.setDouble( 4, 0d );
	pstmt.setInt( 5, 0 );
	pstmt.executeUpdate();
	popStatement( pstmt );

	prop.setValueid( valId );
	prop.setCharvalue( charVal );

	return prop;
    }

    /**
     * Retrieves the a value of a property
     *
     * @param prop the property.
     *
     * @return the updated numeric value.
     */
    public Property retrievePropertyValue( Property prop ) 
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_PROPERTYVAL_BY_PROPERTY );
	pstmt.setLong( 1, prop.getPropertyid() );
     	ResultSet res = pstmt.executeQuery();
     	List<Property> fl = new ArrayList<Property>();
     	Iterator it = TableUtils.toObjects( res, new Property() );
	while( it.hasNext() ) {
	    fl.add( (Property)it.next() );
	}
	res.close();
	popStatement( pstmt );
	if( fl.size() > 0 ) {
	    Property pVal = fl.get(0);
	    prop.setValueid( pVal.getValueid() );
	    String charVal = pVal.getCharvalue();
	    if( charVal != null )
		prop.setCharvalue( charVal );
	    double numVal = pVal.getNumvalue();
	    if( numVal != 0d )
		prop.setNumvalue( numVal );
	}
	return prop;
    }

    /**
     * Stores a property.
     *
     * @param prop The property.
     * @return the stored property.
     */
    public Property storeProperty( long userId, Property prop ) throws SQLException {
	Property pUpd = findPropertyById( prop.getPropertyid() );

	// confirm type

	PropertyType pType = findTypeById( prop.getTypeid() );
	if( pType == null ) {
	    log.warn( "Property type id "+prop.getTypeid()+" does not exist. Assuming type id 0 (unknown)" );
	    pType = findTypeById( 0L );
	    prop.setTypeid( 0L );
	}
	prop.setTypename( pType.getTypename() );

     	PreparedStatement pstmt = null;
     	int nn = 2;
     	if( pUpd == null ) {
     	    pstmt = getStatement( STMT_PROPERTY_INSERT );
     	    pstmt.setLong( 1, prop.getPropertyid() );
     	    log.debug( "Creating a new property: "+prop.getPropertyid()+" called \""+prop.toString()+"\"" );
     	}
     	else {
     	    pstmt = getStatement( STMT_PROPERTY_UPDATE );
     	    pstmt.setLong( 7, prop.getPropertyid() );
     	    log.debug( "Updating existing property: "+prop.getPropertyid()+" called \""+prop.toString()+"\"" );
	    nn--;
     	}
	
     	pstmt.setString( nn, prop.getPropertyname() );
     	nn++;

     	pstmt.setString( nn, prop.getLabel() );
     	nn++;

     	pstmt.setLong( nn, prop.getTypeid() );
     	nn++;

     	pstmt.setString( nn, prop.getUnit() );
     	nn++;

     	pstmt.setLong( nn, prop.getParentid() );
     	nn++;

	prop.updateTrackid();

     	pstmt.setLong( nn, prop.getTrackid() );
     	nn++;

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Property stored: "+prop.getPropertyid()+" "+prop.toString() );	    

	pstmt = getStatement( STMT_COLSPEC_DELETE );
	pstmt.setLong( 1, prop.getPropertyid() );
	pstmt.executeUpdate();

	if( prop.hasColumnSpec() ) {
	    prop.setColumnid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	    pstmt = getStatement( STMT_COLSPEC_INSERT );
	    pstmt.setLong( 1, prop.getColumnid() );
	    pstmt.setLong( 2, prop.getPropertyid() );
	    pstmt.setString( 3, prop.getDbformat() );
	    pstmt.setString( 4, prop.getInformat() );
	    pstmt.setString( 5, prop.getOutformat() );
	    pstmt.setInt( 6, prop.getDigits() );
	    pstmt.setInt( 7, prop.getMinoccurs() );
	    pstmt.setInt( 8, prop.getMaxoccurs() );
	    pstmt.setString( 9, String.valueOf(prop.isMandatory()));
	    pstmt.executeUpdate();
	    popStatement( pstmt );
	    log.debug( "Column specification updated: "+prop.getColumnid()+", property: "+prop.getPropertyid()+" "+prop.toString() );
	}

	if( prop.hasValue() ) {
	    pstmt = getStatement( STMT_PROPERTYVAL_DELETE );
	    pstmt.setLong( 1, prop.getPropertyid() );
	    pstmt.executeUpdate();
	    popStatement( pstmt );

	    long valId = prop.getValueid();
	    if( valId == 0L ) 
		valId = DataHasher.hash( UUID.randomUUID().toString().getBytes() );

	    pstmt = getStatement( STMT_PROPERTYVAL_INSERT );
	    pstmt.setLong( 1, valId );
	    pstmt.setLong( 2, prop.getPropertyid() );
	    pstmt.setString( 3, prop.getCharvalue() );
	    pstmt.setDouble( 4, prop.getNumvalue() );
	    pstmt.setInt( 5, 0 );
	    pstmt.executeUpdate();
	    popStatement( pstmt );

	    prop.setValueid( valId );
	}

	trackChange( pUpd, prop, userId, "Property "+((pUpd==null)?"created":"updated"), null );

     	return prop;
    }

    /**
     * Returns an property set by id.
     *
     * @param setId the id of the property set.
     *
     * @return the <code>PropertySet</code> object matching the query.
     */
    public PropertySet findPropertySetById( long setId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_PROPERTYSET_BY_ID );
	pstmt.setLong( 1, setId );
	ResultSet res = pstmt.executeQuery();
	List<PropertySet> fl = new ArrayList<PropertySet>();
	Iterator it = TableUtils.toObjects( res, new PropertySet() );
	long lastSetid = -1L;
	PropertySet propSet = null;
     	while( it.hasNext() ) {
     	    PropertySet pSet = (PropertySet)it.next();
	    if( pSet.getListid() != lastSetid ) {
		propSet = pSet;
		fl.add( propSet );
		lastSetid = pSet.getListid();
	    }
	    Property prop = findPropertyById( pSet.getPropertyid() );
	    if( prop != null )
		propSet.addProperty( prop );
	}
     	res.close();
	popStatement( pstmt );

	return ((fl.size() <= 0)?null:fl.get(0));
    }

    /**
     * Returns an property set by name.
     *
     * @param setName the name of the property set.
     *
     * @return the <code>PropertySet</code> objects matching the query.
     */
    public PropertySet[] findPropertySetByName( String setName ) throws SQLException {
	String sName = Stringx.getDefault( setName, "" ).trim();
	if( sName.length() <= 0 )
	    sName = "%";
     	
	PreparedStatement pstmt = getStatement( STMT_PROPERTYSET_BY_NAME );
	pstmt.setString( 1, sName );
	ResultSet res = pstmt.executeQuery();
	List<PropertySet> fl = new ArrayList<PropertySet>();
	Iterator it = TableUtils.toObjects( res, new PropertySet() );
	long lastSetid = -1L;
	PropertySet propSet = null;
     	while( it.hasNext() ) {
     	    PropertySet pSet = (PropertySet)it.next();
	    if( pSet.getListid() != lastSetid ) {
		propSet = pSet;
		fl.add( propSet );
		lastSetid = pSet.getListid();
	    }
	    Property prop = findPropertyById( pSet.getPropertyid() );
	    if( prop != null )
		propSet.addProperty( prop );
     	}	       
     	res.close();
	popStatement( pstmt );

      	PropertySet[] facs = new PropertySet[ fl.size() ];
      	return (PropertySet[])fl.toArray( facs );	
    }

    /**
     * Create a property set.
     *
     * @param setName the property set name.
     * @param setType the type of the property set.
     *
     * @return the newly allocated property set.
     */
    public PropertySet createPropertySet( String setName, String setType ) throws SQLException {
	String sName = setName;
	if( (setName == null) || (setName.trim().length() <= 0) )
	    sName = UUID.randomUUID().toString();

	// return property set if existing

	PropertySet[] pSets = findPropertySetByName( sName );
     	if( pSets.length > 0 )
	    return pSets[0]; 

	// create property set type if not existing
	
	String sType = Stringx.getDefault( setType, "unknown" ).trim();
	if( sType.length() <= 0 )
	    sType = "unknown";
	PropertyType pType = findTypeByName( sType );
	if( pType == null )
	    pType = createType( sType );

	// create property set

	PropertySet pSet = new PropertySet();
	pSet.setListname( sName );
	pSet.setTypeid( pType.getTypeid() );

	PreparedStatement pstmt = getStatement( STMT_PROPERTYSET_INSERT );

	pstmt.setLong( 1, pSet.getListid() );
	pstmt.setString( 2, pSet.getListname() );
	pstmt.setLong( 3, pSet.getTypeid() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Property set created: "+pSet.getListname()+" ("+
		   pSet.getListid()+") typeid: "+pSet.getTypeid() );

	return pSet;
    }

    /**
     * Returns an property set by id.
     *
     * @param setId the id of the property set.
     *
     * @return the <code>PropertySet</code> object matching the query.
     */
    public PropertySet deletePropertySet( long setId ) throws SQLException {
	PropertySet pSet = findPropertySetById( setId );
	if( pSet == null ) {
	    log.error( "Cannot find property set "+setId );
	    return null;
	}
	Property[] props = pSet.getProperties();
	for( int i = 0; i < props.length; i++ ) 
	    deleteProperty( props[i].getPropertyid() );

	PreparedStatement pstmt = getStatement( STMT_PROPERTYSET_DELETE );
	pstmt.setLong( 1, pSet.getListid() );
	ResultSet res = pstmt.executeQuery();

	pstmt = getStatement( STMT_PROPMEMBER_DELETE );
	pstmt.setLong( 1, pSet.getListid() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	return pSet;
    }

    /**
     * Store a property set.
     *
     * @param userId the user id.
     * @param pSet the property set.
     *
     * @return the updated property set.
     */
    public PropertySet storePropertySet( long userId, PropertySet pSet ) throws SQLException {
	PropertySet eSet = findPropertySetById( pSet.getListid() );
	if( eSet == null ) 
	    throw new SQLException( "Property set does not exist: "+pSet );

	Property[] props = pSet.getProperties();
	for( int i = 0; i < props.length; i++ ) {
	    Property prop = storeProperty( userId, props[i] );
	    pSet.setProperty( i, prop );
	}
	log.debug( "Number of properties stored: "+props.length );

	// delete property membership if existing and re-establish to ensure proper order 
	// and to confirm attributes changed

	props = pSet.getProperties();

	PreparedStatement delStmt = getStatement( STMT_PROPMEMBER_DELETE );
	delStmt.setLong( 1, pSet.getListid() );
	delStmt.executeUpdate();
	popStatement( delStmt );

	log.debug( "Removed all members from property set: "+pSet );

	PreparedStatement insStmt = getStatement( STMT_PROPMEMBER_INSERT );
	for( int i = 0; i < props.length; i++ ) {
	    insStmt.setLong( 1, pSet.getListid() );
	    insStmt.setLong( 2, props[i].getPropertyid() );
	    insStmt.setInt( 3, i+1 );
	    insStmt.setString( 4, String.valueOf(props[i].isDisplay()) );
	    insStmt.executeUpdate();
	}
	popStatement( insStmt );

	log.debug( props.length+" members re-confirmed, property set: "+pSet );
	
	return pSet;
    }

    /**
     * Returns a newly created <code>PropertyType</code> object.
     *
     * @param typeName The type name.
     * @return the <code>PropertyType</code> object.
     */
    public PropertyType createType( String typeName ) throws SQLException {
	return createType( typeName, null );
    }

}
