package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.User;


/**
 * <code>UserLookup</code> specifies functionality to support user management.
 *
 * Created: Wed Aug 22 07:40:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface UserLookup {

    /**
     * Returns user information by apikey.
     *
     * @param apikey The API key.
     * @return the User object.
     */
    public User findUserByApikey( String apikey ) throws SQLException;

    /**
     * Returns user information by muid.
     *
     * @param muid The MUID.
     * @return the User object.
     */
    public User findUserByMuid( String apikey ) throws SQLException;

    /**
     * Returns user information by apikey.
     *
     * @param apikey The API key.
     * @return the User object.
     */
    public User findUserById( long userid ) throws SQLException;

}
