package com.emd.simbiom.dao;

import java.io.File;
import java.io.IOException;

import java.sql.SQLException;

import com.emd.simbiom.model.StorageDocument;

import com.emd.simbiom.upload.InventoryUploadTemplate;
import com.emd.simbiom.upload.UploadBatch;
import com.emd.simbiom.upload.UploadLog;


/**
 * <code>UploadManagement</code> specifies upload management.
 *
 * Created: Wed Aug 22 09:50:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface UploadManagement {

    /**
     * Returns a list of templates and associated upload batches.
     *
     * @return the SampleType object.
     */
    public InventoryUploadTemplate[] findTemplateByName( String templateName ) throws SQLException;

    /**
     * Returns a list of templates and associated upload batches.
     *
     * @return the SampleType object.
     */
    public InventoryUploadTemplate findTemplateById( long templateId ) throws SQLException;

    /**
     * Returns a list of upload batches ordered by logstamp descending.
     * Limits list to 100 entries.
     * 
     * @return the list of uploads.
     */
    public UploadBatch[] findLatestLogs() throws SQLException;

    /**
     * Returns a list of log messages related to the given upload batch.
     * 
     * @param upBatch the upload batch.
     * @param levels comma separated list of log levels to be included.
     * @return the list of log messages.
     */
    public UploadLog[] findLogByUpload( UploadBatch upBatch, String levels ) throws SQLException;

    /**
     * Stores the given upload template.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public InventoryUploadTemplate storeTemplate( InventoryUploadTemplate template ) throws SQLException;

    /**
     * Deletes the given upload template and accompanying uploads and logs.
     *
     * @param template the template to delete.
     * @return false if template cannot be deleted.
     */
    public boolean deleteTemplate( InventoryUploadTemplate template ) throws SQLException;

    /**
     * Updates a given upload template.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public InventoryUploadTemplate updateTemplateByName( InventoryUploadTemplate template ) throws SQLException;

    /**
     * Appends a log message to the upload log.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public UploadLog addUploadMessage( UploadBatch upload, String level, long line, String msg )
	throws SQLException;

    /**
     * Appends a log message to the upload log.
     *
     * @param template the template to store.
     *
     * @return the (newly) stored template.
     */
    public UploadLog addUploadMessage( UploadBatch upload, String level, String msg ) 
	throws SQLException;

    /**
     * Creates a new storage document from a <code>File</code> object.
     *
     * @param file the file location.
     * @param mime the mime type.
     * @return a newly created <code>StorageDocument</code> based on the given file.
     */
    public StorageDocument createOutput( UploadBatch upload, File file, String mime ) 
	throws IOException;

    /**
     * Returns the storage document associated with a given upload.
     *
     * @param uploadId the upload id (if 0 the md5sum will be used).
     * @param md5 the md5sum of the file (if null, upload id will be used).
     * @return a list of matching <code>StorageDocument</code> objects.
     */
    public StorageDocument[] findOutputs( long uploadId, String md5 ) 
	throws SQLException;

    /**
     * Returns the document with the given id.
     *
     * @param documentId the document id.
     * @return the document or null (if not existing).
     */
    public StorageDocument findOutputById( long documentId )
	throws SQLException;

    /**
     * Stores an output document in the database. An existing document will be replaced.
     *
     * @param md5 the md5sum.
     * @param file the file location.
     * @return a <code>StorageDocument</code> object.
     */
    public StorageDocument storeOutput( StorageDocument document, File file )
	throws SQLException;

    /**
     * Returns a list of output files for a given report template.
     * 
     * @param template the report template.
     * @return the list of output files.
     */
    public StorageDocument[] findOutputByTemplate( InventoryUploadTemplate template ) 
	throws SQLException;

}
