package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleDetails;
import com.emd.simbiom.model.SampleSummary;

/**
 * <code>SampleReports</code> handles sample reporting, e.g. summary, details etc.
 *
 * Created: Tue Aug 28 20:30:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface SampleReports {

    /**
     * Returns a list of samples associated with the query content.
     * This is searching across all the content related to samples
     *
     * @param query The query string.
     * @return A list of samples.
     */
    public Sample[] findSampleByContent( String query ) throws SQLException;

    /**
     * Returns a list of sample summaries.
     *
     * @param categoryPath the groupings to be applied.
     *
     * @return an array of summary groups.
     */
    public SampleSummary[] createSampleSummary( String categoryPath ) throws SQLException;

    /**
     * Returns a list of samples by sample type.
     *
     * @param categoryPath the groupings to be applied.
     *
     * @return an array of summary groups.
     * @param type The sample type.
     * @return A list of samples.
     */
    public Sample[] findSampleByCategory( String categoryPath ) throws SQLException;

    /**
     * Returns the sample details of the given sample id.
     *
     * @param sampleId the id of the sample.
     *
     * @return the <code>SampleDetails</code> object (or null).
     */
    public SampleDetails findSampleDetailsById( String sampleId ) throws SQLException;

    /**
     * Creates a report of all attributes linked to a sample.
     *
     * @param sample the sample. 
     *
     * @return a newly created <code>RestrictionRule</code> object.
     *
     * @exception SQLException in case of a duplicate rule has been found.
     */
    public SampleDetails createSampleDetails( Sample sample ) throws SQLException;

}
