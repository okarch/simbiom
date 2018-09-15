package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.User;


/**
 * <code>UserLookupDAO</code> implements the <code>UserLookup</code> API.
 *
 * Created: Thu Aug 21 17:40:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class UserLookupDAO extends BasicDAO implements UserLookup {

    private static Log log = LogFactory.getLog(UserLookupDAO.class);

    private static final String STMT_USER_BY_APIKEY      = "biobank.user.findByApikey";
    private static final String STMT_USER_BY_MUID        = "biobank.user.findByMuid";
    private static final String STMT_USER_BY_ID          = "biobank.user.findById";

    private static final String[] entityNames = new String[] {
	"user"
    };


    public UserLookupDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns user information by apikey.
     *
     * @param apikey The API key.
     * @return the User object.
     */
    public User findUserByApikey( String apikey ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_USER_BY_APIKEY );
     	pstmt.setString( 1, apikey );
     	ResultSet res = pstmt.executeQuery();
     	User user = null;
     	if( res.next() ) 
     	    user = (User)TableUtils.toObject( res, new User() );
     	res.close();
	popStatement( pstmt );
     	return user;
    }

    /**
     * Returns user information by muid.
     *
     * @param muid The MUID.
     * @return the User object.
     */
    public User findUserByMuid( String apikey ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_USER_BY_MUID );
     	pstmt.setString( 1, apikey );
     	ResultSet res = pstmt.executeQuery();
     	User user = null;
     	if( res.next() ) 
     	    user = (User)TableUtils.toObject( res, new User() );
     	res.close();
	popStatement( pstmt );
     	return user;
    }

    /**
     * Returns user information by apikey.
     *
     * @param apikey The API key.
     * @return the User object.
     */
    public User findUserById( long userid ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_USER_BY_ID );
     	pstmt.setLong( 1, userid );
     	ResultSet res = pstmt.executeQuery();
     	User user = null;
     	if( res.next() ) 
     	    user = (User)TableUtils.toObject( res, new User() );
     	res.close();
	popStatement( pstmt );
     	return user;
    }

}
