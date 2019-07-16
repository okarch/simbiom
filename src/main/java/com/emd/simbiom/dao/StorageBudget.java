package com.emd.simbiom.dao;

import java.io.File;

import java.sql.SQLException;

import com.emd.simbiom.model.Billing;
import com.emd.simbiom.model.DocumentContent;
import com.emd.simbiom.model.Invoice;
import com.emd.simbiom.model.StorageDocument;
import com.emd.simbiom.model.StorageGroup;
import com.emd.simbiom.model.StorageProject;

import com.emd.simbiom.util.Period;

/**
 * <code>StorageBudget</code> specifies the API for budget operations.
 *
 * Created: Thu Aug  9 21:18:19 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface StorageBudget {

    /**
     * Creates a new storage project.
     *
     * @param project the project name (null or empty creates a new name based on the datetime).
     *
     * @return a newly created <code>StorageProject</code>
     */
    public StorageProject createStorageProject( String project ) 
	throws SQLException;

    /**
     * Returns the storage project with the given id.
     *
     * @param projectId the project id.
     * @return the storage project or null (if not existing).
     */
    public StorageProject findStorageProjectById( long projectId ) 
	throws SQLException;

    /**
     * Returns the storage projects matching the given name.
     *
     * @param projectTitle the project name which may include wildcards.
     * @return an potentially empty array of storage projects.
     */
    public StorageProject[] findStorageProject( String projectTitle ) 
	throws SQLException;

    /**
     * Stores the project including storage group.
     *
     * @param the project to store.
     * @return the stored projects.
     */
    public StorageProject storeStorageProject( StorageProject project ) 
	throws SQLException;

    /**
     * Returns the storage group with the given id.
     *
     * @param groupId the storage group id.
     * @return the storage group or null (if not existing).
     */
    public StorageGroup findStorageGroupById( long groupId ) throws SQLException;

    /**
     * Returns the billing information of a given storage project and / or  purchase order.
     *
     * @param projectId the project id (can be 0 to indicate search for all POs).
     * @param poNum purchase order (can be null).
     * @return an potentially empty array of storage projects.
     */
    public Billing[] findBilling( long projectId, String poNum ) throws SQLException;

    /**
     * Returns the billing information of a given storage project and / or  purchase order.
     *
     * @param project the storage project.
     * @return an potentially empty array of billing records.
     */
    public Billing[] findBillingByProject( StorageProject project ) throws SQLException;

    /**
     * Returns the billing information of a given storage project and / or  purchase order.
     *
     * @param poNum purchase order (can be null).
     * @return an potentially empty array of billing records.
     */
    public Billing[] findBillingByPurchase( String poNum ) throws SQLException;

    /**
     * Returns the billing information with the given id.
     *
     * @param billId the billing id.
     * @return the billing info or null (if not existing).
     */
    public Billing findBillingById( long billId ) throws SQLException;

    /**
     * Creates a new billing record for a given storage project.
     *
     * @param project the storage project.
     * @param poNum the purchase order.
     *
     * @return a newly created <code>Billing</code>
     */
    public Billing createBilling( StorageProject project, String poNum ) 
	throws SQLException;

    /**
     * Stores the project including storage group.
     *
     * @param the project to store.
     * @return the stored projects.
     */
    public Billing storeBilling( Billing billing ) throws SQLException;

    /**
     * Returns the invoice with the given id.
     *
     * @param invId the invoice id.
     * @return the invoice or null (if not existing).
     */
    public Invoice findInvoiceById( long invId ) throws SQLException;

    /**
     * Returns the invoice with the given reference.
     *
     * @param invRef the invoice reference.
     * @return the invoice or null (if not existing).
     */
    public Invoice findInvoice( String invRef ) throws SQLException;

    /**
     * Returns the invoice including the given term.
     *
     * @param invTerm the term to be searched in an invoice.
     * @return an (potentially empty) array of matching invoices.
     */
    public Invoice[] findInvoiceByTerm( String invTerm ) 
	throws SQLException;

    /**
     * Returns the invoices carrying a certain status.
     *
     * @param status the invoice status.
     * @return an (potentially empty) array of matching invoices.
     */
    public Invoice[] findInvoiceByStatus( int status ) 
	throws SQLException;

    /**
     * Returns the invoice within a given period. Valid period specifications include:
     * Q1/2018, 02/2017, 07.03.2018-22.04.2018 (as according to locale), -3M, -2Y
     *
     * @param period a string specifying the invoice period.
     * @param descending true if invoices should be ordered by descending dates. 
     * @return the invoice or null (if not existing).
     */
    public Invoice[] findInvoiceByPeriod( String period, boolean descending ) 
	throws SQLException;

    /**
     * Returns the invoice within a given period. Valid period specifications include:
     * Q1/2018, 02/2017, 07.03.2018-22.04.2018 (as according to locale), -3M, -2Y
     *
     * @param period the invoice period.
     * @param descending true if invoices should be ordered by descending dates. 
     * @return the invoice or null (if not existing).
     */
    public Invoice[] findInvoiceByPeriod( Period period, boolean descending ) 
        throws SQLException;

    /**
     * Returns the invoices related to a given PO.
     *
     * @param purchase the PO number.
     * @param descending true if invoices should be ordered by descending dates. 
     * @return the invoice or null (if not existing).
     */
    public Invoice[] findInvoiceByPurchase( String purchase, boolean descending ) 
	throws SQLException;

    /**
     * Creates a new invoice.
     *
     * @param invoice the invoice reference.
     *
     * @return a newly created <code>Invoice</code>
     */
    public Invoice createInvoice( String invoice ) throws SQLException;

    /**
     * Creates a new invoice.
     *
     * @param invoice the invoice reference.
     *
     * @return a newly created <code>Invoice</code>
     */
    public Invoice createInvoice( Invoice inv ) throws SQLException;

    /**
     * Stores / updates the invoice.
     *
     * @param invoice the invoice.
     * @return the stored invoice.
     */
    public Invoice storeInvoice( Invoice invoice ) throws SQLException;

    /**
     * Returns the storage document associated with a given project.
     *
     * @param projectId a string specifying the invoice period.
     * @param md5 true if invoices should be ordered by descending dates. 
     * @return the invoice or null (if not existing).
     */
    public StorageDocument[] findDocuments( long projectId, String md5 ) 
	throws SQLException;

    /**
     * Returns the document with the given id.
     *
     * @param documentId the document id.
     * @return the document or null (if not existing).
     */
    public StorageDocument findDocumentId( long documentId )
	throws SQLException;

    /**
     * Returns the string content identified by md5sum.
     * The raw format is zipped binary data which is represented as hex number characters.
     *
     * @return the content read from the database.
     */
    public DocumentContent findDocumentContent( String md5 )
	throws SQLException;

    /**
     * Stores a document in the database. An existing document will be replaced.
     *
     * @param md5 the md5sum.
     * @param file the file location.
     * @return a <code>StorageDocument</code> object.
     */
    public StorageDocument storeDocument( StorageDocument document, File file )
	throws SQLException;

}
