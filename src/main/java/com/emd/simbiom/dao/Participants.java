package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Donor;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.Subject;

/**
 * <code>Participants/code> specifies handling of donors and subjects.
 *
 * Created: Mon Aug 27 19:32:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface Participants {

     /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Subject findSubjectByName( Study study, String subjectId ) throws SQLException;

    /**
     * Returns a subject by id.
     *
     * @param donorId the subject's id.
     *
     * @return the <code>Subject</code> object.
     */
    public Subject findSubjectById( long donorId ) throws SQLException;

    /**
     * Returns a subject by study and sample.
     *
     * @param study the study.
     * @param sample the sample.
     * @return the Organization object.
     */
    public Subject findSubjectBySample( Study study, Sample sample ) throws SQLException;

    /**
     * Create a study subject.
     *
     * @param study the study.
     * @param subjectid the subject id.
     *
     * @return the newly allocated subject.
     */
    public Subject createSubject( Study study, String subjectId ) throws SQLException;

    /**
     * Updates a subject.
     *
     * @param userId the user id.
     * @param subject the subject.
     * @param props additional properties of the subject (can be null).
     *
     * @return the updated subject.
     */
    public Subject storeSubject( long userId, Subject subject ) throws SQLException;

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Donor[] findSampleDonor( Sample sample ) throws SQLException;

    /**
     * Assign a sample to a donor.
     *
     * @param userId the user id
     * @param subject the subject.
     * @param sample the sample to be assigned.
     *
     * @return the <code>Donor</code> object.
     */
    public Donor assignDonor( long userId, Subject subject, Sample sample ) throws SQLException;

    /**
     * Returns grouped property values for the given property.
     *
     * @return the SampleType object.
     */
    public String[] findDonorPropertyTerms( String propertyName ) throws SQLException;

}
