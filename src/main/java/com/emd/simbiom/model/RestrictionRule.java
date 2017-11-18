package com.emd.simbiom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>RestrictionRule</code> represents a sample restriction class (e.g. storage duration).
 *
 * Created: Thu Nov  2 18:23:15 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class RestrictionRule implements Copyable {
    private long restrictid;
    private long propertyid;
    private long typeid;
    private long parentid;

    private String rule;
    private String restriction;
    private String datatype;
    private String propertyname;
    private String typename;
    private String label;
    private String unit;

    private List<String> choices;


    public RestrictionRule() {
	this.restrictid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.rule="";
	this.choices = new ArrayList<String>();
    }

    /**
     * Get the <code>Restrictid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getRestrictid() {
	return restrictid;
    }

    /**
     * Set the <code>Restrictid</code> value.
     *
     * @param restrictid The new Restrictid value.
     */
    public final void setRestrictid(final long restrictid) {
	this.restrictid = restrictid;
    }

    /**
     * Get the <code>Rule</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getRule() {
	return rule;
    }

    /**
     * Set the <code>Rule</code> value.
     *
     * @param rule The new Rule value.
     */
    public final void setRule(final String rule) {
	this.rule = rule;
    }

    /**
     * Get the <code>Restriction</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getRestriction() {
	return restriction;
    }

    /**
     * Set the <code>Restriction</code> value.
     *
     * @param restriction The new Restriction value.
     */
    public final void setRestriction(final String restriction) {
	this.restriction = restriction;
    }

    /**
     * Get the <code>Datatype</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getDatatype() {
	return datatype;
    }

    /**
     * Set the <code>Datatype</code> value.
     *
     * @param datatype The new Datatype value.
     */
    public final void setDatatype(final String datatype) {
	this.datatype = datatype;
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
     * Adds an item to the list of choices.
     *
     * @param choice the item to add.
     */
    public final void addChoice( final String choice ) {
	choices.add( choice );
    }

    /**
     * Adds all items from the given array to the list of choices.
     *
     * @param items an array of items.
     */
    public final void addChoices( final String[] items ) {
	for( int i = 0; i < items.length; i++ )
	    choices.add( items[i] );
    }

    /**
     * Returns an array of choice items.
     *
     * @return an (potentially empty) array of items.
     */
    public String[] getChoices() {
	String[] cols = new String[choices.size()];
	return (String[])choices.toArray( cols );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new RestrictionRule();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getRule(), String.valueOf( getRestrictid() ) );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof User ) {
	    RestrictionRule f = (RestrictionRule)obj;
	    return (f.getRestrictid() == this.getRestrictid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getRestrictid()).hashCode();
    }

}
