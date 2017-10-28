package com.emd.simbiom.feeds;

import java.math.BigInteger;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Sample;

import com.emd.util.Stringx;

/**
 * <code>FeedContent</code> represents feed content items.
 *
 * Created: Sun Apr 26 15:02:55 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class FeedContent {
    private long typeid;
    private long studyid;

    private Date published;

    // private int  week;
    // private int  year;
    private int  numSamples;

    private String typename;
    private String studyname;
    private String link;

    public FeedContent( Sample sample ) {
	this.typeid = sample.getTypeid();
	this.studyid = sample.getStudyid();
	this.published = sample.getCreated();
	if( published == null )
	    published = new Date(1L);
	// Calendar cal = Calendar.getInstance().setTime(published);
	// this.week = cal.get( Calendar.WEEK_OF_YEAR );
	// this.year = cal.get( Calendar.YEAR );
	this.numSamples = 1;
    }

    /**
     * Get the <code>Typeid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTypeid() {
	return typeid;
    }

    /**
     * Set the <code>Typeid</code> value.
     *
     * @param typeid The new Typeid value.
     */
    public final void setTypeid(final long typeid) {
	this.typeid = typeid;
    }

    /**
     * Get the <code>Typename</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTypename() {
	return typename;
    }

    /**
     * Set the <code>Typename</code> value.
     *
     * @param typename The new Typename value.
     */
    public final void setTypename(final String typename) {
	this.typename = typename;
    }

    /**
     * Get the <code>Studyid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getStudyid() {
	return studyid;
    }

    /**
     * Set the <code>Studyid</code> value.
     *
     * @param studyid The new Studyid value.
     */
    public final void setStudyid(final long studyid) {
	this.studyid = studyid;
    }

    /**
     * Get the <code>Studyname</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getStudyname() {
	return studyname;
    }

    /**
     * Set the <code>Studyname</code> value.
     *
     * @param studyname The new Studyname value.
     */
    public final void setStudyname(final String studyname) {
	this.studyname = studyname;
    }

    /**
     * Increases the number of samples for the given type.
     *
     * @param sample The sample.
     */
    public final void addSample(final Sample sample) {
	numSamples++;
	if( published.before( sample.getCreated() ) )
	    published = sample.getCreated();	
    }

    /**
     * Get the <code>Published</code> value.
     *
     * @return a <code>Date</code> value
     */
    public final Date getPublished() {
	return published;
    }

    /**
     * Set the <code>Published</code> value.
     *
     * @param published The new Published value.
     */
    public final void setPublished(final Date published) {
	this.published = published;
    }

    /**
     * Get the <code>Week</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getWeek() {
	Calendar cal = Calendar.getInstance();
	cal.setTime(published);
	return cal.get( Calendar.WEEK_OF_YEAR );
    }

    /**
     * Get the <code>Year</code> value.
     *
     * @return an <code>int</code> value
     */
    public final int getYear() {
	Calendar cal = Calendar.getInstance();
	cal.setTime(published);
	return cal.get( Calendar.YEAR );
    }

    /**
     * Get the <code>Link</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getLink() {
	return link;
    }

    /**
     * Set the <code>Link</code> value.
     *
     * @param link The new Link value.
     */
    public final void setLink(final String link) {
	this.link = link;
    }

    /**
     * Get the id of this content.
     *
     * @return an <code>int</code> value
     */
    public final int getId() {
	StringBuilder stb = new StringBuilder();
	stb.append( String.valueOf(getWeek()) );
	stb.append( String.valueOf(getYear()).substring(2) );
	String stId = String.valueOf( Math.abs(getStudyid()) );
	if( stId.length() > 3 )
	    stb.append( stId.substring( stId.length()-3 ) );
	else
	    stb.append( stId );
	try {
	    BigInteger intVal = new BigInteger(stb.toString());
	    return intVal.intValue();
	}
	catch( NumberFormatException nfe ) {
	}
	return 0;
    }

    /**
     * Returns a feed item title.
     *
     * @return the feed item's title.
     */
    public String getTitle() {
	StringBuilder stb = new StringBuilder();
	stb.append( "Study " );
	stb.append( Stringx.getDefault( getStudyname(), ((getStudyid()>0)?String.format("unknown (%l)",getStudyid()):"not assigned") ) );
	stb.append( " " );
	stb.append( String.valueOf( numSamples ) );
	stb.append( " " );
	stb.append( Stringx.getDefault( getTypename(), "" ) );
	stb.append( " samples" );

	return stb.toString();
    }

    /**
     * Returns a feed item title.
     *
     * @return the feed item's title.
     */
    public String getSummary() {
	StringBuilder stb = new StringBuilder();
	stb.append( String.valueOf( numSamples ) );
	stb.append( " " );
	stb.append( Stringx.getDefault( getTypename(), "" ) );
	stb.append( String.format(" samples have been checked-in at week %d, %d",getWeek(),getYear()) );
	stb.append( " - " );
	stb.append( Stringx.getDefault(getLink(),"") );

	return stb.toString();
    }

}
