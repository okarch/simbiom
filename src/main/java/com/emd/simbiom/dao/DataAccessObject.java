package com.emd.simbiom.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>DataAccessObject</code> implements basic data access object funtionality to be used as no-op proxy.
 *
 * Created: Sat Sep  1 12:56:07 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class DataAccessObject extends BasicDAO {

    private static Log log = LogFactory.getLog(DataAccessObject.class);

    public DataAccessObject( DatabaseDAO db ) {
	super( db );
    }

    /**
     * Returns a list of supported entity names.
     *
     * @return an array of entity names.
     */
    public String[] getEntityNames() {
	return new String[0];
    }

}
