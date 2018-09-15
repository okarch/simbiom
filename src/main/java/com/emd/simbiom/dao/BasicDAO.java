package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Trackable;

/**
 * <code>BasicDAO</code> implements basic database access.
 *
 * Created: Sat Aug 11 07:56:07 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public abstract class BasicDAO {
    private DatabaseDAO database;
    private DataAccessObjectProvider dataAccessObjectProvider;

    private static Log log = LogFactory.getLog(BasicDAO.class);

    protected BasicDAO( DatabaseDAO db ) {
	this.database = db;
	this.dataAccessObjectProvider = null;
    }

    /**
     * Get the <code>Database</code> value.
     *
     * @return a <code>DatabaseDAO</code> value
     */
    public final DatabaseDAO getDatabase() {
	return database;
    }

    /**
     * Set the <code>Database</code> value.
     *
     * @param database The new Database value.
     */
    public final void setDatabase(final DatabaseDAO database) {
	this.database = database;
    }

    /**
     * Get the <code>DataAccessObjectProvider</code> value.
     *
     * @return a <code>DataAccessObjectProvider</code> value
     */
    public final DataAccessObjectProvider getDataAccessObjectProvider() {
	return dataAccessObjectProvider;
    }

    /**
     * Set the <code>DataAccessObjectProvider</code> value.
     *
     * @param dataAccessObjectProvider The new DataAccessObjectProvider value.
     */
    public final void setDataAccessObjectProvider(final DataAccessObjectProvider dataAccessObjectProvider) {
	this.dataAccessObjectProvider = dataAccessObjectProvider;
    }

    protected PreparedStatement getStatement( String stmtName ) throws SQLException {
	if( database == null )
	    throw new SQLException( "Invalid database configuration" );
	return database.getStatement( stmtName );
    }

    protected void popStatement( PreparedStatement pstmt ) {
	if( database != null )
	    database.popStatement( pstmt );
    }

    protected void trackChange( Trackable before,
				Trackable changed,
				long userId,
				String activity,
				String remark )
	throws SQLException {
	if( database != null )
	    database.trackChange( before, changed, userId, activity, remark );
    }

    protected SampleInventory getDAO() {
	if( dataAccessObjectProvider != null )
	    return dataAccessObjectProvider.getSampleInventory();
	return null;
    }

    protected FullContentSearch getContentSearch() {
	return database.getContentSearch();
    }

    /**
     * Initializes the database entities.
     */
    public void initDAO() throws SQLException {
	if( database == null )
	    throw new SQLException( "Invalid database configuration" );
	String[] ents = getEntityNames();
	log.debug( "Number of entities to be created: "+ents.length );
	for( int i = 0; i < ents.length; i++ ) 
	    database.createEntity( ents[i] );
    }

    /**
     * Returns a list of supported entity names.
     *
     * @return an array of entity names.
     */
    public abstract String[] getEntityNames();

    /**
     * Clean up resources occupied by this DAO.
     *
     */
    protected void closeDAO() {
	if( database != null )
	    database.close();
    }
}
