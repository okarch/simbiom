package com.emd.simbiom.job;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

import com.emd.simbiom.util.DataHasher;

/**
 * <code>InventoryTask</code> represents a scheduler task.
 *
 * Created: Sun Dec 29 09:13:14 2019
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class InventoryTask implements Copyable {
    private long taskid;
    private long jobid;

    private String title;
    private String eventtype;
    private String event;
    private String status;

    private Timestamp created;
    private Timestamp modified;
    private Timestamp expired;

    private List<InventoryJob> jobs;

    /**
     * Creates a new scheduler task.
     */
    public InventoryTask() {
	this.setTaskid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.created = new Timestamp(System.currentTimeMillis());
	this.setEventtype( "cron" ); 
	this.setStatus( "enabled" );
	this.jobs = new ArrayList<InventoryJob>();
    }

    /**
     * Get the <code>Taskid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getTaskid() {
	return taskid;
    }

    /**
     * Set the <code>Taskid</code> value.
     *
     * @param taskid The new Taskid value.
     */
    public final void setTaskid(final long taskid) {
	this.taskid = taskid;
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
     * Get the <code>Modified</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getModified() {
	return modified;
    }

    /**
     * Set the <code>Modified</code> value.
     *
     * @param modified The new Modified value.
     */
    public final void setModified(final Timestamp modified) {
	this.modified = modified;
    }

    /**
     * Get the <code>Eventtype</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getEventtype() {
	return eventtype;
    }

    /**
     * Set the <code>Eventtype</code> value.
     *
     * @param eventtype The new Eventtype value.
     */
    public final void setEventtype(final String eventtype) {
	this.eventtype = eventtype;
    }

    /**
     * Get the <code>Event</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getEvent() {
	return event;
    }

    /**
     * Set the <code>Event</code> value.
     *
     * @param event The new Event value.
     */
    public final void setEvent(final String event) {
	this.event = event;
    }

    /**
     * Get the <code>Expired</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getExpired() {
	return expired;
    }

    /**
     * Set the <code>Expired</code> value.
     *
     * @param expired The new Expired value.
     */
    public final void setExpired(final Timestamp expired) {
	this.expired = expired;
    }

    /**
     * Get the Created value.
     * @return the Created value.
     */
    public Timestamp getCreated() {
	return created;
    }

    /**
     * Set the Created value.
     * @param newCreated The new Created value.
     */
    public void setCreated(Timestamp newCreated) {
	this.created = newCreated;
    }

    /**
     * Get the <code>Jobid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getJobid() {
	return jobid;
    }

    /**
     * Set the <code>Jobid</code> value.
     *
     * @param jobid The new Jobid value.
     */
    public final void setJobid(final long jobid) {
	this.jobid = jobid;
    }

    /**
     * Get the <code>Status</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getStatus() {
	return status;
    }

    /**
     * Set the <code>Status</code> value.
     *
     * @param status The new Status value.
     */
    public final void setStatus(final String status) {
	this.status = status;
    }

    /**
     * Adds a new job to this scheduled task.
     *
     * @param the job.
     * @return the newly added job.
     */
    public InventoryJob addJob( InventoryJob job ) {
	job.setTaskid( getTaskid() );
	this.jobs.add( job );
	return job;
    }

    /**
     * Creates a sample from the given sample type.
     *
     * @param sType the sample type.
     * @return a <code>Sample</code> object.
     */
    // public static Sample getInstance( SampleType sType ) {
    // 	Sample st = new Sample();
    // 	st.setTypeid( sType.getTypeid() );
    // 	st.setSamplename( Stringx.getDefault(sType.getTypename(),"unknown")+
    // 			  " "+Stringx.getDateString( "yyyy-MM-dd hh:mm:ss", st.getCreated() ) );
    // 	return st;
    // }

    /**
     * Updates the trackid to reflect the current content.
     *
     * @return the updated trackid
     */
    // public long updateTrackid() {
    // 	long contId = contentId();
    // 	setTrackid( contId );
    // 	long stmp = getStamp();
    // 	stmp++;
    // 	setStamp( stmp );
    // 	return contId;
    // }

    // protected long contentId() {
    // 	StringBuilder stb = new StringBuilder();
    // 	stb.append( Stringx.getDefault( getSampleid(), "" ) );
    // 	stb.append( Stringx.getDefault(getSamplename(), "" ) );
    // 	stb.append( String.valueOf( getTypeid() ) );
    // 	stb.append( String.valueOf( getStamp() ) );
    // 	stb.append( String.valueOf( getCreated().getTime() ) );

    // 	// calculate hash from properties

    // 	stb.append( String.valueOf(getPropertyContentId() ) );

    // 	return DataHasher.hash( stb.toString().getBytes() );
    // }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new InventoryTask();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getTitle(), 
				   Stringx.getDefault( getEvent(), String.valueOf(getTaskid()) ) );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof InventoryTask ) {
	    InventoryTask f = (InventoryTask)obj;
	    return (f.getTaskid() == this.getTaskid());
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getTaskid()).hashCode();
    }

    /**
     * Compares this object with the specified object for order. 
     * Returns a negative integer, zero, or a positive integer as this object is less 
     * than, equal to, or greater than the specified object. 
     *
     */
    // public int compareTo( Object o) {
    // 	return getSampleid().compareTo( ((Sample)o).getSampleid() );
    // }
}
