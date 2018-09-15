package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.StudySample;
import com.emd.simbiom.model.Sample;

/**
 * <code>Studies</code> specifies functionality to handles studies.
 *
 * Created: Sun Aug 26 21:16:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface Studies {

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public Study findStudyByName( String studyName ) throws SQLException;

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public String[] findStudyTerms() throws SQLException;

    /**
     * Returns a study by id.
     *
     * @param studyId the study id
     * @return the SampleType object.
     */
    public Study findStudyById( long studyId ) throws SQLException;

    /**
     * Returns a study by id.
     *
     * @param sample the sample.
     * @return the <code>Study</code> objects (or empty array.
     */
    public Study[] findStudySample( Sample sample ) throws SQLException;

    /**
     * Create a study.
     *
     * @param studyName the study name.
     *
     * @return the newly allocated study.
     */
    public Study createStudy( String studyName ) throws SQLException;

    /**
     * Assign a Create an accession.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public StudySample assignStudySample( long userId, Study study, Sample sample ) throws SQLException;


}
