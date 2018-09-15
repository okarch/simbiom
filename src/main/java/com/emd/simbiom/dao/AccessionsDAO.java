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

import com.emd.simbiom.model.Accession;
import com.emd.simbiom.model.Organization;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.Sample;

import com.emd.util.Stringx;

/**
 * <code>AccessionsDAO</code> implements the <code>Accessions</code> API.
 *
 * Created: Mon Aug 27 20:08:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class AccessionsDAO extends BasicDAO implements Accessions {

    private static Log log = LogFactory.getLog(AccessionsDAO.class);

    private static final String STMT_ACC_BY_ACC          = "biobank.accession.findAccession";
    private static final String STMT_ACC_BY_ALL          = "biobank.accession.findAccessionAll";
    private static final String STMT_ACC_BY_SAMPLE       = "biobank.accession.findSampleAccession";
    private static final String STMT_ACC_INSERT          = "biobank.accession.insert";

    private static final String[] entityNames = new String[] {
	"accession"
    };

    public AccessionsDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns a list of accessions associated with a study and issued by a certain organization.
     *
     * @param study the study context.
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Accession[] findAccession( Study study, Organization org, String acc ) throws SQLException {

	PreparedStatement pstmt = null;
	if( study != null ) {
	    pstmt = getStatement( STMT_ACC_BY_ACC );
	    pstmt.setLong( 3, study.getStudyid() );
	}
	else {
	    pstmt = getStatement( STMT_ACC_BY_ALL );
	}

	pstmt.setString( 1, acc );
	pstmt.setLong( 2, org.getOrgid() );

     	ResultSet res = pstmt.executeQuery();
     	List<Accession> fl = new ArrayList<Accession>();
     	Iterator it = TableUtils.toObjects( res, new Accession() );
	while( it.hasNext() ) {
	    fl.add( (Accession)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Accession[] facs = new Accession[ fl.size() ];
     	return (Accession[])fl.toArray( facs );
    }

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Accession[] findAccession( Organization org, String acc ) throws SQLException {
	return findAccession( null, org, acc );
    }

    /**
     * Returns a list of accessions associated with a sample.
     *
     * @param sample the sample.
     *
     * @return Array of <code>Accession</code> object.
     */
    public Accession[] findSampleAccession( Sample sample ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_ACC_BY_SAMPLE );
	pstmt.setString( 1, sample.getSampleid() );

     	ResultSet res = pstmt.executeQuery();
     	List<Accession> fl = new ArrayList<Accession>();
     	Iterator it = TableUtils.toObjects( res, new Accession() );
	while( it.hasNext() ) {
	    fl.add( (Accession)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Accession[] facs = new Accession[ fl.size() ];
     	return (Accession[])fl.toArray( facs );
    }

    /**
     * Create an accession.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public Accession createAccession( long userId, 
				      Organization org, 
				      Sample sample,
				      String accession,
				      String accType ) 
	throws SQLException {

	Accession acc = new Accession();
	acc.setSampleid( sample.getSampleid() );
	acc.setAccession( accession );
	acc.setAcctype( accType );
	acc.setOrgid( org.getOrgid() );

	acc.updateTrackid();

	PreparedStatement pstmt = getStatement( STMT_ACC_INSERT );
	pstmt.setString( 1, acc.getSampleid() );
	pstmt.setString( 2, acc.getAccession() );
	pstmt.setString( 3, acc.getAcctype() );
	pstmt.setLong( 4, acc.getOrgid() );
	pstmt.setLong( 5, acc.getTrackid() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Accession created: "+acc.getAccession()+" sample: "+
		   acc.getSampleid()+") trackid: "+acc.getTrackid() );
	
	trackChange( null, acc, userId, "Accession created", null );
 
	return acc;
    }

}
