package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>SampleEvent</code> represents timpoints at which samples are taken.
 *
 * Created: Wed Mar  4 08:00:15 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class SampleEvent implements Copyable {
    private long timeid;
    private long orgid;

    private String visit;
    private String cycle;
    private String dosage;
    private String unit;

    private int day;

    private float hour;
    private float quantity;

    private static final String PATTERN_CYCLE = "CY?C?L?E?([0-9]+)";
    private static final String PATTERN_DAY   = "DA?Y?([0-9]+)";
    private static final String PATTERN_HOUR  = "HO?U?R?([0-9]*[.]?[0-9]*)";

    public static final String SHIPMENT = "shipment";
    public static final String RECEIVED = "received";

    /**
     * Creates a new sample timepoint.
     */
    public SampleEvent() {
	this.timeid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.visit = "";
    }

    /**
     * Parses a visit label into cycle, day and hour
     * specific timepoints.
     *
     * @param visitDesc the visit description
     * @return an initialized sample event.
     */
    public static SampleEvent parseVisit( String visitDesc ) {
	SampleEvent se = new SampleEvent();
	String normVisit = StringUtils.replaceChars( visitDesc.trim().toUpperCase(), "_:;+#-()[]{}!%&/?=|<> ", "" );
	try {
	    Matcher m = Pattern.compile( PATTERN_CYCLE ).matcher( normVisit );
	    while( m.find() ) {
		if( m.groupCount() > 0 ) 
		    se.setCycle( m.group(1) );
	    }
	    m = Pattern.compile( PATTERN_DAY ).matcher( normVisit );
	    while( m.find() ) {
		if( m.groupCount() > 0 ) 
		    se.setDay( Stringx.toInt(m.group(1), 0) );
	    }
	    m = Pattern.compile( PATTERN_HOUR ).matcher( normVisit );
	    while( m.find() ) {
		if( m.groupCount() > 0 ) 
		    se.setHour( Stringx.toFloat(m.group(1), 0F) );
	    }
	}
	catch( PatternSyntaxException pse ) {
	    // should not happen ;-)
	}
	return se;
    }

    /**
     * Creates a specific sample shipment event.
     *
     * @param desc additional description (goes to dosage).
     * @return an initialized sample event.
     */
    public static SampleEvent createLogistics( String evtType, String desc ) {
	SampleEvent se = new SampleEvent();
	se.setVisit( evtType );
	if( desc != null )
	    se.setDosage( desc.trim() );
	return se;
    }

    /**
     * Get the <code>Timeid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTimeid() {
	return timeid;
    }

    /**
     * Set the <code>Timeid</code> value.
     *
     * @param timeid The new Timeid value.
     */
    public final void setTimeid(final long timeid) {
	this.timeid = timeid;
    }

    /**
     * Get the <code>Orgid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getOrgid() {
	return orgid;
    }

    /**
     * Set the <code>Orgid</code> value.
     *
     * @param orgid The new Orgid value.
     */
    public final void setOrgid(final long orgid) {
	this.orgid = orgid;
    }

    /**
     * Get the <code>Visit</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getVisit() {
	return visit;
    }

    /**
     * Set the <code>Visit</code> value.
     *
     * @param visit The new Visit value.
     */
    public final void setVisit(final String visit) {
	this.visit = visit;
    }

    /**
     * Get the <code>Cycle</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getCycle() {
	return cycle;
    }

    /**
     * Set the <code>Cycle</code> value.
     *
     * @param cycle The new Cycle value.
     */
    public final void setCycle(final String cycle) {
	this.cycle = cycle;
    }

    /**
     * Get the <code>Day</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getDay() {
	return day;
    }

    /**
     * Set the <code>Day</code> value.
     *
     * @param day The new Day value.
     */
    public final void setDay(final int day) {
	this.day = day;
    }

    /**
     * Get the <code>Hour</code> value.
     *
     * @return a <code>float</code> value
     */
    public final float getHour() {
	return hour;
    }

    /**
     * Set the <code>Hour</code> value.
     *
     * @param hour The new Hour value.
     */
    public final void setHour(final float hour) {
	this.hour = hour;
    }

    /**
     * Get the <code>Dosage</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getDosage() {
	return dosage;
    }

    /**
     * Set the <code>Dosage</code> value.
     *
     * @param dosage The new Dosage value.
     */
    public final void setDosage(final String dosage) {
	this.dosage = dosage;
    }

    /**
     * Get the <code>Quantity</code> value.
     *
     * @return a <code>float</code> value
     */
    public final float getQuantity() {
	return quantity;
    }

    /**
     * Set the <code>Quantity</code> value.
     *
     * @param quantity The new Quantity value.
     */
    public final void setQuantity(final float quantity) {
	this.quantity = quantity;
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
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new SampleEvent();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getVisit(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof SampleEvent ) {
	    SampleEvent f = (SampleEvent)obj;
	    return (f.getTimeid() == this.getTimeid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getTimeid()).hashCode();
    }

}
