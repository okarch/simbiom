package com.emd.simbiom.dao;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.DocumentContent;
import com.emd.simbiom.model.Trackable;

/**
 * <code>BasicDAO</code> implements basic database access.
 *
 * Created: Sat Aug 11 07:56:07 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public abstract class BasicDAO implements DocumentLoader {
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

    protected DocumentContent findDocumentContent( String md5 )
	throws SQLException {
	if( database != null )
	    return database.findDocumentContent( md5 );
	return null;
    }

    /**
     * Writes document content to the given <code>OutputStream</code>.
     *
     * @param md5sum the content identifier.
     * @param mime the mime type (can be null if provided could be used to apply some output encoding)
     * @param outs the output stream to write content to.
     *
     * @return true if content was written, false otherwise.
     *
     * @exception IOException is thrown when an error occurs.
     */
    public boolean writeContent( String md5sum, String mime, OutputStream outs )
	throws IOException {

	if( database != null )
	    return database.writeContent( md5sum, mime, outs );
	return false;
    }

    /**
     * Reads from the given <code>InputStream</code> and stores the content.
     *
     * @param md5sum the content identifier (if null it will be calculated based on the content).
     * @param mime the mime type (can be null if provided could be used to apply some input encoding)
     * @param ins the input stream to read from.
     *
     * @return the md5sum calculated from the content.
     *
     */
    // public String storeContent( String md5sum, String mime, InputStream ins )
    // 	throws IOException {

    // 	// String updCont = null;
    // 	StringWriter sw = new StringWriter();
    // 	WriterOutputStream outs = new WriterOutputStream( sw );
    // 	ZipCoder.encodeTo( ins, outs );
    // 	outs.flush();
    // 	String updCont = sw.toString();
    // 	ins.close();
    // 	outs.close();
    // 	String md5 = UploadContent.calculateMd5sum( updCont );
    // 	log.debug( "Coded content length "+String.valueOf(updCont.length())+" md5sum: "+md5 );

    // 	PreparedStatement pstmt = null;
    // 	try {
    // 	    pstmt = getStatement( STMT_RAW_DELETE );
    // 	    pstmt.setString( 1, md5 );
    // 	    pstmt.executeUpdate();
    // 	}
    // 	catch( SQLException sqe ) {
    // 	    log.warn( "Deleting "+md5+": "+Stringx.getDefault(sqe.getMessage(),"") );
    // 	}

    // 	try {
    // 	    pstmt = getStatement( STMT_RAW_INSERT );
    // 	    pstmt.setString( 1, md5 );
    // 	    pstmt.setString( 2, updCont );
    // 	    pstmt.executeUpdate();
    // 	}
    // 	catch( SQLException sqe ) {
    // 	    log.error( sqe );
    // 	    throw new IOException( sqe );
    // 	}

    // 	return md5;
    // }

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
