package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Donor;
import com.emd.simbiom.model.Property;
import com.emd.simbiom.model.PropertyType;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.Subject;

import com.emd.util.Stringx;

/**
 * <code>ParticipantsDAO</code> implements the <code>Participants</code> API.
 *
 * Created: Mon Aug 27 16:48:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class ParticipantsDAO extends BasicDAO implements Participants {

    private static Log log = LogFactory.getLog(ParticipantsDAO.class);

    private static final String STMT_DONOR_BY_SAMPLE     = "biobank.donor.findBySample";
    private static final String STMT_DONOR_DUPLICATE     = "biobank.donor.duplicate";
    private static final String STMT_DONOR_INSERT        = "biobank.donor.insert";
    private static final String STMT_DONOR_TERMS         = "biobank.propertyvalue.findTerms";

    private static final String STMT_DONORPROP_INSERT    = "biobank.donorproperty.insert";

    private static final String STMT_SUBJECT_BY_ID       = "biobank.subject.findById";
    private static final String STMT_SUBJECT_BY_NAME     = "biobank.subject.findByName";
    private static final String STMT_SUBJECT_BY_SAMPLE   = "biobank.subject.findBySample";
    private static final String STMT_SUBJECT_INSERT      = "biobank.subject.insert";
    private static final String STMT_SUBJECT_UPDATE      = "biobank.subject.update";

    private static final long TYPE_NUMERIC               = 2L; // as defined in biobank.sql

    private static final String[] entityNames = new String[] {
	"donor",
	"donorproperty",
	"subject"
    };

    public ParticipantsDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

     /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Subject findSubjectByName( Study study, String subjectId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SUBJECT_BY_NAME );
	pstmt.setLong( 1, study.getStudyid() );
     	pstmt.setString( 2, Stringx.getDefault(subjectId,"").trim() );
     	ResultSet res = pstmt.executeQuery();

     	// Subject sType = null;
     	// if( res.next() ) 
     	//     sType = (Subject)TableUtils.toObject( res, new Subject() );
     	// res.close();

	Iterator it = TableUtils.toObjects( res, new Subject() );
	Subject subject = null;
	while( it.hasNext() ) {
	    Subject subj = (Subject)it.next();
	    if( subject == null )
		subject = subj;
	    Property prop = getDAO().findPropertyById( subj.getPropertyid() );
	    if( prop != null )
	 	subject.addProperty( prop );
	}
	res.close();
	popStatement( pstmt );

     	return subject;
    }

    /**
     * Returns a subject by id.
     *
     * @param donorId the subject's id.
     *
     * @return the <code>Subject</code> object.
     */
    public Subject findSubjectById( long donorId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SUBJECT_BY_ID );
	pstmt.setLong( 1, donorId );
     	ResultSet res = pstmt.executeQuery();

     	// Subject sType = null;
     	// if( res.next() ) 
     	//     sType = (Subject)TableUtils.toObject( res, new Subject() );
     	// res.close();

	Iterator it = TableUtils.toObjects( res, new Subject() );
	Subject subject = null;
	while( it.hasNext() ) {
	    Subject subj = (Subject)it.next();
	    if( subject == null )
		subject = subj;
	    Property prop = getDAO().findPropertyById( subj.getPropertyid() );
	    if( prop != null )
	 	subject.addProperty( prop );
	}
	res.close();
	popStatement( pstmt );

     	return subject;
    }

    /**
     * Returns a subject by study and sample.
     *
     * @param study the study.
     * @param sample the sample.
     * @return the Organization object.
     */
    public Subject findSubjectBySample( Study study, Sample sample ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SUBJECT_BY_SAMPLE );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, study.getStudyid() );
     	ResultSet res = pstmt.executeQuery();

	Iterator it = TableUtils.toObjects( res, new Subject() );
	Subject subject = null;
	while( it.hasNext() ) {
	    Subject subj = (Subject)it.next();
	    if( subject == null )
		subject = subj;
	    Property prop = getDAO().findPropertyById( subj.getPropertyid() );
	    if( prop != null )
	 	subject.addProperty( prop );
	}
	res.close();
	popStatement( pstmt );

     	// Subject sType = null;
     	// if( res.next() ) 
     	//     sType = (Subject)TableUtils.toObject( res, new Subject() );
     	// res.close();
     	// return sType;

     	return subject;
    }

    /**
     * Create a study subject.
     *
     * @param study the study.
     * @param subjectid the subject id.
     *
     * @return the newly allocated subject.
     */
    public Subject createSubject( Study study, String subjectId ) throws SQLException {
	Subject subj = findSubjectByName( study, subjectId );
	if( subj != null )
	    throw new SQLException( "Subject already exists: "+subjectId+" (study: "+study.getStudyname()+")" );

	subj = new Subject();
	subj.setStudyid( study.getStudyid() );
	subj.setSubjectid( subjectId );

	PreparedStatement pstmt = getStatement( STMT_SUBJECT_INSERT );
	pstmt.setLong( 1, subj.getDonorid() );
	pstmt.setLong( 2, subj.getStudyid() );
	pstmt.setString( 3, subj.getSubjectid() );
	pstmt.setString( 4, subj.getSpecies() );
	pstmt.setLong( 5, subj.getTaxon() );
	pstmt.setLong( 6, subj.getOrgid() );
	pstmt.setInt( 7, subj.getAge() );
	pstmt.setString( 8, subj.getGender() );
	pstmt.setString( 9, subj.getEthnicity() );
	pstmt.setString( 10, subj.getUsubjid() );
	pstmt.setTimestamp( 11, subj.getEnrolled() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Subject created: "+subj.getSubjectid()+" ("+
		   subj.getDonorid()+")" );
	 
	return subj;
    }

    /**
     * Updates a subject.
     *
     * @param userId the user id.
     * @param subject the subject.
     * @param props additional properties of the subject (can be null).
     *
     * @return the updated subject.
     */
    public Subject storeSubject( long userId, Subject subject ) throws SQLException {
	if( subject == null )
	    throw new SQLException( "Subject is invalid" );
	    
	Subject subj = findSubjectById( subject.getDonorid() );
	if( subj == null )
	    throw new SQLException( "Cannot find subject id: "+subject.getDonorid() );

	log.debug( "Subject "+subject+" age: "+subject.getAge()+" gender: "+subject.getGender()+" ethnicity: "+subject.getEthnicity() );

	PreparedStatement pstmt = getStatement( STMT_SUBJECT_UPDATE );

	pstmt.setLong( 11, subject.getDonorid() );

	pstmt.setLong( 1, subject.getStudyid() );
	pstmt.setString( 2, subject.getSubjectid() );
	pstmt.setString( 3, subject.getSpecies() );
	pstmt.setLong( 4, subject.getTaxon() );
	pstmt.setLong( 5, subject.getOrgid() );
	pstmt.setInt( 6, subject.getAge() );
	pstmt.setString( 7, subject.getGender() );
	pstmt.setString( 8, subject.getEthnicity() );
	pstmt.setString( 9, subject.getUsubjid() );
	pstmt.setTimestamp( 10, subject.getEnrolled() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Subject updated: "+subject.getSubjectid()+" ("+
		   subject.getDonorid()+")" );

	Set<Map.Entry<String,Object>> entries = subject.getAttributes();
	for( Map.Entry me : entries ) {
	    Object key = me.getKey();
	    Object val = me.getValue();
	    boolean addProp = false;
	    if( key != null ) {
		Property prop = subject.getProperty( key.toString() );
		if( prop == null ) {
		    prop = new Property();
		    prop.setPropertyname( key.toString() );
		    prop.setLabel( key.toString() );
		    addProp = true;
		}
		else if( val == null ) {
		    // delete property
		    prop = null;
		}

		if( prop != null ) {
		    long typeid = ((prop.getTypeid() == 0L)?PropertyType.suggestTypeid( val ):prop.getTypeid());
		    
		    PropertyType pType = getDAO().findTypeById( typeid );
		    if( pType == null )
			pType = getDAO().findTypeById( 0L );
		    prop.setTypeid( pType.getTypeid() );
		    prop.setTypename( pType.getTypename() );

		    prop = getDAO().storeProperty( userId, prop );

		    if( prop.getTypeid() == TYPE_NUMERIC ) 
			prop = getDAO().assignPropertyValue( prop, PropertyType.toDouble( val, 0d ) );
		    else
			prop = getDAO().assignPropertyValue( prop, val.toString() );
		}

		if( addProp ) {

		    pstmt = getStatement( STMT_DONORPROP_INSERT );
		    pstmt.setLong( 1, subject.getDonorid() );
		    pstmt.setLong( 2, prop.getPropertyid() );
		    pstmt.executeUpdate();
		    popStatement( pstmt );

		    subject.addProperty( prop );
		}
	    }
	}
	
	return subject;
    }

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Donor[] findSampleDonor( Sample sample ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_DONOR_BY_SAMPLE );
	pstmt.setString( 1, sample.getSampleid() );

     	ResultSet res = pstmt.executeQuery();
     	List<Donor> fl = new ArrayList<Donor>();
     	Iterator it = TableUtils.toObjects( res, new Donor() );
	while( it.hasNext() ) {
	    fl.add( (Donor)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Donor[] facs = new Donor[ fl.size() ];
     	return (Donor[])fl.toArray( facs );
    }

    /**
     * Assign a sample to a donor.
     *
     * @param userId the user id
     * @param subject the subject.
     * @param sample the sample to be assigned.
     *
     * @return the <code>Donor</code> object.
     */
    public Donor assignDonor( long userId, Subject subject, Sample sample ) throws SQLException {

 	PreparedStatement pstmt = getStatement( STMT_DONOR_DUPLICATE );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, subject.getDonorid() );

     	ResultSet res = pstmt.executeQuery();
	Donor donor = null;
	if( res.next() )
	    donor = (Donor)TableUtils.toObject( res, new Donor() );
     	res.close();
	if( donor != null ) {
	    log.warn( "Ignored duplicate assignment of "+sample+" to "+subject );
	    return donor;
	}

	donor = new Donor();
	donor.setDonorid( subject.getDonorid() );
	donor.setSampleid( sample.getSampleid() );

	donor.updateTrackid();

	pstmt = getStatement( STMT_DONOR_INSERT );
	pstmt.setString( 1, donor.getSampleid() );
	pstmt.setLong( 2, donor.getDonorid() );
	pstmt.setLong( 3, donor.getTrackid() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Assigned sample "+sample+" to subject "+subject );
	
	trackChange( null, donor, userId, "Sample assigned to donor", null );

	return donor;
    }

    /**
     * Returns grouped property values for the given property.
     *
     * @return the SampleType object.
     */
    public String[] findDonorPropertyTerms( String propertyName ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_DONOR_TERMS );
	pstmt.setString( 1, propertyName );

     	ResultSet res = pstmt.executeQuery();

     	List<String> fl = new ArrayList<String>();
     	Iterator it = TableUtils.toObjects( res, new Property() );
	while( it.hasNext() ) {
	    Property sType = (Property)it.next();
	    if( sType.getCharvalue() != null )
		fl.add( sType.getCharvalue() );
	}	       
	res.close();
	popStatement( pstmt );

	String[] terms = new String[fl.size()];
     	return (String[])fl.toArray( terms );
    }

}
