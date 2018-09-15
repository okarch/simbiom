package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.CharSetUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Age;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleType;

import com.emd.util.Stringx;

/**
 * <code>SamplesDAO</code> implements the <code>Samples</code> API.
 *
 * Created: Mon Aug 27 16:35:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class SamplesDAO extends BasicDAO implements Samples {

    private static Log log = LogFactory.getLog(SamplesDAO.class);

    private static final String STMT_SAMPLE_INSERT       = "biobank.sample.insert";
    private static final String STMT_SAMPLE_BY_ID        = "biobank.sample.findById";
    private static final String STMT_SAMPLE_BY_DONORPROP = "biobank.sample.findByDonorProperty";
    private static final String STMT_SAMPLE_BY_STUDY     = "biobank.sample.findByStudy";
    private static final String STMT_SAMPLE_BY_TYPE      = "biobank.sample.findByType";
    private static final String STMT_SAMPLE_LAST_CREATED = "biobank.sample.findByLastCreated";

    private static final String STMT_STYPE_BY_ID         = "biobank.sampleType.findById";
    private static final String STMT_STYPE_BY_NAME       = "biobank.sampleType.findByName";
    private static final String STMT_STYPE_LIKE_NAME     = "biobank.sampleType.findLikeName";
    private static final String STMT_STYPE_CREATE        = "biobank.sampleType.insert";
    private static final String STMT_STYPE_TERMS         = "biobank.sampleType.findTerms";
    private static final String STMT_STYPE_LOOKUP        = "biobank.sampleType.mapType";

    private static final String[] entityNames = new String[] {
	"sample",
	"sampleType"
    };

    public SamplesDAO( DatabaseDAO db ) {
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
    public SampleType[] findSampleTypeByNameAll( String sampleType ) throws SQLException {
 	PreparedStatement pstmt = null;
	String st = Stringx.getDefault(sampleType,"").trim().toLowerCase();
	if( (st.length() <= 0) || (st.indexOf( "%" ) >= 0) ) {
	    if( st.length() <= 0 )
		st = "%";
	    pstmt = getStatement( STMT_STYPE_LIKE_NAME );
	}
	else
	    pstmt = getStatement( STMT_STYPE_BY_NAME );
     	pstmt.setString( 1, st );

     	ResultSet res = pstmt.executeQuery();

     	List<SampleType> fl = new ArrayList<SampleType>();
     	Iterator it = TableUtils.toObjects( res, new SampleType() );
	while( it.hasNext() ) {
	    fl.add( (SampleType)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	SampleType[] facs = new SampleType[ fl.size() ];
     	return (SampleType[])fl.toArray( facs );
    }

    /**
     * Maps a sample type term to another sample type.
     *
     * @return the list of matching <code>SampleType</code> objects.
     */
    public SampleType[] mapSampleType( String sampleType ) throws SQLException {
	String normName = Stringx.getDefault( sampleType, "" ).trim(). toLowerCase();
	if( normName.length() <= 0 )
	    return findSampleTypeByNameAll( "unknown" );
	String[] tokens = CharSetUtils.squeeze(StringUtils.replaceChars( normName, "_:.=,/();-+?!#*%1234567890", "           " ), " " ).trim().split( " " );

     	List<SampleType> sTypes = new ArrayList<SampleType>();

	PreparedStatement pstmt = getStatement( STMT_STYPE_LOOKUP );
	for( int i = 0; i < tokens.length; i++ ) {
	    pstmt.setString( 1, tokens[i] );
	    ResultSet res = pstmt.executeQuery();

	    Iterator it = TableUtils.toObjects( res, new SampleType() );
	    while( it.hasNext() ) {		
		SampleType st = (SampleType)it.next();
		if( !sTypes.contains( st ) )
		    sTypes.add( st );
	    }	       
	    res.close();
	}
	popStatement( pstmt );

	if( sTypes.size() <= 0 )
	    return findSampleTypeByNameAll( "unknown" );	    
	
     	SampleType[] facs = new SampleType[ sTypes.size() ];
     	return (SampleType[])sTypes.toArray( facs );
    }

    /**
     * Returns a sample type by name. 
     *
     * @return the SampleType object.
     */
    public SampleType findSampleTypeByName( String sampleType ) throws SQLException {
	SampleType[] sTypes = findSampleTypeByNameAll( sampleType );
	return ((sTypes.length ==1)?sTypes[0]:null);
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public SampleType findSampleTypeById( long typeId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STYPE_BY_ID );
     	pstmt.setLong( 1, typeId );
     	ResultSet res = pstmt.executeQuery();
     	SampleType sType = null;
     	if( res.next() ) 
     	    sType = (SampleType)TableUtils.toObject( res, new SampleType() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public String[] findSampleTypeTerms() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STYPE_TERMS );

     	ResultSet res = pstmt.executeQuery();

     	List<String> fl = new ArrayList<String>();
     	Iterator it = TableUtils.toObjects( res, new SampleType() );
	while( it.hasNext() ) {
	    SampleType sType = (SampleType)it.next();
	    fl.add( sType.getTypename() );
	}	       
	res.close();
	popStatement( pstmt );

	String[] terms = new String[fl.size()];
     	return (String[])fl.toArray( terms );
    }

    /**
     * Creates a sample type (if not existing).
     *
     * @param sampleType the name of the sample type
     *
     * @return the sample type.
     */
    public SampleType createSampleType( String sampleType ) throws SQLException {
	if( (sampleType == null) || (sampleType.trim().length() <= 0) )
	    throw new SQLException( "Invalid sample type" );
	SampleType sType = findSampleTypeByName( sampleType );
	PreparedStatement pstmt = null;
	if( sType == null ) {
	    sType = SampleType.getInstance( sampleType );
	    
	    pstmt = getStatement( STMT_STYPE_CREATE );
	    pstmt.setLong( 1, sType.getTypeid() );
	    pstmt.setString( 2, sType.getTypename() );
	    pstmt.setTimestamp( 3, sType.getCreated() );
	    pstmt.executeUpdate();

	    log.debug( "New sample type created: "+sType+" (id: "+sType.getTypeid()+")" );
	}
	popStatement( pstmt );

	return sType;
    }

    /**
     * Create a sample.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public Sample createSample( long userId, String sampleType ) throws SQLException {
	SampleType sType = createSampleType( sampleType );
	Sample samp = Sample.getInstance( sType );

	PreparedStatement pstmt = getStatement( STMT_SAMPLE_INSERT );
	pstmt.setString( 1, samp.getSampleid() );
	pstmt.setString( 2, samp.getSamplename() );
	pstmt.setLong( 3, samp.getTypeid() );
	pstmt.setLong( 4, samp.getStamp() );
	pstmt.setLong( 5, samp.getTrackid() );
	pstmt.setTimestamp( 6, samp.getCreated() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Sample created: "+samp.getSamplename()+" ("+
		   samp.getSampleid()+") trackid: "+samp.getTrackid() );
	
	trackChange( null, samp, userId, "Sample created", null );
 
	return samp;
    }

    /**
     * Returns a sample by id.
     *
     * @param sampleId The sample id.
     * @return the Sample object or null (if not existing).
     */
    public Sample findSampleById( String sampleId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_BY_ID );
     	pstmt.setString( 1, sampleId );
     	ResultSet res = pstmt.executeQuery();
     	Sample sType = null;
     	if( res.next() ) 
     	    sType = (Sample)TableUtils.toObject( res, new Sample() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

    /**
     * Returns a list of samples recently created.
     *
     * @param age The age of the samples (created or collected, older or newer).
     * @return A list of samples.
     */
    public Sample[] findSampleByAge( Age age ) throws SQLException {
 	PreparedStatement pstmt = getStatement( age.getStatementName() );
	pstmt.setTimestamp( 1, age.getTimestamp() );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of samples recently created.
     *
     * @return A list of samples.
     */
    public Sample[] findSampleLastCreated() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_LAST_CREATED );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of samples recently created.
     *
     * @param age The age of the samples (created or collected, older or newer).
     * @return A list of samples.
     */
    public Sample[] findSampleByStudy( String study ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_BY_STUDY );
	pstmt.setString( 1, study );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of samples by sample type.
     *
     * @param type The sample type.
     * @return A list of samples.
     */
    public Sample[] findSampleByType( String type ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_BY_TYPE );
	pstmt.setString( 1, type );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of samples where the donor property match the given value.
     *
     * @param propertyName The property name.
     * @param propertyVal The property value.
     * @return A list of samples.
     */
    public Sample[] findSampleByDonorProperty( String propertyName, String propertyVal ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_BY_DONORPROP );
	pstmt.setString( 1, propertyName );
	pstmt.setString( 2, propertyVal );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

}
