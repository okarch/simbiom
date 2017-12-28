package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Resolver;
import com.emd.util.Stringx;


/**
 * <code>Subject</code> represents a sample donor in a study.
 *
 * Created: Tue Feb 24 21:32:49 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Subject implements Copyable {
    private long donorid;
    private long studyid;
    private long taxon;
    private long orgid;

    private int age;

    private String subjectid;
    private String species;
    private String gender;
    private String ethnicity;
    private String Usubjid;

    private Timestamp enrolled;

    private static Log log = LogFactory.getLog(Subject.class);

    private static final String[] DATE_FORMATS = {
	"yyyy-MM-dd",
	"dd-MMM-yyyy",
	"yyyy-MMM-dd",
	"yyyy.MM.dd",
	"dd.MM.yyyy",
	"dd-MM-yyyy"
    };


    /**
     * Creates a new <code>Subject</code> instance.
     */
    public Subject() {
	this.donorid = DataHasher.hash( UUID.randomUUID().toString().getBytes() );
	this.subjectid = "";
	this.species = "human";
	this.taxon = 9606L;
    }

    /**
     * Formats a subject name. Formats are using a combination of variable name and masks
     * Exsamples:
     * %{site:???}%{d:-}%{subject:????} 001-0001
     * %{site:---}%{subject:????} deletes the site from the a combined subject identifier containing a site and subject, 0001
     * %{site:000}%{subject:9999} if site is missing it will be replaced by 000, if subject is missing it will be replaced by 9999
     *
     * @param siteId a site id.
     * @param subjectId a subject id.
     * @param format the format of the subject identifier, e.g. 
     * @return a formatted subject id.
     */
    public static String formatSubjectId( String siteId, String subjectId, String format ) {
	String fmt = format;
	if( (format == null) || (format.trim().length() <= 0) )
	    fmt = "%{subject}";
	Properties vars = new Properties();
	vars.put( "site", StringUtils.replaceChars(Stringx.getDefault( siteId,"" ).trim(),"_:.- =#/+", "" ));
	vars.put( "subject", StringUtils.replaceChars(Stringx.getDefault( subjectId,"" ).trim(),"_:.- =#/+", "" ));

	return Stringx.substituteAll( format, "%{", "}", new SubjectFormatter( vars ) );
    }

    /**
     * Calculates the age from the birth date. If format is not given (null) multiple formats are tested.
     *
     * @param dtString the birth date.
     * @param format the format of the date, see SimpleDateFormatter.
     *
     * @return the age or 0 (in case of invalid date)
     */
    public static int ageFromBirthDate( String dtString, String format ) {
	String[] fmts = null;
	if( format == null ) 
	    fmts = DATE_FORMATS;
	else
	    fmts = new String[] { format };

	Date birthDt = null;
	try {
	    birthDt = DateUtils.parseDateStrictly( dtString, fmts );
	}
	catch( ParseException pex ) {
	    birthDt = null;
	}

	if( birthDt == null )
	    return 0;
	long ageMillis = System.currentTimeMillis() - birthDt.getTime();
	double ageNum = (double) (ageMillis / (365L * 24L * 60L * 60L * 1000L));
	if( (ageNum < 0) || (ageNum > 120d) )
	    return 0;
	if( (ageNum > 0) && (ageNum < 1) )
	    return 1;
	return (int)ageNum;
    }

    /**
     * Calculates the age from the birth date. Multiple date formats will be tested.
     *
     * @param dtString the birth date.
     *
     * @return the age or 0 (in case of invalid date)
     */
    public static int ageFromBirthDate( String dtString ) {
	return ageFromBirthDate( dtString, null );
    }

    /**
     * Creates a Timestamp from a date string. 
     *
     * @param dtString the date string.
     * @param format the format of the date, see SimpleDateFormatter.
     *
     * @return the <code>Timestamp</code> object.
     */
    public static Timestamp formatTimestamp( String dtString, String format ) {
	String[] fmts = null;
	if( format == null ) 
	    fmts = DATE_FORMATS;
	else
	    fmts = new String[] { format };

	Date dt = null;
	try {
	    dt = DateUtils.parseDateStrictly( dtString, fmts );
	}
	catch( ParseException pex ) {
	    dt = null;
	}
	return ((dt != null)?(new Timestamp( dt.getTime() )):null);
    }

    /**
     * Creates a Timestamp from a date string. 
     *
     * @param dtString the date string.
     *
     * @return the <code>Timestamp</code> object.
     */
    public static Timestamp formatTimestamp( String dtString ) {
	return formatTimestamp( dtString, null );
    }

    /**
     * Get the <code>Donorid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getDonorid() {
	return donorid;
    }

    /**
     * Set the <code>Donorid</code> value.
     *
     * @param donorid The new Donorid value.
     */
    public final void setDonorid(final long donorid) {
	this.donorid = donorid;
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
     * Get the <code>Subjectid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSubjectid() {
	return subjectid;
    }

    /**
     * Set the <code>Subjectid</code> value.
     *
     * @param subjectid The new Subjectid value.
     */
    public final void setSubjectid(final String subjectid) {
	this.subjectid = subjectid;
    }

    /**
     * Get the <code>Taxon</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTaxon() {
	return taxon;
    }

    /**
     * Set the <code>Taxon</code> value.
     *
     * @param taxon The new Taxon value.
     */
    public final void setTaxon(final long taxon) {
	this.taxon = taxon;
    }

    /**
     * Get the <code>Species</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSpecies() {
	return species;
    }

    /**
     * Set the <code>Species</code> value.
     *
     * @param species The new Species value.
     */
    public final void setSpecies(final String species) {
	this.species = species;
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
     * Get the <code>Age</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getAge() {
	return age;
    }

    /**
     * Set the <code>Age</code> value.
     *
     * @param age The new Age value.
     */
    public final void setAge(final int age) {
	this.age = age;
    }

    /**
     * Get the <code>Gender</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getGender() {
	return gender;
    }

    /**
     * Set the <code>Gender</code> value.
     *
     * @param gender The new Gender value.
     */
    public final void setGender(final String gender) {
	this.gender = gender;
    }

    /**
     * Get the <code>Ethnicity</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getEthnicity() {
	return ethnicity;
    }

    /**
     * Set the <code>Ethnicity</code> value.
     *
     * @param ethnicity The new Ethnicity value.
     */
    public final void setEthnicity(final String ethnicity) {
	this.ethnicity = ethnicity;
    }

    /**
     * Get the <code>Usubjid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getUsubjid() {
	return Usubjid;
    }

    /**
     * Set the <code>Usubjid</code> value.
     *
     * @param Usubjid The new Usubjid value.
     */
    public final void setUsubjid(final String Usubjid) {
	this.Usubjid = Usubjid;
    }

    /**
     * Get the <code>Enrolled</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getEnrolled() {
	return enrolled;
    }

    /**
     * Set the <code>Enrolled</code> value.
     *
     * @param enrolled The new Enrolled value.
     */
    public final void setEnrolled(final Timestamp enrolled) {
	this.enrolled = enrolled;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Subject();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getSubjectid(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Subject ) {
	    Subject f = (Subject)obj;
	    return (f.getDonorid() == this.getDonorid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getDonorid()).hashCode();
    }

}

class SubjectFormatter implements Resolver {

    private Properties variables;

    private static Log log = LogFactory.getLog(SubjectFormatter.class);

    SubjectFormatter( Properties vars ) {
	this.variables = vars;
	log.debug( "Subject formatter created from variables: "+vars );
    }

    /**
     * Resolves the given string by its value
     *
     * @param var the variable name
     *
     * @return the value string
     */
    public String resolve( String var ) {
	int i = var.indexOf( ":" );
	String vName = var;
	String vPattern = "";
	if( (i > 0) && (i < var.length()-1) ) {
	    vName = var.substring(0,i);
	    vPattern = var.substring(i+1);
	}
	log.debug( "Variable name: "+vName+" pattern: "+vPattern );
	if( "d".equalsIgnoreCase(vName) ) 
	    return vPattern;
	String prop = variables.getProperty( vName, "" );
	StringBuilder stb = new StringBuilder();
	for( i = 0; i < vPattern.length(); i++ ) {
	    if( vPattern.charAt(i) == '-' )
		continue;
	    if( vPattern.charAt(i) == '?' ) {
		if( i < prop.length() ) 
		    stb.append( prop.charAt(i) );
		continue;
	    }
	    stb.append( vPattern.charAt(i) );
	}
	return stb.toString();
    }
}
