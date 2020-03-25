package com.emd.simbiom.dao;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.net.URL;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import java.util.zip.DataFormatException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import org.apache.commons.dbcp2.BasicDataSource;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;

import org.apache.commons.dbutils.DbUtils;

import org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.DocumentContent;
import com.emd.simbiom.model.Trackable;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Stringx;

/**
 * <code>DatabaseDAO</code> the database access the sample inventory.
 *
 * Created: Mon Jan 12 14:27:17 2015
 *
 * @author <a href="mailto:">Oliver</a>
 * @version 1.0
 */
public class DatabaseDAO implements DocumentLoader {
    private String       url;
    private String       username;
    private String       password;
    private String       driver;
    private String       template;
    private String       schema;
    private String       test;
    private int          retryCount;
    private int          popCount;
    private long         lastActivity;

    private boolean      initDatabaseFromTemplate;
    private boolean pooled;

    private Connection   dbSource;
    private Connection   lastConnection;

    private DataSource   dataSource;
    
    private FullContentSearch contentSearch;

    private PropertiesConfiguration templateConfig;
    private Map<String,PreparedStatement>  statements;

    private static Log log = LogFactory.getLog(DatabaseDAO.class);
    private static DatabaseDAO databaseDAO;

    private static final int DEFAULT_RETRY       = 10;

    private static final String DEFAULT_DRIVER   = "com.mysql.jdbc.Driver";
    private static final String DEFAULT_URL      = "jdbc:mysql://localhost/biobank";
    private static final String DEFAULT_USERNAME = "biobank";
    private static final String DEFAULT_PASSWORD = "biobank";
    private static final String DEFAULT_TEMPLATE = "biobank-mysql.properties";
    private static final String DEFAULT_SCHEMA   = "biobank";
    private static final String DEFAULT_TEST     = "select 1 from dual";

    private static final String DEFAULT_CONT_SERVER = "localhost:7272";

    private static final String STMT_CONTENT_BY_MD5      = "biobank.uploadraw.findMd5";

    private static final String STMT_TRACK_BY_ID         = "biobank.track.findById";
    private static final String STMT_TRACK_DELETE        = "biobank.track.delete";
    private static final String STMT_TRACK_INSERT        = "biobank.track.insert";

    private static final String DEFAULT_RESOURCE = "biobank-mysql.properties";

    private static final long   DEFAULT_REFRESH  = 7L * 60L * 1000L; // 7 minutes

    private static final long   IDLE_PERIOD             =  10L * 60L * 1000L; // 10 minutes

    private static final String[] GROUPINGS = {
	"Diseases",
	"Locations",
	"Molecules",
	"Sample types",
	"Studies",
	"Indications"
    };
    /**
     * Describe pooled here.
     */

    // private static final long   POLICY_INTERVAL  = 10L * 60L * 60L * 1000L; // every 10 minutes

    public DatabaseDAO() {
	this.driver = DEFAULT_DRIVER;
	this.url = DEFAULT_URL;
	this.username = DEFAULT_USERNAME;
	this.password = DEFAULT_PASSWORD;
	this.template = DEFAULT_TEMPLATE;
	this.retryCount = DEFAULT_RETRY;
	this.schema = DEFAULT_SCHEMA;
	this.test = DEFAULT_TEST;
	this.statements = new HashMap<String,PreparedStatement>();
	this.initDatabaseFromTemplate = false;
	this.lastActivity = System.currentTimeMillis();
    }

    /**
     * Creates a singleton instance to be used for interpretation of
     * list activities
     */
    public static DatabaseDAO getInstance() {
	if( databaseDAO == null ) {
	    databaseDAO = new DatabaseDAO();
	    databaseDAO.setTemplate( DEFAULT_RESOURCE );
	    databaseDAO.setInitDatabaseFromTemplate( true );
	    log.debug( "New database instance created" );
	}
	return databaseDAO;
    }

    /**
     * Get the Url value.
     * @return the Url value.
     */
    public String getUrl() {
	return url;
    }

