package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Cost;
import com.emd.simbiom.model.CostEstimate;
import com.emd.simbiom.model.CostItem;
import com.emd.simbiom.model.CostSample;

import com.emd.util.Stringx;


/**
 * <code>StorageCostDAO</code> implements the <code>StorageCost</code> API.
 *
 * Created: Thu Aug 16 21:40:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class StorageCostDAO extends BasicDAO implements StorageCost {

    private static Log log = LogFactory.getLog(StorageCostDAO.class);

    private static final String COST_PER_MONTH   = "month";

    private static final String STMT_COST_BY_ID          = "biobank.cost.findById";

    private static final String STMT_COSTITEM_INSERT     = "biobank.item.insert";
    private static final String STMT_COSTITEM_DELETE     = "biobank.item.delete";
    private static final String STMT_COSTITEM_EXPIRED    = "biobank.item.expired";

    private static final String STMT_COSTSAMPLE_BY_TYPE  = "biobank.costsample.findByType";
    private static final String STMT_COSTSAMPLE_ALL_TYPE = "biobank.costsample.findAllTypes";

    private static final String STMT_ESTIMATE_BY_ID      = "biobank.estimate.findById";
    private static final String STMT_ESTIMATE_INSERT     = "biobank.estimate.insert";
    private static final String STMT_ESTIMATE_UPDATE     = "biobank.estimate.update";
    private static final String STMT_ESTIMATE_EXPIRED    = "biobank.estimate.expired";

    private static final long   COST_EXPIRE              = 3L * 24L * 60L * 60L * 1000L; // 3 days

    public static final String[] entityNames = new String[] {
	"cost",
	"costsample",
	"item",
	"estimate"
    };

    public StorageCostDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns a list of cost items per sample type.
     *
     * @param sampleType the name of the sample type.
     * @param region storage region.
     *
     * @return an array of CostSample objects.
     */
    public CostSample[] findCostBySampleType( String sampleType, String region ) throws SQLException {
	PreparedStatement pstmt = null;
	if( Stringx.getDefault(sampleType,"").trim().length() <= 0 )
	    pstmt = getStatement( STMT_COSTSAMPLE_ALL_TYPE );
	else {
	    pstmt = getStatement( STMT_COSTSAMPLE_BY_TYPE );
	    pstmt.setString( 1, sampleType );
	    pstmt.setString( 2, region );
	}

     	ResultSet res = pstmt.executeQuery();
     	List<CostSample> fl = new ArrayList<CostSample>();
     	Iterator it = TableUtils.toObjects( res, new CostSample() );
	while( it.hasNext() ) {
	    fl.add( (CostSample)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	CostSample[] facs = new CostSample[ fl.size() ];
     	return (CostSample[])fl.toArray( facs );
    }

    /**
     * Returns a list of cost items per sample type.
     *
     * @param sampleType the name of the sample type.
     * @param region storage region.
     *
     * @return an array of CostSample objects.
     */
    public CostSample[] findCostBySampleType( String sampleType ) throws SQLException {
	return findCostBySampleType( sampleType, "" );
    }

    /**
     * Returns a cost item by id.
     *
     * @param costid the id of the cost item.
     *
     * @return the Cost object or null.
     */
    public Cost findCostById( long costid ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_COST_BY_ID );
     	pstmt.setLong( 1, costid );
     	ResultSet res = pstmt.executeQuery();
     	Cost sType = null;
     	if( res.next() ) 
     	    sType = (Cost)TableUtils.toObject( res, new Cost() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

    /**
     * Creates a new cost estimate.
     *
     * @param project the project name (null or empty creates a new name based on the datetime).
     *
     * @return a newly created <code>CostEstimate</code>
     */
    public CostEstimate createCostEstimate( String project ) 
	throws SQLException {

	CostEstimate estimate = new CostEstimate();
	String pName = Stringx.getDefault( project, "").trim();
	if( pName.length() <= 0 )
	    pName = "Estimate as of "+Stringx.currentDateString("MMM dd, yyyy");
	estimate.setProjectname( pName );

	PreparedStatement pstmt = getStatement( STMT_ESTIMATE_INSERT );
	pstmt.setLong( 1, estimate.getEstimateid() );
	pstmt.setString( 2, estimate.getProjectname() );
        pstmt.setString( 3, estimate.getRegion() );
	pstmt.setTimestamp( 4, estimate.getCreated() );
	pstmt.setInt( 5, estimate.getDuration() );
	pstmt.setFloat( 6, estimate.getTotal() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Cost estimate created: "+estimate );

	// clean expired cost estimates

	Timestamp expDate = new Timestamp( System.currentTimeMillis() - COST_EXPIRE );

	pstmt = getStatement( STMT_COSTITEM_EXPIRED );
	pstmt.setTimestamp( 1, expDate );
     	pstmt.executeUpdate();
	pstmt = getStatement( STMT_ESTIMATE_EXPIRED );
	pstmt.setTimestamp( 1, expDate );
     	pstmt.executeUpdate();
	popStatement( pstmt );

	return estimate;
    }

    /**
     * Returns a cost estimate by id.
     *
     * @param estimateId the id of the cost estimate.
     *
     * @return a cost estimate if found or null otherwise.
     */
    public CostEstimate findCostEstimateById( long estimateId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_ESTIMATE_BY_ID );
     	pstmt.setLong( 1, estimateId );

     	ResultSet res = pstmt.executeQuery();

     	// List<CostItem> fl = new ArrayList<CostItem>();
	CostEstimate estimate = null;
	while( res.next() ) {
	    if( estimate == null )
		estimate = (CostEstimate)TableUtils.toObject( res, new CostEstimate() );
	    CostItem ci = (CostItem)TableUtils.toObject( res, new CostItem() );
	    estimate.addCost( ci );
	}	       
	res.close();
	popStatement( pstmt );

	return estimate;
    }

    private CostEstimate addCostItem( CostEstimate ce, CostSample cs, long numItems ) {
	CostItem ci = new CostItem();
	ci.setEstimateid( ce.getEstimateid() );
	ci.setCostid( cs.getCostid() );
	ci.setItemtype( cs.toItemName() );
	ci.setItemcount( numItems );
	ce.addTotal( (float)numItems * cs.getPrice() );
	ce.addCost( ci );
	return ce;
    }

    /**
     * Adds sample storage costs to an estimate.
     *
     * @param ce the cost estimate to be updated.
     * @param cs the cost per sample.
     * @param numItems the number of samples.
     *
     * @return the updated cost estimate.
     */
    public CostEstimate addCostItem( long estimateId, CostSample cs, long numItems ) 
	throws SQLException {

	CostEstimate ce = findCostEstimateById( estimateId );
	if( ce == null ) {
	    log.error( "Cannot find cost estimate id: "+estimateId );
	    throw new SQLException( "Cannot find cost estimate id: "+estimateId );
	}
	return addCostItem( ce, cs, numItems );
    }

    /**
     * Updates an existing cost estimate.
     *
     * @param estimate the cost estimate to be updated.
     *
     * @return an updated <code>CostEstimate</code> object.
     */
    public CostEstimate updateCostEstimate( CostEstimate estimate )
	throws SQLException {

	String pName = Stringx.getDefault( estimate.getProjectname(), "").trim();
	if( pName.length() <= 0 )
	    pName = "Estimate as of "+Stringx.currentDateString("MMM dd, yyyy");
	estimate.setProjectname( pName );
	CostItem[] items = estimate.getCosts();

	// check if project costs have been added.

	CostSample[] extraCosts = findCostBySampleType( "project", estimate.getRegion() );
	boolean costExists = false;
	for( int j = 0; j < extraCosts.length; j++ ) {
	    costExists = false;
	    for( int i= 0; i < items.length; i++ ) {
		if( items[i].getCostid() == extraCosts[j].getCostid() ) {
		    costExists = true;
		    break;
		}
	    }

	    if( !costExists ) 
		estimate = addCostItem( estimate, extraCosts[j], 1L );
	}

 	// check if registration costs exist

	extraCosts = findCostBySampleType( SAMPLE_REGISTRATION, estimate.getRegion() );
	costExists = false;
	for( int i= 0; i < items.length; i++ ) {
	    if( items[i].getCostid() == extraCosts[0].getCostid() ) {
		costExists = true;
		break;
	    }
	}
	if( !costExists )
	    estimate = addCostItem( estimate, extraCosts[0], 0L );
	
	PreparedStatement pstmt = getStatement( STMT_COSTITEM_DELETE );
	pstmt.setLong( 1, estimate.getEstimateid() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	estimate.setTotal( 0f );
	long numSamples = 0L;
	List<CostItem> adminCosts = new ArrayList<CostItem>();

	pstmt = getStatement( STMT_COSTITEM_INSERT );
	
	for( CostItem ci : estimate.getCosts() ) {
	    Cost cost = findCostById( ci.getCostid() );
	    if( cost != null ) {

		if( SAMPLE_ADMIN.equals(cost.getServicegroup()) ) {
		    adminCosts.add( ci );
		}
		else {
		    float amount = (float)ci.getItemcount() * cost.getPrice();
		    if( COST_PER_MONTH.equals( cost.getFrequency() ) )
			amount = amount * (float)estimate.getDuration();
		    estimate.addTotal( amount );

		    if( SAMPLE_STORAGE.equals(cost.getServicegroup()) ) 
			numSamples+=ci.getItemcount();

		    pstmt.setLong( 1, ci.getCostitemid() ); 
		    pstmt.setLong( 2, estimate.getEstimateid() );
		    pstmt.setString( 3, ci.getItemtype() ); 
		    pstmt.setLong( 4, cost.getCostid() );
		    pstmt.setLong( 5, ci.getItemcount() );
		    pstmt.executeUpdate();
		
		    log.debug( "Cost item inserted: "+ci );
		}
	    }
	    else
		log.warn( "Cost id "+String.valueOf(ci.getCostid())+" is invalid" );
	}

	// add admin costs per sample

	for( CostItem ci : adminCosts ) {
	    Cost cost = findCostById( ci.getCostid() );
	    if( cost != null ) {

		ci.setItemcount(  numSamples );
		estimate.addTotal( (float)ci.getItemcount() * cost.getPrice() );

		pstmt.setLong( 1, ci.getCostitemid() ); 
		pstmt.setLong( 2, estimate.getEstimateid() );
		pstmt.setString( 3, ci.getItemtype() ); 
		pstmt.setLong( 4, cost.getCostid() );
		pstmt.setLong( 5, ci.getItemcount() );
		pstmt.executeUpdate();
	    }
	    else
		log.warn( "Cost id "+String.valueOf(ci.getCostid())+" is invalid" );
	    
	}
	popStatement( pstmt );

	pstmt = getStatement( STMT_ESTIMATE_UPDATE );
	pstmt.setString( 1, estimate.getProjectname() );
	pstmt.setString( 2, estimate.getRegion() );
	pstmt.setTimestamp( 3, estimate.getCreated() );
	pstmt.setInt( 4, estimate.getDuration() );
	pstmt.setFloat( 5, estimate.getTotal() );

	pstmt.setLong( 6, estimate.getEstimateid() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Cost estimate updated: "+estimate );

	return estimate;
    }

}
