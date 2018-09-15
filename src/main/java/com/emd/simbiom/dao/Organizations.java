package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Organization;
// import com.emd.simbiom.model.Sample;
// import com.emd.simbiom.model.Study;

/**
 * <code>Organizations</code> specifies operations to handle sites and labs.
 *
 * Created: Thu Aug 23 18:50:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface Organizations {

    /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Organization findOrganizationByName( String orgName ) throws SQLException;

    /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Organization findOrganizationById( long orgid ) throws SQLException;

    /**
     * Create a study.
     *
     * @param studyName the study name.
     *
     * @return the newly allocated study.
     */
    public Organization createOrganization( String orgName, String orgType ) throws SQLException;

    /**
     * Updates an existing organization.
     *
     * @param org the Organization.
     *
     * @return the updated organization.
     */
    public Organization storeOrganization( Organization org ) throws SQLException;

    /**
     * Returns the site where the sample has been taken.
     *
     * @param sampleid The sample id.
     * @return the Orgainzation or clinical site the sample has been taken.
     */
    public Organization findCollectionSite( String sampleid ) throws SQLException;

}