    /**
     * Set the Url value.
     * @param newUrl The new Url value.
     */
    public void setUrl(String newUrl) {
	this.url = newUrl;
    }

    /**
     * Get the Username value.
     * @return the Username value.
     */
    public String getUsername() {
	return username;
    }

    /**
     * Set the Username value.
     * @param newUsername The new Username value.
     */
    public void setUsername(String newUsername) {
	this.username = newUsername;
    }

    /**
     * Get the Driver value.
     * @return the Driver value.
     */
    public String getDriver() {
	return driver;
    }

    /**
     * Set the Driver value.
     * @param newDriver The new Driver value.
     */
    public void setDriver(String newDriver) {
	this.driver = newDriver;
    }

    /**
     * Get the Password value.
     * @return the Password value.
     */
    public String getPassword() {
	return password;
    }

    /**
     * Set the Password value.
     * @param newUsername The new Password value.
     */
    public void setPassword(String newPassword) {
	this.password = newPassword;
    }

    /**
     * Get the Template value.
     * @return the Template value.
     */
    public String getTemplate() {
	return template;
    }

    /**
     * Set the Template value.
     * @param newTemplate The new Template value.
     */
    public void setTemplate(String newTemplate) {
	this.template = newTemplate;
    }

    /**
     * Get the RetryCount value.
     * @return the RetryCount value.
     */
    public int getRetryCount() {
	return retryCount;
    }

    /**
     * Set the RetryCount value.
     * @param newRetryCount The new RetryCount value.
     */
    public void setRetryCount(int newRetryCount) {
	this.retryCount = newRetryCount;
    }

    /**
     * Get the InitDatabaseFromTemplate value.
     * @return the InitDatabaseFromTemplate value.
     */
    public boolean isInitDatabaseFromTemplate() {
	return initDatabaseFromTemplate;
    }

    /**
     * Set the InitDatabaseFromTemplate value.
     * @param newInitDatabaseFromTemplate The new InitDatabaseFromTemplate value.
     */
    public void setInitDatabaseFromTemplate(boolean newInitDatabaseFromTemplate) {
	this.initDatabaseFromTemplate = newInitDatabaseFromTemplate;
    }

    /**
     * Get the <code>Schema</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSchema() {
	return schema;
    }

    /**
     * Set the <code>Schema</code> value.
     *
     * @param schema The new Schema value.
     */
    public final void setSchema(final String schema) {
	this.schema = schema;
    }

    /**
     * Get the <code>Test</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTest() {
	return test;
    }

    /**
     * Set the <code>Test</code> value.
     *
     * @param test The new Test value.
     */
    public final void setTest(final String test) {
	this.test = test;
    }

    /**
     * Get the <code>ContentSearch</code> value.
     *
     * @return a <code>FullContentSearch</code> value
     */
    public final FullContentSearch getContentSearch() {
	return contentSearch;
    }

    /**
     * Set the <code>ContentSearch</code> value.
     *
     * @param contentSearch The new ContentSearch value.
     */
    public final void setContentSearch(final FullContentSearch contentSearch) {
	this.contentSearch = contentSearch;
    }

    /**
     * Get the <code>Pooled</code> value.
     *
     * @return a <code>boolean</code> value
     */
    public final boolean isPooled() {
	return pooled;
    }

    /**
     * Set the <code>Pooled</code> value.
     *
     * @param pooled The new Pooled value.
     */
    public final void setPooled(final boolean pooled) {
	this.pooled = pooled;
    }

    private DataSource createBasicDataSource() {
	BasicDataSource ds = new BasicDataSource();
	ds.setDriverClassName( getDriver() );
        ds.setUrl( getUrl() );
	ds.setUsername( getUsername() );
	ds.setPassword( getPassword() );

	ds.setMinIdle(5);
	ds.setMaxIdle(10);
	ds.setMaxWaitMillis( 5000L );
	ds.setPoolPreparedStatements( true );
	ds.setMaxOpenPreparedStatements( -1 );

	return ds;
    }

