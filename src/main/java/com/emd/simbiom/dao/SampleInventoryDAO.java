package com.emd.simbiom.dao;

/**
 * <code>SampleInventoryDAO</code> the database access the sample inventory.
 *
 * Created: Mon Jan 12 14:27:17 2015
 *
 * @author <a href="mailto:">Oliver</a>
 * @version 1.0
 */
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

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import org.apache.commons.dbutils.DbUtils;

import org.apache.commons.lang.StringEscapeUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Accession;
import com.emd.simbiom.model.Age;
import com.emd.simbiom.model.Cost;
import com.emd.simbiom.model.CostEstimate;
import com.emd.simbiom.model.CostItem;
import com.emd.simbiom.model.CostSample;
import com.emd.simbiom.model.Donor;
import com.emd.simbiom.model.Organization;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleEvent;
import com.emd.simbiom.model.SampleProcess;
import com.emd.simbiom.model.SampleSummary;
import com.emd.simbiom.model.SampleType;
import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.StudySample;
import com.emd.simbiom.model.Subject;
import com.emd.simbiom.model.Trackable;
import com.emd.simbiom.model.Treatment;
import com.emd.simbiom.model.User;

import com.emd.simbiom.upload.InventoryUploadTemplate;
import com.emd.simbiom.upload.UploadBatch;
import com.emd.simbiom.upload.UploadLog;

import com.emd.util.Stringx;

public class SampleInventoryDAO {
    private String       url;
    private String       username;
    private String       password;
    private String       driver;
    private String       template;
    private String       schema;
    private String       test;
    private int          retryCount;
    private boolean      initDatabaseFromTemplate;
    private long         lastActivity;

    private Connection   dbSource;
    
    private FullContentSearch contentSearch;

    private PropertiesConfiguration templateConfig;
    private Map<String,PreparedStatement>  statements;

    private static Log log = LogFactory.getLog(SampleInventoryDAO.class);
    private static SampleInventoryDAO sampleInventoryDAO;

    private static final int DEFAULT_RETRY       = 10;

    private static final String COST_PER_MONTH   = "month";

    private static final String DEFAULT_DRIVER   = "com.mysql.jdbc.Driver";
    private static final String DEFAULT_URL      = "jdbc:mysql://localhost/biobank";
    private static final String DEFAULT_USERNAME = "biobank";
    private static final String DEFAULT_PASSWORD = "biobank";
    private static final String DEFAULT_TEMPLATE = "biobank-mysql.properties";
    private static final String DEFAULT_SCHEMA   = "biobank";
    private static final String DEFAULT_TEST     = "select 1 from dual";

    private static final String DEFAULT_CONT_SERVER = "localhost:7272";

    public static final String SAMPLE_ADMIN             = "sample admin";
    public static final String SAMPLE_REGISTRATION      = "registration";
    public static final String SAMPLE_STORAGE           = "sample storage";

    private static final String STMT_ACC_BY_ACC          = "biobank.accession.findAccession";
    private static final String STMT_ACC_BY_ALL          = "biobank.accession.findAccessionAll";
    private static final String STMT_ACC_BY_SAMPLE       = "biobank.accession.findSampleAccession";
    private static final String STMT_ACC_INSERT          = "biobank.accession.insert";

    private static final String STMT_COST_BY_ID          = "biobank.cost.findById";

    private static final String STMT_COSTITEM_INSERT     = "biobank.item.insert";
    private static final String STMT_COSTITEM_DELETE     = "biobank.item.delete";
    private static final String STMT_COSTITEM_EXPIRED    = "biobank.item.expired";

    private static final String STMT_COSTSAMPLE_BY_TYPE  = "biobank.costsample.findByType";
    private static final String STMT_COSTSAMPLE_ALL_TYPE = "biobank.costsample.findAllTypes";

    private static final String STMT_DONOR_BY_SAMPLE     = "biobank.donor.findBySample";
    private static final String STMT_DONOR_DUPLICATE     = "biobank.donor.duplicate";
    private static final String STMT_DONOR_INSERT        = "biobank.donor.insert";

    private static final String STMT_ESTIMATE_BY_ID      = "biobank.estimate.findById";
    private static final String STMT_ESTIMATE_INSERT     = "biobank.estimate.insert";
    private static final String STMT_ESTIMATE_UPDATE     = "biobank.estimate.update";
    private static final String STMT_ESTIMATE_EXPIRED    = "biobank.estimate.expired";

    private static final String STMT_EVENT_BY_ORG        = "biobank.event.findBySite";
    private static final String STMT_EVENT_BY_ID         = "biobank.event.findById";
    private static final String STMT_EVENT_INSERT        = "biobank.event.insert";

    private static final String STMT_ORG_BY_ID           = "biobank.organization.findById";
    private static final String STMT_ORG_BY_NAME         = "biobank.organization.findByName";
    private static final String STMT_ORG_COLLECT         = "biobank.organization.findCollectionSite";
    private static final String STMT_ORG_INSERT          = "biobank.organization.insert";
    private static final String STMT_ORG_UPDATE          = "biobank.organization.update";

    private static final String STMT_PROCESS_BY_EVENT    = "biobank.process.findByEvent";
    private static final String STMT_PROCESS_BY_VISIT    = "biobank.process.findByVisit";
    private static final String STMT_PROCESS_INSERT      = "biobank.process.insert";
    private static final String STMT_PROCESS_UPDATE      = "biobank.process.update";

    private static final String STMT_RAW_FIND_OLD        = "biobank.uploadraw.findArchiveLoads";
    private static final String STMT_RAW_INSERT          = "biobank.uploadraw.insert";

    private static final String STMT_SAMPLE_INSERT       = "biobank.sample.insert";
    private static final String STMT_SAMPLE_BY_ID        = "biobank.sample.findById";
    private static final String STMT_SAMPLE_BY_STUDY     = "biobank.sample.findByStudy";
    private static final String STMT_SAMPLE_BY_TYPE      = "biobank.sample.findByType";
    private static final String STMT_SAMPLE_LAST_CREATED = "biobank.sample.findByLastCreated";

    private static final String STMT_STYPE_BY_ID         = "biobank.sampleType.findById";
    private static final String STMT_STYPE_BY_NAME       = "biobank.sampleType.findByName";
    private static final String STMT_STYPE_LIKE_NAME     = "biobank.sampleType.findLikeName";
    private static final String STMT_STYPE_CREATE        = "biobank.sampleType.insert";
    private static final String STMT_STYPE_TERMS         = "biobank.sampleType.findTerms";

    private static final String STMT_STUDY_BY_ID         = "biobank.study.findById";
    private static final String STMT_STUDY_BY_NAME       = "biobank.study.findByName";
    private static final String STMT_STUDY_TERMS         = "biobank.study.findTerms";
    private static final String STMT_STUDY_INSERT        = "biobank.study.insert";

    private static final String STMT_STSAMP_DUPLICATE    = "biobank.studysample.duplicate";
    private static final String STMT_STSAMP_INSERT       = "biobank.studysample.insert";

    private static final String STMT_SUBJECT_BY_NAME     = "biobank.subject.findByName";
    private static final String STMT_SUBJECT_BY_SAMPLE   = "biobank.subject.findBySample";
    private static final String STMT_SUBJECT_INSERT      = "biobank.subject.insert";

    private static final String STMT_TEMPLATE_BY_ID      = "biobank.template.findById";
    private static final String STMT_TEMPLATE_BY_NAME    = "biobank.template.findByName";
    private static final String STMT_TEMPLATE_INSERT     = "biobank.template.insert";
    private static final String STMT_TEMPLATE_UPDATE     = "biobank.template.update";
    private static final String STMT_TEMPLATE_DELETE     = "biobank.template.delete";

    private static final String STMT_TRACK_BY_ID         = "biobank.track.findById";
    private static final String STMT_TRACK_DELETE        = "biobank.track.delete";
    private static final String STMT_TRACK_INSERT        = "biobank.track.insert";

    private static final String STMT_TREAT_BY_NAME       = "biobank.treatment.findByName";
    private static final String STMT_TREAT_INSERT        = "biobank.treatment.insert";

    private static final String STMT_UPLOAD_INSERT       = "biobank.upload.insert";
    private static final String STMT_UPLOAD_DELETE       = "biobank.upload.deleteAll";
    private static final String STMT_UPLOAD_LOG          = "biobank.upload.findLatestLogs";
    private static final String STMT_UPLOAD_MOVE         = "biobank.upload.move";

