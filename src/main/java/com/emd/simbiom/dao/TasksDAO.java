package com.emd.simbiom.dao;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.job.InventoryJob;
import com.emd.simbiom.job.InventoryTask;

import com.emd.simbiom.model.DocumentContent;
import com.emd.simbiom.model.StorageDocument;

import com.emd.util.Stringx;

/**
 * <code>StorageBudgetDAO</code> implements the <code>StorageBudget</code> API.
 *
 * Created: Thu Aug  9 21:40:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class TasksDAO extends BasicDAO implements Tasks {

    private static Log log = LogFactory.getLog(TasksDAO.class);

    private static final String STMT_SCHEDULE_BY_NAME        = "biobank.schedule.findByName";
    private static final String STMT_SCHEDULE_BY_ID          = "biobank.schedule.findById";
    private static final String STMT_SCHEDULE_INSERT         = "biobank.schedule.insert";
    private static final String STMT_SCHEDULE_UPDATE         = "biobank.schedule.update";

    private static final String STMT_JOB_BY_NAME             = "biobank.job.findByName";
    private static final String STMT_JOB_BY_ID               = "biobank.job.findById";
    private static final String STMT_JOB_INSERT              = "biobank.job.insert";
    private static final String STMT_JOB_DELETE              = "biobank.job.delete";

    // private static final String STMT_CONTENT_DELETE          = "biobank.uploadraw.delete";
    // private static final String STMT_CONTENT_INSERT          = "biobank.uploadraw.insert";

    // private static final String STMT_DOCUMENT_BY_DID         = "biobank.document.findId";
    // private static final String STMT_DOCUMENT_BY_MD5         = "biobank.document.findMd5";
    // private static final String STMT_DOCUMENT_BY_PRJMD5      = "biobank.document.findProjectMd5";
    // private static final String STMT_DOCUMENT_BY_PROJECT     = "biobank.document.findProject";
    // private static final String STMT_DOCUMENT_DELETE         = "biobank.document.delete";
    // private static final String STMT_DOCUMENT_INSERT         = "biobank.document.insert";
    // private static final String STMT_DOCUMENT_UPDATE         = "biobank.document.update";

    public static final String[] entityNames = new String[] {
        "schedule",
	"job"
    };

    public TasksDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Creates a new task.
     *
     * @param title The title of the task.
     * @param eventType The event type of the trigger.
     * @param event The event specification.
     *
     * @return a newly created <code>InventoryTask</code>
     */
    public InventoryTask createTask( String title, String eventType, String event ) 
	throws SQLException {

	if( event == null )
	    throw new SQLException( "Trigger event is missing." );

	InventoryTask task = new InventoryTask();
	if( title != null )
	    task.setTitle( title.trim() );
	else
	    task.setTitle( "Untitled "+Stringx.currentDateString("MMM dd, yyyy hh:mm:ss") );

	if( eventType != null )
	    task.setEventtype( eventType );
	task.setEvent( event );
	
	PreparedStatement pstmt = getStatement( STMT_SCHEDULE_INSERT );
	pstmt.setLong( 1, task.getTaskid() );
	pstmt.setString( 2, task.getTitle() );
	pstmt.setTimestamp( 3, task.getCreated() );
	pstmt.setTimestamp( 4, task.getModified() );
	pstmt.setString( 5, task.getEventtype() );
	pstmt.setString( 6, task.getEvent() );
	pstmt.setString( 7, task.getStatus() );
	pstmt.setTimestamp( 8, task.getExpired() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Scheduled task created: "+task );

	return task;
    }

    /**
     * Returns the scheduled task with the given id.
     *
     * @param taskId the project id.
     * @return the scheduled task or null (if it's not existing).
     */
    public InventoryTask findTaskById( long taskId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_SCHEDULE_BY_ID );
     	pstmt.setLong( 1, taskId );

     	ResultSet res = pstmt.executeQuery();

     	// List<CostItem> fl = new ArrayList<CostItem>();
	InventoryTask task = null;
	while( res.next() ) {
	    if( task == null )
		task = (InventoryTask)TableUtils.toObject( res, new InventoryTask() );
	    InventoryJob job = (InventoryJob)TableUtils.toObject( res, new InventoryJob() );
	    if( task.getJobid() != 0L )
		task.addJob( job );
	}	       
	res.close();
	popStatement( pstmt );

	return task;
    }

    /**
     * Stores the task.
     *
     * @param the task to store.
     * @return the stored task.
     */
    public InventoryTask storeTask( InventoryTask task ) 
	throws SQLException {

	InventoryTask tsk = findTaskById( task.getTaskid() );
	if( tsk == null )
	    throw new SQLException( "Task "+task+" does not exist." );

	task.setModified( new Timestamp( System.currentTimeMillis() ) );

	PreparedStatement pstmt = getStatement( STMT_SCHEDULE_UPDATE );

	pstmt.setString( 1, task.getTitle() );
	pstmt.setTimestamp( 2, task.getCreated() );
	pstmt.setTimestamp( 3, task.getModified() );
	pstmt.setString( 4, task.getEventtype() );
	pstmt.setString( 5, task.getEvent() );
	pstmt.setString( 6, task.getStatus() );
	pstmt.setTimestamp( 7, task.getExpired() );

	pstmt.setLong( 8, task.getTaskid() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	// FIX ME: store the jobs as well (??)

	log.debug( "Scheduled task updated: "+task );

	return task;
    }

    /**
     * Returns scheduled tasks matching the given name.
     *
     * @param title the task title which may include wildcards.
     * @return an potentially empty array of scheduled tasks.
     */
    public InventoryTask[] findTaskByName( String title ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_SCHEDULE_BY_NAME );
	String st = Stringx.getDefault(title,"").trim().toLowerCase();
	if( st.length() <= 0 )
	    st = "%";
     	pstmt.setString( 1, st );

     	ResultSet res = pstmt.executeQuery();

	List<InventoryTask> fl = new ArrayList<InventoryTask>();
	InventoryTask lastTask = null;
	while( res.next() ) {
	    InventoryTask task = (InventoryTask)TableUtils.toObject( res, new InventoryTask() );
	    if( (lastTask == null) || !(lastTask.equals(task)) ) {
		lastTask = task;
		fl.add( lastTask );
	    }
	    InventoryJob job = (InventoryJob)TableUtils.toObject( res, new InventoryJob() );
	    // log.debug( "Adding storage group: "+sGrp );
	    if( task.getJobid() != 0L )
		lastTask.addJob( job );
	}	       
	res.close();
	popStatement( pstmt );
	
	InventoryTask[] tasks = new InventoryTask[ fl.size() ];
	return (InventoryTask[])fl.toArray( tasks );
    }

    /**
     * Returns the job with the given id.
     *
     * @param jobId the job id.
     * @return the job or null (if not existing).
     */
    public InventoryJob findJobById( long jobId ) 
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_JOB_BY_ID );
     	pstmt.setLong( 1, jobId );

     	ResultSet res = pstmt.executeQuery();
	InventoryJob job = null;
	if( res.next() )
	    job = (InventoryJob)TableUtils.toObject( res, new InventoryJob() );
	res.close();
	popStatement( pstmt );

	return job;
    }

    /**
     * Returns scheduled jobs matching the given name.
     *
     * @param title the job's title which may include wildcards.
     * @return an potentially empty array of scheduled jobs.
     */
    public InventoryJob[] findJobByName( String title ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_JOB_BY_NAME );
	String st = Stringx.getDefault(title,"").trim().toLowerCase();
	if( st.length() <= 0 )
	    st = "%";
     	pstmt.setString( 1, st );

     	ResultSet res = pstmt.executeQuery();

	List<InventoryJob> fl = new ArrayList<InventoryJob>();
      	Iterator it = TableUtils.toObjects( res, new InventoryJob() );
     	while( it.hasNext() ) {
     	    InventoryJob job = (InventoryJob)it.next();
     	    fl.add( job );
     	}	       
	res.close();
	popStatement( pstmt );
	
	InventoryJob[] jobs = new InventoryJob[ fl.size() ];
	return (InventoryJob[])fl.toArray( jobs );
    }

    /**
     * Assigns a job to a scheduled task. Task is expected to exist already.
     * If the job does not exist it will be created, it will be updated otherwise.
     *
     * @param task the task to be updated.
     * @param job the job to be added.
     * @return the updated task.
     */
    public InventoryTask assignJob( InventoryTask task, InventoryJob job ) 
	throws SQLException {

	InventoryTask tsk = findTaskById( task.getTaskid() );
	if( tsk == null )
	    throw new SQLException( "Task "+task+" does not exist." );

	InventoryJob jb = findJobById( job.getJobid() );
	if( jb == null ) {
	    String jbTitle = Stringx.getDefault( job.getJobtitle(), "" );
	    if( jbTitle.length() > 0 ) {
		InventoryJob[] jbs = findJobByName( jbTitle );
		if( jbs.length > 0 )
		    jb = jbs[0];

		if( jbs.length > 1 )
		    log.warn( "Job ambiguity detected in using job name \""+jbTitle+"\"" );
	    }
	}

	PreparedStatement pstmt = null;
	if( jb != null ) {
	    log.warn( "Job "+jb.getJobid()+" exists already." );

	    Timestamp prevCreated = jb.getCreated();

	    // delete the existing job first

	    pstmt = getStatement( STMT_JOB_DELETE );
	    pstmt.setLong( 1, jb.getJobid() );
	    pstmt.executeUpdate();
	    popStatement( pstmt );

	    // update creation and modification dates
	    
	    job.setCreated( prevCreated );
	    job.setModified( new Timestamp(System.currentTimeMillis()) );
	    
	    log.debug( "Previous job "+jb.getJobid()+" has been deleted." );
	}
	
	pstmt = getStatement( STMT_JOB_INSERT );
	pstmt.setLong( 1, job.getJobid() );
	pstmt.setLong( 2, task.getTaskid() );
	pstmt.setLong( 3, job.getUserid() );
	pstmt.setString( 4, job.getJobtitle() );
	pstmt.setTimestamp( 5, job.getCreated() );
	pstmt.setTimestamp( 6, job.getModified() );
	pstmt.setString( 7, job.getJobtype() );
	pstmt.setString( 8, job.getJob() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Job has been inserted: "+job.getJobid() );

	// update task to record modification

	task = storeTask( task );
	task = findTaskById( task.getTaskid() );

	log.debug( "Updated task: "+task );

	return task;
    }
    
    /**
     * Runs a direct query and returns a list of matching objects of the same class 
     * than the given prototype.
     *
     * @param stmt the (query) statement.
     * @param proto the object prototype.
     * @return an array of matching objects.
     */
    public Object[] runQuery( String stmt, Object proto ) throws SQLException {
	return runStatement( stmt, proto );
    }

    /**
     * Runs a generic query and returns a list of instantiated objects.
     *
     * @param query the query to run.
     * @param entity the entity name.
     *
     * @return a list of objects matching the query.
     */
    // public List runQuery( String query, String entity ) throws SQLException {
    // }


    /**
     * Returns the invoice including the given term.
     *
     * @param invTerm the term to be searched in an invoice.
     * @return an (potentially empty) array of matching invoices.
     */
    // public Invoice[] findInvoiceByTerm( String invTerm ) throws SQLException {
    // 	String sTerm = Stringx.getDefault( invTerm, "" ).trim().toLowerCase()+"%";
    // 	if( sTerm.length() > 1 )
    // 	    sTerm = "%"+sTerm;

    //  	Set<Invoice> fl = new HashSet<Invoice>();
    // 	for( int i = 0; i < STMT_INVOICE_BY_TERM.length; i++ ) {
    // 	    PreparedStatement pstmt = getStatement( STMT_INVOICE_BY_TERM[i] );
    // 	    pstmt.setString( 1, sTerm );

    // 	    ResultSet res = pstmt.executeQuery();
    // 	    Iterator it = TableUtils.toObjects( res, new Invoice() );
    // 	    Invoice lastInv = null;
    // 	    while( it.hasNext() ) {
    // 		Invoice inv = (Invoice)it.next();
    // 		if( (lastInv == null) || (lastInv.getInvoiceid() != inv.getInvoiceid()) ) {
    // 		    lastInv = inv;
    // 		    if( !fl.contains( inv ) )
    // 			fl.add( inv );
    // 		}
    // 		lastInv.addProject( inv.getTitle() );
    // 		lastInv.addProjectcode( inv.getProjectcode() );
    // 	    }
    // 	    res.close();
    // 	    popStatement( pstmt );
    // 	}

    //  	Invoice[] facs = new Invoice[ fl.size() ];
    //  	return (Invoice[])fl.toArray( facs );
    // }

    /**
     * Returns the storage document associated with a given project.
     *
     * @param projectId a string specifying the invoice period.
     * @param md5 true if invoices should be ordered by descending dates. 
     * @return the invoice or null (if not existing).
     */
    // public StorageDocument[] findDocuments( long projectId, String md5 ) 
    // 	throws SQLException {
	
    // 	log.debug( "Search document: md5="+((md5==null)?"":md5)+" project: "+projectId ); 

    // 	PreparedStatement pstmt = null;
    // 	if( (projectId != 0L) && (md5 != null) ) {
    // 	    pstmt = getStatement( STMT_DOCUMENT_BY_PRJMD5 );
    // 	    pstmt.setLong( 1, projectId );
    // 	    pstmt.setString( 2, md5 );
    // 	}
    // 	else if( projectId != 0L ) {
    // 	    pstmt = getStatement( STMT_DOCUMENT_BY_PROJECT );
    // 	    pstmt.setLong( 1, projectId );
    // 	}
    // 	else if( md5 != null ) {
    // 	    pstmt = getStatement( STMT_DOCUMENT_BY_MD5 );
    // 	    pstmt.setString( 1, md5 );
    // 	}

    //  	ResultSet res = pstmt.executeQuery();
    //  	List<StorageDocument> fl = new ArrayList<StorageDocument>();
    //  	Iterator it = TableUtils.toObjects( res, new StorageDocument() );
    // 	while( it.hasNext() ) {
    // 	    StorageDocument doc = (StorageDocument)it.next();
    // 	    doc.setDocumentLoader( this );
    // 	    fl.add( doc );
    // 	}
    // 	res.close();
    // 	popStatement( pstmt );

    //  	StorageDocument[] facs = new StorageDocument[ fl.size() ];
    //  	return (StorageDocument[])fl.toArray( facs );	
    // }

    /**
     * Returns the document with the given id.
     *
     * @param documentId the document id.
     * @return the document or null (if not existing).
     */
    // public StorageDocument findDocumentId( long documentId )
    // 	throws SQLException {

    // 	PreparedStatement pstmt = getStatement( STMT_DOCUMENT_BY_DID );
    //  	pstmt.setLong( 1, documentId );

    //  	ResultSet res = pstmt.executeQuery();
    //  	StorageDocument doc = null;
    //  	if( res.next() ) {
    //  	    doc = (StorageDocument)TableUtils.toObject( res, new StorageDocument() );
    // 	    doc.setDocumentLoader( this );
    // 	}
    //  	res.close();
    // 	popStatement( pstmt );

    // 	log.debug( "Find document by id "+documentId+": "+((doc!=null)?"match found":"no match") );
	
    // 	return doc;
    // }

    /**
     * Stores a document in the database. An existing document will be replaced.
     *
     * @param md5 the md5sum.
     * @param file the file location.
     * @return a <code>StorageDocument</code> object.
     */
    // public StorageDocument storeDocument( StorageDocument document, File file )
    // 	throws SQLException {
	
    // 	StorageDocument[] sDocs = findDocuments( document.getProjectid(), document.getMd5sum() );
    // 	StorageDocument sDoc = null;
    // 	if( sDocs.length > 0 ) 
    // 	    sDoc = sDocs[0];
    // 	else
    // 	    sDoc = findDocumentId( document.getDocumentid() );

    // 	PreparedStatement pstmt = null;
    
    // 	int nn = 1;
    // 	if( sDoc != null ) {
    // 	    document.setDocumentid( sDoc.getDocumentid() );
    // 	    pstmt = getStatement( STMT_DOCUMENT_UPDATE );
    // 	    pstmt.setLong( 8, document.getDocumentid() );
    // 	}
    // 	else {
    // 	    pstmt = getStatement( STMT_DOCUMENT_INSERT );
    // 	    pstmt.setLong( 1, document.getDocumentid() );
    // 	    nn++;
    // 	}
    // 	pstmt.setLong( nn, document.getProjectid() );
    // 	nn++;
    // 	pstmt.setTimestamp( nn, document.getCreated() );
    // 	nn++;
    // 	pstmt.setTimestamp( nn, document.getFiledate() );
    // 	nn++;
    // 	pstmt.setLong( nn, document.getDocumentsize() );
    // 	nn++;
    // 	pstmt.setString( nn, document.getMime() );
    // 	nn++;
    // 	pstmt.setString( nn, document.getTitle() );
    // 	nn++;
    // 	pstmt.setString( nn, document.getMd5sum() );
    // 	nn++;

    //  	pstmt.executeUpdate();
    // 	popStatement( pstmt );

    // 	document.setDocumentLoader( this );

    // 	log.debug( "Storage document saved: "+document );

    // 	// read content fully to memory

    // 	String updContent = null;
    // 	try {
    // 	    updContent = FileUtils.readFileToString( file );
    // 	}
    // 	catch( IOException ioe ) {
    // 	    throw new SQLException( ioe );
    // 	}

    // 	log.debug( "Storage document content "+updContent.length()+" bytes read" );
	
    // 	// delete the content (if any)

    // 	pstmt = getStatement( STMT_CONTENT_DELETE );
    // 	try {
    // 	    pstmt.setString( 1, document.getMd5sum() );
    // 	    pstmt.executeUpdate();
    // 	}
    // 	catch( SQLException sqe ) {
    // 	    // ignore
    // 	}
    // 	popStatement( pstmt );

    // 	log.debug( "Storage document content deleted: "+document );

    // 	// upload new content

    // 	pstmt = getStatement( STMT_CONTENT_INSERT );
    // 	pstmt.setString( 1, document.getMd5sum() );
    // 	pstmt.setString( 2, updContent );
    // 	pstmt.executeUpdate();
    // 	popStatement( pstmt );

    // 	log.debug( "Storage document content stored: "+document.getMd5sum() );
	
    // 	return document;
    // }

    /**
     * Returns the string content identified by md5sum.
     * The raw format is zipped binary data which is represented as hex number characters.
     *
     * @return the content read from the database.
     */
    // public DocumentContent findDocumentContent( String md5 )
    // 	throws SQLException {
    // 	return findDocumentContent( md5 );
    // }

}
