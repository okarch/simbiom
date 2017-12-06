package com.emd.simbiom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Restriction</code> describes a restriction applied to a study and / or site.
 *
 * Created: Sun Dec  3 12:26:55 2017
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Restriction implements Copyable {
    private long applyid;
    private long restrictid;
    private long studyid;
    private long orgid;
    private long propertyid;

    private String charvalue;

    private double numvalue;

    private RestrictionRule restrictionRule;
    private Study study;
    private Organization organization;
    private Property property;

    /**
     * Creates a new restriction.
     */
    public Restriction() {
	this.applyid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.restrictid = 0L;
	this.studyid = 0L;
	this.orgid = 0L;
	this.propertyid = 0L;
    }

    /**
     * Get the <code>Applyid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getApplyid() {
	return applyid;
    }

    /**
     * Set the <code>Applyid</code> value.
     *
     * @param applyid The new Applyid value.
     */
    public final void setApplyid(final long applyid) {
	this.applyid = applyid;
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
     * Get the <code>Studyid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getStudyid() {
	return studyid;
    }

    /**
     * Set the <code>Studyid</code> value.
     *
     * @param studyid The new Studyid value.
     */
    public final void setStudyid(final long studyid) {
	this.studyid = studyid;
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
	this.numvalue = numvalue;
    }

    /**
     * Get the <code>RestrictionRule</code> value.
     *
     * @return a <code>RestrictionRule</code> value
     */
    public final RestrictionRule getRestrictionRule() {
	return restrictionRule;
    }

    /**
     * Set the <code>RestrictionRule</code> value.
     *
     * @param restrictionRule The new RestrictionRule value.
     */
    public final void setRestrictionRule(final RestrictionRule restrictionRule) {
	this.restrictionRule = restrictionRule;
    }

    /**
     * Get the <code>Study</code> value.
     *
     * @return a <code>Study</code> value
     */
    public final Study getStudy() {
	return study;
    }

    /**
     * Set the <code>Study</code> value.
     *
     * @param study The new Study value.
     */
    public final void setStudy(final Study study) {
	this.study = study;
    }

    /**
     * Get the <code>Organization</code> value.
     *
     * @return an <code>Organization</code> value
     */
    public final Organization getOrganization() {
	return organization;
    }

    /**
     * Set the <code>Organization</code> value.
     *
     * @param organization The new Organization value.
     */
    public final void setOrganization(final Organization organization) {
	this.organization = organization;
    }

    /**
     * Get the <code>Property</code> value.
     *
     * @return a <code>Property</code> value
     */
    public final Property getProperty() {
	return property;
    }

    /**
     * Set the <code>Property</code> value.
     *
     * @param property The new Property value.
     */
    public final void setProperty(final Property property) {
	this.property = property;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Restriction();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return String.valueOf( getApplyid() );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Restriction ) {
	    Restriction f = (Restriction)obj;
	    return (f.getApplyid() == this.getApplyid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getApplyid()).hashCode();
    }

}
