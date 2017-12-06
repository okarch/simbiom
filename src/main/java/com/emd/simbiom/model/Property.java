package com.emd.simbiom.model;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Property</code> defines data columns and other attributes.
 *
 * Created: Mon Jul 17 10:55:20 2017
 *
 * @author <a href="mailto:m01061@tonga.bci.merck.de">Oliver Karch</a>
 * @version 1.0
 */
public class Property extends AbstractTrackable implements Comparable, Copyable {
    private long propertyid;
    private long typeid;
    private long columnid;
    private long parentid;
    private long valueid;

    private String propertyname;
    private String label;
    private String typename;
    private String dbformat;
    private String informat;
    private String outformat;
    private String unit;
    private String charvalue;

    private int columnsize;
    private int digits;
    private int minoccurs;
    private int maxoccurs;

    private boolean mandatory;
    private boolean display;

    private boolean valuePresent;

    private static final String ITEM_TYPE = "property";
    /**
     * Describe numvalue here.
     */
    private double numvalue;

  
    public Property() {
	super( ITEM_TYPE );
	this.propertyid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.setTrackid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.display = true;
	this.valuePresent = false;
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
     * Get the <code>Propertyname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getPropertyname() {
	return propertyname;
    }

    /**
     * Set the <code>Propertyname</code> value.
     *
     * @param propertyname The new Propertyname value.
     */
    public final void setPropertyname(final String propertyname) {
	this.propertyname = propertyname;
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
     * Updates the trackid to reflect the current content.
     *
     * @return the updated trackid
     */
    public long updateTrackid() {
	long contId = contentId();
	setTrackid( contId );
	return contId;
    }

    protected long contentId() {
	StringBuilder stb = new StringBuilder();
	stb.append( getPropertyid() );
	stb.append( Stringx.getDefault( getPropertyname(), "" ) );
	stb.append( getTypeid() );
	stb.append( getParentid() );
	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Get the <code>Trackid</code> value.
     *
     * @return a <code>long</code> value
     */

    /**
     * Get the <code>Dbformat</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getDbformat() {
	return dbformat;
    }

    /**
     * Set the <code>Dbformat</code> value.
     *
     * @param dbformat The new Dbformat value.
     */
    public final void setDbformat(final String dbformat) {
	this.dbformat = dbformat;
    }

    /**
     * Get the <code>Informat</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getInformat() {
	return informat;
    }

    /**
     * Set the <code>Informat</code> value.
     *
     * @param informat The new Informat value.
     */
    public final void setInformat(final String informat) {
	this.informat = informat;
    }

    /**
     * Get the <code>Outformat</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getOutformat() {
	return outformat;
    }

    /**
     * Set the <code>Outformat</code> value.
     *
     * @param outformat The new Outformat value.
     */
    public final void setOutformat(final String outformat) {
	this.outformat = outformat;
    }

    /**
     * Get the <code>Columnsize</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getColumnsize() {
	return columnsize;
    }

    /**
     * Set the <code>Columnsize</code> value.
     *
     * @param columnsize The new Columnsize value.
     */
    public final void setColumnsize(final int columnsize) {
	this.columnsize = columnsize;
    }

    /**
     * Get the <code>Digits</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getDigits() {
	return digits;
    }

    /**
     * Set the <code>Digits</code> value.
     *
     * @param digits The new Digits value.
     */
    public final void setDigits(final int digits) {
	this.digits = digits;
    }

    /**
     * Get the <code>Minoccurs</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getMinoccurs() {
	return minoccurs;
    }

    /**
     * Set the <code>Minoccurs</code> value.
     *
     * @param minoccurs The new Minoccurs value.
     */
    public final void setMinoccurs(final int minoccurs) {
	this.minoccurs = minoccurs;
    }

    /**
     * Get the <code>Maxoccurs</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getMaxoccurs() {
	return maxoccurs;
    }

    /**
     * Set the <code>Maxoccurs</code> value.
     *
     * @param maxoccurs The new Maxoccurs value.
     */
    public final void setMaxoccurs(final int maxoccurs) {
	this.maxoccurs = maxoccurs;
    }

    /**
     * Get the <code>Mandatory</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isMandatory() {
	return mandatory;
    }

    /**
     * Set the <code>Mandatory</code> value.
     *
     * @param mandatory The new Mandatory value.
     */
    public final void setMandatory(final boolean mandatory) {
	this.mandatory = mandatory;
    }

    /**
     * Get the <code>Columnid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getColumnid() {
	return columnid;
    }

    /**
     * Set the <code>Columnid</code> value.
     *
     * @param columnid The new Columnid value.
     */
    public final void setColumnid(final long columnid) {
	this.columnid = columnid;
    }

    /**
     * Get the <code>Display</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isDisplay() {
	return display;
    }

    /**
     * Set the <code>Display</code> value.
     *
     * @param display The new Display value.
     */
    public final void setDisplay(final boolean display) {
	this.display = display;
    }

    /**
     * Get the <code>Unit</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getUnit() {
	return unit;
    }

    /**
     * Set the <code>Unit</code> value.
     *
     * @param unit The new Unit value.
     */
    public final void setUnit(final String unit) {
	this.unit = unit;
    }

    /**
     * Get the <code>Parentid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getParentid() {
	return parentid;
    }

    /**
     * Set the <code>Parentid</code> value.
     *
     * @param parentid The new Parentid value.
     */
    public final void setParentid(final long parentid) {
	this.parentid = parentid;
    }

    /**
     * Determines if a column specification is present.
     * @return true if a column specification is present, false otherwise.
     */
    public boolean hasColumnSpec() {
	return ( (dbformat != null) || (informat != null) || (outformat != null) ||
		 (columnsize > 0) || (digits != 0) || (minoccurs > 0) || (maxoccurs != 0) ); 
    }

    /**
     * Get the <code>Valueid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getValueid() {
	return valueid;
    }

    /**
     * Set the <code>Valueid</code> value.
     *
     * @param valueid The new Valueid value.
     */
    public final void setValueid(final long valueid) {
	this.valueid = valueid;
    }

    /**
     * Get the <code>Charvalue</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getCharvalue() {
	return charvalue;
    }

    /**
     * Set the <code>Charvalue</code> value.
     *
     * @param charvalue The new Charvalue value.
     */
    public final void setCharvalue(final String charvalue) {
	this.valuePresent = true;
	this.charvalue = charvalue;
    }

    /**
     * Get the <code>Numvalue</code> value.
     *
     * @return a <code>double</code> value
     */
    public final double getNumvalue() {
	return numvalue;
    }

    /**
     * Set the <code>Numvalue</code> value.
     *
     * @param numvalue The new Numvalue value.
     */
    public final void setNumvalue(final double numvalue) {
	this.valuePresent = true;
	this.numvalue = numvalue;
    }

    /**
     * Checks if a value has been set.
     */
    public boolean hasValue() {
	return ((this.valueid != 0L) || (this.valuePresent));
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Property();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	String st =  Stringx.getDefault( Stringx.getDefault( getLabel(), getPropertyname() ), "" );
	return ((st.length() > 0)?st:String.valueOf(getPropertyid()));
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Property ) {
	    Property f = (Property)obj;
	    return (f.getPropertyid() == this.getPropertyid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getPropertyid()).hashCode();
    }

    /**
     * Compares this object with the specified object for order. 
     * Returns a negative integer, zero, or a positive integer as this object is less 
     * than, equal to, or greater than the specified object. 
     *
     */
    public int compareTo( Object o) {
	return getPropertyname().compareTo( ((Property)o).getPropertyname() );
    }

}
