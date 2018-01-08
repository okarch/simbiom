package com.emd.simbiom.model;

import java.math.BigDecimal;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>PropertyType</code> specifies the type of a <code>Property</code> or <code>PropertySet</code>.
 *
 * Created: Sat Jul 29 21:18:08 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class PropertyType implements Copyable {
    private long typeid;

    private String typename;
    private String label;

    public static final long TYPE_UNKNOWN   = 0L;
    public static final long TYPE_NUMBER    = 2L;
    public static final long TYPE_STRING    = 4L;

    public PropertyType() {
	this.typeid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
    }

    /**
     * Suggests a typid from the content of the object.
     *
     * @param value the value to inspect.
     * @return an property type (or unknown if it could not be detected).
     */
    public static long suggestTypeid( Object value ) {
	if( value != null ) {
	    if( value instanceof Number )
		return TYPE_NUMBER;
	    try {
		BigDecimal nn = new BigDecimal( value.toString() );
		return TYPE_NUMBER;
	    }
	    catch( NumberFormatException nfe ) {
		// ignore, it's always string
	    }
	    return TYPE_STRING;
	}
	return TYPE_UNKNOWN;
    }

    public static double toDouble( Object value, double def ) {
	if( value == null )
	    return def;
	if( value instanceof Number )
	    return ((Number)value).doubleValue();
	try {
	    BigDecimal nn = new BigDecimal( value.toString() );
	    return nn.doubleValue();
	}
	catch( NumberFormatException nfe ) {
	}
	return def;
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
     * Get the <code>Label</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getLabel() {
	return label;
    }

    /**
     * Set the <code>Label</code> value.
     *
     * @param label The new Label value.
     */
    public final void setLabel(final String label) {
	this.label = label;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	PropertyType pType = new PropertyType();
	pType.typename = this.typename;
	pType.label = this.label;
	return pType;
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	String st =  Stringx.getDefault( getLabel(), Stringx.getDefault( getTypename(), String.valueOf( getTypeid() ) ) );
	return st;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof PropertyType ) {
	    PropertyType f = (PropertyType)obj;
	    return (f.getTypeid() == this.getTypeid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getTypeid()).hashCode();
    }

}
