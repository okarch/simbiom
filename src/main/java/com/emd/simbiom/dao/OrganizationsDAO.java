package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Organization;
// import com.emd.simbiom.model.Sample;
// import com.emd.simbiom.model.Study;

import com.emd.util.Stringx;

/**
 * <code>OrganizationsDAO</code> implements the <code>Organizations</code> API.
 *
 * Created: Thu Aug 23 08:50:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class OrganizationsDAO extends BasicDAO implements Organizations {

    private static Log log = LogFactory.getLog(OrganizationsDAO.class);

    private static final String STMT_ORG_BY_ID           = "biobank.organization.findById";
    private static final String STMT_ORG_BY_NAME         = "biobank.organization.findByName";
    private static final String STMT_ORG_COLLECT         = "biobank.organization.findCollectionSite";
    private static final String STMT_ORG_INSERT          = "biobank.organization.insert";
    private static final String STMT_ORG_UPDATE          = "biobank.organization.update";

    private static final String[] entityNames = new String[] {
	"organization"
    };


    public OrganizationsDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Organization findOrganizationByName( String orgName ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_ORG_BY_NAME );
     	pstmt.setString( 1, Stringx.getDefault(orgName,"").trim() );
     	ResultSet res = pstmt.executeQuery();
     	Organization sType = null;
     	if( res.next() ) 
     	    sType = (Organization)TableUtils.toObject( res, new Organization() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

    /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Organization findOrganizationById( long orgid ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_ORG_BY_ID );
     	pstmt.setLong( 1, orgid );
     	ResultSet res = pstmt.executeQuery();
     	Organization sType = null;
     	if( res.next() ) 
     	    sType = (Organization)TableUtils.toObject( res, new Organization() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

    /**
     * Create a study.
     *
     * @param studyName the study name.
     *
     * @return the newly allocated study.
     */
    public Organization createOrganization( String orgName, String orgType ) throws SQLException {
	Organization org = findOrganizationByName( orgName );
	if( org != null )
	    throw new SQLException( "Organization already exists: "+orgName );

	org = new Organization();
	org.setOrgname( orgName );
	org.setOrgtype( orgType );

	PreparedStatement pstmt = getStatement( STMT_ORG_INSERT );
	pstmt.setLong( 1, org.getOrgid() );
	pstmt.setString( 2, org.getOrgname() );
	pstmt.setString( 3, org.getSiteid() );
	pstmt.setInt( 4, org.getCountryid() );
	pstmt.setString( 5, org.getOrgtype() );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Organization created: "+org.getOrgname()+" ("+
		   org.getOrgid()+")" );
	 
	return org;
    }

    /**
     * Updates an existing organization.
     *
     * @param org the Organization.
     *
     * @return the updated organization.
     */
    public Organization storeOrganization( Organization org ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_ORG_UPDATE );

	pstmt.setString( 1, org.getOrgname() );
	pstmt.setString( 2, org.getSiteid() );
	pstmt.setInt( 3, org.getCountryid() );
	pstmt.setString( 4, org.getOrgtype() );

	pstmt.setLong( 5, org.getOrgid() );

	pstmt.executeUpdate();
	popStatement( pstmt );
	return org;
    }

    /**
     * Returns the site where the sample has been taken.
     *
     * @param sampleid The sample id.
     * @return the Orgainzation or clinical site the sample has been taken.
     */
    public Organization findCollectionSite( String sampleid ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_ORG_COLLECT );
     	pstmt.setString( 1, sampleid );
     	ResultSet res = pstmt.executeQuery();

     	Organization sType = null;
     	if( res.next() ) 
     	    sType = (Organization)TableUtils.toObject( res, new Organization() );
     	res.close();
	popStatement( pstmt );
     	return sType;
    }

}
