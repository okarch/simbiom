package com.emd.simbiom.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.io.FileUtils;

import com.emd.simbiom.model.RepositoryRecord;
import com.emd.simbiom.model.SampleType;
import com.emd.simbiom.model.StorageDocument;
import com.emd.simbiom.model.StorageGroup;
import com.emd.simbiom.model.StorageProject;

import com.emd.simbiom.upload.InventoryUploadTemplate;
import com.emd.simbiom.upload.UploadBatch;
import com.emd.simbiom.upload.UploadLog;

import com.emd.simbiom.util.DataHasher;

import com.emd.io.WriterOutputStream;

import com.emd.util.Stringx;

/**
 * <code>UploadManagementDAO</code> implements the <code>UploadManagement</code> API.
 *
 * Created: Wed Aug 22 07:50:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class UploadManagementDAO extends BasicDAO implements UploadManagement {

    private static Log log = LogFactory.getLog(UploadManagementDAO.class);

    private static final String STMT_CONTENT_DELETE      = "biobank.uploadraw.delete";
    private static final String STMT_CONTENT_INSERT      = "biobank.uploadraw.insert";

    private static final String STMT_RAW_FIND_OLD        = "biobank.uploadraw.findArchiveLoads";
    private static final String STMT_RAW_INSERT          = "biobank.uploadraw.insert";

    private static final String STMT_TEMPLATE_BY_ID      = "biobank.template.findById";
    private static final String STMT_TEMPLATE_BY_NAME    = "biobank.template.findByName";
    private static final String STMT_TEMPLATE_INSERT     = "biobank.template.insert";
    private static final String STMT_TEMPLATE_UPDATE     = "biobank.template.update";
    private static final String STMT_TEMPLATE_DELETE     = "biobank.template.delete";

    private static final String STMT_UPLOAD_INSERT       = "biobank.upload.insert";
    private static final String STMT_UPLOAD_DELETE       = "biobank.upload.deleteAll";
    private static final String STMT_UPLOAD_LOG          = "biobank.upload.findLatestLogs";
    private static final String STMT_UPLOAD_MOVE         = "biobank.upload.move";

    private static final String STMT_OUTPUT_BY_DID       = "biobank.output.findId";
    private static final String STMT_OUTPUT_BY_REPORT    = "biobank.upload.findOutput";
    private static final String STMT_OUTPUT_INSERT       = "biobank.output.insert";
    private static final String STMT_OUTPUT_BY_MD5       = "biobank.output.findMd5";
    private static final String STMT_OUTPUT_UPDATE       = "biobank.output.update";
    private static final String STMT_OUTPUT_BY_UPDMD5    = "biobank.output.findUploadMd5";
    private static final String STMT_OUTPUT_BY_UPLOAD    = "biobank.output.findUpload";

    private static final String STMT_DOCUMENT_DELETE         = "biobank.document.delete";

    private static final String STMT_LOG_INSERT          = "biobank.log.insert";
    private static final String STMT_LOG_DELETE          = "biobank.log.deleteAll";
    private static final String STMT_LOG_FIND_BY_UPLOAD  = "biobank.log.findByUpload";

    private static final String STMT_REPOSITORY_CLEAN    = "biobank.repository.clean";
    private static final String STMT_REPOSITORY_BY_REGID = "biobank.repository.registration";
    private static final String STMT_REPOSITORY_INSERT   = "biobank.repository.insert";

    private static final String STMT_REGISTRATION_BY_GROUP = "biobank.registration.findByGroup";
    private static final String STMT_REGISTRATION_INSERT   = "biobank.registration.insert";
    private static final String STMT_REGISTRATION_DELETE   = "biobank.registration.deleteAll";

    private static final String STMT_REGISTRATION_LREG     = "biobank.registration.latestRegistered";
    private static final String STMT_REGISTRATION_LSHIP    = "biobank.registration.latestShipped";
    private static final String STMT_REGISTRATION_LDISP    = "biobank.registration.latestDisposed";

    private static final String[] STMT_REGISTRATION_LATEST = {
	"biobank.registration.latestUpdates0",
	"biobank.registration.latestUpdates1",
	"biobank.registration.latestUpdates2"
    };

    private static final long   ONE_DAY          = 24L * 60L * 60L * 1000L; // 1 day

    private static final String REPOSITORY_DELIMITER     = "[|]";
    private static final int    BATCH_SIZE               = 500;


    private static final String[] entityNames = new String[] {
	"template",
	"upload",
	"uploadraw",
	"log",
	"repository",
	"registration"
    };


    public UploadManagementDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
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
	popStatement( pstmt );

     	InventoryUploadTemplate[] facs = new InventoryUploadTemplate[ fl.size() ];
     	return (InventoryUploadTemplate[])fl.toArray( facs );
    }

    //
    // FIX ME: INSERT where appropriate:
    //
    //	popStatement( pstmt );
    //

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
	popStatement( pstmt );

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
	popStatement( pstmt );

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
	popStatement( pstmt );

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
	popStatement( insRaw );
	popStatement( pstmt );

	log.debug( "Move content of "+updLoads.size()+" upload batches" );
	pstmt = getStatement( STMT_UPLOAD_MOVE );
	for( Long updId : updLoads ) {
	    pstmt.setLong( 1, updId.longValue() );
	    pstmt.executeUpdate();
	}
	popStatement( pstmt );
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
	popStatement( pstmt );

	pstmt = getStatement( STMT_UPLOAD_INSERT );
	for( UploadBatch upd : nb ) {
	    pstmt.setLong( 1, upd.getUploadid() );
	    pstmt.setLong( 2, upd.getTemplateid() );
	    pstmt.setTimestamp( 3, upd.getUploaded() ); 
	    pstmt.setLong( 4, upd.getUserid() ); 
	    pstmt.setString( 5, upd.getMd5sum() ); 
	    pstmt.setString( 6, upd.getUpload() ); 
	    pstmt.executeUpdate();
	    popStatement( pstmt );
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
	popStatement( pstmt );

	// Remove upload batches

     	pstmt = getStatement( STMT_UPLOAD_DELETE );
	pstmt.setLong( 1, templ.getTemplateid() );
     	pstmt.executeUpdate();
	popStatement( pstmt );

	// Remove the template itself

     	pstmt = getStatement( STMT_TEMPLATE_DELETE );
	pstmt.setLong( 1, templ.getTemplateid() );
     	pstmt.executeUpdate();
	popStatement( pstmt );

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
	pstmt.setString( 6, Stringx.strtrunc(updLog.getMessage(),252,"..") );
	pstmt.executeUpdate();
	popStatement( pstmt );

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
     * Creates a new storage document from a <code>File</code> object.
     *
     * @param file the file location.
     * @param mime the mime type.
     * @return a newly created <code>StorageDocument</code> based on the given file.
     */
    public StorageDocument createOutput( UploadBatch upload, File file, String mime ) 
	throws IOException {
	
	InputStream fIns = new FileInputStream( file );

	StringWriter sw = new StringWriter();
	WriterOutputStream wOuts = new WriterOutputStream( sw );
	// TeeOutputStream tOuts = new TeeOutputStream( fOuts, wOuts );

	long orgSize = DataHasher.encodeTo( fIns, wOuts );
	wOuts.flush();
	// tOuts.flush();
	String updCont = sw.toString();
	wOuts.close();
	// tOuts.close();
	fIns.close();

	String md5 = DataHasher.calculateMd5sum( updCont );
	log.debug( "Storage document created from file: ("+md5+"): "+file );

	StorageDocument sDoc = StorageDocument.fromFile( file, md5 );

	sDoc.setUploadid( upload.getUploadid() );
	sDoc.setMime( Stringx.getDefault(mime,"") );
	sDoc.setDocumentsize( orgSize );

	return sDoc;
    }

    /**
     * Returns the storage document associated with a given upload.
     *
     * @param uploadId the upload id (if 0 the md5sum will be used).
     * @param md5 the md5sum of the file (if null, upload id will be used).
     * @return a list of matching <code>StorageDocument</code> objects.
     */
    public StorageDocument[] findOutputs( long uploadId, String md5 ) 
	throws SQLException {
	
	log.debug( "Search document: md5="+((md5==null)?"":md5)+" uploadid: "+uploadId ); 

	PreparedStatement pstmt = null;
	if( (uploadId != 0L) && (md5 != null) ) {
	    pstmt = getStatement( STMT_OUTPUT_BY_UPDMD5 );
	    pstmt.setLong( 1, uploadId );
	    pstmt.setString( 2, md5 );
	}
	else if( uploadId != 0L ) {
	    pstmt = getStatement( STMT_OUTPUT_BY_UPLOAD );
	    pstmt.setLong( 1, uploadId );
	}
	else if( md5 != null ) {
	    pstmt = getStatement( STMT_OUTPUT_BY_MD5 );
	    pstmt.setString( 1, md5 );
	}

     	ResultSet res = pstmt.executeQuery();
     	List<StorageDocument> fl = new ArrayList<StorageDocument>();
     	Iterator it = TableUtils.toObjects( res, new StorageDocument() );
	while( it.hasNext() ) {
	    StorageDocument doc = (StorageDocument)it.next();
	    doc.setDocumentLoader( this );
	    fl.add( doc );
	}
	res.close();
	popStatement( pstmt );

     	StorageDocument[] facs = new StorageDocument[ fl.size() ];
     	return (StorageDocument[])fl.toArray( facs );	
    }

    /**
     * Returns the document with the given id.
     *
     * @param documentId the document id.
     * @return the document or null (if not existing).
     */
    public StorageDocument findOutputById( long documentId )
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_OUTPUT_BY_DID );
     	pstmt.setLong( 1, documentId );

     	ResultSet res = pstmt.executeQuery();
     	StorageDocument doc = null;
     	if( res.next() ) {
     	    doc = (StorageDocument)TableUtils.toObject( res, new StorageDocument() );
	    doc.setDocumentLoader( this );
	}
     	res.close();
	popStatement( pstmt );

	log.debug( "Find document by id "+documentId+": "+((doc!=null)?"match found":"no match") );
	
	return doc;
    }

    /**
     * Stores an output document in the database. An existing document will be replaced.
     *
     * @param md5 the md5sum.
     * @param file the file location.
     * @return a <code>StorageDocument</code> object.
     */
    public StorageDocument storeOutput( StorageDocument document, File file )
	throws SQLException {
	
	StorageDocument[] sDocs = findOutputs( document.getUploadid(), document.getMd5sum() );
	StorageDocument sDoc = null;
	if( sDocs.length > 0 ) 
	    sDoc = sDocs[0];
	else
	    sDoc = findOutputById( document.getDocumentid() );

	// read content fully to memory

	String updContent = null;
	String md5Read = null;
	long orgSize = 0L;
	try {
	    InputStream fIns = new FileInputStream( file );

	    StringWriter sw = new StringWriter();
	    WriterOutputStream wOuts = new WriterOutputStream( sw );
	    orgSize = DataHasher.encodeTo( fIns, wOuts );
	    wOuts.flush();
	    updContent = sw.toString();
	    wOuts.close();
	    fIns.close();
	    md5Read = DataHasher.calculateMd5sum( updContent );
	}
	catch( IOException ioe ) {
	    throw new SQLException( ioe );
	}

	if( md5Read == null )
	    throw new SQLException( "Cannot determine md5sum from "+file );

	StorageDocument rDoc = StorageDocument.fromFile( file, md5Read );
	log.debug( "Storage document created from file: ("+md5Read+"): "+file );

	String prevMd5 = document.getMd5sum();

	document.setFiledate( rDoc.getFiledate() );
	document.setDocumentsize( orgSize );
	document.setMd5sum( md5Read );
	document.setTitle( rDoc.getTitle() );

	PreparedStatement pstmt = null;
    
	int nn = 1;
	if( sDoc != null ) {
	    document.setDocumentid( sDoc.getDocumentid() );
	    pstmt = getStatement( STMT_OUTPUT_UPDATE );
	    pstmt.setLong( 8, document.getDocumentid() );
	}
	else {
	    pstmt = getStatement( STMT_OUTPUT_INSERT );
	    pstmt.setLong( 1, document.getDocumentid() );
	    nn++;
	}
	pstmt.setLong( nn, document.getUploadid() );
	nn++;
	pstmt.setTimestamp( nn, document.getCreated() );
	nn++;
	pstmt.setTimestamp( nn, document.getFiledate() );
	nn++;
	pstmt.setLong( nn, document.getDocumentsize() );
	nn++;
	pstmt.setString( nn, document.getMime() );
	nn++;
	pstmt.setString( nn, document.getTitle() );
	nn++;
	pstmt.setString( nn, document.getMd5sum() );
	nn++;

     	pstmt.executeUpdate();
	popStatement( pstmt );

	document.setDocumentLoader( this );

	log.debug( "Storage document saved: "+document );


	log.debug( "Storage document content "+updContent.length()+" bytes read" );
	
	// delete the content (if any)

	pstmt = getStatement( STMT_CONTENT_DELETE );
	try {
	    pstmt.setString( 1, prevMd5 );
	    pstmt.executeUpdate();
	}
	catch( SQLException sqe ) {
	    // ignore
	}
	try {
	    pstmt.setString( 1, document.getMd5sum() );
	    pstmt.executeUpdate();
	}
	catch( SQLException sqe ) {
	    // ignore
	}
	popStatement( pstmt );

	log.debug( "Storage document content deleted: "+document );

	// upload new content

	pstmt = getStatement( STMT_CONTENT_INSERT );
	pstmt.setString( 1, document.getMd5sum() );
	pstmt.setString( 2, updContent );
	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Storage document content stored: "+document.getMd5sum() );
	
	return document;
    }

    /**
     * Returns a list of output files for a given report template.
     * 
     * @param template the report template.
     * @return the list of output files.
     */
    public StorageDocument[] findOutputByTemplate( InventoryUploadTemplate template ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_OUTPUT_BY_REPORT );
	pstmt.setLong( 1, template.getTemplateid() );

     	ResultSet res = pstmt.executeQuery();
     	List<StorageDocument> fl = new ArrayList<StorageDocument>();
     	Iterator it = TableUtils.toObjects( res, new StorageDocument() );
	while( it.hasNext() ) {
	    StorageDocument doc = (StorageDocument)it.next();
	    doc.setDocumentLoader( this );
	    fl.add( doc );
	}
	res.close();
	popStatement( pstmt );

     	StorageDocument[] facs = new StorageDocument[ fl.size() ];
     	return (StorageDocument[])fl.toArray( facs );	
    }

    private String unquote( String token ) {
	if( token.startsWith( "\"" ) && token.endsWith("\"" ) ) 
	    return token.substring(1,token.length()-1).trim();
	return token;
    }	    

    private RepositoryRecord parseRecord( long recNum, String line, String delimiter ) {
	String[] tokens = line.split( delimiter );

	RepositoryRecord repo = new RepositoryRecord();
	repo.setRecordid( recNum );

	if( tokens.length > 0 )
	    repo.setStudy( Stringx.strtrunc(unquote(tokens[0]),128) );
	if( tokens.length > 1 )
	    repo.setProject( Stringx.strtrunc(unquote(tokens[1]),128) );
	if( tokens.length > 2 )
	    repo.setStoragegroup( unquote(Stringx.strtrunc(tokens[2],128)) );
	if( tokens.length > 3 )
	    repo.setStatus( unquote(Stringx.strtrunc(tokens[3],80)) );
	if( tokens.length > 4 )
	    repo.setDivision( unquote(Stringx.strtrunc(tokens[4],80)) );
	if( tokens.length > 5 )
	    repo.setOriginid( unquote(tokens[5]) );
	if( tokens.length > 6 )
	    repo.setRegistrationid( unquote(tokens[6]) );
	if( tokens.length > 7 )
	    repo.parseRegistered( unquote(tokens[7]) );
	if( tokens.length > 8 )
	    repo.parseShipped( unquote(tokens[8]) );
	if( tokens.length > 9 )
	    repo.parseDisposed( unquote(tokens[9]) );
	if( tokens.length > 10 )
	    repo.setParamname( unquote(tokens[10]) );
	if( tokens.length > 11 )
	    repo.setParamvalue( unquote(tokens[11]) );

	return repo;
    }