    // private DataSource createPoolingDataSource() {
    // 	// First, we'll create a ConnectionFactory that the
    // 	// pool will use to create Connections.
    // 	// We'll use the DriverManagerConnectionFactory,

    // 	ConnectionFactory connectionFactory =
    // 	    new DriverManagerConnectionFactory( getUrl(), getUsername(), getPassword() );
 
    // 	// Next we'll create the PoolableConnectionFactory, which wraps
    // 	// the "real" Connections created by the ConnectionFactory with
    // 	// the classes that implement the pooling functionality.

    // 	PoolableConnectionFactory poolableConnectionFactory =
    // 	    new PoolableConnectionFactory(connectionFactory, null);
	
    // 	// Now we'll need a ObjectPool that serves as the
    // 	// actual pool of connections.
    // 	// We'll use a GenericObjectPool instance, although
    // 	// any ObjectPool implementation will suffice.
	
    // 	ObjectPool<PoolableConnection> connectionPool =
    // 	    new GenericObjectPool<>(poolableConnectionFactory);
         
    // 	// Set the factory's pool property to the owning pool

    // 	poolableConnectionFactory.setPool(connectionPool);

    // 	// Finally, we create the PoolingDriver itself,
    // 	// passing in the object pool we created.

    // 	PoolingDataSource<PoolableConnection> ds =
    // 	    new PoolingDataSource<>(connectionPool);
	
    // 	return ds;
    // }

    private Connection connect( boolean forceConnect ) throws SQLException {
	initTemplate();

	if( isPooled() ) {
	    if( dataSource == null ) {
		log.debug( "Creating pooled inventory datasource" );
		dataSource = createBasicDataSource();
	    }
	    return dataSource.getConnection();

 // 118     public static void printDataSourceStats(DataSource ds) {
 // 119         BasicDataSource bds = (BasicDataSource) ds;
 // 120         System.out.println("NumActive: " + bds.getNumActive());
 // 121         System.out.println("NumIdle: " + bds.getNumIdle());
 // 122     }

	}
	else {
	    if( dbSource == null ) {
		log.debug( "Creating sample inventory datasource" );
		String st = getDriver();
		if( !DbUtils.loadDriver( st ) )
		    throw new SQLException(  "Cannot load jdbc driver: "+st );
		log.debug( "Database driver loaded: "+st );
		dbSource = DriverManager.getConnection( getUrl(), getUsername(), getPassword() );
	    }
	    else if( dbSource.isClosed() || forceConnect ) {

		if( forceConnect ) {
		    try {
			dbSource.close();
		    }
		    catch( SQLException sqe ) {
			log.debug( "Cannot close: "+Stringx.getDefault( sqe.getMessage(), "" ) );
		    }
		}
		
		int retries = getRetryCount();
		do {
		    retries--;
		    try {
			dbSource = DriverManager.getConnection( getUrl(), getUsername(), getPassword() );
			return dbSource;
		    }
		    catch( SQLException sqe ) {
			log.warn( sqe );
		    }
		    try {
			Thread.currentThread().sleep( 1000L );
		    }
		    catch( InterruptedException iex ) {
			throw new SQLException( "Interrupted retry" );
		    }
		}
		while( retries > 0 );
	    }
	}
	return dbSource;
    }

    // private Connection connect( boolean forceConnect ) throws SQLException {
    // 	if( dbSource == null ) {
    // 	    log.debug( "Creating sample inventory datasource" );
    // 	    String st = getDriver();
    // 	    if( !DbUtils.loadDriver( st ) )
    // 		throw new SQLException(  "Cannot load jdbc driver: "+st );
    // 	    log.debug( "Database driver loaded: "+st );
    // 	    dbSource = DriverManager.getConnection( getUrl(), getUsername(), getPassword() );
    // 	}
    // 	else if( dbSource.isClosed() || forceConnect ) {

