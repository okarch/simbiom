package com.emd.simbiom.model;

import java.sql.Timestamp;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>SampleProcess</code> holds a relationship of sample to a timepoint.
 *
 * Created: Tue Mar  7 10:45:45 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class SampleProcess extends AbstractTrackable implements Copyable {
    private String sampleid;
    private String visit;

    private long treatid;
    private long timeid;

    private int step;

    // private String dosage;

    private Timestamp processed;

    private static final String ITEM_TYPE = "processing";

    private static final  String[] DATE_PATTERNS = {
	"dd-MMM-yyyy hh:mm:ss", 
	"dd-MMM-yyyy hh:mm", 
	"dd-MMM-yyyy", 
	"yyyy/MM/dd hh:mm:ss", 
	"yyyy/MM/dd hh:mm",
	"yyyy/MM/dd",
	"dd.MM.yyyy hh:mm:ss",
	"dd.MM.yyyy hh:mm",
	"dd.MM.yyyy"
    };

    public static final long MISSING_DATETIME = 1000L;

    /**
     * Treatment id representing a collection task.
     */
    public static final long   TREATID_COLLECTION = 5L;
    /**
     * Treatment id representing a package task.
     */
    public static final long   TREATID_PACKAGED   = 6L;
    /**
     * Treatment id representing a unpackage task.
     */
    public static final long   TREATID_UNPACKAGED = 7L;

    public SampleProcess() {
	super( ITEM_TYPE );
	this.processed = new Timestamp( MISSING_DATETIME );
	this.setTrackid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
    }

    private SampleProcess( SampleProcess sp ) {
	super( ITEM_TYPE );
	this.processed = sp.getProcessed();
	this.setTrackid( sp.getTrackid() );
	this.sampleid = sp.getSampleid();
	this.treatid = sp.getTreatid();
	this.timeid = sp.getTimeid();
	this.step = sp.getStep();
    }

    // public static long parseDate( String dt, String pat ) {
    // 	SimpleDateFormat formatter = new SimpleDateFormat( pat );
    // 	long ld = -1L;
    // 	ParsePosition pp = new ParsePosition( 0 );
    // 	Date date = formatter.parse( dt, pp );
    // 	if( pp.getErrorIndex() >= 0 )
    // 	    return -1L;
    // 	return date.getTime();
    // }

    private static SampleProcess parseEventDate( String dtFormat, String dtString ) {
	SampleProcess sp = new SampleProcess();
	Date date = null;
	if( (dtFormat == null) || (dtFormat.trim().length() <= 0) ) {
	    try {
		date = DateUtils.parseDate( dtString, DATE_PATTERNS );
	    }
	    catch( ParseException pex ) {
		date = null;
	    }
	}
	else {
	    SimpleDateFormat formatter = new SimpleDateFormat( dtFormat, Locale.US );
	    ParsePosition pp = new ParsePosition( 0 );
	    date = formatter.parse( dtString, pp );
	    if( pp.getErrorIndex() >= 0 )
		date = null;
	}
	if( date != null )
	    sp.setProcessed( new Timestamp(date.getTime()) );
	return sp;
    }

    /**
     * Creates a sample collection process event.
     *
     * @param dtFormat the date format.
     * @param dtString the collection date string.
     *
     * @return an initialized process object.
     */
    public static SampleProcess parseCollection( String dtFormat, String dtString ) {
	// SampleProcess sp = new SampleProcess();
	// SimpleDateFormat formatter = new SimpleDateFormat( dtFormat, Locale.US );
	// ParsePosition pp = new ParsePosition( 0 );
	// Date date = formatter.parse( dtString, pp );
	// if( pp.getErrorIndex() < 0 )
	//     sp.setProcessed( new Timestamp(date.getTime()) );
	SampleProcess sp = parseEventDate( dtFormat, dtString );
	sp.setTreatid( TREATID_COLLECTION );
	return sp;
    }

    /**
     * Creates a sample shipment process event.
     *
     * @param dtFormat the date format.
     * @param dtString the shipment date string.
     *
     * @return an initialized process object.
     */
    public static SampleProcess parseShipment( String dtFormat, String dtString ) {
	SampleProcess sp = parseEventDate( dtFormat, dtString );
	sp.setTreatid( TREATID_PACKAGED );
	return sp;	
    }

    /**
     * Creates a sample shipment process event.
     *
     * @param dtFormat the date format.
     * @param dtString the shipment date string.
     *
     * @return an initialized process object.
     */
    public static SampleProcess parseReceipt( String dtFormat, String dtString ) {
	SampleProcess sp = parseEventDate( dtFormat, dtString );
	sp.setTreatid( TREATID_UNPACKAGED );
	return sp;	
    }

    /**
     * Get the <code>Sampleid</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSampleid() {
	return sampleid;
    }

    /**
     * Set the <code>Sampleid</code> value.
     *
     * @param sampleid The new Sampleid value.
     */
    public final void setSampleid(final String sampleid) {
	this.sampleid = sampleid;
    }

    /**
     * Get the <code>Treatid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTreatid() {
	return treatid;
    }

    /**
     * Set the <code>Treatid</code> value.
     *
     * @param treatid The new Treatid value.
     */
    public final void setTreatid(final long treatid) {
	this.treatid = treatid;
    }

    /**
     * Get the <code>Timeid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTimeid() {
	return timeid;
    }

    /**
     * Set the <code>Timeid</code> value.
     *
     * @param timeid The new Timeid value.
     */
    public final void setTimeid(final long timeid) {
	this.timeid = timeid;
    }

    /**
     * Get the <code>Processed</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getProcessed() {
	if( processed == null )
	    this.processed = new Timestamp( MISSING_DATETIME ); 
	return processed;
    }

    /**
     * Set the <code>Processed</code> value.
     *
     * @param processed The new Processed value.
     */
    public final void setProcessed(final Timestamp processed) {
	this.processed = processed;
    }

    /**
     * Set the <code>Processed</code> value.
     *
     * @param dtFormat the date format.
     * @param dtString the collection date string.
     */
    public void initProcessed(  String dtFormat, String dtString ) {
	long dt = Stringx.parseDate( dtString, dtFormat );
	if( dt > 0 )
	    setProcessed( new Timestamp(dt) );
    }

    /**
     * Get the <code>Step</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getStep() {
	return step;
    }

    /**
     * Set the <code>Step</code> value.
     *
     * @param step The new Step value.
     */
    public final void setStep(final int step) {
	this.step = step;
    }

    /**
     * Updates the trackid to reflect the current content.
     *
     * @return the updated trackid
     */
    public long updateTrackid() {
	long contId = contentId();
	setTrackid( contId );
	return contId;
    }

    protected long contentId() {
	StringBuilder stb = new StringBuilder();
	stb.append( Stringx.getDefault( getSampleid(), "" ) );
	stb.append( String.valueOf( getTreatid() ) );
	stb.append( String.valueOf( getTimeid() ) );
	stb.append( String.valueOf( getStep() ) );
	stb.append( String.valueOf( getProcessed().getTime() ) );
	return DataHasher.hash( stb.toString().getBytes() );
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new SampleProcess( this );
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return getProcessed().toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof SampleProcess ) {
	    SampleProcess f = (SampleProcess)obj;
	    return (f.contentId() == this.contentId() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.contentId()).hashCode();
    }

    /**
     * Get the <code>Visit</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getVisit() {
	return visit;
    }

    /**
     * Set the <code>Visit</code> value.
     *
     * @param visit The new Visit value.
     */
    public final void setVisit(final String visit) {
	this.visit = visit;
    }

}
