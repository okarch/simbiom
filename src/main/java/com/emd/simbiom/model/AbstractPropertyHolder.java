package com.emd.simbiom.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Stringx;


/**
 * <code>AbstractPropertyHolder</code> implements an interface to retrieve properties.
 *
 * Created: Fri Jun 14 19:31:38 2019
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class AbstractPropertyHolder implements PropertyHolder {
    private long propertyid;

    private List<Property> properties;
    private Map<String,Object> attributes;

    protected AbstractPropertyHolder() {
	this.properties = new ArrayList<Property>();
	this.attributes = new HashMap<String,Object>();
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
     * @param idx the index of the property to be set.
     * @param prop the <code>Property</code> object to be set.
     */
    public void setAttribute( String attrName, Object value ) {
	this.attributes.put( attrName, value );
    }

    /**
     * Returns the set of attributes.
     *
     * @return a Set of <code>Map.Entry</code> objects.
     */
    public Set<Map.Entry<String,Object>> getAttributes() {
	return this.attributes.entrySet();
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
     * Returns a property by name.
     *
     * @return the <code>Property</code> found (or null otherwise).
     */
    public Property getProperty( String name ) {
	for( Property p : this.properties ) {
	    if( p.toString().equals( name ) )
		return p;
	}
	return null;
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
     * Returns the list of properties matching the given name (label and / or name).
     *
     * @param item the item to search.
     * 
     * @return an (potentially empty) array of <code>Property</code> objects representing the item.
     */
    public Property[] getProperties( final String item ) {
	Property[] props = getProperties();
	List<Property> pnList = new ArrayList<Property>();
	for( int i = 0; i < props.length; i++ ) {
	    if( props[i].toString().equals( item ) ) 
		pnList.add( props[i] );
	}
	Property[] cols = new Property[pnList.size()];
	return (Property[])pnList.toArray( cols );
    }	

    /**
     * Empties the list of properties.
     *
     */
    public void clearProperties() {
	properties.clear();
    }

    /**
     * Calculates a content hash derived from the attribute value pairs.
     *
     * @return the content hash derived from attribute value pairs.
     */
    public long getPropertyContentId() {
	StringBuilder stb = new StringBuilder();
	Set<Map.Entry<String,Object>> entries = this.getAttributes();
	for( Map.Entry me : entries ) {
	    Object key = me.getKey();
	    Object val = me.getValue();
	    if( key != null ) 
		stb.append( key.toString() );
	    if( val != null )
		stb.append( val.toString() );
	}
	Property[] props = getProperties();
	for( int i = 0; i < props.length; i++ ) {
	    stb.append( String.valueOf(props[i].getTrackid()) );
	    stb.append( Stringx.getDefault( props[i].getCharvalue(), "" ) );
	    stb.append( String.valueOf( props[i].getNumvalue() ) );
	}
	return DataHasher.hash( stb.toString().getBytes() );
    }

}