    // 	    if( forceConnect ) {
    // 		try {
    // 		    dbSource.close();
    // 		}
    // 		catch( SQLException sqe ) {
    // 		    log.debug( "Cannot close: "+Stringx.getDefault( sqe.getMessage(), "" ) );
    // 		}
    // 	    }
		
    // 	    int retries = getRetryCount();
    // 	    do {
    // 		retries--;
    // 		try {
    // 		    dbSource = DriverManager.getConnection( getUrl(), getUsername(), getPassword() );
    // 		    return dbSource;
    // 		}
    // 		catch( SQLException sqe ) {
    // 		    log.warn( sqe );
    // 		}
    // 		try {
    // 		    Thread.currentThread().sleep( 1000L );
    // 		}
    // 		catch( InterruptedException iex ) {
    // 		    throw new SQLException( "Interrupted retry" );
    // 		}
    // 	    }
    // 	    while( retries > 0 );
    // 	}

    // 	return dbSource;
    // }

    private PropertiesConfiguration readTemplate() throws SQLException {
	URL url = this.getClass().getClassLoader().getResource( getTemplate() );
	if( url == null )
	    url = this.getClass().getResource( "/"+getTemplate() );
	if( url == null ) 
	    throw new SQLException( "Cannot locate template configuration: "+getTemplate() );
	try {
	    PropertiesConfiguration config = new PropertiesConfiguration( url );
	    FileChangedReloadingStrategy frs = new FileChangedReloadingStrategy();
	    frs.setRefreshDelay( DEFAULT_REFRESH );
	    config.setReloadingStrategy( frs );
	    log.debug( "Sample inventory database template read from "+url );
	    return config;
	}
	catch( ConfigurationException cex ) {
	    throw new SQLException( "Error loading configuration from "+url );
	}
    }

    private void createTable( Connection con, String entity ) throws SQLException {
	String schemaName = Stringx.getDefault(getSchema(),DEFAULT_SCHEMA);
	String tabName = templateConfig.getString( schemaName+"."+entity+".table" );
	log.debug( "Verify table "+Stringx.getDefault(tabName,"UNKNOWN") );
	boolean tabExists = false;
	if( !(tabExists = TableUtils.tableExists( con, tabName )) &&
	    !TableUtils.createTable( con, tabName, templateConfig.getString(schemaName+"."+entity+".structure"), false ) ) {
	    con.close();
	    throw new SQLException( "Cannot create table "+tabName );
	}
	if( !tabExists ) {
	    String[] idx = templateConfig.getStringArray( schemaName+"."+entity+".index" );
	    for( int i = 0; i < idx.length; i++ ) {
		if( !TableUtils.indexTable( con, tabName, idx[i], false ) )
		    log.warn( "Cannot create index on "+tabName+" "+idx[i] );
		log.debug( "Index "+idx[i]+" verified ("+tabName+")" );
	    }
	}
    }

    private void initTemplate() throws SQLException {
	if( templateConfig == null ) {
	    log.debug( "Initialize sample inventory database" );

	    templateConfig = readTemplate();
	    if( isInitDatabaseFromTemplate() ) {
		setDriver( templateConfig.getString( "db.driver", DEFAULT_DRIVER ) );
		setUrl( templateConfig.getString( "db.url", DEFAULT_URL ) );
		setUsername( templateConfig.getString( "db.username", DEFAULT_USERNAME ) );
		setPassword( templateConfig.getString( "db.password", DEFAULT_PASSWORD ) );
		setRetryCount( templateConfig.getInt( "db.retryCount", DEFAULT_RETRY ) );
		setSchema( templateConfig.getString( "db.schema", DEFAULT_SCHEMA ) );
		setTest( templateConfig.getString( "db.test", DEFAULT_TEST ) );
		setPooled( Stringx.toBoolean(templateConfig.getString( "db.pooled", "false" ),false) );

		FullContentSearch fcs = FullContentSearch.getInstance( templateConfig.getString( "db.content.server", DEFAULT_CONT_SERVER ) );
		setContentSearch( fcs );
	    }

	    Connection con = connect( false );
	    createTable( con, "track" ); 

	    if( isPooled() )
		con.close();

	    log.debug( "Initialize sample inventory database done" );
	}
    }