// Atacicept|Atacicept - EMR700461-023|023 - Serum - Circulating Proteins - 1.4mL|Stored|BioStorage Europe|L1789843-36|A645BL321-001|Feb 27 2018  3:19PM|||Visit|W24_ET

    /**
     * Uploads a repository dump in a single run.
     * Any previous data is overwritten
     *
     * @param file the repositoy file.
     * @param addRecords indicates whether records should be added (if false, repository will be cleaned initially).
     * @return the number of entries successfully uploaded.
     */
    public long storeRepository( File file, boolean addRecords ) 
	throws SQLException {
	return storeRepository( file, addRecords, BATCH_SIZE );
    }

    /**
     * Uploads a repository dump in a single run.
     * Any previous data is overwritten
     *
     * @param file the repositoy file.
     * @param addRecords indicates whether records should be added (if false, repository will be cleaned initially).
     * @param batchSize the size of the batch to be committed.
     * @return the number of entries successfully uploaded.
     */
    public long storeRepository( File file, boolean addRecords, int batchSize ) 
	throws SQLException {

	if( !file.exists() || !file.canRead() )
	    throw new SQLException( "Cannot access file: "+file );

	log.debug( "Loading "+file.length()+" bytes from "+file );

	// delete the current repository

	if( !addRecords ) {
	    log.debug( "Cleaning repository" );
	    try {
		PreparedStatement pstmt = getStatement( STMT_REPOSITORY_CLEAN );
		pstmt.executeUpdate();
		popStatement( pstmt );
	    }
	    catch( SQLException sqe ) {
		log.warn( sqe );
	    }
	}

	// bulk load the content

	long nSuccess = 0L;
	long nCount = 0L;

	List<String> summaryLog = new ArrayList<String>();
	String msg = "Repository storage summary as of "+Stringx.currentDateString( "dd-MMM-yyyy hh:mm" );
	summaryLog.add( msg );
	msg = "Loading "+file.length()+" bytes from "+file+" ("+file.lastModified()+")";
	summaryLog.add( msg );

	try {
	    BufferedReader br = new BufferedReader( new FileReader(file) );
	    PreparedStatement repo = getStatement( STMT_REPOSITORY_INSERT );
	    String line = null;
	    int bCount = 0;
	    do {
		line = br.readLine();
		if( line != null ) {
		    nCount++;
		    if( nCount > 1 ) {
			// log.debug( "Parsing repository record from line: "+line );
			RepositoryRecord re = parseRecord( nCount, line, REPOSITORY_DELIMITER );
			// log.debug( "Repository record parsed: "+re );
			if( !re.isValid() ) {
			    msg = "Line parsed: "+line;
			    summaryLog.add( msg );
			    log.debug( msg );
			    msg = "Record "+nCount+" is invalid: "+re+" Errors: "+re.checkValidity();
			    summaryLog.add( "[ERROR] "+msg );
			    log.error( msg );
			}
			else {
			    if( bCount > batchSize ) {
				int[] updCount = repo.executeBatch();
				nSuccess = nSuccess+((long)bCount);
				log.debug( "Successfully loaded records: "+nSuccess );
				bCount = 0;
			    }

			    repo.setLong( 1, re.getRecordid() );
			    repo.setTimestamp( 2, re.getModified() );
			    repo.setString( 3, re.getStudy() );
			    repo.setString( 4, re.getProject() );
			    repo.setString( 5, re.getStoragegroup() );
			    repo.setString( 6, re.getStatus() );
			    repo.setString( 7, re.getDivision() );
			    repo.setString( 8, re.getOriginid() );
			    repo.setString( 9, re.getRegistrationid() );
			    repo.setTimestamp( 10, re.getRegistered() );
			    repo.setTimestamp( 11, re.getShipped() );
			    repo.setTimestamp( 12, re.getDisposed() );
			    repo.setString( 13, re.getParamname() );
			    repo.setString( 14, re.getParamvalue() );

			    repo.addBatch();
			    // log.debug( "Batch added: "+bCount );
			    bCount++;
			}
		    }
		    else {
			log.debug( "Header line skipped" );
		    }
		}
	    } while( line != null );
	    msg = "Records read: "+nCount;
	    summaryLog.add( msg );
	    log.debug( msg );
	    if( bCount > 0 ) {
		int[] updCount = repo.executeBatch();
		nSuccess = nSuccess+((long)bCount);
	    }
	    popStatement( repo );
	    msg = "Successfully loaded records: "+nSuccess;
	    summaryLog.add( msg );
	    log.debug( msg );
	    br.close();
	}
	catch( IOException ioe ) {
	    log.error( Stringx.getDefault(ioe.getMessage(),"General SQL error") );
	    throw new SQLException( ioe );
	}

	// establish link to storage projects for each registration id

	PreparedStatement delStmt = getStatement( STMT_REGISTRATION_DELETE );
	delStmt.executeUpdate();
	popStatement( delStmt );

	PreparedStatement pstmt = getStatement( STMT_REPOSITORY_BY_REGID );
	PreparedStatement regStmt = getStatement( STMT_REGISTRATION_INSERT );
	PreparedStatement insGrp = getStatement( StorageBudgetDAO.STMT_GROUP_INSERT );
     	ResultSet res = pstmt.executeQuery();
     	List<RepositoryRecord> fl = new ArrayList<RepositoryRecord>();
     	Iterator it = TableUtils.toObjects( res, new RepositoryRecord() );
	StorageProject prj = null;
	String lastProject = null;
	while( it.hasNext() ) {
	    RepositoryRecord rec = (RepositoryRecord)it.next();
	    if( (lastProject == null) || (!rec.getProject().equals( lastProject )) ) {
		lastProject = rec.getProject();
		prj = null;
		StorageProject[] projects = getDAO().findStorageProject( lastProject );
		if( projects.length <= 0 ) {
		    msg = "Storage project does not exist: "+lastProject+" registration id: "+rec.getRegistrationid();
		    log.error( msg );
		    summaryLog.add( "[ERROR] "+msg );
		}
		else if( projects.length > 1 ) {
		    msg = "Multiple storage projects found: "+lastProject+" registration id: "+rec.getRegistrationid();
		    log.error( msg );
		    summaryLog.add( "[ERROR] "+msg );
		}
		else {
		    prj = projects[0];
		}
	    }
	    if( prj != null ) {
		StorageGroup grp = prj.findStorageGroup( rec.getStoragegroup() );
		if( grp == null ) {
		    grp = new StorageGroup();
		    grp.setGroupname( rec.getStoragegroup() );
		    grp.setProjectid( prj.getProjectid() );

		    insGrp.setLong( 1, grp.getGroupid() );
		    insGrp.setLong( 2, grp.getProjectid() );
		    insGrp.setString( 3, grp.getGroupname() );
		    insGrp.setString( 4, grp.getGroupref() );
		    insGrp.executeUpdate();

		    prj.addStorageGroup( grp );
		    msg = "Storage group created: "+grp;
		    log.warn( msg );
		    summaryLog.add( "[WARNING] "+msg );
		}

		// first pass mapping of sample type using storage group

		SampleType[] sTypes = getDAO().mapSampleType( rec.getStoragegroup() );
		long sType = 1L;
		if( sTypes.length > 0 )
		    sType = sTypes[0].getTypeid();
		if( sTypes.length > 1 ) {
		    msg ="Ambiguous sample type mapping found "+rec.getRegistrationid()+" group: "+rec.getStoragegroup()+" sample type assigned: "+sTypes[0];
		    log.warn( msg );
		    summaryLog.add( "[WARNING] "+msg );
		}
		else if( sTypes.length <= 0 ) {
		    msg = "No samples type mapping found "+rec.getRegistrationid()+" group: "+rec.getStoragegroup();
		    log.error( msg );
		    summaryLog.add( "[ERROR] "+msg );
		}
		
		regStmt.setString( 1, rec.getRegistrationid() );
		regStmt.setLong( 2, grp.getGroupid() );
		regStmt.setLong( 3, sType );
		regStmt.setString( 4, rec.getStatus() );
		regStmt.setString( 5, rec.getDivision() );
		regStmt.setTimestamp( 6, rec.getRegistered() );
		regStmt.setTimestamp( 7, rec.getShipped() );
		regStmt.setTimestamp( 8, rec.getDisposed() );

		regStmt.executeUpdate();
	    }
	}
	res.close();
	popStatement( pstmt );
	popStatement( regStmt );
	popStatement( insGrp );

	// Create a summary log

	if( summaryLog.size() > 0 ) {
	    summaryLog.add( "Processing completed: "+Stringx.currentDateString( "dd-MMM-yyyy hh:mm" ) );
	    try {
		File logF = new File( file.getPath()+".log" );
		FileUtils.writeLines( logF, summaryLog );
	    }
	    catch( IOException ioe ) {
		log.error( ioe );
	    }
	}
	
	return nSuccess;
    }

    private Timestamp latestStatus( String stmt, long groupId ) 
	throws SQLException {

 	PreparedStatement pstmt = getStatement( stmt );
	pstmt.setLong( 1, groupId );

     	ResultSet res = pstmt.executeQuery();
	Timestamp latestDt = new Timestamp( 1000L );
	if( res.next() ) {
     	    RepositoryRecord rec = (RepositoryRecord)TableUtils.toObject( res, new RepositoryRecord() );
	    if( STMT_REGISTRATION_LREG.equals(stmt) ) {
		latestDt = rec.getRegistered();
	    }
	    else if( STMT_REGISTRATION_LSHIP.equals(stmt) ) {
		latestDt = rec.getShipped();
	    }
	    else if( STMT_REGISTRATION_LDISP.equals(stmt) ) {
		latestDt = rec.getDisposed();
	    }
	}
	res.close();
	popStatement( pstmt );

	return latestDt;
    }

    /**
     * Returns the repository samples storage document associated with a given upload.
     *
     * @param groupId the upload id (if 0 the md5sum will be used).
     * @param status an optional storage status.
     * @return a list of matching <code>RepositoryRecord</code> objects.
     */
    public RepositoryRecord[] findRepositoryMember( long groupId, String status ) 
	throws SQLException {

	Timestamp registered = latestStatus( STMT_REGISTRATION_LREG, groupId );
	Timestamp shipped = latestStatus( STMT_REGISTRATION_LSHIP, groupId );
	Timestamp disposed = latestStatus( STMT_REGISTRATION_LDISP, groupId );

 	PreparedStatement pstmt = getStatement( STMT_REGISTRATION_BY_GROUP );
	pstmt.setLong( 1, groupId );

     	ResultSet res = pstmt.executeQuery();

     	List<RepositoryRecord> fl = new ArrayList<RepositoryRecord>();
     	Iterator it = TableUtils.toObjects( res, new RepositoryRecord() );
	while( it.hasNext() ) {
	    RepositoryRecord rec = (RepositoryRecord)it.next();
	    rec.setLatestRegistered( registered );
	    rec.setLatestShipped( shipped );
	    rec.setLatestDisposed( disposed );
	    fl.add( rec );
	}	       
	res.close();
	popStatement( pstmt );

     	RepositoryRecord[] facs = new RepositoryRecord[ fl.size() ];
     	return (RepositoryRecord[])fl.toArray( facs );	
    }

    /**
     * Returns the repository update summary.
     *
     * @param days days to look back into update history.
     * @return a list of matching <code>RepositoryRecord</code> objects.
     */
    public RepositoryRecord[] findRepositoryUpdates( int days ) 
	throws SQLException {

	long ct = System.currentTimeMillis();
	long age = (long)days * 24L * 60L * 60L * 1000L;
	if( ct-age <= 0 )
	    throw new SQLException( "Invalid days value: "+days );

	Timestamp ts = new Timestamp( ct-age );
	log.debug( "Find repository updates after "+ts );

	// Timestamp registered = latestStatus( STMT_REGISTRATION_LREG, groupId );
	// Timestamp shipped = latestStatus( STMT_REGISTRATION_LSHIP, groupId );
	// Timestamp disposed = latestStatus( STMT_REGISTRATION_LDISP, groupId );

 	PreparedStatement pstmt = null;
     	List<RepositoryRecord> fl = new ArrayList<RepositoryRecord>();
	for( int i = 0; i < STMT_REGISTRATION_LATEST.length; i++ ) {
	    pstmt = getStatement( STMT_REGISTRATION_LATEST[i] );
	    pstmt.setTimestamp( 1, ts );

	    ResultSet res = pstmt.executeQuery();
	    Iterator it = TableUtils.toObjects( res, new RepositoryRecord() );
	    while( it.hasNext() ) {
		RepositoryRecord rec = (RepositoryRecord)it.next();
		// log.debug( "Updated members: "+Stringx.getDefault(rec.getProject(),"")+" "+rec.getStoragegroup() );
		// rec.setLatestRegistered( registered );
		// rec.setLatestShipped( shipped );
		// rec.setLatestDisposed( disposed );
		fl.add( rec );
	    }
	    res.close();
	    popStatement( pstmt );
	}

     	RepositoryRecord[] facs = new RepositoryRecord[ fl.size() ];
     	return (RepositoryRecord[])fl.toArray( facs );	
    }
    

}
