package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Age;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleType;

/**
 * <code>Samples</code> specifies handling of samples and sample types.
 *
 * Created: Mon Aug 27 18:35:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface Samples {

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public SampleType[] findSampleTypeByNameAll( String sampleType ) throws SQLException;

    /**
     * Maps a sample type term to another sample type.
     *
     * @return the list of matching <code>SampleType</code> objects.
     */
    public SampleType[] mapSampleType( String sampleType ) throws SQLException;

    /**
     * Returns a sample type by name. 
     *
     * @return the SampleType object.
     */
    public SampleType findSampleTypeByName( String sampleType ) throws SQLException;

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public SampleType findSampleTypeById( long typeId ) throws SQLException;

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public String[] findSampleTypeTerms() throws SQLException;

    /**
     * Creates a sample type (if not existing).
     *
     * @param sampleType the name of the sample type
     *
     * @return the sample type.
     */
    public SampleType createSampleType( String sampleType ) throws SQLException;

    /**
     * Create a sample.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public Sample createSample( long userId, String sampleType ) throws SQLException;

    /**
     * Returns a sample by id.
     *
     * @param sampleId The sample id.
     * @return the Sample object or null (if not existing).
     */
    public Sample findSampleById( String sampleId ) throws SQLException;

    /**
     * Returns a list of samples recently created.
     *
     * @param age The age of the samples (created or collected, older or newer).
     * @return A list of samples.
     */
    public Sample[] findSampleByAge( Age age ) throws SQLException;

    /**
     * Returns a list of samples recently created.
     *
     * @return A list of samples.
     */
    public Sample[] findSampleLastCreated() throws SQLException;

    /**
     * Returns a list of samples recently created.
     *
     * @param age The age of the samples (created or collected, older or newer).
     * @return A list of samples.
     */
    public Sample[] findSampleByStudy( String study ) throws SQLException;

    /**
     * Returns a list of samples by sample type.
     *
     * @param type The sample type.
     * @return A list of samples.
     */
    public Sample[] findSampleByType( String type ) throws SQLException;

    /**
     * Returns a list of samples where the donor property match the given value.
     *
     * @param propertyName The property name.
     * @param propertyVal The property value.
     * @return A list of samples.
     */
    public Sample[] findSampleByDonorProperty( String propertyName, String propertyVal ) throws SQLException;

    /**
     * Loads the sample related properties.
     *
     * @param sample the sample.
     * @param merge merges the existing properties into the loaded properties if set to true.
     *
     * @return the annotated sample.
     */
    public Sample loadSampleProperties( Sample sample, boolean merge )
	throws SQLException;

    /**
     * Updates a sample.
     *
     * @param userId the user id.
     * @param sample the subject.
     *
     * @return the updated sample.
     */
    public Sample storeSample( long userId, Sample sample ) throws SQLException;

}