    private boolean connectionExpired() {
	return ((System.currentTimeMillis() - lastActivity) > IDLE_PERIOD);
    }

    private boolean testFailed( Connection con ) throws SQLException {
	boolean failed = false;
	if( connectionExpired() ) {
	    log.debug( "Testing connection" );
	    failed = true;
	    Statement stmt = con.createStatement();
	    ResultSet res = stmt.executeQuery( getTest() );
	    if( res.next() )
		failed = false;
	    res.close();
	    stmt.close();
	}
	return failed;
    }	

    private Connection reuseConnect( boolean forceConnect ) throws SQLException {
	boolean invalidate = false;
	Connection con = null;
	if( lastConnection != null ) {
	    try {
		if( lastConnection.isClosed() || testFailed( lastConnection ) )
		    invalidate = true;
	    }
	    catch( SQLException sqe ) {
		log.warn( sqe );
		invalidate = true;
	    }
	    if( invalidate ) {
		log.debug( "Re-establishing db connection." );
		con = connect( forceConnect );
	    }
	    else {
		// log.debug( "Re-using db connection." );
		con = lastConnection;
	    }
	}
	else {
	    log.debug( "Creating db connection." );
	    con = connect( forceConnect );
	}
	lastConnection = con;
	return con;
    }

    /**
     * Creates the corresponding database structures.
     *
     * @param entity the entity name to create.
     */
    public void createEntity( String entity ) throws SQLException {
	Connection con = connect( false );
	createTable( con, entity );
	if( isPooled() )
	    con.close();
    }

    /**
     * Returns a preapered statement with the given name.
     *
     * @param stmtName the name of the statement.
     * @return a prepared statement.
     */
    public PreparedStatement getStatement( String stmtName ) throws SQLException {
	// initialize the database if not done yet

	initTemplate();

	PreparedStatement pstmt = null;


	boolean forceConnect = false;
	boolean invalidate = false;

	if( isPooled() ) {
	    Connection con = reuseConnect( forceConnect );
	    // if( lastConnection != null ) {
	    // 	try {
	    // 	    if( lastConnection.isClosed() || testFailed( lastConnection ) )
	    // 		invalidate = true;
	    // 	}
	    // 	catch( SQLException sqe ) {
	    // 	    log.warn( sqe );
	    // 	    invalidate = true;
	    // 	}
	    // 	if( invalidate ) {
	    // 	    log.debug( "Re-establishing db connection." );
	    // 	    con = connect( forceConnect );
	    // 	}
	    // 	else {
	    // 	    log.debug( "Re-using db connection." );
	    // 	    con = lastConnection;
	    // 	}
	    // }
	    // else {
	    // 	log.debug( "Creating db connection." );
	    // 	con = connect( forceConnect );
	    // }
	    String querySt = templateConfig.getString( stmtName );
	    if( querySt == null )
		throw new SQLException( "Cannot find named statement: "+stmtName );
	    pstmt = con.prepareStatement( querySt );
	    // lastConnection = con;
	//     statements.put( stmtName, pstmt );
	//     // con.close();
	    lastActivity = System.currentTimeMillis();
	    return pstmt;
	}

	pstmt = statements.get( stmtName );

	// test prepared statement if it works properly

	if( pstmt != null ) {
	    try {
		Connection con = pstmt.getConnection();
		if( (con == null) || (con.isClosed()) || (testFailed(con)) ) 
		    invalidate = true;
	    }
	    catch( SQLException sqe ) {
		log.warn( sqe );
		invalidate = true;
	    }
	    if( invalidate ) {
		log.warn( "Invalidate statement \""+stmtName+"\"" );
		statements.remove( stmtName );
		pstmt = null;
		forceConnect = true;
	    }
	}

	// create it as required

	if( pstmt == null ) {
	    log.warn( "Cannot find statement \""+stmtName+"\". Need to create it" );
	    String querySt = templateConfig.getString( stmtName );
	    if( querySt == null )
		throw new SQLException( "Cannot find named statement: "+stmtName );
	    log.debug( "Preparing named statement \""+stmtName+"\": "+querySt );
	    Connection con = reuseConnect( forceConnect );
	    // if( lastConnection != null ) {
	    // 	try {
	    // 	    if( lastConnection.isClosed() || testFailed( lastConnection ) )
	    // 		invalidate = true;
	    // 	}
	    // 	catch( SQLException sqe ) {
	    // 	    log.warn( sqe );
	    // 	    invalidate = true;
	    // 	}
	    // 	if( invalidate ) {
	    // 	    log.debug( "Re-establishing db connection." );
	    // 	    con = connect( forceConnect );
	    // 	}
	    // 	else {
	    // 	    log.debug( "Re-using db connection." );
	    // 	    con = lastConnection;
	    // 	}
	    // }
	    // else {
	    // 	log.debug( "Creating db connection." );
	    // 	con = connect( forceConnect );
	    // }
	    pstmt = con.prepareStatement( templateConfig.getString( stmtName ) );
	    statements.put( stmtName, pstmt );
	    lastConnection = con;
	    log.debug( "Named statement \""+stmtName+"\" registered" );
	}

	lastActivity = System.currentTimeMillis();

	return pstmt;
    }

