package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Cost;
import com.emd.simbiom.model.CostEstimate;
import com.emd.simbiom.model.CostItem;
import com.emd.simbiom.model.CostSample;


/**
 * <code>StorageCost</code> specifies storage cost related functionality.
 *
 * Created: Thu Aug 16 21:50:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface StorageCost {

    public static final String SAMPLE_ADMIN             = "sample admin";
    public static final String SAMPLE_REGISTRATION      = "registration";
    public static final String SAMPLE_STORAGE           = "sample storage";

    /**
     * Returns a list of cost items per sample type.
     *
     * @param sampleType the name of the sample type.
     * @param region storage region.
     *
     * @return an array of CostSample objects.
     */
    public CostSample[] findCostBySampleType( String sampleType, String region ) throws SQLException;

    /**
     * Returns a list of cost items per sample type.
     *
     * @param sampleType the name of the sample type.
     * @param region storage region.
     *
     * @return an array of CostSample objects.
     */
    public CostSample[] findCostBySampleType( String sampleType ) throws SQLException;

    /**
     * Returns a cost item by id.
     *
     * @param costid the id of the cost item.
     *
     * @return the Cost object or null.
     */
    public Cost findCostById( long costid ) throws SQLException;

    /**
     * Creates a new cost estimate.
     *
     * @param project the project name (null or empty creates a new name based on the datetime).
     *
     * @return a newly created <code>CostEstimate</code>
     */
    public CostEstimate createCostEstimate( String project ) throws SQLException;

    /**
     * Returns a cost estimate by id.
     *
     * @param estimateId the id of the cost estimate.
     *
     * @return a cost estimate if found or null otherwise.
     */
    public CostEstimate findCostEstimateById( long estimateId ) throws SQLException;

    /**
     * Adds sample storage costs to an estimate.
     *
     * @param ce the cost estimate to be updated.
     * @param cs the cost per sample.
     * @param numItems the number of samples.
     *
     * @return the updated cost estimate.
     */
    public CostEstimate addCostItem( long estimateId, CostSample cs, long numItems ) throws SQLException;

    /**
     * Updates an existing cost estimate.
     *
     * @param estimate the cost estimate to be updated.
     *
     * @return an updated <code>CostEstimate</code> object.
     */
    public CostEstimate updateCostEstimate( CostEstimate estimate ) throws SQLException;

}
