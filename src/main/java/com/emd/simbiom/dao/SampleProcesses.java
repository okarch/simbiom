package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Organization;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleEvent;
import com.emd.simbiom.model.SampleProcess;
import com.emd.simbiom.model.Treatment;

/**
 * <code>SampleProcesses</code> specifies functionality to handle sample processing.
 *
 * Created: Sun Aug 26 21:50:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface SampleProcesses {

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public SampleEvent[] findSiteEvent( Organization org, String visitDesc ) throws SQLException;

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleEvent findEventById( long timeid ) throws SQLException;

    /**
     * Creates a new site visit.
     *
     * @param organization the site of the event conducted.
     * @param visit the visit label.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createSiteEvent( Organization org, String visitDesc ) throws SQLException;

    /**
     * Creates a new shipment event.
     *
     * @param organization the sender organization.
     * @param eventName the event name.
     * @param desc the logistics description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createLogisticsEvent( Organization org, String eventName, String desc ) throws SQLException;

    /**
     * Creates a new shipment event.
     *
     * @param organization the sender organization.
     * @param desc the shipment description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createShipmentEvent( Organization org, String desc ) throws SQLException;

    /**
     * Creates a new reception event.
     *
     * @param organization the receiving organization.
     * @param desc the reception description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createReceiverEvent( Organization org, String desc ) throws SQLException;

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess[] findSampleProcess( SampleEvent event, Sample sample, long treatid ) throws SQLException;

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess findCollectionProcess( SampleEvent event, Sample sample ) throws SQLException;

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess findVisit( Sample sample ) throws SQLException;
    
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
	throws SQLException;

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
	throws SQLException;

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
	throws SQLException;

    /**
     * Assign a sample to a treatment event.
     *
     * @param userId the user id.
     * @param sample the sample.
     * @param treatment the treatment.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignTreatment( long userId, 
					  Sample sample,
					  Treatment treatment )
	throws SQLException;

}
