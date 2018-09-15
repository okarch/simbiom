package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Accession;
import com.emd.simbiom.model.Organization;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.Sample;

/**
 * <code>Accessions</code> specifies handling of sample accessions.
 *
 * Created: Mon Aug 28 17:18:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface Accessions {

    /**
     * Returns a list of accessions associated with a study and issued by a certain organization.
     *
     * @param study the study context.
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Accession[] findAccession( Study study, Organization org, String acc ) throws SQLException;

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Accession[] findAccession( Organization org, String acc ) throws SQLException;

    /**
     * Returns a list of accessions associated with a sample.
     *
     * @param sample the sample.
     *
     * @return Array of <code>Accession</code> object.
     */
    public Accession[] findSampleAccession( Sample sample ) throws SQLException;

    /**
     * Create an accession.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public Accession createAccession( long userId, 
				      Organization org, 
				      Sample sample,
				      String accession,
				      String accType ) 
	throws SQLException;

}
