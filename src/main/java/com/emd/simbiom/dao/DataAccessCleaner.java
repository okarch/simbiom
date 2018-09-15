package com.emd.simbiom.dao;

/**
 * Describe interface DataAccessCleaner here.
 *
 *
 * Created: Fri Aug 31 18:10:14 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface DataAccessCleaner {

    /**
     * Clean up resources occupied by this DAO.
     *
     */
    public void close();
}