    /**
     * Runs a direct query and returns a list of matching objects of the same class 
     * than the given prototype.
     *
     * @param stmt the (query) statement.
     * @param proto the object prototype.
     * @return an array of matching objects.
     */
    public Object[] runStatement( String stmt, Object proto ) throws SQLException {
	Connection con = reuseConnect( false );
	List objList = TableUtils.query( con, stmt, proto );
	Object[] result = objList.toArray();
	if( isPooled() ) {
	    try {
		con.close();
	    }
	    catch( SQLException sqe ) {
		log.warn( sqe );
	    }
	}
	return result;
    }

    /**
     * Returns the statement to the pool if configured.
     *
     * @param pstmt the prepared statement to be returned.
     */
    public void popStatement( PreparedStatement pstmt ) {
	if( pstmt == null )
	    return;
	if( isPooled() ) {
	    try {
		pstmt.close();
	    }
	    catch( SQLException sqe ) {
		log.warn( sqe );
	    }
	    int nActive = ((BasicDataSource)dataSource).getNumActive();
	    int nIdle = ((BasicDataSource)dataSource).getNumIdle();
	    popCount++;
	    if( (popCount % 5000) == 0 ) {
		log.debug( "Active connections: "+nActive+" idle connections: "+nIdle );
		popCount = 0;
	    }
	}
    }

