package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
// import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
// import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Organization;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleEvent;
import com.emd.simbiom.model.SampleProcess;
import com.emd.simbiom.model.Treatment;

import com.emd.util.Stringx;

/**
 * <code>SampleProcessesDAO</code> implements the <code>SampleProcesses</code> API.
 *
 * Created: Sun Aug 26 21:35:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class SampleProcessesDAO extends BasicDAO implements SampleProcesses {

    private static Log log = LogFactory.getLog(SampleProcessesDAO.class);

    private static final String STMT_EVENT_BY_ORG        = "biobank.event.findBySite";
    private static final String STMT_EVENT_BY_ID         = "biobank.event.findById";
    private static final String STMT_EVENT_INSERT        = "biobank.event.insert";

    private static final String STMT_PROCESS_BY_EVENT    = "biobank.process.findByEvent";
    private static final String STMT_PROCESS_BY_VISIT    = "biobank.process.findByVisit";
    private static final String STMT_PROCESS_INSERT      = "biobank.process.insert";
    private static final String STMT_PROCESS_UPDATE      = "biobank.process.update";

    private static final String[] entityNames = new String[] {
	"event"
    };

    public SampleProcessesDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public SampleEvent[] findSiteEvent( Organization org, String visitDesc ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_EVENT_BY_ORG );
	pstmt.setLong( 1, org.getOrgid() );
	pstmt.setString( 2, visitDesc );

     	ResultSet res = pstmt.executeQuery();
     	List<SampleEvent> fl = new ArrayList<SampleEvent>();
     	Iterator it = TableUtils.toObjects( res, new SampleEvent() );
	while( it.hasNext() ) {
	    fl.add( (SampleEvent)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	SampleEvent[] facs = new SampleEvent[ fl.size() ];
     	return (SampleEvent[])fl.toArray( facs );	
    }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleEvent findEventById( long timeid ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_EVENT_BY_ID );
     	pstmt.setLong( 1, timeid );
     	ResultSet res = pstmt.executeQuery();
     	SampleEvent sType = null;
     	if( res.next() ) 
     	    sType = (SampleEvent)TableUtils.toObject( res, new SampleEvent() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

    /**
     * Creates a new site visit.
     *
     * @param organization the site of the event conducted.
     * @param visit the visit label.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createSiteEvent( Organization org, String visitDesc ) throws SQLException {
	SampleEvent[] events = findSiteEvent( org, visitDesc );
	if( events.length > 0 ) 
	    throw new SQLException( "Site timpoint exists already" );
	
	SampleEvent se = SampleEvent.parseVisit( visitDesc );
	se.setOrgid( org.getOrgid() );

 	PreparedStatement pstmt = getStatement( STMT_EVENT_INSERT );
	pstmt.setLong( 1, se.getTimeid() );
	pstmt.setLong( 2, se.getOrgid() );
	pstmt.setString( 3, visitDesc );
	pstmt.setString( 4, se.getCycle() );
	pstmt.setInt( 5, se.getDay() );
	pstmt.setFloat( 6, se.getHour() );
	pstmt.setString( 7, se.getDosage() );
	pstmt.setFloat( 8, se.getQuantity() );
	pstmt.setString( 9, se.getUnit() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	return se;
    }

    /**
     * Creates a new shipment event.
     *
     * @param organization the sender organization.
     * @param eventName the event name.
     * @param desc the logistics description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createLogisticsEvent( Organization org, String eventName, String desc ) throws SQLException {
	String dDesc = Stringx.getDefault(desc,"").trim();
	SampleEvent[] events = findSiteEvent( org, eventName );
	if( events.length > 0 ) {
	    for( int i = 0; i < events.length; i++ ) {
		String sDesc = Stringx.getDefault(events[i].getDosage(),"").trim();
		if( sDesc.equals(dDesc) )
		    return events[i];
	    }
	}
	
	SampleEvent se = SampleEvent.createLogistics( eventName, dDesc );
	se.setOrgid( org.getOrgid() );

 	PreparedStatement pstmt = getStatement( STMT_EVENT_INSERT );
	pstmt.setLong( 1, se.getTimeid() );
	pstmt.setLong( 2, se.getOrgid() );
	pstmt.setString( 3, se.getVisit() );
	pstmt.setString( 4, se.getCycle() );
	pstmt.setInt( 5, se.getDay() );
	pstmt.setFloat( 6, se.getHour() );
	pstmt.setString( 7, se.getDosage() );
	pstmt.setFloat( 8, se.getQuantity() );
	pstmt.setString( 9, se.getUnit() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	return se;
    }

    /**
     * Creates a new shipment event.
     *
     * @param organization the sender organization.
     * @param desc the shipment description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createShipmentEvent( Organization org, String desc ) throws SQLException {
	return createLogisticsEvent( org, SampleEvent.SHIPMENT, desc );
    }

    /**
     * Creates a new reception event.
     *
     * @param organization the receiving organization.
     * @param desc the reception description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createReceiverEvent( Organization org, String desc ) throws SQLException {
	return createLogisticsEvent( org, SampleEvent.RECEIVED, desc );
    }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess[] findSampleProcess( SampleEvent event, Sample sample, long treatid ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_PROCESS_BY_EVENT );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, treatid );
	pstmt.setLong( 3, event.getTimeid() );

     	ResultSet res = pstmt.executeQuery();
     	List<SampleProcess> fl = new ArrayList<SampleProcess>();
     	Iterator it = TableUtils.toObjects( res, new SampleProcess() );
	while( it.hasNext() ) {
	    fl.add( (SampleProcess)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	SampleProcess[] facs = new SampleProcess[ fl.size() ];
     	return (SampleProcess[])fl.toArray( facs );
    }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess findCollectionProcess( SampleEvent event, Sample sample ) throws SQLException {
	SampleProcess[] collEvents = findSampleProcess( event, sample, SampleProcess.TREATID_COLLECTION );
	if( collEvents.length <= 0 )
	    return null;
	return collEvents[0];
    }
    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess findVisit( Sample sample ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_PROCESS_BY_VISIT );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, SampleProcess.TREATID_COLLECTION );

     	ResultSet res = pstmt.executeQuery();
     	SampleProcess sType = null;
     	if( res.next() ) 
     	    sType = (SampleProcess)TableUtils.toObject( res, new SampleProcess() );
     	res.close();
	popStatement( pstmt );

	return sType;
    }
    
    /**
     * Assign a sample to a colletion event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignCollectionEvent( long userId, 
						SampleEvent event, 
						Sample sample,
						String collDate,
						String dtFormat ) 
	throws SQLException {

	SampleProcess collEvent = findCollectionProcess( event, sample );
	SampleProcess prevEvent = null;

	if( collEvent == null ) {
	    log.debug( "Parse collection string: "+collDate );
	    collEvent = SampleProcess.parseCollection( dtFormat, collDate );
	    collEvent.setSampleid( sample.getSampleid() );
	    collEvent.setTimeid( event.getTimeid() );
	}
	else {
	    try {
		prevEvent = (SampleProcess)collEvent.copy();
	    }
	    catch( CloneNotSupportedException cne ) {
		log.error( cne );
	    }
	    collEvent.initProcessed( dtFormat, collDate );
	}

	collEvent.updateTrackid();

	if( (prevEvent != null) && (prevEvent.equals( collEvent )) ) {
	    log.warn( "Nothing to be updated. Collection event exists already" );
	    return collEvent;
	}

     	PreparedStatement pstmt = null;
     	int nn = 4;
     	if( prevEvent == null ) {
     	    pstmt = getStatement( STMT_PROCESS_INSERT );
     	    pstmt.setString( 1, collEvent.getSampleid() );
     	    pstmt.setLong( 2, collEvent.getTreatid() );
     	    pstmt.setLong( 3, collEvent.getTimeid() );
	    log.debug( "Creating new collection event: "+collEvent+" sample: "+sample );
     	}
     	else {
     	    pstmt = getStatement( STMT_PROCESS_UPDATE );
     	    pstmt.setString( 4, collEvent.getSampleid() );
     	    pstmt.setLong( 5, collEvent.getTreatid() );
     	    pstmt.setLong( 6, collEvent.getTimeid() );
     	    nn = 1;
	    log.debug( "Updating existing collection event: "+collEvent+" sample: "+sample );
     	}
	
     	pstmt.setInt( nn, collEvent.getStep() );
     	nn++;

     	pstmt.setTimestamp( nn, collEvent.getProcessed() );
     	nn++;

	pstmt.setLong( nn, collEvent.getTrackid() );
     	nn++;

     	pstmt.executeUpdate();
	popStatement( pstmt );

	trackChange( prevEvent, collEvent, userId, "Sample collection event "+((prevEvent==null)?"assigned":"updated"), null );
	return collEvent;
    }

    /**
     * Assign a sample to a colletion event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignCollectionEvent( long userId, 
						SampleEvent event, 
						Sample sample,
						Date collDate )
	throws SQLException {

	SampleProcess collEvent = findCollectionProcess( event, sample );
	SampleProcess prevEvent = null;

	if( collEvent == null ) {
	    log.debug( "Creating collection event on "+collDate );
	    collEvent = SampleProcess.fromCollectionDate( collDate );
	    collEvent.setSampleid( sample.getSampleid() );
	    collEvent.setTimeid( event.getTimeid() );
	}
	else {
	    try {
		prevEvent = (SampleProcess)collEvent.copy();
	    }
	    catch( CloneNotSupportedException cne ) {
		log.error( cne );
	    }
	    collEvent.setProcessed( new Timestamp( collDate.getTime() ) );
	}

	collEvent.updateTrackid();

	if( (prevEvent != null) && (prevEvent.equals( collEvent )) ) {
	    log.warn( "Nothing to be updated. Collection event exists already" );
	    return collEvent;
	}

     	PreparedStatement pstmt = null;
     	int nn = 4;
     	if( prevEvent == null ) {
     	    pstmt = getStatement( STMT_PROCESS_INSERT );
     	    pstmt.setString( 1, collEvent.getSampleid() );
     	    pstmt.setLong( 2, collEvent.getTreatid() );
     	    pstmt.setLong( 3, collEvent.getTimeid() );
	    log.debug( "Creating new collection event: "+collEvent+" sample: "+sample );
     	}
     	else {
     	    pstmt = getStatement( STMT_PROCESS_UPDATE );
     	    pstmt.setString( 4, collEvent.getSampleid() );
     	    pstmt.setLong( 5, collEvent.getTreatid() );
     	    pstmt.setLong( 6, collEvent.getTimeid() );
     	    nn = 1;
	    log.debug( "Updating existing collection event: "+collEvent+" sample: "+sample );
     	}
	
     	pstmt.setInt( nn, collEvent.getStep() );
     	nn++;

     	pstmt.setTimestamp( nn, collEvent.getProcessed() );
     	nn++;

	pstmt.setLong( nn, collEvent.getTrackid() );
     	nn++;

     	pstmt.executeUpdate();
	popStatement( pstmt );

	trackChange( prevEvent, collEvent, userId, "Sample collection event "+((prevEvent==null)?"assigned":"updated"), null );
	return collEvent;
    }

    /**
     * Assign a sample to a shipment event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignShipmentEvent( long userId, 
					      SampleEvent event, 
					      Sample sample,
					      String evDate,
					      String dtFormat ) 
	throws SQLException {

	SampleProcess shipProc = SampleProcess.parseShipment( dtFormat, evDate );
	shipProc.setSampleid( sample.getSampleid() );
	shipProc.setTimeid( event.getTimeid() );
	shipProc.updateTrackid();

	SampleProcess[] shipped = findSampleProcess( event, sample, SampleProcess.TREATID_PACKAGED );
	for( int i = 0; i < shipped.length; i++ ) {
	    if( shipped[i].equals( shipProc ) ) {
		log.warn( "Nothing to be updated. Shipment at "+shipProc+" is existing already" );
		return shipped[i];
	    }
	}

	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
	pstmt.setString( 1, shipProc.getSampleid() );
	pstmt.setLong( 2, shipProc.getTreatid() );
	pstmt.setLong( 3, shipProc.getTimeid() );
     	pstmt.setInt( 4, shipProc.getStep() );
     	pstmt.setTimestamp( 5, shipProc.getProcessed() );
	pstmt.setLong( 6, shipProc.getTrackid() );

     	pstmt.executeUpdate();
	popStatement( pstmt );
	// log.debug( "Creating new shipment event: "+shipProc+" sample: "+sample );

	trackChange( null, shipProc, userId, "Sample shipment event assigned", null );
	return shipProc;
    }

    /**
     * Assign a sample to a receiver event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignReceiverEvent( long userId, 
					      SampleEvent event, 
					      Sample sample,
					      String evDate,
					      String dtFormat ) 
	throws SQLException {

	SampleProcess shipProc = SampleProcess.parseReceipt( dtFormat, evDate );
	shipProc.setSampleid( sample.getSampleid() );
	shipProc.setTimeid( event.getTimeid() );
	shipProc.updateTrackid();

	SampleProcess[] shipped = findSampleProcess( event, sample, SampleProcess.TREATID_UNPACKAGED );
	for( int i = 0; i < shipped.length; i++ ) {
	    if( shipped[i].equals( shipProc ) ) {
		log.warn( "Nothing to be updated. Reception at "+shipProc+" is existing already" );
		return shipped[i];
	    }
	}

	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
	pstmt.setString( 1, shipProc.getSampleid() );
	pstmt.setLong( 2, shipProc.getTreatid() );
	pstmt.setLong( 3, shipProc.getTimeid() );
     	pstmt.setInt( 4, shipProc.getStep() );
     	pstmt.setTimestamp( 5, shipProc.getProcessed() );
	pstmt.setLong( 6, shipProc.getTrackid() );

     	pstmt.executeUpdate();
	popStatement( pstmt );
	// log.debug( "Creating new shipment event: "+shipProc+" sample: "+sample );

	trackChange( null, shipProc, userId, "Sample reception event assigned", null );
	return shipProc;
    }

    /**
     * Assign a sample to a colletion event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignTreatment( long userId, 
					  Sample sample,
					  Treatment treatment )
	throws SQLException {

	// find the collection site

	Organization org = getDAO().findCollectionSite( sample.getSampleid() );
	if( org == null )
	    org = getDAO().findOrganizationById( -2L );  // generic Merck Serono sponsor
	log.debug( "Collection site: "+org );

	// check if medication event exists

	SampleEvent[] medicEvents = findSiteEvent( org, "study medication" );
	SampleEvent medicEvent = null;
	if( medicEvents.length > 0 ) {
	    medicEvent = medicEvents[0];
	}
	else {
	    medicEvent = createSiteEvent( org, "study medication" );
	    log.debug( "New study medication event created for site "+org );
	}

	// Test if the treatment has been assigned already

	SampleProcess[] mProcs = findSampleProcess( medicEvent, sample, treatment.getTreatid() );
	if( mProcs.length > 0 ) {
	    log.debug( "Study medication already assigned: "+mProcs[0] );
	    return mProcs[0];
	}

	// create sample processing

	SampleProcess mProc = new SampleProcess();
	mProc.setSampleid( sample.getSampleid() );
	mProc.setTreatid( treatment.getTreatid() );
	mProc.setTimeid( medicEvent.getTimeid() );
	mProc.setStep( 100 );
	mProc.setProcessed( new Timestamp( SampleProcess.MISSING_DATETIME ) );

	mProc.updateTrackid();

     	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
	pstmt.setString( 1, mProc.getSampleid() );
	pstmt.setLong( 2, mProc.getTreatid() );
	pstmt.setLong( 3, mProc.getTimeid() );
     	pstmt.setInt( 4, mProc.getStep() );
     	pstmt.setTimestamp( 5, mProc.getProcessed() );
	pstmt.setLong( 6, mProc.getTrackid() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	trackChange( null, mProc, userId, "Sample medication event assigned", null );
	return mProc;
    }

}
