package com.emd.simbiom.util;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.emd.util.Stringx;

/**
 * <code>Period</code> represensts a period of time.
 *
 * Created: Thu Oct  4 10:24:57 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class Period { 
    private Date startDate;
    private Date endDate;

    private static Log log = LogFactory.getLog(Period.class);

    private static final String[] periodPatterns = new String[] {
        "MMMdd,yyyy",
	"yyyy",
        "yyyy/MM",
	"MM/yyyy",
	"MMMyyyy",
	"dd-MMM-yyyy",
	"dd-MM-yyyy",
        "dd.MM.yyyy",
        "MM/dd/yyyy",
    };

    // private static final int[] QUARTER_MONTH = { 1, 4, 7, 10 };

    public static final Period UNDEFINED        = new Period();
    public static final Period CURRENT_MONTH    = fromMonth( 0 );
    public static final Period CURRENT_QUARTER  = fromQuarter( 0 );
    public static final Period CURRENT_YEAR     = Period.fromYear( 0 );
    public static final Period LAST_MONTH       = Period.fromMonth( -1 );
    public static final Period LAST_QUARTER     = Period.fromQuarter( -1 );
    public static final Period LAST_YEAR        = Period.fromYear( -1 );

    public Period() {
	this.startDate = new Date();
	this.startDate.setTime( 0L );
	this.endDate = new Date();
	this.endDate.setTime( 0L );
    }

    public Period( Date dtStart, Date dtEnd ) {
	this.startDate = dtStart;
	this.endDate = dtEnd;
    }

    /**
     * Returns a period constructed from the current month by adding the amount of months.
     *
     * @param month the month (0 applies current month, negative substracts from current month)
     * @return a period representing the month.
     */
    public static Period fromMonth( int month ) {
	Calendar cal = Calendar.getInstance();
	cal.set( Calendar.DAY_OF_MONTH, 1 );
	cal.add( Calendar.MONTH, month );
	cal.set( Calendar.HOUR_OF_DAY, 0 ); 
	cal.set( Calendar.MINUTE, 0 ); 
	cal.set( Calendar.SECOND, 1 ); 
	Date dtStart = new Date();
	dtStart.setTime( cal.getTimeInMillis() );
	int dd = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	cal.set( Calendar.DAY_OF_MONTH, dd );
	cal.set( Calendar.HOUR_OF_DAY, 23 ); 
	cal.set( Calendar.MINUTE, 59 ); 
	cal.set( Calendar.SECOND, 59 ); 
	Date dtEnd = new Date();
	dtEnd.setTime( cal.getTimeInMillis() );
	return new Period( dtStart, dtEnd );
    }

    /**
     * Returns a period constructed from the current quarter by adding the amount of quarters.
     *
     * @param quarters the amount of quarters (0 applies current quarter, negative substracts from current quarter)
     * @return a period based on quarters.
     */
    public static Period fromQuarter( int quarters ) {
	Calendar cal = Calendar.getInstance();
	// int mm = (int)(Math.floor( (((double)cal.get( Calendar.MONTH )+1) / 4d) )) + 1;
	int mm = (int)(Math.floor( (((double)cal.get( Calendar.MONTH )+1) / 4d) ));
	cal.set( Calendar.MONTH, mm );
	cal.add( Calendar.MONTH, quarters*3 );
	cal.set( Calendar.DAY_OF_MONTH, 1 );
	cal.set( Calendar.HOUR_OF_DAY, 0 ); 
	cal.set( Calendar.MINUTE, 0 ); 
	cal.set( Calendar.SECOND, 1 ); 
	Date dtStart = new Date();
	dtStart.setTime( cal.getTimeInMillis() );
	cal.add( Calendar.MONTH, 2 );
	int dd = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	cal.set( Calendar.DAY_OF_MONTH, dd );
	cal.set( Calendar.HOUR_OF_DAY, 23 ); 
	cal.set( Calendar.MINUTE, 59 ); 
	cal.set( Calendar.SECOND, 59 ); 
	Date dtEnd = new Date();
	dtEnd.setTime( cal.getTimeInMillis() );
	return new Period( dtStart, dtEnd );
    }

    /**
     * Returns a period constructed from the current year by adding the amount of years.
     *
     * @param years the amount of years (0 applies current year, negative substracts from current year)
     * @return a period representing the year.
     */
    public static Period fromYear( int years ) {
	Calendar cal = Calendar.getInstance();
	cal.set( Calendar.DAY_OF_MONTH, 1 );
	cal.set( Calendar.MONTH, Calendar.JANUARY );
	cal.set( Calendar.HOUR_OF_DAY, 0 ); 
	cal.set( Calendar.MINUTE, 0 ); 
	cal.set( Calendar.SECOND, 1 ); 
	// cal.getTime();
	cal.roll( Calendar.YEAR, years );
	Date dtStart = new Date();
	dtStart.setTime( cal.getTimeInMillis() );
	cal.set( Calendar.DAY_OF_MONTH, 31 );
	cal.set( Calendar.MONTH, Calendar.DECEMBER );
	cal.set( Calendar.HOUR_OF_DAY, 23 ); 
	cal.set( Calendar.MINUTE, 59 ); 
	cal.set( Calendar.SECOND, 59 ); 
	Date dtEnd = new Date();
	dtEnd.setTime( cal.getTimeInMillis() );
	return new Period( dtStart, dtEnd );
    }

    private static Period parseQuarter( int negate, String pSt ) 
	throws PeriodParseException {

	int quart = 0;
	if( pSt.startsWith( "Q" ) || pSt.startsWith("q") ) {

	    if( pSt.length() <= 1 )
		throw new PeriodParseException( "Cannot parse quarter" );

	    String qSt = pSt.substring(1,2);
	    int nQ = Stringx.toInt( qSt, -1 );
	    if( (nQ < 1) && (nQ > 4) )
		throw new PeriodParseException( "Invalid quarter" );

	    int mm = ((nQ-1) * 3) + 1;
	    int year = Calendar.getInstance().get(Calendar.YEAR);
	    if( pSt.length() >= 4 ) {
		nQ = Stringx.toInt(pSt.substring(2),0);
		if( nQ <= 0 )
		    throw new PeriodParseException( "Invalid quarter" );

		if( nQ < 100 ) {
		    if( year-nQ >= 2000 )
			nQ = 2000 + nQ;
		    else
			nQ = 1900 + nQ;
		}
		year = nQ;
	    }

	    Calendar cal = Calendar.getInstance();
	    cal.set( Calendar.DAY_OF_MONTH, 1 );
	    cal.set( Calendar.MONTH, mm );
	    cal.set( Calendar.YEAR, year );
	    cal.set( Calendar.HOUR_OF_DAY, 0 ); 
	    cal.set( Calendar.MINUTE, 0 ); 
	    cal.set( Calendar.SECOND, 1 ); 
	    Date dtStart = new Date();
	    dtStart.setTime( cal.getTimeInMillis() );
	    cal.add( Calendar.MONTH, 2 );
	    int dd = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    cal.set( Calendar.DAY_OF_MONTH, dd );
	    cal.set( Calendar.HOUR_OF_DAY, 23 ); 
	    cal.set( Calendar.MINUTE, 59 ); 
	    cal.set( Calendar.SECOND, 59 ); 
	    Date dtEnd = new Date();
	    dtEnd.setTime( cal.getTimeInMillis() );

	    return new Period( dtStart, dtEnd );
	}
	else if( pSt.endsWith( "q" ) || pSt.endsWith( "Q" ) ) {

	    if( pSt.length() <= 1 )
		throw new PeriodParseException( "Cannot parse quarter" );

	    String qSt = pSt.substring(0, pSt.length()-1 );
	    int nQ = Stringx.toInt( qSt, -1 );
	    if( nQ < 0 )
		throw new PeriodParseException( "Invalid quarter" );

	    return Period.fromQuarter( negate * nQ );
	}
	return null;
    }

    private static Period parseDelta( int negate, String pSt ) 
	throws PeriodParseException {

	String ucSt = pSt.toUpperCase();
	if( ucSt.endsWith( "M" ) ) {

	    if( ucSt.length() < 2 )
		throw new PeriodParseException( "Cannot parse relative period" );

	    int nDelta = Stringx.toInt( pSt.substring( 0, pSt.length()-1 ), -1 );
	    if( nDelta < 0 )
		throw new PeriodParseException( "Invalid period number" );

	    return fromMonth( negate * nDelta );
	}
	else if( ucSt.endsWith( "Y" ) ) {
	    if( ucSt.length() < 2 )
		throw new PeriodParseException( "Cannot parse relative period" );

	    int nDelta = Stringx.toInt( pSt.substring( 0, pSt.length()-1 ), -1 );
	    if( nDelta < 0 )
		throw new PeriodParseException( "Invalid period number" );

	    return fromYear( negate * nDelta );
	}
	return null;
    }

    private static Period parsePatterns( String nPeriod ) 
	throws PeriodParseException {

	Date periodDate = null;
	int patternIndex = -1;
	for( int i = 0; i < periodPatterns.length; i++ ) {
	    SimpleDateFormat formatter = new SimpleDateFormat( periodPatterns[i] );
	    try {
		// log.debug( "Applying "+periodPatterns[i]+" to "+nPeriod );
		periodDate = formatter.parse( nPeriod );
		if( periodDate != null ) {
		    patternIndex = i;
		    break;
		}
	    }
	    catch( ParseException pex ) {
		log.warn( "Parse exception: "+pex );
		periodDate = null;
	    }
	}

	if( periodDate == null ) 
	    throw new PeriodParseException( "No matching data pattern" );

	Calendar cal = Calendar.getInstance();
	cal.set( Calendar.HOUR_OF_DAY, 0 );
	cal.set( Calendar.MINUTE, 0 );
	cal.set( Calendar.SECOND, 0 );
	cal.set( Calendar.MILLISECOND, 0 );
	cal.set( Calendar.DAY_OF_MONTH, 1 );
	cal.set( Calendar.MONTH, periodDate.getMonth() ); 
	cal.set( Calendar.YEAR, periodDate.getYear()+1900 );
	
	Date dtStart = new Date();
	Date dtEnd = new Date();
	if( patternIndex == 0 ) {
	    cal.set( Calendar.MONTH, Calendar.JANUARY );

	    dtStart.setTime( cal.getTimeInMillis() );

	    cal.set( Calendar.MONTH, Calendar.DECEMBER );
	    cal.set( Calendar.DAY_OF_MONTH, 31 );
	    cal.set( Calendar.HOUR_OF_DAY, 23 );
	    cal.set( Calendar.MINUTE, 59 );
	    cal.set( Calendar.SECOND, 59 );
	    cal.set( Calendar.MILLISECOND, 999 );

	    dtEnd.setTime( cal.getTimeInMillis() );
	}
	else if( (patternIndex >= 1) && (patternIndex < 3) ) {
	    dtStart.setTime( cal.getTimeInMillis() );

	    int dd = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    cal.set( Calendar.DAY_OF_MONTH, dd );

	    dtEnd.setTime( cal.getTimeInMillis() );
	}
	else {
	    cal.set( Calendar.DAY_OF_MONTH, periodDate.getDate() );
	    dtStart.setTime( cal.getTimeInMillis() );

	    cal.set( Calendar.HOUR_OF_DAY, 23 );
	    cal.set( Calendar.MINUTE, 59 );
	    cal.set( Calendar.SECOND, 59 );
	    cal.set( Calendar.MILLISECOND, 999 );

	    dtEnd.setTime( cal.getTimeInMillis() );
	}

	// log.debug( "Start date: "+dtStart+" end date: "+dtEnd );

	return new Period( dtStart, dtEnd );
    }

    /**
     * Parses a time periods from a string, e.g.:
     * 2018/01 
     * 04/2017
     * Q1/2018
     * -3Q
     * -2M
     * -1Y
     * 01.04.2002..22.04.2018
     *
     * @param period the period string.
     * @return a <code>Period</code> object potentially holding Period.UNDEFINED in case of invalid parse results.
     */
    public static Period parse( String period ) 
	throws PeriodParseException {

	String nPeriod = StringUtils.remove( period.trim(), " " );

	// test minus at the beginning

	int negate = 1;
	if( nPeriod.startsWith( "-" ) ) {
	    negate = -1;

	    if( nPeriod.length() <= 1 )
		throw new PeriodParseException( "Parse error. Period string too short" );

	    nPeriod = period.substring(1);
	}

	// check occurence of quarter at the start or end

	Period pPeriod = parseQuarter( negate, nPeriod );
	if( pPeriod != null )
	    return pPeriod;

	// check occurence of month or year relative to current date

	pPeriod = parseDelta( negate, nPeriod );
	if( pPeriod != null )
	    return pPeriod;

	// extract range (if any)

	List<String> tokens = new ArrayList<String>();
	tokens.add( StringUtils.substringBefore( nPeriod, ".." ).trim() );
	tokens.add( StringUtils.substringAfter( nPeriod, ".." ).trim() );	     

	// parse range entries and join together

	for( String tok : tokens ) {
	    if( tok.length() > 0 ) {
		Period patPeriod = parsePatterns( tok );
		// if( patPeriod == null )
		//     break;
		if( pPeriod == null )
		    pPeriod = patPeriod;
		else
		    pPeriod.join( patPeriod );
	    }
	}

	return pPeriod;
    }

    /**
     * Joins two periods together. The resulting period spans both periods.
     *
     * @param period the period to join.
     * @return the joined period.
     */
    public Period join( Period period ) {
	if( period == null )
	    return null;
	Date startDt = period.getStartDate();
	Date endDt = period.getEndDate();
	if( startDt.after( this.getStartDate() ) )
	    startDt = this.getStartDate();
	if( endDt.before( this.endDate ) )
	    endDt = this.endDate;
	return new Period( startDt, endDt );
    }

    /**
     * Returns the duration in milliseconds.
     *
     * @return duration in milliseconds.
     */
    public long getDuration() {
	long sTime = getStartDate().getTime();
	long eTime = getEndDate().getTime();
	return eTime - sTime;
    }

    /**
     * Tests if the period duration is 0 (meaning period is not defined).
     *
     * @return true if period duration is less or equal 0.
     */
    public boolean isUndefined() {
	return (getDuration() <= 0);
    }

    /**
     * Clears up starting and ending time. The time fields of the period will
     * updated so that the time of the start date will be set to 0:0:0 and 
     * end date will be set to 23:59:59.
     *
     */
    public void clearTime() {
	Calendar cal = Calendar.getInstance();
	cal.setTime( getStartDate() );
	cal.set( Calendar.HOUR_OF_DAY, 0 );
	cal.set( Calendar.MINUTE, 0 );
	cal.set( Calendar.SECOND, 0 );
	cal.set( Calendar.MILLISECOND, 0 );

	this.startDate = cal.getTime();

	cal.setTime( getEndDate() );
	cal.set( Calendar.HOUR_OF_DAY, 23 );
	cal.set( Calendar.MINUTE, 59 );
	cal.set( Calendar.SECOND, 59 );
	cal.set( Calendar.MILLISECOND, 999 );

	this.endDate = cal.getTime();
    }

    /**
     * Returns this period as array of <code>Timestamp</code> objects.
     *
     * @return an array of <code>Timestamp</code> objects.
     */
    public Timestamp[] toTimestamp() {
	Timestamp[] tsts = new Timestamp[2];
	tsts[0] = new Timestamp( startDate.getTime() );
	tsts[1] = new Timestamp( endDate.getTime() );
	return tsts;
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	StringBuilder stb = new StringBuilder();
	stb.append( startDate );
	stb.append( " - " );
	stb.append( endDate );
	return stb.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof Period ) {
	    Period f = (Period)obj;
	    return ( (f.getStartDate().equals(this.getStartDate())) && 
		     (f.getEndDate().equals(this.getEndDate())) );
	}
	return false;
    }

    /**
     * Get the <code>StartDate</code> value.
     *
     * @return a <code>Date</code> value
     */
    public final Date getStartDate() {
	return startDate;
    }

    /**
     * Set the <code>StartDate</code> value.
     *
     * @param startDate The new StartDate value.
     */
    public final void setStartDate(final Date startDate) {
	this.startDate = startDate;
    }

    /**
     * Get the <code>EndDate</code> value.
     *
     * @return a <code>Date</code> value
     */
    public final Date getEndDate() {
	return endDate;
    }

    /**
     * Set the <code>EndDate</code> value.
     *
     * @param endDate The new EndDate value.
     */
    public final void setEndDate(final Date endDate) {
	this.endDate = endDate;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	StringBuilder stb = new StringBuilder();
	stb.append( String.valueOf(startDate.getTime()) );
	stb.append( "-" );
	stb.append( String.valueOf(endDate.getTime()) );
	return stb.hashCode();
    }

}
