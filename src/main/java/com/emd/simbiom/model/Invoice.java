package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.lang.StringUtils;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>Invoice</code> represensts invoices received by storage provider.
 *
 * Created: Sat Sep 29 17:24:57 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class Invoice implements Copyable {
    private long invoiceid;

    private String purchase;
    private String invoice;
    private String currency;
    private String title;
    private String reason;

    private Timestamp started;
    private Timestamp ended;
    private Timestamp verified;
    private Timestamp approved;
    private Timestamp rejected;
    private Timestamp created;

    private float numsamples;
    private float amount;

    private List<String> projects;
    private List<String> projectCodes;

    private static Log log = LogFactory.getLog(Invoice.class);

    private static final String[] periodPatterns = new String[] {
        "yyyy/MM",
	"MM/yyyy"
    };
    /**
     * Describe projectcode here.
     */
    private String projectcode;

    public Invoice() {
	this.setInvoiceid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setPurchase( "" );
	this.setInvoice( "" );
	this.setStarted( getMonthStart(-1) );
	this.setEnded( getMonthEnd(-1) );
	this.setApproved( new Timestamp( 1000L ) );
	this.setVerified( new Timestamp( 1000L ) );
	this.setCurrency( "EUR" );
	this.projects = new ArrayList<String>();
	this.projectCodes = new ArrayList<String>();
	this.setCreated( new Timestamp( System.currentTimeMillis() ) );
	this.setRejected( new Timestamp( 1000L ) );
	this.setReason( "" );
    }

    /**
     * Returns the starting date of the month.
     * 
     * @param month the month (0 applies current month, negative substracts from current month.
     * @return the start date of the given month.
     */ 
    public static Timestamp getMonthStart( int month ) {
	Calendar cal = Calendar.getInstance();
	cal.add( Calendar.MONTH, month );
	cal.set( Calendar.DAY_OF_MONTH, 1 );
	return new Timestamp( cal.getTimeInMillis() );
    }

    /**
     * Returns the starting date of the month.
     * 
     * @param month the month (0 applies current month, negative substracts from current month.
     * @return the start date of the given month.
     */ 
    public static Timestamp getMonthEnd( int month ) {
	Calendar cal = Calendar.getInstance();
	cal.add( Calendar.MONTH, month );
	int dd = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	cal.set( Calendar.DAY_OF_MONTH, dd );
	return new Timestamp( cal.getTimeInMillis() );
    }

    /**
     * Parses a time period from a string, e.g. 2018/01 or 04/2017.
     *
     * @param period the period string.
     * @return an array of start and end <code>Timestamp</code> objects. Length
     * will be zero if a parsing error occured.
     */
    public static Timestamp[] parsePeriod( String period ) {
	String nPeriod = StringUtils.remove( period.trim(), " " );
	Date periodDate = null;
	for( int i = 0; i < periodPatterns.length; i++ ) {
	    SimpleDateFormat formatter = new SimpleDateFormat( periodPatterns[i] );
	    try {
		log.debug( "Applying "+periodPatterns[i]+" to "+nPeriod );
		periodDate = formatter.parse( nPeriod );
		if( periodDate != null )
		    break;
	    }
	    catch( ParseException pex ) {
		log.warn( "Parse exception: "+pex );
		periodDate = null;
	    }
	}
	Timestamp[] periodStamp = null;
	if( periodDate != null ) {
	    Calendar cal = Calendar.getInstance();
	    periodStamp = new Timestamp[2];
	    cal.set( Calendar.MONTH, periodDate.getMonth() );
	    cal.set( Calendar.YEAR, periodDate.getYear()+1900 );
	    cal.set( Calendar.DAY_OF_MONTH, 1 );
	    periodStamp[0] = new Timestamp( cal.getTimeInMillis() );
	    int dd = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    cal.set( Calendar.DAY_OF_MONTH, dd );
	    periodStamp[1] = new Timestamp( cal.getTimeInMillis() );
	    log.debug( "Period start: "+periodStamp[0]+" end: "+periodStamp[1] );
	}
	else
	    periodStamp = new Timestamp[0];
	return periodStamp;
    }

    /**
     * Get the <code>Invoiceid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getInvoiceid() {
	return invoiceid;
    }

    /**
     * Set the <code>Invoiceid</code> value.
     *
     * @param invoiceid The new Invoiceid value.
     */
    public final void setInvoiceid(final long invoiceid) {
	this.invoiceid = invoiceid;
    }

    /**
     * Get the <code>Purchase</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getPurchase() {
	return purchase;
    }

    /**
     * Set the <code>Purchase</code> value.
     *
     * @param purchase The new Purchase value.
     */
    public final void setPurchase(final String purchase) {
	this.purchase = purchase;
    }

    /**
     * Get the <code>Invoice</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getInvoice() {
	return invoice;
    }

    /**
     * Set the <code>Invoice</code> value.
     *
     * @param invoice The new Invoice value.
     */
    public final void setInvoice(final String invoice) {
	this.invoice = invoice;
    }

    /**
     * Get the <code>Started</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getStarted() {
	return started;
    }

    /**
     * Set the <code>Started</code> value.
     *
     * @param started The new Started value.
     */
    public final void setStarted(final Timestamp started) {
	this.started = started;
    }

    /**
     * Get the <code>Ended</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getEnded() {
	return ended;
    }

    /**
     * Set the <code>Ended</code> value.
     *
     * @param ended The new Ended value.
     */
    public final void setEnded(final Timestamp ended) {
	this.ended = ended;
    }

    /**
     * Get the <code>Verified</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getVerified() {
	return verified;
    }

    /**
     * Set the <code>Verified</code> value.
     *
     * @param verified The new Verified value.
     */
    public final void setVerified(final Timestamp verified) {
	this.verified = verified;
    }

    /**
     * Get the <code>Approved</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getApproved() {
	return approved;
    }

    /**
     * Set the <code>Approved</code> value.
     *
     * @param approved The new Approved value.
     */
    public final void setApproved(final Timestamp approved) {
	this.approved = approved;
    }

    /**
     * Get the <code>Currency</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getCurrency() {
	return currency;
    }

    /**
     * Set the <code>Currency</code> value.
     *
     * @param currency The new Currency value.
     */
    public final void setCurrency(final String currency) {
	this.currency = currency;
    }

    /**
     * Get the <code>Numsamples</code> value.
     *
     * @return a <code>float</code> value
     */
    public final float getNumsamples() {
	return numsamples;
    }

    /**
     * Set the <code>Numsamples</code> value.
     *
     * @param numsamples The new Numsamples value.
     */
    public final void setNumsamples(final float numsamples) {
	this.numsamples = numsamples;
    }

    /**
     * Get the <code>Amount</code> value.
     *
     * @return a <code>float</code> value
     */
    public final float getAmount() {
	return amount;
    }

    /**
     * Set the <code>Amount</code> value.
     *
     * @param amount The new Amount value.
     */
    public final void setAmount(final float amount) {
	this.amount = amount;
    }

    /**
     * Get the <code>Rejected</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getRejected() {
	return rejected;
    }

    /**
     * Set the <code>Rejected</code> value.
     *
     * @param rejected The new Rejected value.
     */
    public final void setRejected(final Timestamp rejected) {
	this.rejected = rejected;
    }

    /**
     * Get the <code>Reason</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getReason() {
	return reason;
    }

    /**
     * Set the <code>Reason</code> value.
     *
     * @param reason The new Reason value.
     */
    public final void setReason(final String reason) {
	this.reason = reason;
    }

    /**
     * Get the <code>Created</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getCreated() {
	return created;
    }

    /**
     * Set the <code>Created</code> value.
     *
     * @param created The new Created value.
     */
    public final void setCreated(final Timestamp created) {
	this.created = created;
    }

    /**
     * Get the <code>Title</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTitle() {
	return title;
    }

    /**
     * Set the <code>Title</code> value.
     *
     * @param title The new Title value.
     */
    public final void setTitle(final String title) {
	this.title = title;
    }

    /**
     * Adds a project title to the list.
     *
     * @param title The new Title value.
     */
    public final void addProject(final String title) {
	if( (title != null) && (!this.projects.contains(title)) )
	    this.projects.add( title );
    }

    /**
     * Returns the list of project titles.
     *
     * @return a (potentially empty) array of project titles.
     */
    public String[] getProjects() {
	String[] pTitles = new String[ projects.size() ];
	return (String[])this.projects.toArray( pTitles );
    }

    /**
     * Get the <code>Projectcode</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getProjectcode() {
	return projectcode;
    }

    /**
     * Set the <code>Projectcode</code> value.
     *
     * @param projectcode The new Projectcode value.
     */
    public final void setProjectcode(final String projectcode) {
	this.projectcode = projectcode;
    }

    /**
     * Adds a project code to the list.
     *
     * @param title The new Title value.
     */
    public final void addProjectcode(final String title) {
	if( (title != null) && (!projectCodes.contains(title)) )
	    this.projectCodes.add( title );
    }

    /**
     * Returns the list of project titles.
     *
     * @return a (potentially empty) array of project titles.
     */
    public String[] getProjectcodes() {
	String[] pTitles = new String[ projectCodes.size() ];
	return (String[])this.projectCodes.toArray( pTitles );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new Invoice();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getInvoice(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Invoice ) {
	    Invoice f = (Invoice)obj;
	    return (f.getInvoiceid() == this.getInvoiceid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getInvoiceid()).hashCode();
    }

}
