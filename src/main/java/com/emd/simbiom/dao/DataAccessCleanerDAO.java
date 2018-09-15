package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>DataAccessCleanerDAO</code> implements the <code>DataAccessCleaner</code> API.
 *
 * Created: Fr Aug 31 18:08:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class DataAccessCleanerDAO extends BasicDAO implements DataAccessCleaner {

    private static Log log = LogFactory.getLog(DataAccessCleanerDAO.class);

    public DataAccessCleanerDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return new String[0];
    }

    /**
     * Clean up resources occupied by this DAO.
     */
    public void close() {
	closeDAO();
	log.debug( "Data Access Object cleaned" );
    }

}
