package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
// import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
// import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.StudySample;
import com.emd.simbiom.model.Sample;

import com.emd.util.Stringx;

/**
 * <code>StudiesDAO</code> implements the <code>Studies</code> API.
 *
 * Created: Thu Aug 23 17:50:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class StudiesDAO extends BasicDAO implements Studies {

    private static Log log = LogFactory.getLog(StudiesDAO.class);

    private static final String STMT_STUDY_BY_ID         = "biobank.study.findById";
    private static final String STMT_STUDY_BY_NAME       = "biobank.study.findByName";
    private static final String STMT_STUDY_BY_SAMPLE     = "biobank.study.findBySample";
    private static final String STMT_STUDY_TERMS         = "biobank.study.findTerms";
    private static final String STMT_STUDY_INSERT        = "biobank.study.insert";

    private static final String STMT_STSAMP_DUPLICATE    = "biobank.studysample.duplicate";
    private static final String STMT_STSAMP_INSERT       = "biobank.studysample.insert";

    private static final String[] entityNames = new String[] {
	"study",
	"studysample"
    };


    public StudiesDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public Study findStudyByName( String studyName ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STUDY_BY_NAME );
     	pstmt.setString( 1, Stringx.getDefault(studyName,"").trim() );
     	ResultSet res = pstmt.executeQuery();
     	Study sType = null;
     	if( res.next() ) 
     	    sType = (Study)TableUtils.toObject( res, new Study() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public String[] findStudyTerms() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STUDY_TERMS );

     	ResultSet res = pstmt.executeQuery();

     	List<String> fl = new ArrayList<String>();
     	Iterator it = TableUtils.toObjects( res, new Study() );
	while( it.hasNext() ) {
	    Study study = (Study)it.next();
	    fl.add( study.getStudyname() );
	}	       
	res.close();
	popStatement( pstmt );

	String[] terms = new String[fl.size()];
     	return (String[])fl.toArray( terms );
    }

    /**
     * Returns a study by id.
     *
     * @param studyId the study id
     * @return the SampleType object.
     */
    public Study findStudyById( long studyId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STUDY_BY_ID );
     	pstmt.setLong( 1, studyId );
     	ResultSet res = pstmt.executeQuery();
     	Study sType = null;
     	if( res.next() ) 
     	    sType = (Study)TableUtils.toObject( res, new Study() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

    /**
     * Returns a study by id.
     *
     * @param sample the sample.
     * @return the <code>Study</code> objects (or empty array.
     */
    public Study[] findStudySample( Sample sample ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STUDY_BY_SAMPLE );
	pstmt.setString( 1, sample.getSampleid() );
     	ResultSet res = pstmt.executeQuery();
     	List<Study> fl = new ArrayList<Study>();
     	Iterator it = TableUtils.toObjects( res, new Study() );
	while( it.hasNext() ) {
	    Study study = (Study)it.next();
	    fl.add( study );
	}	       
	res.close();
	popStatement( pstmt );
	Study[] terms = new Study[fl.size()];
     	return (Study[])fl.toArray( terms );
    }

    /**
     * Create a study.
     *
     * @param studyName the study name.
     *
     * @return the newly allocated study.
     */
    public Study createStudy( String studyName ) throws SQLException {
	Study study = findStudyByName( studyName );
	if( study != null )
	    throw new SQLException( "Study already exists: "+studyName );

	study = new Study();
	study.setStudyname( studyName );

	PreparedStatement pstmt = getStatement( STMT_STUDY_INSERT );
	pstmt.setLong( 1, study.getStudyid() );
	pstmt.setString( 2, study.getStudyname() );
	pstmt.setTimestamp( 3, study.getStarted() );
	pstmt.setTimestamp( 4, study.getExpire() );
	pstmt.setString( 5, study.getStatus() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Study created: "+study.getStudyname()+" ("+
		   study.getStudyid()+")" );
	 
	return study;
    }

    /**
     * Assign a Create an accession.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public StudySample assignStudySample( long userId, Study study, Sample sample ) throws SQLException {

 	PreparedStatement pstmt = getStatement( STMT_STSAMP_DUPLICATE );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, study.getStudyid() );

     	ResultSet res = pstmt.executeQuery();
	StudySample studySample = null;
	if( res.next() )
	    studySample = (StudySample)TableUtils.toObject( res, new StudySample() );
     	res.close();
	popStatement( pstmt );
	if( studySample != null ) {
	    log.warn( "Ignored duplicate assignment of "+sample+" to "+study );
	    return studySample;
	}

	studySample = new StudySample();
	studySample.setStudyid( study.getStudyid() );
	studySample.setSampleid( sample.getSampleid() );

	studySample.updateTrackid();

	pstmt = getStatement( STMT_STSAMP_INSERT );
	pstmt.setString( 1, studySample.getSampleid() );
	pstmt.setLong( 2, studySample.getStudyid() );
	pstmt.setLong( 3, studySample.getTrackid() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Assigned sample "+sample+" to study "+study );
	
	trackChange( null, studySample, userId, "Sample assigned to study", null );

	return studySample;
    }

}