    private static final String STMT_USER_BY_APIKEY      = "biobank.user.findByApikey";
    private static final String STMT_USER_BY_ID          = "biobank.user.findById";

    private static final String STMT_LOG_INSERT          = "biobank.log.insert";
    private static final String STMT_LOG_DELETE          = "biobank.log.deleteAll";
    private static final String STMT_LOG_FIND_BY_UPLOAD  = "biobank.log.findByUpload";

    private static final long   COST_EXPIRE              = 3L * 24L * 60L * 60L * 1000L; // 3 days

    private static final String DEFAULT_RESOURCE = "biobank-mysql.properties";

    private static final long   DEFAULT_REFRESH  = 7L * 60L * 1000L; // 7 minutes

    private static final long   ONE_DAY          = 24L * 60L * 60L * 1000L; // 1 day

    private static final long   IDLE_PERIOD             =  10L * 60L * 1000L; // 10 minutes

    private static final String[] GROUPINGS = {
	"Diseases",
	"Locations",
	"Molecules",
	"Sample types",
	"Studies"
    };

    // private static final long   POLICY_INTERVAL  = 10L * 60L * 60L * 1000L; // every 10 minutes

    public SampleInventoryDAO() {
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
    public static SampleInventoryDAO getInstance() {
	if( sampleInventoryDAO == null ) {
	    sampleInventoryDAO = new SampleInventoryDAO();
	    sampleInventoryDAO.setTemplate( DEFAULT_RESOURCE );
	    sampleInventoryDAO.setInitDatabaseFromTemplate( true );
	    log.debug( "New sample inventory instance created" );
	}
	return sampleInventoryDAO;
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

    private Connection connect( boolean forceConnect ) throws SQLException {
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

	return dbSource;
    }

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

		FullContentSearch fcs = FullContentSearch.getInstance( templateConfig.getString( "db.content.server", DEFAULT_CONT_SERVER ) );
		setContentSearch( fcs );
	    }

	    Connection con = connect( false );

	    createTable( con, "accession" ); 
	    createTable( con, "donor" ); 
	    createTable( con, "event" ); 
	    createTable( con, "organization" ); 
	    createTable( con, "sample" ); 
	    createTable( con, "sampleType" ); 
	    createTable( con, "study" ); 
	    createTable( con, "studysample" ); 
	    createTable( con, "subject" ); 
	    createTable( con, "treatment" ); 
	    createTable( con, "track" ); 
	    createTable( con, "user" ); 

	    createTable( con, "template" ); 
	    createTable( con, "upload" ); 
	    createTable( con, "uploadraw" ); 
	    createTable( con, "log" ); 

	    createTable( con, "cost" ); 
	    createTable( con, "costsample" ); 
	    createTable( con, "item" ); 
	    createTable( con, "estimate" ); 

// 	    con.close();
	    log.debug( "Initialize sample inventory database done" );
	}
    }

    private boolean connectionExpired() {
	return ((System.currentTimeMillis() - lastActivity) > IDLE_PERIOD);
    }

    private boolean testFailed( Connection con ) throws SQLException {
	boolean failed = false;
	if( connectionExpired() ) {
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

    private PreparedStatement getStatement( String stmtName ) throws SQLException {
	// initialize the database if not done yet

	initTemplate();

	PreparedStatement pstmt = statements.get( stmtName );

	// test prepared statement if it works properly

	boolean forceConnect = false;
	if( pstmt != null ) {
	    boolean invalidate = false;
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
	    Connection con = connect( forceConnect );
	    pstmt = con.prepareStatement( templateConfig.getString( stmtName ) );
	    statements.put( stmtName, pstmt );
	    log.debug( "Named statement \""+stmtName+"\" registered" );
	}

	lastActivity = System.currentTimeMillis();

	return pstmt;
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public SampleType[] findSampleTypeByNameAll( String sampleType ) throws SQLException {
 	PreparedStatement pstmt = null;
	String st = Stringx.getDefault(sampleType,"").trim().toLowerCase();
	if( (st.length() <= 0) || (st.indexOf( "%" ) >= 0) ) {
	    if( st.length() <= 0 )
		st = "%";
	    pstmt = getStatement( STMT_STYPE_LIKE_NAME );
	}
	else
	    pstmt = getStatement( STMT_STYPE_BY_NAME );
     	pstmt.setString( 1, st );

     	ResultSet res = pstmt.executeQuery();

     	List<SampleType> fl = new ArrayList<SampleType>();
     	Iterator it = TableUtils.toObjects( res, new SampleType() );
	while( it.hasNext() ) {
	    fl.add( (SampleType)it.next() );
	}	       
	res.close();

     	SampleType[] facs = new SampleType[ fl.size() ];
     	return (SampleType[])fl.toArray( facs );
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public SampleType findSampleTypeByName( String sampleType ) throws SQLException {
	SampleType[] sTypes = findSampleTypeByNameAll( sampleType );
	return ((sTypes.length ==1)?sTypes[0]:null);
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public SampleType findSampleTypeById( long typeId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STYPE_BY_ID );
     	pstmt.setLong( 1, typeId );
     	ResultSet res = pstmt.executeQuery();
     	SampleType sType = null;
     	if( res.next() ) 
     	    sType = (SampleType)TableUtils.toObject( res, new SampleType() );
     	res.close();
     	return sType;
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public String[] findSampleTypeTerms() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STYPE_TERMS );

     	ResultSet res = pstmt.executeQuery();

     	List<String> fl = new ArrayList<String>();
     	Iterator it = TableUtils.toObjects( res, new SampleType() );
	while( it.hasNext() ) {
	    SampleType sType = (SampleType)it.next();
	    fl.add( sType.getTypename() );
	}	       
	res.close();

	String[] terms = new String[fl.size()];
     	return (String[])fl.toArray( terms );
    }

    /**
     * Creates a sample type (if not existing).
     *
     * @param sampleType the name of the sample type
     *
     * @return the sample type.
     */
    public SampleType createSampleType( String sampleType ) throws SQLException {
	if( (sampleType == null) || (sampleType.trim().length() <= 0) )
	    throw new SQLException( "Invalid sample type" );
	SampleType sType = findSampleTypeByName( sampleType );
	PreparedStatement pstmt = null;
	if( sType == null ) {
	    sType = SampleType.getInstance( sampleType );
	    
	    pstmt = getStatement( STMT_STYPE_CREATE );
	    pstmt.setLong( 1, sType.getTypeid() );
	    pstmt.setString( 2, sType.getTypename() );
	    pstmt.setTimestamp( 3, sType.getCreated() );
	    pstmt.executeUpdate();

	    log.debug( "New sample type created: "+sType+" (id: "+sType.getTypeid()+")" );
	}
	return sType;
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
    }

    private void trackChange( Trackable before,
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
	    if( prevId == -1L ) {
		String msg = "Previous track information could not be found";
		log.warn( msg );
		insertTrack( before, -1L, userId, activity, msg );
	    }
	    
	}
	if( changed != null ) {
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
	}
    }

    /**
     * Create a sample.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public Sample createSample( long userId, String sampleType ) throws SQLException {
	SampleType sType = createSampleType( sampleType );
	Sample samp = Sample.getInstance( sType );

	PreparedStatement pstmt = getStatement( STMT_SAMPLE_INSERT );
	pstmt.setString( 1, samp.getSampleid() );
	pstmt.setString( 2, samp.getSamplename() );
	pstmt.setLong( 3, samp.getTypeid() );
	pstmt.setLong( 4, samp.getStamp() );
	pstmt.setLong( 5, samp.getTrackid() );
	pstmt.setTimestamp( 6, samp.getCreated() );
	pstmt.executeUpdate();

	log.debug( "Sample created: "+samp.getSamplename()+" ("+
		   samp.getSampleid()+") trackid: "+samp.getTrackid() );
	
	trackChange( null, samp, userId, "Sample created", null );
 
	return samp;
    }

    /**
     * Returns a sample by id.
     *
     * @param sampleId The sample id.
     * @return the Sample object or null (if not existing).
     */
    public Sample findSampleById( String sampleId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_BY_ID );
     	pstmt.setString( 1, sampleId );
     	ResultSet res = pstmt.executeQuery();
     	Sample sType = null;
     	if( res.next() ) 
     	    sType = (Sample)TableUtils.toObject( res, new Sample() );
     	res.close();
     	return sType;
    }

    /**
     * Returns a list of samples recently created.
     *
     * @param age The age of the samples (created or collected, older or newer).
     * @return A list of samples.
     */
    public Sample[] findSampleByAge( Age age ) throws SQLException {
 	PreparedStatement pstmt = getStatement( age.getStatementName() );
	pstmt.setTimestamp( 1, age.getTimestamp() );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of samples recently created.
     *
     * @return A list of samples.
     */
    public Sample[] findSampleLastCreated() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_LAST_CREATED );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of samples recently created.
     *
     * @param age The age of the samples (created or collected, older or newer).
     * @return A list of samples.
     */
    public Sample[] findSampleByStudy( String study ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_BY_STUDY );
	pstmt.setString( 1, study );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of samples by sample type.
     *
     * @param type The sample type.
     * @return A list of samples.
     */
    public Sample[] findSampleByType( String type ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SAMPLE_BY_TYPE );
	pstmt.setString( 1, type );

     	ResultSet res = pstmt.executeQuery();
     	List<Sample> fl = new ArrayList<Sample>();
     	Iterator it = TableUtils.toObjects( res, new Sample() );
	while( it.hasNext() ) {
	    fl.add( (Sample)it.next() );
	}	       
	res.close();

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of templates and associated upload batches.
     *
     * @return the SampleType object.
     */
    public InventoryUploadTemplate[] findTemplateByName( String templateName ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_TEMPLATE_BY_NAME );
	StringBuilder stb = new StringBuilder( "%" );
	if( (templateName != null) && (templateName.length() > 0) ) {
	    stb.append( templateName.toLowerCase() );
	    stb.append( "%" );
	}
	pstmt.setString( 1, stb.toString() );

     	ResultSet res = pstmt.executeQuery();
	
     	List<InventoryUploadTemplate> fl = new ArrayList<InventoryUploadTemplate>();
     	InventoryUploadTemplate lastTemplate = null;
	while( res.next() ) {
     	    InventoryUploadTemplate templ = (InventoryUploadTemplate)TableUtils.toObject( res, new InventoryUploadTemplate() );
     	    if( (lastTemplate == null) || (templ.getTemplateid() != lastTemplate.getTemplateid()) ) {
     		fl.add( templ );
     		lastTemplate = templ;
     	    }
     	    UploadBatch uBatch = (UploadBatch)TableUtils.toObject( res, new UploadBatch() );
	    if( uBatch.isValid() )
		lastTemplate.addUploadBatch( uBatch );
     	}
	res.close();
     	InventoryUploadTemplate[] facs = new InventoryUploadTemplate[ fl.size() ];
     	return (InventoryUploadTemplate[])fl.toArray( facs );
    }

    /**
     * Returns a list of templates and associated upload batches.
     *
     * @return the SampleType object.
     */
    public InventoryUploadTemplate findTemplateById( long templateId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_TEMPLATE_BY_ID );
	pstmt.setLong( 1, templateId );

     	ResultSet res = pstmt.executeQuery();
	
     	List<InventoryUploadTemplate> fl = new ArrayList<InventoryUploadTemplate>();
     	InventoryUploadTemplate lastTemplate = null;
	while( res.next() ) {
     	    InventoryUploadTemplate templ = (InventoryUploadTemplate)TableUtils.toObject( res, new InventoryUploadTemplate() );
     	    if( (lastTemplate == null) || (templ.getTemplateid() != lastTemplate.getTemplateid()) ) {
     		fl.add( templ );
     		lastTemplate = templ;
     	    }
     	    UploadBatch uBatch = (UploadBatch)TableUtils.toObject( res, new UploadBatch() );
	    if( uBatch.isValid() )
		lastTemplate.addUploadBatch( uBatch );
     	}
	res.close();
	if( fl.size() > 0 )
	    return fl.get( 0 );
	return null;
    }

    /**
     * Returns a list of upload batches ordered by logstamp descending.
     * Limits list to 100 entries.
     * 
     * @return the list of uploads.
     */
    public UploadBatch[] findLatestLogs() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_UPLOAD_LOG );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadBatch> fl = new ArrayList<UploadBatch>();
     	Iterator it = TableUtils.toObjects( res, new UploadBatch() );
	while( it.hasNext() ) {
	    fl.add( (UploadBatch)it.next() );
	}	       
	res.close();

     	UploadBatch[] facs = new UploadBatch[ fl.size() ];
     	return (UploadBatch[])fl.toArray( facs );
    }

    /**
     * Returns a list of log messages related to the given upload batch.
     * 
     * @param upBatch the upload batch.
     * @param levels comma separated list of log levels to be included.
     * @return the list of log messages.
     */
    public UploadLog[] findLogByUpload( UploadBatch upBatch, String levels ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_LOG_FIND_BY_UPLOAD );
	pstmt.setLong( 1, upBatch.getUploadid() );

     	ResultSet res = pstmt.executeQuery();

     	List<UploadLog> fl = new ArrayList<UploadLog>();
     	Iterator it = TableUtils.toObjects( res, new UploadLog() );
	while( it.hasNext() ) {
	    UploadLog log = (UploadLog)it.next();
	    if( (levels == null) || (levels.trim().length() <= 0) || (levels.indexOf(log.getLevel()) >= 0) )
		fl.add( log );
	}	       
	res.close();

     	UploadLog[] facs = new UploadLog[ fl.size() ];
     	return (UploadLog[])fl.toArray( facs );
   }

    private void archiveUploads( long templateId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_RAW_FIND_OLD );
	pstmt.setLong( 1, templateId );
	Timestamp older = new Timestamp( System.currentTimeMillis() - ONE_DAY );
	log.debug( "Find all upload content older than "+older );
	pstmt.setTimestamp( 2, older );

	PreparedStatement insRaw = getStatement( STMT_RAW_INSERT );

     	ResultSet res = pstmt.executeQuery();
	Set<String> md5s = new HashSet<String>();
	List<Long> updLoads = new ArrayList<Long>();
	while( res.next() ) {
	    String md5sum = res.getString(1);
	    updLoads.add( new Long( res.getLong(2) ) );
	    if( (md5sum == null) || md5s.contains( md5sum ) )
		continue;
	    String cont = Stringx.getDefault(res.getString(3),"");
	    insRaw.setString( 1, md5sum );
	    insRaw.setString( 2, cont );
	    insRaw.executeUpdate();
	    md5s.add( md5sum );
	    log.debug( "Content "+md5sum+" archived" );
	}
	res.close();

	log.debug( "Move content of "+updLoads.size()+" upload batches" );
	pstmt = getStatement( STMT_UPLOAD_MOVE );
	for( Long updId : updLoads ) {
	    pstmt.setLong( 1, updId.longValue() );
	    pstmt.executeUpdate();
	}
    }

    /**
     * Stores the given upload template.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public InventoryUploadTemplate storeTemplate( InventoryUploadTemplate template ) throws SQLException {
	InventoryUploadTemplate templ = findTemplateById( template.getTemplateid() );

     	PreparedStatement pstmt = null;
     	int nn = 2;
     	if( templ == null ) {
     	    pstmt = getStatement( STMT_TEMPLATE_INSERT );
     	    pstmt.setLong( 1, template.getTemplateid() );
     	    log.debug( "Creating a new template entry: "+template.getTemplateid()+" called \""+template.toString()+"\"" );
     	}
     	else {
     	    pstmt = getStatement( STMT_TEMPLATE_UPDATE );
     	    pstmt.setLong( 3, template.getTemplateid() );
     	    nn--;
     	    log.debug( "Updating existing template entry: "+template.getTemplateid()+" called \""+template.toString()+"\"" );
     	}
	
     	pstmt.setString( nn, template.getTemplatename() );
     	nn++;

     	pstmt.setString( nn, template.getTemplate() );
     	nn++;

     	pstmt.executeUpdate();

	UploadBatch[] nBatches = template.getUploadBatches();
	List<UploadBatch> nb = new ArrayList<UploadBatch>();
	if( templ != null ) {
	    UploadBatch[] oBatches = templ.getUploadBatches();
	    for( int i = 0; i < nBatches.length; i++ ) {
		boolean foundIt = false;
		for( int j = 0; j < oBatches.length; j++ ) {
		    if( oBatches[j].equals( nBatches[i] ) ) {
			foundIt = true;
			break;
		    }
		}
		if( !foundIt )
		    nb.add( nBatches[i] );
	    }
	}
	else if( nBatches.length > 0 ) {
	    nb.addAll( Arrays.asList( nBatches ) );
	}

	pstmt = getStatement( STMT_UPLOAD_INSERT );
	for( UploadBatch upd : nb ) {
	    pstmt.setLong( 1, upd.getUploadid() );
	    pstmt.setLong( 2, upd.getTemplateid() );
	    pstmt.setTimestamp( 3, upd.getUploaded() ); 
	    pstmt.setLong( 4, upd.getUserid() ); 
	    pstmt.setString( 5, upd.getMd5sum() ); 
	    pstmt.setString( 6, upd.getUpload() ); 
	    pstmt.executeUpdate();
	}

	archiveUploads( template.getTemplateid() );

	if( templ != null )
	    template = findTemplateById( template.getTemplateid() );
     	return template;
    }

    /**
     * Deletes the given upload template and accompanying uploads and logs.
     *
     * @param template the template to delete.
     * @return false if template cannot be deleted.
     */
    public boolean deleteTemplate( InventoryUploadTemplate template ) throws SQLException {
	InventoryUploadTemplate templ = findTemplateById( template.getTemplateid() );
	if( templ == null ) {
	    log.warn( "Cannot delete non-existing template id "+template.getTemplateid() );
	    return false;
	}

	// Remove log entries

     	PreparedStatement pstmt = getStatement( STMT_LOG_DELETE );
	pstmt.setLong( 1, template.getTemplateid() );
     	pstmt.executeUpdate();

	// Remove upload batches

     	pstmt = getStatement( STMT_UPLOAD_DELETE );
	pstmt.setLong( 1, templ.getTemplateid() );
     	pstmt.executeUpdate();

	// Remove the template itself

     	pstmt = getStatement( STMT_TEMPLATE_DELETE );
	pstmt.setLong( 1, templ.getTemplateid() );
     	pstmt.executeUpdate();

	log.debug( "Template \""+Stringx.getDefault(templ.getTemplatename(),String.valueOf(templ.getTemplateid()))+"\" has been removed" );

	return true;
    }

    /**
     * Updates a given upload template.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public InventoryUploadTemplate updateTemplateByName( InventoryUploadTemplate template ) throws SQLException {
	InventoryUploadTemplate[] templs = findTemplateByName( template.getTemplatename() );
	if( (templs.length <= 0) || (templs.length > 1) )
	    throw new SQLException( "Cannot find "+template.getTemplatename()+" or ambiguous" );

	templs[0].setTemplate( template.getTemplate() );
	return storeTemplate( templs[0] );
    }

    /**
     * Appends a log message to the upload log.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public UploadLog addUploadMessage( UploadBatch upload, String level, long line, String msg ) 
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_LOG_INSERT );

	UploadLog updLog = new UploadLog();
	updLog.setUploadid( upload.getUploadid() );
	updLog.setLogstamp( new Timestamp(System.currentTimeMillis()) );
	updLog.setLevel( level );
	updLog.setLine( line );
	updLog.setMessage( Stringx.getDefault(msg,"").trim() );

	pstmt.setLong( 1, updLog.getLogid() );
	pstmt.setLong( 2, updLog.getUploadid() );
	pstmt.setTimestamp( 3, updLog.getLogstamp() );
	pstmt.setString( 4, updLog.getLevel() );
	pstmt.setLong( 5, updLog.getLine() );
	pstmt.setString( 6, updLog.getMessage() );
	pstmt.executeUpdate();

	return updLog;
    }

    /**
     * Appends a log message to the upload log.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public UploadLog addUploadMessage( UploadBatch upload, String level, String msg ) 
	throws SQLException {

	return addUploadMessage( upload, level, 0L, msg );
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
     	return user;
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public Study findStudyByName( String studyName ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STUDY_BY_NAME );
     	pstmt.setString( 1, Stringx.getDefault(studyName,"").trim() );
     	ResultSet res = pstmt.executeQuery();
     	Study sType = null;
     	if( res.next() ) 
     	    sType = (Study)TableUtils.toObject( res, new Study() );
     	res.close();
     	return sType;
    }

    /**
     * Returns a sample type by name.
     *
     * @return the SampleType object.
     */
    public String[] findStudyTerms() throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STUDY_TERMS );

     	ResultSet res = pstmt.executeQuery();

     	List<String> fl = new ArrayList<String>();
     	Iterator it = TableUtils.toObjects( res, new Study() );
	while( it.hasNext() ) {
	    Study study = (Study)it.next();
	    fl.add( study.getStudyname() );
	}	       
	res.close();

	String[] terms = new String[fl.size()];
     	return (String[])fl.toArray( terms );
    }

    /**
     * Returns a study by id.
     *
     * @param studyId the study id
     * @return the SampleType object.
     */
    public Study findStudyById( long studyId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_STUDY_BY_ID );
     	pstmt.setLong( 1, studyId );
     	ResultSet res = pstmt.executeQuery();
     	Study sType = null;
     	if( res.next() ) 
     	    sType = (Study)TableUtils.toObject( res, new Study() );
     	res.close();
     	return sType;
    }

    /**
     * Create a study.
     *
     * @param studyName the study name.
     *
     * @return the newly allocated study.
     */
    public Study createStudy( String studyName ) throws SQLException {
	Study study = findStudyByName( studyName );
	if( study != null )
	    throw new SQLException( "Study already exists: "+studyName );

	study = new Study();
	study.setStudyname( studyName );

	PreparedStatement pstmt = getStatement( STMT_STUDY_INSERT );
	pstmt.setLong( 1, study.getStudyid() );
	pstmt.setString( 2, study.getStudyname() );
	pstmt.setTimestamp( 3, study.getStarted() );
	pstmt.setTimestamp( 4, study.getExpire() );
	pstmt.setString( 5, study.getStatus() );
	pstmt.executeUpdate();

	log.debug( "Study created: "+study.getStudyname()+" ("+
		   study.getStudyid()+")" );
	 
	return study;
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

	log.debug( "Organization created: "+org.getOrgname()+" ("+
		   org.getOrgid()+")" );
	 
	return org;
    }

    /**
     * Create a study.
     *
     * @param studyName the study name.
     *
     * @return the newly allocated study.
     */
    public Organization storeOrganization( Organization org ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_ORG_UPDATE );

	pstmt.setString( 1, org.getOrgname() );
	pstmt.setString( 2, org.getSiteid() );
	pstmt.setInt( 3, org.getCountryid() );
	pstmt.setString( 4, org.getOrgtype() );

	pstmt.setLong( 5, org.getOrgid() );

	pstmt.executeUpdate();
	return org;
    }

    /**
     * Returns an organization by name.
     *
     * @return the Organization object.
     */
    public Subject findSubjectByName( Study study, String subjectId ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SUBJECT_BY_NAME );
	pstmt.setLong( 1, study.getStudyid() );
     	pstmt.setString( 2, Stringx.getDefault(subjectId,"").trim() );
     	ResultSet res = pstmt.executeQuery();
     	Subject sType = null;
     	if( res.next() ) 
     	    sType = (Subject)TableUtils.toObject( res, new Subject() );
     	res.close();
     	return sType;
    }

    /**
     * Returns a subject by study and sample.
     *
     * @param study the study.
     * @param sample the sample.
     * @return the Organization object.
     */
    public Subject findSubjectBySample( Study study, Sample sample ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SUBJECT_BY_SAMPLE );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, study.getStudyid() );
     	ResultSet res = pstmt.executeQuery();
     	Subject sType = null;
     	if( res.next() ) 
     	    sType = (Subject)TableUtils.toObject( res, new Subject() );
     	res.close();
     	return sType;
    }

    /**
     * Create a study subject.
     *
     * @param study the study.
     * @param subjectid the subject id.
     *
     * @return the newly allocated subject.
     */
    public Subject createSubject( Study study, String subjectId ) throws SQLException {
	Subject subj = findSubjectByName( study, subjectId );
	if( subj != null )
	    throw new SQLException( "Subject already exists: "+subjectId+" (study: "+study.getStudyname()+")" );

	subj = new Subject();
	subj.setStudyid( study.getStudyid() );
	subj.setSubjectid( subjectId );

	PreparedStatement pstmt = getStatement( STMT_SUBJECT_INSERT );
	pstmt.setLong( 1, subj.getDonorid() );
	pstmt.setLong( 2, subj.getStudyid() );
	pstmt.setString( 3, subj.getSubjectid() );
	pstmt.setString( 4, subj.getSpecies() );
	pstmt.setLong( 5, subj.getTaxon() );
	pstmt.executeUpdate();

	log.debug( "Subject created: "+subj.getSubjectid()+" ("+
		   subj.getDonorid()+")" );
	 
	return subj;
    }

    /**
     * Returns a list of accessions associated with a study and issued by a certain organization.
     *
     * @param study the study context.
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Accession[] findAccession( Study study, Organization org, String acc ) throws SQLException {

	PreparedStatement pstmt = null;
	if( study != null ) {
	    pstmt = getStatement( STMT_ACC_BY_ACC );
	    pstmt.setLong( 3, study.getStudyid() );
	}
	else {
	    pstmt = getStatement( STMT_ACC_BY_ALL );
	}

	pstmt.setString( 1, acc );
	pstmt.setLong( 2, org.getOrgid() );

     	ResultSet res = pstmt.executeQuery();
     	List<Accession> fl = new ArrayList<Accession>();
     	Iterator it = TableUtils.toObjects( res, new Accession() );
	while( it.hasNext() ) {
	    fl.add( (Accession)it.next() );
	}	       
	res.close();

     	Accession[] facs = new Accession[ fl.size() ];
     	return (Accession[])fl.toArray( facs );
    }

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Accession[] findAccession( Organization org, String acc ) throws SQLException {
	return findAccession( null, org, acc );
    }

    /**
     * Returns a list of accessions associated with a sample.
     *
     * @param sample the sample.
     *
     * @return Array of <code>Accession</code> object.
     */
    public Accession[] findSampleAccession( Sample sample ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_ACC_BY_SAMPLE );
	pstmt.setString( 1, sample.getSampleid() );

     	ResultSet res = pstmt.executeQuery();
     	List<Accession> fl = new ArrayList<Accession>();
     	Iterator it = TableUtils.toObjects( res, new Accession() );
	while( it.hasNext() ) {
	    fl.add( (Accession)it.next() );
	}	       
	res.close();

     	Accession[] facs = new Accession[ fl.size() ];
     	return (Accession[])fl.toArray( facs );
    }

    /**
     * Create an accession.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public Accession createAccession( long userId, 
				      Organization org, 
				      Sample sample,
				      String accession,
				      String accType ) 
	throws SQLException {

	Accession acc = new Accession();
	acc.setSampleid( sample.getSampleid() );
	acc.setAccession( accession );
	acc.setAcctype( accType );
	acc.setOrgid( org.getOrgid() );

	acc.updateTrackid();

	PreparedStatement pstmt = getStatement( STMT_ACC_INSERT );
	pstmt.setString( 1, acc.getSampleid() );
	pstmt.setString( 2, acc.getAccession() );
	pstmt.setString( 3, acc.getAcctype() );
	pstmt.setLong( 4, acc.getOrgid() );
	pstmt.setLong( 5, acc.getTrackid() );
	pstmt.executeUpdate();

	log.debug( "Accession created: "+acc.getAccession()+" sample: "+
		   acc.getSampleid()+") trackid: "+acc.getTrackid() );
	
	trackChange( null, acc, userId, "Accession created", null );
 
	return acc;
    }

    /**
     * Assign a Create an accession.
     *
     * @param userId the user id
     * @param sampleType the sample type
     *
     * @return the newly allocated file watch.
     */
    public StudySample assignStudySample( long userId, Study study, Sample sample ) throws SQLException {

 	PreparedStatement pstmt = getStatement( STMT_STSAMP_DUPLICATE );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, study.getStudyid() );

     	ResultSet res = pstmt.executeQuery();
	StudySample studySample = null;
	if( res.next() )
	    studySample = (StudySample)TableUtils.toObject( res, new StudySample() );
     	res.close();
	if( studySample != null ) {
	    log.warn( "Ignored duplicate assignment of "+sample+" to "+study );
	    return studySample;
	}

	studySample = new StudySample();
	studySample.setStudyid( study.getStudyid() );
	studySample.setSampleid( sample.getSampleid() );

	studySample.updateTrackid();

	pstmt = getStatement( STMT_STSAMP_INSERT );
	pstmt.setString( 1, studySample.getSampleid() );
	pstmt.setLong( 2, studySample.getStudyid() );
	pstmt.setLong( 3, studySample.getTrackid() );
	pstmt.executeUpdate();

	log.debug( "Assigned sample "+sample+" to study "+study );
	
	trackChange( null, studySample, userId, "Sample assigned to study", null );

	return studySample;
    }

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public Donor[] findSampleDonor( Sample sample ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_DONOR_BY_SAMPLE );
	pstmt.setString( 1, sample.getSampleid() );

     	ResultSet res = pstmt.executeQuery();
     	List<Donor> fl = new ArrayList<Donor>();
     	Iterator it = TableUtils.toObjects( res, new Donor() );
	while( it.hasNext() ) {
	    fl.add( (Donor)it.next() );
	}	       
	res.close();

     	Donor[] facs = new Donor[ fl.size() ];
     	return (Donor[])fl.toArray( facs );
    }

    /**
     * Assign a sample to a donor.
     *
     * @param userId the user id
     * @param subject the subject.
     * @param sample the sample to be assigned.
     *
     * @return the <code>Donor</code> object.
     */
    public Donor assignDonor( long userId, Subject subject, Sample sample ) throws SQLException {

 	PreparedStatement pstmt = getStatement( STMT_DONOR_DUPLICATE );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, subject.getDonorid() );

     	ResultSet res = pstmt.executeQuery();
	Donor donor = null;
	if( res.next() )
	    donor = (Donor)TableUtils.toObject( res, new Donor() );
     	res.close();
	if( donor != null ) {
	    log.warn( "Ignored duplicate assignment of "+sample+" to "+subject );
	    return donor;
	}

	donor = new Donor();
	donor.setDonorid( subject.getDonorid() );
	donor.setSampleid( sample.getSampleid() );

	donor.updateTrackid();

	pstmt = getStatement( STMT_DONOR_INSERT );
	pstmt.setString( 1, donor.getSampleid() );
	pstmt.setLong( 2, donor.getDonorid() );
	pstmt.setLong( 3, donor.getTrackid() );
	pstmt.executeUpdate();

	log.debug( "Assigned sample "+sample+" to subject "+subject );
	
	trackChange( null, donor, userId, "Sample assigned to donor", null );

	return donor;
    }

    /**
     * Returns a list of accessions issued by a certain organization across all studies.
     *
     * @param organization the organization issueing the accession.
     * @param acc the accession.
     *
     * @return the SampleType object.
     */
    public SampleEvent[] findSiteEvent( Organization org, String visitDesc ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_EVENT_BY_ORG );
	pstmt.setLong( 1, org.getOrgid() );
	pstmt.setString( 2, visitDesc );

     	ResultSet res = pstmt.executeQuery();
     	List<SampleEvent> fl = new ArrayList<SampleEvent>();
     	Iterator it = TableUtils.toObjects( res, new SampleEvent() );
	while( it.hasNext() ) {
	    fl.add( (SampleEvent)it.next() );
	}	       
	res.close();

     	SampleEvent[] facs = new SampleEvent[ fl.size() ];
     	return (SampleEvent[])fl.toArray( facs );	
    }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleEvent findEventById( long timeid ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_EVENT_BY_ID );
     	pstmt.setLong( 1, timeid );
     	ResultSet res = pstmt.executeQuery();
     	SampleEvent sType = null;
     	if( res.next() ) 
     	    sType = (SampleEvent)TableUtils.toObject( res, new SampleEvent() );
     	res.close();
     	return sType;
    }

    /**
     * Creates a new site visit.
     *
     * @param organization the site of the event conducted.
     * @param visit the visit label.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createSiteEvent( Organization org, String visitDesc ) throws SQLException {
	SampleEvent[] events = findSiteEvent( org, visitDesc );
	if( events.length > 0 ) 
	    throw new SQLException( "Site timpoint exists already" );
	
	SampleEvent se = SampleEvent.parseVisit( visitDesc );
	se.setOrgid( org.getOrgid() );

 	PreparedStatement pstmt = getStatement( STMT_EVENT_INSERT );
	pstmt.setLong( 1, se.getTimeid() );
	pstmt.setLong( 2, se.getOrgid() );
	pstmt.setString( 3, visitDesc );
	pstmt.setString( 4, se.getCycle() );
	pstmt.setInt( 5, se.getDay() );
	pstmt.setFloat( 6, se.getHour() );
	pstmt.setString( 7, se.getDosage() );
	pstmt.setFloat( 8, se.getQuantity() );
	pstmt.setString( 9, se.getUnit() );

	pstmt.executeUpdate();

	return se;
    }

    /**
     * Creates a new shipment event.
     *
     * @param organization the sender organization.
     * @param eventName the event name.
     * @param desc the logistics description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createLogisticsEvent( Organization org, String eventName, String desc ) throws SQLException {
	String dDesc = Stringx.getDefault(desc,"").trim();
	SampleEvent[] events = findSiteEvent( org, eventName );
	if( events.length > 0 ) {
	    for( int i = 0; i < events.length; i++ ) {
		String sDesc = Stringx.getDefault(events[i].getDosage(),"").trim();
		if( sDesc.equals(dDesc) )
		    return events[i];
	    }
	}
	
	SampleEvent se = SampleEvent.createLogistics( eventName, dDesc );
	se.setOrgid( org.getOrgid() );

 	PreparedStatement pstmt = getStatement( STMT_EVENT_INSERT );
	pstmt.setLong( 1, se.getTimeid() );
	pstmt.setLong( 2, se.getOrgid() );
	pstmt.setString( 3, se.getVisit() );
	pstmt.setString( 4, se.getCycle() );
	pstmt.setInt( 5, se.getDay() );
	pstmt.setFloat( 6, se.getHour() );
	pstmt.setString( 7, se.getDosage() );
	pstmt.setFloat( 8, se.getQuantity() );
	pstmt.setString( 9, se.getUnit() );

	pstmt.executeUpdate();

	return se;
    }

    /**
     * Creates a new shipment event.
     *
     * @param organization the sender organization.
     * @param desc the shipment description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createShipmentEvent( Organization org, String desc ) throws SQLException {
	return createLogisticsEvent( org, SampleEvent.SHIPMENT, desc );
    }

    /**
     * Creates a new reception event.
     *
     * @param organization the receiving organization.
     * @param desc the reception description.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleEvent createReceiverEvent( Organization org, String desc ) throws SQLException {
	return createLogisticsEvent( org, SampleEvent.RECEIVED, desc );
    }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess[] findSampleProcess( SampleEvent event, Sample sample, long treatid ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_PROCESS_BY_EVENT );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, treatid );
	pstmt.setLong( 3, event.getTimeid() );

     	ResultSet res = pstmt.executeQuery();
     	List<SampleProcess> fl = new ArrayList<SampleProcess>();
     	Iterator it = TableUtils.toObjects( res, new SampleProcess() );
	while( it.hasNext() ) {
	    fl.add( (SampleProcess)it.next() );
	}	       
	res.close();

     	SampleProcess[] facs = new SampleProcess[ fl.size() ];
     	return (SampleProcess[])fl.toArray( facs );
    }

    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess findCollectionProcess( SampleEvent event, Sample sample ) throws SQLException {
	SampleProcess[] collEvents = findSampleProcess( event, sample, SampleProcess.TREATID_COLLECTION );
	if( collEvents.length <= 0 )
	    return null;
	return collEvents[0];
    }
    /**
     * Returns an sample event by id.
     *
     * @param timeid The event id.
     * @return the Sample event object or null (if not existing).
     */
    public SampleProcess findVisit( Sample sample ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_PROCESS_BY_VISIT );
	pstmt.setString( 1, sample.getSampleid() );
	pstmt.setLong( 2, SampleProcess.TREATID_COLLECTION );

     	ResultSet res = pstmt.executeQuery();
     	SampleProcess sType = null;
     	if( res.next() ) 
     	    sType = (SampleProcess)TableUtils.toObject( res, new SampleProcess() );
     	res.close();

	return sType;
    }
    
    /**
     * Assign a sample to a colletion event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignCollectionEvent( long userId, 
						SampleEvent event, 
						Sample sample,
						String collDate,
						String dtFormat ) 
	throws SQLException {

	SampleProcess collEvent = findCollectionProcess( event, sample );
	SampleProcess prevEvent = null;

	if( collEvent == null ) {
	    log.debug( "Parse collection string: "+collDate );
	    collEvent = SampleProcess.parseCollection( dtFormat, collDate );
	    collEvent.setSampleid( sample.getSampleid() );
	    collEvent.setTimeid( event.getTimeid() );
	}
	else {
	    try {
		prevEvent = (SampleProcess)collEvent.copy();
	    }
	    catch( CloneNotSupportedException cne ) {
		log.error( cne );
	    }
	    collEvent.initProcessed( dtFormat, collDate );
	}

	collEvent.updateTrackid();

	if( (prevEvent != null) && (prevEvent.equals( collEvent )) ) {
	    log.warn( "Nothing to be updated. Collection event exists already" );
	    return collEvent;
	}

     	PreparedStatement pstmt = null;
     	int nn = 4;
     	if( prevEvent == null ) {
     	    pstmt = getStatement( STMT_PROCESS_INSERT );
     	    pstmt.setString( 1, collEvent.getSampleid() );
     	    pstmt.setLong( 2, collEvent.getTreatid() );
     	    pstmt.setLong( 3, collEvent.getTimeid() );
	    log.debug( "Creating new collection event: "+collEvent+" sample: "+sample );
     	}
     	else {
     	    pstmt = getStatement( STMT_PROCESS_UPDATE );
     	    pstmt.setString( 4, collEvent.getSampleid() );
     	    pstmt.setLong( 5, collEvent.getTreatid() );
     	    pstmt.setLong( 6, collEvent.getTimeid() );
     	    nn = 1;
	    log.debug( "Updating existing collection event: "+collEvent+" sample: "+sample );
     	}
	
     	pstmt.setInt( nn, collEvent.getStep() );
     	nn++;

     	pstmt.setTimestamp( nn, collEvent.getProcessed() );
     	nn++;

	pstmt.setLong( nn, collEvent.getTrackid() );
     	nn++;

     	pstmt.executeUpdate();

	trackChange( prevEvent, collEvent, userId, "Sample collection event "+((prevEvent==null)?"assigned":"updated"), null );
	return collEvent;
    }

    /**
     * Returns a list of samples associated with the query content.
     * This is searching across all the content related to samples
     *
     * @param query The query string.
     * @return A list of samples.
     */
    public Sample[] findSampleByContent( String query ) throws SQLException {
	FullContentSearch fcs = getContentSearch();
	if( fcs == null )
	    throw new SQLException( "Content server is not configured" );
     	List<Sample> fl = fcs.querySamples( query );
     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of treatments.
     *
     * @param treatment the treatment to search.
     *
     * @return the Treatment objects.
     */
    public Treatment[] findTreatment( String treatment ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_TREAT_BY_NAME );
	pstmt.setString( 1, treatment );
	pstmt.setString( 2, treatment );

     	ResultSet res = pstmt.executeQuery();
     	List<Treatment> fl = new ArrayList<Treatment>();
     	Iterator it = TableUtils.toObjects( res, new Treatment() );
	while( it.hasNext() ) {
	    fl.add( (Treatment)it.next() );
	}	       
	res.close();

     	Treatment[] facs = new Treatment[ fl.size() ];
     	return (Treatment[])fl.toArray( facs );	
    }

    /**
     * Creates a new treatment entry.
     *
     * @param treatment the treatment name.
     * @param desc the treatment description.
     *
     * @return the <code>Treatment</code> object.
     */
    public Treatment createTreatment( String treatment, String desc ) throws SQLException {
	Treatment treat = new Treatment();
	treat.setTreatment( treatment );
	treat.setTreatdesc( desc );

 	PreparedStatement pstmt = getStatement( STMT_TREAT_INSERT );
	pstmt.setLong( 1, treat.getTreatid() );
	pstmt.setString( 2, treat.getTreatment() );
	pstmt.setString( 3, treat.getTreatdesc() );

	pstmt.executeUpdate();

	return treat;
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
     	return sType;
    }

    /**
     * Assign a sample to a colletion event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignTreatment( long userId, 
					  Sample sample,
					  Treatment treatment )
	throws SQLException {

	// find the collection site

	Organization org = findCollectionSite( sample.getSampleid() );
	if( org == null )
	    org = findOrganizationById( -2L );  // generic Merck Serono sponsor

	// check if medication event exists

	SampleEvent[] medicEvents = findSiteEvent( org, "study medication" );
	SampleEvent medicEvent = null;
	if( medicEvents.length > 0 ) {
	    medicEvent = medicEvents[0];
	}
	else {
	    medicEvent = createSiteEvent( org, "study medication" );
	    log.debug( "New study medication event created for site "+org );
	}

	// Test if the treatment has been assigned already

	SampleProcess[] mProcs = findSampleProcess( medicEvent, sample, treatment.getTreatid() );
	if( mProcs.length > 0 ) 
	    return mProcs[0];

	// create sample processing

	SampleProcess mProc = new SampleProcess();
	mProc.setSampleid( sample.getSampleid() );
	mProc.setTreatid( treatment.getTreatid() );
	mProc.setTimeid( medicEvent.getTimeid() );
	mProc.setStep( 100 );
	mProc.setProcessed( new Timestamp( SampleProcess.MISSING_DATETIME ) );

	mProc.updateTrackid();

     	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
	pstmt.setString( 1, mProc.getSampleid() );
	pstmt.setLong( 2, mProc.getTreatid() );
	pstmt.setLong( 3, mProc.getTimeid() );
     	pstmt.setInt( 4, mProc.getStep() );
     	pstmt.setTimestamp( 5, mProc.getProcessed() );
	pstmt.setLong( 6, mProc.getTrackid() );

     	pstmt.executeUpdate();

	trackChange( null, mProc, userId, "Sample medication event assigned", null );
	return mProc;
    }

    /**
     * Assign a sample to a shipment event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignShipmentEvent( long userId, 
					      SampleEvent event, 
					      Sample sample,
					      String evDate,
					      String dtFormat ) 
	throws SQLException {

	SampleProcess shipProc = SampleProcess.parseShipment( dtFormat, evDate );
	shipProc.setSampleid( sample.getSampleid() );
	shipProc.setTimeid( event.getTimeid() );
	shipProc.updateTrackid();

	SampleProcess[] shipped = findSampleProcess( event, sample, SampleProcess.TREATID_PACKAGED );
	for( int i = 0; i < shipped.length; i++ ) {
	    if( shipped[i].equals( shipProc ) ) {
		log.warn( "Nothing to be updated. Shipment at "+shipProc+" is existing already" );
		return shipped[i];
	    }
	}

	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
	pstmt.setString( 1, shipProc.getSampleid() );
	pstmt.setLong( 2, shipProc.getTreatid() );
	pstmt.setLong( 3, shipProc.getTimeid() );
     	pstmt.setInt( 4, shipProc.getStep() );
     	pstmt.setTimestamp( 5, shipProc.getProcessed() );
	pstmt.setLong( 6, shipProc.getTrackid() );

     	pstmt.executeUpdate();
	// log.debug( "Creating new shipment event: "+shipProc+" sample: "+sample );

	trackChange( null, shipProc, userId, "Sample shipment event assigned", null );
	return shipProc;
    }

    /**
     * Assign a sample to a receiver event.
     *
     * @param userId the user id.
     * @param event the sample event.
     * @param sample the sample.
     * @param collDate the collection date.
     * @param dtFormat the date format.
     *
     * @return the <code>SampleEvent</code> object.
     */
    public SampleProcess assignReceiverEvent( long userId, 
					      SampleEvent event, 
					      Sample sample,
					      String evDate,
					      String dtFormat ) 
	throws SQLException {

	SampleProcess shipProc = SampleProcess.parseReceipt( dtFormat, evDate );
	shipProc.setSampleid( sample.getSampleid() );
	shipProc.setTimeid( event.getTimeid() );
	shipProc.updateTrackid();

	SampleProcess[] shipped = findSampleProcess( event, sample, SampleProcess.TREATID_UNPACKAGED );
	for( int i = 0; i < shipped.length; i++ ) {
	    if( shipped[i].equals( shipProc ) ) {
		log.warn( "Nothing to be updated. Reception at "+shipProc+" is existing already" );
		return shipped[i];
	    }
	}

	PreparedStatement pstmt = getStatement( STMT_PROCESS_INSERT );
	pstmt.setString( 1, shipProc.getSampleid() );
	pstmt.setLong( 2, shipProc.getTreatid() );
	pstmt.setLong( 3, shipProc.getTimeid() );
     	pstmt.setInt( 4, shipProc.getStep() );
     	pstmt.setTimestamp( 5, shipProc.getProcessed() );
	pstmt.setLong( 6, shipProc.getTrackid() );

     	pstmt.executeUpdate();
	// log.debug( "Creating new shipment event: "+shipProc+" sample: "+sample );

	trackChange( null, shipProc, userId, "Sample reception event assigned", null );
	return shipProc;
    }

    private Properties parseCategories( String categoryPath ) {
	String[] terms = categoryPath.split( "[|]" );
	Properties props = new Properties();
	String catName = null;
	boolean isValue = false;
	StringBuilder stb = new StringBuilder( "biobank.summary");
	for( int i = 0; i < terms.length; i++ ) {
	    if( isValue ) {
		isValue = false;
		props.put( catName, terms[i] );
	    }
	    else {
		catName = terms[i].replace( ' ', '_' );
		stb.append( "." );
		stb.append( catName );
		// props.put( catName, "" );
		isValue = true;
	    }
	}
	props.put( "path", stb.toString() );
	return props;
    }

    private PreparedStatement getSummaryStatement( Properties props ) 
	throws SQLException {

	String catPath = props.getProperty( "path" );
	PreparedStatement pstmt = getStatement( catPath );
	if( pstmt != null ) {
	    String[] cats = catPath.split( "[.]" );
	    int j = 1;
	    for( int i = 0; i < cats.length; i++ ) {
		String valSt = props.getProperty( cats[i] );
		if( valSt != null ) {
		    pstmt.setString( j, valSt );
		    j++;
		}
	    }
	}
	return pstmt;
    }

    /**
     * Returns a list of sample summaries.
     *
     * @param categoryPath the groupings to be applied.
     *
     * @return an array of summary groups.
     */
    public SampleSummary[] createSampleSummary( String categoryPath ) throws SQLException {
	Properties props = parseCategories( categoryPath );
	log.debug( "Sample summary properties: "+props );

	PreparedStatement pstmt = getSummaryStatement( props );
     	List<SampleSummary> fl = new ArrayList<SampleSummary>();

	if( pstmt != null ) {
	    ResultSet res = pstmt.executeQuery();

	    //
	    // FIX ME: The 5.1.27 mysql jdbc seems to have a bug which was motivating the
	    // debug output below
	    //
	    // ResultSetMetaData meta = res.getMetaData();
	    // for( int i = 1; i <= meta.getColumnCount(); i++ ) 
	    // 	log.debug( meta.getColumnName( i ) );
	    
	    Iterator it = TableUtils.toObjects( res, new SampleSummary() );
	    String catPath = props.getProperty( "path" );
	    while( it.hasNext() ) {
		SampleSummary sSum = (SampleSummary)it.next();
		sSum.setCategoryPath( catPath );
		// log.debug( "Sample summary: "+sSum.getTerm() );
		fl.add( (SampleSummary)it.next() );
	    }	       
	    res.close();
	}
	else
	    log.warn( "Sample summary statement cannot be determined." );

     	SampleSummary[] facs = new SampleSummary[ fl.size() ];
     	return (SampleSummary[])fl.toArray( facs );	
    }

    /**
     * Returns a list of samples by sample type.
     *
     * @param categoryPath the groupings to be applied.
     *
     * @return an array of summary groups.
     * @param type The sample type.
     * @return A list of samples.
     */
    public Sample[] findSampleByCategory( String categoryPath ) throws SQLException {
	Properties props = parseCategories( categoryPath );
	log.debug( "Sample category query properties: "+props );

	PreparedStatement pstmt = getSummaryStatement( props );
     	List<Sample> fl = new ArrayList<Sample>();

	if( pstmt != null ) {
	    ResultSet res = pstmt.executeQuery();
	    Iterator it = TableUtils.toObjects( res, new Sample() );
	    while( it.hasNext() ) {
		fl.add( (Sample)it.next() );
	    }	       
	    res.close();
	}
	else
	    log.warn( "Sample category statement cannot be determined." );

     	Sample[] facs = new Sample[ fl.size() ];
     	return (Sample[])fl.toArray( facs );
    }

    /**
     * Returns a list of cost items per sample type.
     *
     * @param sampleType the name of the sample type.
     *
     * @return an array of CostSample objects.
     */
    public CostSample[] findCostBySampleType( String sampleType ) throws SQLException {
	PreparedStatement pstmt = null;
	if( Stringx.getDefault(sampleType,"").trim().length() <= 0 )
	    pstmt = getStatement( STMT_COSTSAMPLE_ALL_TYPE );
	else {
	    pstmt = getStatement( STMT_COSTSAMPLE_BY_TYPE );
	    pstmt.setString( 1, sampleType );
	}

     	ResultSet res = pstmt.executeQuery();
     	List<CostSample> fl = new ArrayList<CostSample>();
     	Iterator it = TableUtils.toObjects( res, new CostSample() );
	while( it.hasNext() ) {
	    fl.add( (CostSample)it.next() );
	}	       
	res.close();

     	CostSample[] facs = new CostSample[ fl.size() ];
     	return (CostSample[])fl.toArray( facs );
    }

    /**
     * Returns a cost item by id.
     *
     * @param costid the id of the cost item.
     *
     * @return the Cost object or null.
     */
    public Cost findCostById( long costid ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_COST_BY_ID );
     	pstmt.setLong( 1, costid );
     	ResultSet res = pstmt.executeQuery();
     	Cost sType = null;
     	if( res.next() ) 
     	    sType = (Cost)TableUtils.toObject( res, new Cost() );
     	res.close();
     	return sType;
    }

    /**
     * Creates a new cost estimate.
     *
     * @param project the project name (null or empty creates a new name based on the datetime).
     *
     * @return a newly created <code>CostEstimate</code>
     */
    public CostEstimate createCostEstimate( String project ) 
	throws SQLException {

	CostEstimate estimate = new CostEstimate();
	String pName = Stringx.getDefault( project, "").trim();
	if( pName.length() <= 0 )
	    pName = "Estimate as of "+Stringx.currentDateString("MMM dd, yyyy");
	estimate.setProjectname( pName );

	PreparedStatement pstmt = getStatement( STMT_ESTIMATE_INSERT );
	pstmt.setLong( 1, estimate.getEstimateid() );
	pstmt.setString( 2, estimate.getProjectname() );
	pstmt.setTimestamp( 3, estimate.getCreated() );
	pstmt.setInt( 4, estimate.getDuration() );
	pstmt.setFloat( 5, estimate.getTotal() );

     	pstmt.executeUpdate();

	log.debug( "Cost estimate created: "+estimate );

	// clean expired cost estimates

	Timestamp expDate = new Timestamp( System.currentTimeMillis() - COST_EXPIRE );

	pstmt = getStatement( STMT_COSTITEM_EXPIRED );
	pstmt.setTimestamp( 1, expDate );
     	pstmt.executeUpdate();
	pstmt = getStatement( STMT_ESTIMATE_EXPIRED );
	pstmt.setTimestamp( 1, expDate );
     	pstmt.executeUpdate();

	return estimate;
    }

    /**
     * Returns a cost estimate by id.
     *
     * @param estimateId the id of the cost estimate.
     *
     * @return a cost estimate if found or null otherwise.
     */
    public CostEstimate findCostEstimateById( long estimateId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_ESTIMATE_BY_ID );
     	pstmt.setLong( 1, estimateId );

     	ResultSet res = pstmt.executeQuery();

     	// List<CostItem> fl = new ArrayList<CostItem>();
	CostEstimate estimate = null;
	while( res.next() ) {
	    if( estimate == null )
		estimate = (CostEstimate)TableUtils.toObject( res, new CostEstimate() );
	    CostItem ci = (CostItem)TableUtils.toObject( res, new CostItem() );
	    estimate.addCost( ci );
	}	       
	res.close();

	return estimate;
    }

    private CostEstimate addCostItem( CostEstimate ce, CostSample cs, long numItems ) {
	CostItem ci = new CostItem();
	ci.setEstimateid( ce.getEstimateid() );
	ci.setCostid( cs.getCostid() );
	ci.setItemtype( cs.toItemName() );
	ci.setItemcount( numItems );
	ce.addTotal( (float)numItems * cs.getPrice() );
	ce.addCost( ci );
	return ce;
    }

    /**
     * Adds sample storage costs to an estimate.
     *
     * @param ce the cost estimate to be updated.
     * @param cs the cost per sample.
     * @param numItems the number of samples.
     *
     * @return the updated cost estimate.
     */
    public CostEstimate addCostItem( long estimateId, CostSample cs, long numItems ) 
	throws SQLException {

	CostEstimate ce = findCostEstimateById( estimateId );
	if( ce == null ) {
	    log.error( "Cannot find cost estimate id: "+estimateId );
	    throw new SQLException( "Cannot find cost estimate id: "+estimateId );
	}
	return addCostItem( ce, cs, numItems );
    }

    /**
     * Updates an existing cost estimate.
     *
     * @param estimate the cost estimate to be updated.
     *
     * @return an updated <code>CostEstimate</code> object.
     */
    public CostEstimate updateCostEstimate( CostEstimate estimate )
	throws SQLException {

	String pName = Stringx.getDefault( estimate.getProjectname(), "").trim();
	if( pName.length() <= 0 )
	    pName = "Estimate as of "+Stringx.currentDateString("MMM dd, yyyy");
	estimate.setProjectname( pName );
	CostItem[] items = estimate.getCosts();

	// check if project costs have been added.

	CostSample[] extraCosts = findCostBySampleType( "project" );
	boolean costExists = false;
	for( int j = 0; j < extraCosts.length; j++ ) {
	    costExists = false;
	    for( int i= 0; i < items.length; i++ ) {
		if( items[i].getCostid() == extraCosts[j].getCostid() ) {
		    costExists = true;
		    break;
		}
	    }

	    if( !costExists ) 
		estimate = addCostItem( estimate, extraCosts[j], 1L );
	}

	// check if registration costs exist

	extraCosts = findCostBySampleType( SAMPLE_REGISTRATION );
	costExists = false;
	for( int i= 0; i < items.length; i++ ) {
	    if( items[i].getCostid() == extraCosts[0].getCostid() ) {
		costExists = true;
		break;
	    }
	}
	if( !costExists )
	    estimate = addCostItem( estimate, extraCosts[0], 0L );
	
	PreparedStatement pstmt = getStatement( STMT_COSTITEM_DELETE );
	pstmt.setLong( 1, estimate.getEstimateid() );
	pstmt.executeUpdate();

	estimate.setTotal( 0f );
	long numSamples = 0L;
	List<CostItem> adminCosts = new ArrayList<CostItem>();

	pstmt = getStatement( STMT_COSTITEM_INSERT );
	
	for( CostItem ci : estimate.getCosts() ) {
	    Cost cost = findCostById( ci.getCostid() );
	    if( cost != null ) {

		if( SAMPLE_ADMIN.equals(cost.getServicegroup()) ) {
		    adminCosts.add( ci );
		}
		else {
		    float amount = (float)ci.getItemcount() * cost.getPrice();
		    if( COST_PER_MONTH.equals( cost.getFrequency() ) )
			amount = amount * (float)estimate.getDuration();
		    estimate.addTotal( amount );

		    if( SAMPLE_STORAGE.equals(cost.getServicegroup()) ) 
			numSamples+=ci.getItemcount();

		    pstmt.setLong( 1, ci.getCostitemid() ); 
		    pstmt.setLong( 2, estimate.getEstimateid() );
		    pstmt.setString( 3, ci.getItemtype() ); 
		    pstmt.setLong( 4, cost.getCostid() );
		    pstmt.setLong( 5, ci.getItemcount() );
		    pstmt.executeUpdate();
		
		    log.debug( "Cost item inserted: "+ci );
		}
	    }
	    else
		log.warn( "Cost id "+String.valueOf(ci.getCostid())+" is invalid" );
	}

	// add admin costs per sample

	for( CostItem ci : adminCosts ) {
	    Cost cost = findCostById( ci.getCostid() );
	    if( cost != null ) {

		ci.setItemcount(  numSamples );
		estimate.addTotal( (float)ci.getItemcount() * cost.getPrice() );

		pstmt.setLong( 1, ci.getCostitemid() ); 
		pstmt.setLong( 2, estimate.getEstimateid() );
		pstmt.setString( 3, ci.getItemtype() ); 
		pstmt.setLong( 4, cost.getCostid() );
		pstmt.setLong( 5, ci.getItemcount() );
		pstmt.executeUpdate();
	    }
	    else
		log.warn( "Cost id "+String.valueOf(ci.getCostid())+" is invalid" );
	    
	}

	pstmt = getStatement( STMT_ESTIMATE_UPDATE );
	pstmt.setString( 1, estimate.getProjectname() );
	pstmt.setTimestamp( 2, estimate.getCreated() );
	pstmt.setInt( 3, estimate.getDuration() );
	pstmt.setFloat( 4, estimate.getTotal() );

	pstmt.setLong( 5, estimate.getEstimateid() );

	pstmt.executeUpdate();

	log.debug( "Cost estimate updated: "+estimate );

	return estimate;
    }

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
	log.debug( "Data access object cleaned" );
    }

}
