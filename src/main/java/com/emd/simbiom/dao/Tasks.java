package com.emd.simbiom.dao;

// import java.io.File;

import java.sql.SQLException;

import com.emd.simbiom.job.InventoryJob;
import com.emd.simbiom.job.InventoryTask;
// import com.emd.simbiom.model.DocumentContent;
// import com.emd.simbiom.model.Invoice;
// import com.emd.simbiom.model.StorageDocument;
// import com.emd.simbiom.model.StorageGroup;
// import com.emd.simbiom.model.StorageProject;

/**
 * <code>Tasks</code> specifies the API for scheduling and execution of tasks.
 *
 * Created: Sun Dec 29 22:28:19 2019
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface Tasks {

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
	throws SQLException;

    /**
     * Stores the project including storage group.
     *
     * @param the task to store.
     * @return the stored task.
     */
    public InventoryTask storeTask( InventoryTask task ) 
	throws SQLException;

    /**
     * Returns the storage project with the given id.
     *
     * @param taskId the task id.
     * @return the storage project or null (if not existing).
     */
    public InventoryTask findTaskById( long taskId ) 
	throws SQLException;

    /**
     * Returns the storage projects matching the given name.
     *
     * @param projectTitle the project name which may include wildcards.
     * @return an potentially empty array of storage projects.
     */
    public InventoryTask[] findTaskByName( String title ) 
	throws SQLException;

    /**
     * Returns the job with the given id.
     *
     * @param jobId the job id.
     * @return the job or null (if not existing).
     */
    public InventoryJob findJobById( long jobId ) 
	throws SQLException;

    /**
     * Returns scheduled jobs matching the given name.
     *
     * @param title the job's title which may include wildcards.
     * @return an potentially empty array of scheduled jobs.
     */
    public InventoryJob[] findJobByName( String title ) 
	throws SQLException;

    /**
     * Assigns a job to a scheduled task. Task is expected to exist already.
     * If the job does not exist it will be created, it will be updated otherwise.
     *
     * @param task the task to be updated.
     * @param job the job to be added.
     * @return the updated task.
     */
    public InventoryTask assignJob( InventoryTask task, InventoryJob job ) 
	throws SQLException;

    /**
     * Runs a direct query and returns a list of matching objects of the same class 
     * than the given prototype.
     *
     * @param stmt the (query) statement.
     * @param proto the object prototype.
     * @return an array of matching objects.
     */
    public Object[] runQuery( String stmt, Object proto ) 
	throws SQLException;


    /**
     * Returns the storage document associated with a given project.
     *
     * @param projectId a string specifying the invoice period.
     * @param md5 true if invoices should be ordered by descending dates. 
     * @return the invoice or null (if not existing).
     */
    // public StorageDocument[] findDocuments( long projectId, String md5 ) 
    // 	throws SQLException;

    /**
     * Returns the document with the given id.
     *
     * @param documentId the document id.
     * @return the document or null (if not existing).
     */
    // public StorageDocument findDocumentId( long documentId )
    // 	throws SQLException;

    /**
     * Returns the string content identified by md5sum.
     * The raw format is zipped binary data which is represented as hex number characters.
     *
     * @return the content read from the database.
     */
    // public DocumentContent findDocumentContent( String md5 )
    // 	throws SQLException;

    /**
     * Stores a document in the database. An existing document will be replaced.
     *
     * @param md5 the md5sum.
     * @param file the file location.
     * @return a <code>StorageDocument</code> object.
     */
    // public StorageDocument storeDocument( StorageDocument document, File file )
    // 	throws SQLException;

}
