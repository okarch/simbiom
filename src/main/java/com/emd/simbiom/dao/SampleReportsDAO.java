package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Accession;
import com.emd.simbiom.model.DetailsSection;
import com.emd.simbiom.model.Organization;
import com.emd.simbiom.model.Property;
import com.emd.simbiom.model.Restriction;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.Subject;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleDetails;
import com.emd.simbiom.model.SampleProcess;
import com.emd.simbiom.model.SampleSummary;
import com.emd.simbiom.model.SampleType;

import com.emd.util.Stringx;

/**
 * <code>SampleReportsDAO</code> implements the <code>SampleReports</code> API.
 *
 * Created: Tue Aug 28 18:48:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class SampleReportsDAO extends BasicDAO implements SampleReports {

    private static Log log = LogFactory.getLog(SampleReportsDAO.class);

    private static final String STMT_SAMPLEDETAIL_INSERT = "biobank.sampledetail.insert";
    private static final String STMT_SAMPLEDETAIL_BY_ID  = "biobank.sampledetail.findById";
    private static final String STMT_SAMPLEDETAIL_DELETE = "biobank.sampledetail.delete";

    private static final String[] entityNames = new String[] {
	"sampledetail"
    };

    public SampleReportsDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns a list of samples associated with the query content.
     * This is searching across all the content related to samples
     *
     * @param query The query string.
     * @return A list of samples.
     */
    public Sample[] findSampleByContent( String query ) throws SQLException {
	FullContentSearch fcs = getContentSearch();
	if( fcs == null )
	    throw new SQLException( "Content server is not configured" );
     	List<Sample> fl = fcs.querySamples( query );
     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    private Properties parseCategories( String categoryPath ) {
	String[] terms = categoryPath.split( "[|]" );
	Properties props = new Properties();
	String catName = null;
	boolean isValue = false;
	StringBuilder stb = new StringBuilder( "biobank.summary");
	for( int i = 0; i < terms.length; i++ ) {
	    if( isValue ) {
		isValue = false;
		props.put( catName, terms[i] );
	    }
	    else {
		catName = terms[i].replace( ' ', '_' );
		stb.append( "." );
		stb.append( catName );
		// props.put( catName, "" );
		isValue = true;
	    }
	}
	String pSt = stb.toString();
	if( pSt.endsWith( "." ) )
	    pSt = pSt.substring( 0, pSt.length()-1 );
	props.put( "path", pSt );
	return props;
    }

    private PreparedStatement getSummaryStatement( Properties props ) 
	throws SQLException {

	String catPath = props.getProperty( "path" );
	PreparedStatement pstmt = null;
	try {
	    pstmt = getStatement( catPath );
	}
	catch( SQLException sqe ) {
	    log.warn( sqe );
	    pstmt = null;
	}
	if( pstmt != null ) {
	    String[] cats = catPath.split( "[.]" );
	    int j = 1;
	    for( int i = 0; i < cats.length; i++ ) {
		String valSt = props.getProperty( cats[i] );
		if( valSt != null ) {
		    pstmt.setString( j, valSt );
		    j++;
		}
	    }
	}
	return pstmt;
    }

    /**
     * Returns a list of sample summaries.
     *
     * @param categoryPath the groupings to be applied.
     *
     * @return an array of summary groups.
     */
    public SampleSummary[] createSampleSummary( String categoryPath ) throws SQLException {
	Properties props = parseCategories( categoryPath );
	log.debug( "Sample summary properties: "+props );

	PreparedStatement pstmt = getSummaryStatement( props );
     	List<SampleSummary> fl = new ArrayList<SampleSummary>();

	if( pstmt != null ) {
	    ResultSet res = pstmt.executeQuery();

	    //
	    // FIX ME: The 5.1.27 mysql jdbc seems to have a bug which was motivating the
	    // debug output below
	    //
	    // ResultSetMetaData meta = res.getMetaData();
	    // for( int i = 1; i <= meta.getColumnCount(); i++ ) 
	    //  	log.debug( meta.getColumnName( i ) );
	    
	    Iterator it = TableUtils.toObjects( res, new SampleSummary() );
	    String catPath = props.getProperty( "path" );
	    while( it.hasNext() ) {
		SampleSummary sSum = (SampleSummary)it.next();
		sSum.setCategoryPath( catPath );
		// log.debug( "Sample summary: "+sSum.getTerm() );
		fl.add( (SampleSummary)it.next() );
	    }	       
	    res.close();
	    popStatement( pstmt );
	}
	else
	    log.warn( "Sample summary statement cannot be determined." );

     	SampleSummary[] facs = new SampleSummary[ fl.size() ];
     	return (SampleSummary[])fl.toArray( facs );	
    }

    /**
     * Returns a list of samples by sample type.
     *
     * @param categoryPath the groupings to be applied.
     *
     * @return an array of summary groups.
     * @param type The sample type.
     * @return A list of samples.
     */
    public Sample[] findSampleByCategory( String categoryPath ) throws SQLException {
	Properties props = parseCategories( categoryPath );
	log.debug( "Sample category query properties: "+props );

	PreparedStatement pstmt = getSummaryStatement( props );
     	List<Sample> fl = new ArrayList<Sample>();

	if( pstmt != null ) {
	    ResultSet res = pstmt.executeQuery();
	    Iterator it = TableUtils.toObjects( res, new Sample() );
	    while( it.hasNext() ) {
		fl.add( (Sample)it.next() );
	    }	       
	    res.close();
	    popStatement( pstmt );
	}
	else
	    log.warn( "Sample category statement cannot be determined." );

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns the sample details of the given sample id.
     *
     * @param sampleId the id of the sample.
     *
     * @return the <code>SampleDetails</code> object (or null).
     */
    public SampleDetails findSampleDetailsById( String sampleId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_SAMPLEDETAIL_BY_ID );
	pstmt.setString( 1, sampleId );
	ResultSet res = pstmt.executeQuery();
	SampleDetails sDetails = null;
	if( res.next() ) {
	    sDetails = (SampleDetails)TableUtils.toObject( res, new SampleDetails() ); 
	}
	res.close();
	popStatement( pstmt );
	return sDetails;
    }

    private String concatPath( String pref, int idx, int len ) {
	return pref+"["+Stringx.zeroPad( idx, len )+"]";
    }  

    /**
     * Creates a report of all attributes linked to a sample.
     *
     * @param sample the sample. 
     *
     * @return a newly created <code>RestrictionRule</code> object.
     *
     * @exception SQLException in case of a duplicate rule has been found.
     */
    public SampleDetails createSampleDetails( Sample sample ) throws SQLException {
	SampleDetails sDetails = findSampleDetailsById( sample.getSampleid() );
	PreparedStatement pstmt = null;
	if( sDetails != null ) {
	    if( !sDetails.isExpired() ) {
		sDetails.setSample( sample );
		return sDetails;
	    }

	    pstmt = getStatement( STMT_SAMPLEDETAIL_DELETE );
	    pstmt.setString( 1, sample.getSampleid() );
	    pstmt.executeUpdate();
	    popStatement( pstmt );

	    log.debug( "Sample details expired: "+sample.getSampleid() );
	    sDetails = null;
	}
	    
	sDetails = new SampleDetails();
	sDetails.setSample( sample );
	sDetails.setSampleid( sample.getSampleid() );

	SampleType sType = getDAO().findSampleTypeById( sample.getTypeid() );
	if( sType != null )
	    sDetails.setTypename( sType.toString() );

	DetailsSection section = sDetails.createSection( "accessions" );
	Accession[] accs = getDAO().findSampleAccession( sample );
	int maxlen = String.valueOf( accs.length ).length();
	for( int i = 0; i < accs.length; i++ ) {
	    Organization org = getDAO().findOrganizationById( accs[i].getOrgid() );
	    String accPath = concatPath( "accession", i, maxlen );
	    section.addProperties( accPath, accs[i] );
	    section.addProperties( concatPath(accPath+"/organization", i, maxlen), org );
	}
	log.debug( "Prepared section accessions: "+accs.length+" accessions" );

	Study[] studies = getDAO().findStudySample( sample );
	section = sDetails.createSection( "studies" );
	maxlen = String.valueOf( studies.length ).length();
	for( int i = 0; i < studies.length; i++ ) {
	    String sPath = concatPath( "study", i, maxlen );
	    section.addProperties( sPath, studies[i] );
	    Subject subj = getDAO().findSubjectBySample( studies[i], sample );
	    if( subj != null ) {
		String subjPath = concatPath( sPath+"/subject", i, maxlen ); 
		section.addProperties( subjPath, subj );
		Property[] sProps = subj.getProperties();
		int propslen = String.valueOf( sProps.length ).length();
		for( int j = 0; j < sProps.length; j++ ) {
		    Property prop = getDAO().retrievePropertyValue( sProps[j] );
		    section.addProperties( concatPath( subjPath+"/subject-attr", j, propslen), 
					   prop );
		}
	    }
	}
	log.debug( "Prepared section studies: "+studies.length+" studies" );

	getDAO().loadSampleProperties( sample, true );
	section = sDetails.createSection( "sample-properties" );
	Property[] sampProps = sample.getProperties();
	maxlen = String.valueOf( sampProps.length ).length();
	for( int i = 0; i < sampProps.length; i++ ) {
	    String attrPath = concatPath( "sample-attr", i, maxlen );
	    Property prop = getDAO().retrievePropertyValue( sampProps[i] );
	    section.addProperties( attrPath, prop );
	}
	log.debug( "Prepared section sample attributes: "+sampProps.length+" sample attributes" );

	Restriction[] restrictions = getDAO().findSampleRestriction( sample );
	section = sDetails.createSection( "compliance" );
	maxlen = String.valueOf( restrictions.length ).length();
	for( int i = 0; i < restrictions.length; i++ ) {
	    String sPath = concatPath( "studylevel", i, maxlen );
	    section.addProperties( sPath, restrictions[i] );
	    section.addProperties( concatPath( sPath+"/rule", i, maxlen ), restrictions[i].getRestrictionRule() );
	}

	section = sDetails.createSection( "processing" );
	SampleProcess proc = getDAO().findVisit( sample );
	if( proc != null ) {
	    String sPath = concatPath( "visit", 0, 1 );
	    section.addProperties( sPath, proc );
	}

	pstmt = getStatement( STMT_SAMPLEDETAIL_INSERT );
	pstmt.setString( 1, sDetails.getSampleid() );
	pstmt.setTimestamp( 2, sDetails.getCreated() );
	pstmt.setString( 3, sDetails.getDetails() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Sample details created: "+sample.getSampleid() );
	return sDetails;
    }

}
