package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Billing;
import com.emd.simbiom.model.Invoice;
import com.emd.simbiom.model.StorageGroup;
import com.emd.simbiom.model.StorageProject;

import com.emd.simbiom.util.Period;
import com.emd.simbiom.util.PeriodParseException;

import com.emd.util.Stringx;

/**
 * <code>StorageBudgetDAO</code> implements the <code>StorageBudget</code> API.
 *
 * Created: Thu Aug  9 21:40:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class StorageBudgetDAO extends BasicDAO implements StorageBudget {

    private static Log log = LogFactory.getLog(StorageBudgetDAO.class);

    private static final String STMT_BILL_BY_ALL             = "biobank.billing.findAll";
    private static final String STMT_BILL_BY_ID              = "biobank.billing.findById";
    private static final String STMT_BILL_BY_PROJECT         = "biobank.billing.findByProject";
    private static final String STMT_BILL_BY_PURCHASE        = "biobank.billing.findByPurchase";
    private static final String STMT_BILL_BY_PURCHASEPROJECT = "biobank.billing.findByPurchaseProject";
    private static final String STMT_BILL_INSERT             = "biobank.billing.insert";
    private static final String STMT_BILL_UPDATE             = "biobank.billing.update";

    private static final String STMT_INVOICE_BY_ID           = "biobank.invoice.findById";
    private static final String STMT_INVOICE_BY_IDRAW        = "biobank.invoice.findByIdBasic";
    private static final String STMT_INVOICE_BY_REF          = "biobank.invoice.findByInvoice";
    private static final String STMT_INVOICE_BY_PERIOD_ASC   = "biobank.invoice.findByPeriodAsc";
    private static final String STMT_INVOICE_BY_PERIOD_DESC  = "biobank.invoice.findByPeriodDesc";
    private static final String STMT_INVOICE_INSERT          = "biobank.invoice.insert";
    private static final String STMT_INVOICE_UPDATE          = "biobank.invoice.update";

    private static final String STMT_PROJECT_BY_ID       = "biobank.project.findById";
    private static final String STMT_PROJECT_BY_NAME     = "biobank.project.findByTitle";
    private static final String STMT_PROJECT_INSERT      = "biobank.project.insert";
    private static final String STMT_PROJECT_UPDATE      = "biobank.project.update";

    private static final String STMT_GROUP_DELETE        = "biobank.group.deleteAll";
    private static final String STMT_GROUP_INSERT        = "biobank.group.insert";

    private static final String[] STMT_INVOICE_BY_TERM = new String[] { 
	"biobank.invoice.findByTerm1",
	"biobank.invoice.findByTerm2",
	"biobank.invoice.findByTerm3",
	"biobank.invoice.findByTerm4"
    };

    public static final String[] entityNames = new String[] {
        "billing",
	"project",
	"group",
	"invoice"
    };

    public StorageBudgetDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Creates a new storage project.
     *
     * @param project the project name (null or empty creates a new name based on the datetime).
     *
     * @return a newly created <code>StorageProject</code>
     */
    public StorageProject createStorageProject( String project ) 
	throws SQLException {

	StorageProject prj = new StorageProject();
	String pName = Stringx.getDefault( project, "").trim();
	if( pName.length() <= 0 )
	    pName = "Untitled "+Stringx.currentDateString("MMM dd, yyyy");
	prj.setTitle( pName );

	PreparedStatement pstmt = getStatement( STMT_PROJECT_INSERT );
	pstmt.setLong( 1, prj.getProjectid() );
	pstmt.setString( 2, prj.getTitle() );
	pstmt.setTimestamp( 3, prj.getCreated() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Storage project created: "+prj );

	return prj;
    }

    /**
     * Returns the storage project with the given id.
     *
     * @param projectId the project id.
     * @return the storage project or null (if not existing).
     */
    public StorageProject findStorageProjectById( long projectId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_PROJECT_BY_ID );
     	pstmt.setLong( 1, projectId );

     	ResultSet res = pstmt.executeQuery();

     	// List<CostItem> fl = new ArrayList<CostItem>();
	StorageProject project = null;
	while( res.next() ) {
	    if( project == null )
		project = (StorageProject)TableUtils.toObject( res, new StorageProject() );
	    StorageGroup sGrp = (StorageGroup)TableUtils.toObject( res, new StorageGroup() );
	    project.addStorageGroup( sGrp );
	}	       
	res.close();
	popStatement( pstmt );

	return project;
    }

    /**
     * Returns the storage projects matching the given name.
     *
     * @param projectTitle the project name which may include wildcards.
     * @return an potentially empty array of storage projects.
     */
    public StorageProject[] findStorageProject( String projectTitle ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_PROJECT_BY_NAME );
	String st = Stringx.getDefault(projectTitle,"").trim().toLowerCase();
	if( st.length() <= 0 )
	    st = "%";
     	pstmt.setString( 1, st );

     	ResultSet res = pstmt.executeQuery();

	List<StorageProject> fl = new ArrayList<StorageProject>();
	StorageProject lastProject = null;
	while( res.next() ) {
	    StorageProject project = (StorageProject)TableUtils.toObject( res, new StorageProject() );
	    if( (lastProject == null) || !(lastProject.equals(project)) ) {
		lastProject = project;
		fl.add( lastProject );
	    }
	    StorageGroup sGrp = (StorageGroup)TableUtils.toObject( res, new StorageGroup() );
	    // log.debug( "Adding storage group: "+sGrp );
	    lastProject.addStorageGroup( sGrp );
	}	       
	res.close();
	popStatement( pstmt );
	
	StorageProject[] projs = new StorageProject[ fl.size() ];
	return (StorageProject[])fl.toArray( projs );
    }
    
    /**
     * Stores the project including storage group.
     *
     * @param the project to store.
     * @return the stored projects.
     */
    public StorageProject storeStorageProject( StorageProject project ) throws SQLException {
	StorageProject prj = findStorageProjectById( project.getProjectid() ); 
	if( prj == null )
	    throw new SQLException( "Storgae project does not exist: "+project.getProjectid() );

	PreparedStatement pstmt = getStatement( STMT_PROJECT_UPDATE );
	pstmt.setLong( 3, project.getProjectid() );
	pstmt.setString( 1, project.getTitle() );
	pstmt.setTimestamp( 2, project.getCreated() );
     	pstmt.executeUpdate();
	popStatement( pstmt );

	// remove storage groups (if any)

	pstmt = getStatement( STMT_GROUP_DELETE );
	pstmt.setLong( 1, project.getProjectid() );
     	pstmt.executeUpdate();
	popStatement( pstmt );	

	log.debug( "Storage groups removed from project: "+project );

	// repopulate storage groups

	StorageGroup[] grps = project.getStorageGroups();
	pstmt = getStatement( STMT_GROUP_INSERT );
	for( int i = 0; i < grps.length; i++ ) {
	    pstmt.setLong( 1, grps[i].getGroupid() );
	    pstmt.setLong( 2, project.getProjectid() );
	    pstmt.setString( 3, grps[i].getGroupname() );
	    pstmt.setString( 4, grps[i].getGroupref() );
	    pstmt.executeUpdate();
	}
	popStatement( pstmt );	

	log.debug( "Storage project updated: "+project );

	return project;
    }

    /**
     * Returns the billing information of a given storage project and / or  purchase order.
     *
     * @param projectId the project id (can be 0 to indicate search for all POs).
     * @param poNum purchase order (can be null).
     * @return an potentially empty array of storage projects.
     */
    public Billing[] findBilling( long projectId, String poNum ) throws SQLException {
 	PreparedStatement pstmt = null;
	if( (projectId != 0L) && (poNum != null) ) {
	    pstmt = getStatement( STMT_BILL_BY_PURCHASEPROJECT );
	    pstmt.setString( 1, poNum );
	    pstmt.setLong( 2, projectId );
	}
	else if( (projectId != 0L) && (poNum == null) ) {
	    pstmt = getStatement( STMT_BILL_BY_PROJECT );
	    pstmt.setLong( 1, projectId );
	}
	else if( (projectId == 0L) && (poNum != null) ) {
	    pstmt = getStatement( STMT_BILL_BY_PURCHASE );
	    pstmt.setString( 1, poNum );
	}
	else {
	    pstmt = getStatement( STMT_BILL_BY_ALL );
	}
     	ResultSet res = pstmt.executeQuery();

	List<Billing> fl = new ArrayList<Billing>();
	while( res.next() ) {
	    Billing bill = (Billing)TableUtils.toObject( res, new Billing() );
	    fl.add( bill );
	}	       
	res.close();
	popStatement( pstmt );
	
	Billing[] bills = new Billing[ fl.size() ];
	return (Billing[])fl.toArray( bills );
    }

    /**
     * Returns the billing information of a given storage project and / or  purchase order.
     *
     * @param project the storage project.
     * @return an potentially empty array of billing records.
     */
    public Billing[] findBillingByProject( StorageProject project ) throws SQLException {
	return findBilling( project.getProjectid(), null );
    }

    /**
     * Returns the billing information of a given storage project and / or  purchase order.
     *
     * @param poNum purchase order (can be null).
     * @return an potentially empty array of billing records.
     */
    public Billing[] findBillingByPurchase( String poNum ) throws SQLException {
	return findBilling( 0L, poNum );
    }

    /**
     * Returns the billing information with the given id.
     *
     * @param billId the billing id.
     * @return the billing info or null (if not existing).
     */
    public Billing findBillingById( long billId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_BILL_BY_ID );
     	pstmt.setLong( 1, billId );

     	ResultSet res = pstmt.executeQuery();
     	Billing bill = null;
     	if( res.next() ) 
     	    bill = (Billing)TableUtils.toObject( res, new Billing() );
     	res.close();
	popStatement( pstmt );

	return bill;
    }

    /**
     * Creates a new billing record for a given storage project.
     *
     * @param project the storage project.
     * @param poNum the purchase order.
     *
     * @return a newly created <code>Billing</code>
     */
    public Billing createBilling( StorageProject project, String poNum ) 
	throws SQLException {

	Billing bill = new Billing();

	if( project != null )
	    bill.setProjectid( project.getProjectid() );
	if( poNum != null )
	    bill.setPurchase( poNum );

	PreparedStatement pstmt = getStatement( STMT_BILL_INSERT );
	pstmt.setLong( 1, bill.getBillid() );
	pstmt.setLong( 2, bill.getProjectid() );
	pstmt.setString( 3, bill.getPurchase() );
	pstmt.setString( 4, bill.getProjectcode() );
	pstmt.setString( 5, bill.getCurrency() );
	pstmt.setFloat( 6, bill.getTotal() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Billing information created: "+bill );

	return bill;
    }

    /**
     * Stores the project including storage group.
     *
     * @param the project to store.
     * @return the stored projects.
     */
    public Billing storeBilling( Billing billing ) throws SQLException {
	Billing bill = findBillingById( billing.getBillid() ); 
	if( bill == null )
	    throw new SQLException( "Billing information does not exist: "+billing.getBillid() );

	PreparedStatement pstmt = getStatement( STMT_BILL_UPDATE );
	pstmt.setLong( 1, billing.getProjectid() );
	pstmt.setString( 2, billing.getPurchase() );
	pstmt.setString( 3, billing.getProjectcode() );
	pstmt.setString( 4, billing.getCurrency() );
	pstmt.setFloat( 5, billing.getTotal() );

	pstmt.setLong( 6, bill.getBillid() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Billing information updated: "+billing );
	return billing;
    }

    /**
     * Returns the invoice with the given id.
     *
     * @param invId the invoice id.
     * @return the invoice or null (if not existing).
     */
    public Invoice findInvoiceById( long invId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_INVOICE_BY_ID );
     	pstmt.setLong( 1, invId );

	// log.debug( "Find invoice by id "+invId );

     	ResultSet res = pstmt.executeQuery();
     	List<Invoice> fl = new ArrayList<Invoice>();
     	Iterator it = TableUtils.toObjects( res, new Invoice() );
	Invoice lastInv = null;
	while( it.hasNext() ) {
	    Invoice inv = (Invoice)it.next();
	    if( (lastInv == null) || (lastInv.getInvoiceid() != inv.getInvoiceid()) ) {
		lastInv = inv;
		fl.add( inv );
	    }
	    lastInv.addProject( inv.getTitle() );
	}
     	res.close();
	popStatement( pstmt );

	// log.debug( "Find invoice by id "+invId+" matches "+fl.size()+" entries" );

	if( fl.size() > 0 )
	    return fl.get( 0 );

	pstmt = getStatement( STMT_INVOICE_BY_IDRAW );
     	pstmt.setLong( 1, invId );
     	res = pstmt.executeQuery();
     	Invoice inv = null;
     	if( res.next() ) 
     	    inv = (Invoice)TableUtils.toObject( res, new Invoice() );
     	res.close();
	popStatement( pstmt );
	
	return inv;
    }

    /**
     * Returns the invoice with the given reference.
     *
     * @param invRef the invoice reference.
     * @return the invoice or null (if not existing).
     */
    public Invoice findInvoice( String invRef ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_INVOICE_BY_REF );
     	pstmt.setString( 1, invRef );

     	ResultSet res = pstmt.executeQuery();
     	List<Invoice> fl = new ArrayList<Invoice>();
     	Iterator it = TableUtils.toObjects( res, new Invoice() );
	Invoice lastInv = null;
	while( it.hasNext() ) {
	    Invoice inv = (Invoice)it.next();
	    if( (lastInv == null) || (lastInv.getInvoiceid() != inv.getInvoiceid()) ) {
		lastInv = inv;
		fl.add( inv );
	    }
	    lastInv.addProject( inv.getTitle() );
	}
     	res.close();
	popStatement( pstmt );

	if( fl.size() <= 0 )
	    return null;
	return fl.get( 0 );
    }

    /**
     * Returns the invoice including the given term.
     *
     * @param invTerm the term to be searched in an invoice.
     * @return an (potentially empty) array of matching invoices.
     */
    public Invoice[] findInvoiceByTerm( String invTerm ) throws SQLException {
	String sTerm = Stringx.getDefault( invTerm, "" ).trim().toLowerCase()+"%";
	if( sTerm.length() > 1 )
	    sTerm = "%"+sTerm;

     	Set<Invoice> fl = new HashSet<Invoice>();
	for( int i = 0; i < STMT_INVOICE_BY_TERM.length; i++ ) {
	    PreparedStatement pstmt = getStatement( STMT_INVOICE_BY_TERM[i] );
	    pstmt.setString( 1, sTerm );

	    ResultSet res = pstmt.executeQuery();
	    Iterator it = TableUtils.toObjects( res, new Invoice() );
	    Invoice lastInv = null;
	    while( it.hasNext() ) {
		Invoice inv = (Invoice)it.next();
		if( (lastInv == null) || (lastInv.getInvoiceid() != inv.getInvoiceid()) ) {
		    lastInv = inv;
		    if( !fl.contains( inv ) )
			fl.add( inv );
		}
		lastInv.addProject( inv.getTitle() );
	    }
	    res.close();
	    popStatement( pstmt );
	}

     	Invoice[] facs = new Invoice[ fl.size() ];
     	return (Invoice[])fl.toArray( facs );
    }

    /**
     * Returns the invoice within a given period. Valid period specifications include:
     * Q1/2018, 02/2017, 07.03.2018-22.04.2018 (as according to locale), -3M, -2Y
     *
     * @param period a string specifying the invoice period.
     * @param descending true if invoices should be ordered by descending dates. 
     * @return the invoice or null (if not existing).
     */
    public Invoice[] findInvoiceByPeriod( String period, boolean descending ) 
	throws SQLException {

	Timestamp[] tsPeriod = null;
	try {
	    Period pPeriod = Period.parse( period );
	    tsPeriod = pPeriod.toTimestamp();
	}
	catch( PeriodParseException pex ) {
	    throw new SQLException( pex );
	}

	log.debug( "Search invoices starting from "+tsPeriod[0]+" to "+tsPeriod[1] ); 

	PreparedStatement pstmt = ( (descending)?
				    getStatement( STMT_INVOICE_BY_PERIOD_DESC ):
				    getStatement( STMT_INVOICE_BY_PERIOD_ASC ) );

	pstmt.setTimestamp( 1, tsPeriod[0] );
	pstmt.setTimestamp( 2, tsPeriod[1] );

     	ResultSet res = pstmt.executeQuery();
     	List<Invoice> fl = new ArrayList<Invoice>();
     	Iterator it = TableUtils.toObjects( res, new Invoice() );
	Invoice lastInv = null;
	while( it.hasNext() ) {
	    Invoice inv = (Invoice)it.next();
	    if( (lastInv == null) || (lastInv.getInvoiceid() != inv.getInvoiceid()) ) {
		lastInv = inv;
		fl.add( inv );
	    }
	    lastInv.addProject( inv.getTitle() );
	}
	res.close();
	popStatement( pstmt );

     	Invoice[] facs = new Invoice[ fl.size() ];
     	return (Invoice[])fl.toArray( facs );
    }

    /**
     * Returns the invoice within a given period. Valid period specifications include:
     * Q1/2018, 02/2017, 07.03.2018-22.04.2018 (as according to locale), -3M, -2Y
     *
     * @param period the invoice period.
     * @param descending true if invoices should be ordered by descending dates. 
     * @return the invoice or null (if not existing).
     */
    public Invoice[] findInvoiceByPeriod( Period period, boolean descending ) 
	throws SQLException {

	Timestamp[] tsPeriod = period.toTimestamp();

	log.debug( "Search invoices starting from "+tsPeriod[0]+" to "+tsPeriod[1] ); 

	PreparedStatement pstmt = ( (descending)?
				    getStatement( STMT_INVOICE_BY_PERIOD_DESC ):
				    getStatement( STMT_INVOICE_BY_PERIOD_ASC ) );

	pstmt.setTimestamp( 1, tsPeriod[0] );
	pstmt.setTimestamp( 2, tsPeriod[1] );

     	ResultSet res = pstmt.executeQuery();
     	List<Invoice> fl = new ArrayList<Invoice>();
     	Iterator it = TableUtils.toObjects( res, new Invoice() );
	Invoice lastInv = null;
	while( it.hasNext() ) {
	    Invoice inv = (Invoice)it.next();
	    if( (lastInv == null) || (lastInv.getInvoiceid() != inv.getInvoiceid()) ) {
		lastInv = inv;
		fl.add( inv );
	    }
	    lastInv.addProject( inv.getTitle() );
	}
	res.close();
	popStatement( pstmt );

     	Invoice[] facs = new Invoice[ fl.size() ];
     	return (Invoice[])fl.toArray( facs );	
    }

    /**
     * Creates a new invoice.
     *
     * @param invoice the invoice reference.
     *
     * @return a newly created <code>Invoice</code>
     */
    public Invoice createInvoice( String invoice ) 
	throws SQLException {

	String invRef = Stringx.getDefault(invoice,"").trim();
	Invoice inv = findInvoice( invRef );
	if( inv != null )
	    throw new SQLException( "Invoice "+invRef+" exists already." );

	inv = new Invoice();
	inv.setInvoice( invRef );

	PreparedStatement pstmt = getStatement( STMT_INVOICE_INSERT );
	pstmt.setLong( 1, inv.getInvoiceid() );
	pstmt.setString( 2, inv.getPurchase() );
	pstmt.setString( 3, inv.getInvoice() );
	pstmt.setTimestamp( 4, inv.getStarted() );
	pstmt.setTimestamp( 5, inv.getEnded() );
	pstmt.setTimestamp( 6, inv.getVerified() );
	pstmt.setTimestamp( 7, inv.getApproved() );
	pstmt.setString( 8, inv.getCurrency() );
	pstmt.setFloat( 9, inv.getNumsamples() );
	pstmt.setFloat( 10, inv.getAmount() );
	pstmt.setTimestamp( 11, inv.getRejected() );
	pstmt.setString( 12, inv.getReason() );
	pstmt.setTimestamp( 13, inv.getCreated() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Invoice created: "+inv );

	return inv;
    }

    /**
     * Creates a new invoice.
     *
     * @param invoice the invoice reference.
     *
     * @return a newly created <code>Invoice</code>
     */
    public Invoice createInvoice( Invoice inv ) 
	throws SQLException {

	String invRef = Stringx.getDefault(inv.getInvoice(),"").trim();
	Invoice sInv = findInvoice( invRef );
	if( sInv != null )
	    throw new SQLException( "Invoice "+invRef+" exists already." );

	PreparedStatement pstmt = getStatement( STMT_INVOICE_INSERT );
	pstmt.setLong( 1, inv.getInvoiceid() );
	pstmt.setString( 2, inv.getPurchase() );
	pstmt.setString( 3, inv.getInvoice() );
	pstmt.setTimestamp( 4, inv.getStarted() );
	pstmt.setTimestamp( 5, inv.getEnded() );
	pstmt.setTimestamp( 6, inv.getVerified() );
	pstmt.setTimestamp( 7, inv.getApproved() );
	pstmt.setString( 8, inv.getCurrency() );
	pstmt.setFloat( 9, inv.getNumsamples() );
	pstmt.setFloat( 10, inv.getAmount() );
	pstmt.setTimestamp( 11, inv.getRejected() );
	pstmt.setString( 12, inv.getReason() );
	pstmt.setTimestamp( 13, inv.getCreated() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Invoice created: "+inv );

	return inv;
    }

    /**
     * Stores / updates the invoice.
     *
     * @param invoice the invoice.
     * @return the stored invoice.
     */
    public Invoice storeInvoice( Invoice invoice ) throws SQLException {
	// log.debug( "Storing invoice: "+invoice );

	Invoice inv = findInvoiceById( invoice.getInvoiceid() );
	if( inv == null )
	    throw new SQLException( "Invoice "+invoice+" not found." );

	// log.debug( "Invoice retrieved: "+inv );

	if( !invoice.getInvoice().equals(inv.getInvoice()) ) {
	    log.debug( "Invoice reference changed from "+inv+" to "+invoice );
	    Invoice invRef = findInvoice( invoice.getInvoice().trim() );
	    if( invRef != null )
		throw new SQLException( "Invoice "+invRef+" exists already." );
	}
	// log.debug( "Invoice reference is valid: "+inv );

	PreparedStatement pstmt = getStatement( STMT_INVOICE_UPDATE );
	pstmt.setString( 1, invoice.getPurchase() );
	pstmt.setString( 2, invoice.getInvoice() );
	pstmt.setTimestamp( 3, invoice.getStarted() );
	pstmt.setTimestamp( 4, invoice.getEnded() );
	pstmt.setTimestamp( 5, invoice.getVerified() );
	pstmt.setTimestamp( 6, invoice.getApproved() );
	pstmt.setString( 7, invoice.getCurrency() );
	pstmt.setFloat( 8, invoice.getNumsamples() );
	pstmt.setFloat( 9, invoice.getAmount() );
	pstmt.setTimestamp( 10, invoice.getRejected() );
	pstmt.setString( 11, invoice.getReason() );
	pstmt.setTimestamp( 12, invoice.getCreated() );
	pstmt.setLong( 13, invoice.getInvoiceid() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Invoice updated: "+invoice );
	return invoice;
    }    

}