    private void insertTrack( Trackable track, 
			      long prevId,
			      long userId, 
			      String activity,
			      String remark ) 
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_TRACK_INSERT );
	pstmt.setLong( 1, track.getTrackid() );
	pstmt.setTimestamp( 2, new Timestamp(System.currentTimeMillis()) );
	pstmt.setLong( 3, prevId );
	pstmt.setString( 4,  track.getItem() );
	pstmt.setString( 5, activity );
	pstmt.setLong( 6, userId );
	pstmt.setString( 7, Stringx.getDefault( remark, "" ) );
	pstmt.setString( 8, StringEscapeUtils.escapeSql(track.toContent()) );
	pstmt.executeUpdate();
	popStatement( pstmt );
    }

    /**
     * Records changes of entities to an audit trail.
     *
     * @param before the trackable item status before change.
     * @param changed the trackable item changed.
     * @param userId the user conducting the change.
     * @param activity the activity which caused the change.
     * @param remark a justification of the change.
     */
    public void trackChange( Trackable before,
			     Trackable changed,
			     long userId,
			     String activity,
			     String remark )
	throws SQLException {

	PreparedStatement pstmt = null;
	long prevId = -1L;
	if( before != null ) {
	    pstmt = getStatement( STMT_TRACK_BY_ID );
	    pstmt.setLong( 1, before.getTrackid() );
	    ResultSet res = pstmt.executeQuery();
	    if( res.next() ) 
		prevId = before.getTrackid();
	    res.close();
	    popStatement( pstmt );
	    if( prevId == -1L ) {
		String msg = "Previous track information could not be found";
		log.warn( msg );
		insertTrack( before, -1L, userId, activity, msg );
	    }
	    
	}
	
	log.debug( "before track id: "+prevId+" (no before: "+(before==null)+") changed track id: "+changed.getTrackid() );
	if( changed != null ) {
	    if( changed.getTrackid() == prevId )
		return;
	    insertTrack( changed, prevId, userId, activity, remark );
	}
	else if( prevId != -1L ) {
	    pstmt = getStatement( STMT_TRACK_DELETE );
	    pstmt.setTimestamp( 1, new Timestamp(System.currentTimeMillis()) );
	    pstmt.setString( 2, activity );
	    pstmt.setLong( 3, userId );
	    pstmt.setString( 4, Stringx.getDefault( remark, "" ) );
	    pstmt.setLong( 5, prevId );
	    pstmt.executeUpdate();
	    popStatement( pstmt );
	}
    }

    /**
     * Returns the string content identified by md5sum.
     * The raw format is zipped binary data which is represented as hex number characters.
     *
     * @return the content read from the database.
     */
    public DocumentContent findDocumentContent( String md5 )
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_CONTENT_BY_MD5 );
	pstmt.setString( 1, md5 );
	ResultSet res = pstmt.executeQuery();
	DocumentContent cont = null;
	if( res.next() ) 
	    cont = (DocumentContent)TableUtils.toObject( res, new DocumentContent() );
	res.close();
	popStatement( pstmt );
	return cont;
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

	DocumentContent cont = null;
	try {
	    PreparedStatement pstmt = getStatement( STMT_CONTENT_BY_MD5 );
	    pstmt.setString( 1, md5sum );
	    ResultSet res = pstmt.executeQuery();
	    cont = null;
	    if( res.next() ) 
		cont = (DocumentContent)TableUtils.toObject( res, new DocumentContent() );
	    res.close();
	    popStatement( pstmt );
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new IOException( sqe );
	}

	if( cont == null ) {
	    log.warn( "Content not available: "+md5sum );
	    return false;
	}
	    
	byte[] buf = null;
	try {
	    buf = DataHasher.decode( Stringx.getDefault(cont.getUpload(),"") );
	}
	catch( DataFormatException de ) {
	    log.error( de );
	    throw new IOException( de );
	}
	outs.write( buf );
	outs.flush();
	buf = null;
	return true;
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
     * Clean up resources occupied by this DAO.
     *
     */
    public synchronized void close() {
	Iterator<PreparedStatement> it = statements.values().iterator();
	while( it.hasNext() ) {
	    PreparedStatement pstmt = it.next();
	    try {
		pstmt.close();
	    }
	    catch( SQLException sqe ) {
		// we don't care...
	    }
	}
	statements.clear();
	if( dbSource != null ) {
	    try {
		dbSource.close();
	    }
	    catch( SQLException sqe ) {
		// we don't care...
	    }
	    dbSource = null;
	}
	templateConfig = null;
	if( dataSource != null ) {
	    log.debug( "Clearing connection pool" );
     	    if( dataSource instanceof BasicDataSource ) {
		try {
		    ((BasicDataSource)dataSource).close();
		}
		catch( SQLException sqe ) {
		    // we don't care...
		    log.warn( sqe );
		}
	    }
	}
	log.debug( "Data access object cleaned" );
    }

    // public synchronized void close() {
    // 	if( dataSource != null ) {
    // 	    if( dataSource instanceof BasicDataSource )
    // 		((BasicDataSource)dataSource).close();
    // 	}
    // }

}
