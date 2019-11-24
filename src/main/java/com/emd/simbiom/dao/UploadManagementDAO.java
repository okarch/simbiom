package com.emd.simbiom.dao;

import java.io.File;
import java.io.FileInputStream;
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

import com.emd.simbiom.model.StorageDocument;

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

    private static final long   ONE_DAY          = 24L * 60L * 60L * 1000L; // 1 day

    private static final String[] entityNames = new String[] {
	"template",
	"upload",
	"uploadraw",
	"log"
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

}
