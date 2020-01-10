package com.emd.simbiom.job;

import java.sql.Timestamp;

import java.util.UUID;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

import com.emd.simbiom.util.DataHasher;

/**
 * <code>InventoryJob</code> represents a job to be run by a task.
 *
 * Created: Sat Jan  4 11:33:14 2019
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class InventoryJob implements Copyable {
    private long jobid;
    private long taskid;
    private long userid;

    private String jobtitle;
    private String jobtype;
    private String job;

    private Timestamp created;
    private Timestamp modified;

    public static final String ANT      = "ant";
    public static final String VELOCITY = "velocity";


    /**
     * Creates a new scheduler task.
     */
    public InventoryJob() {
	this.setTaskid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.created = new Timestamp(System.currentTimeMillis());
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
     * Get the <code>Userid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getUserid() {
	return userid;
    }

    /**
     * Set the <code>Userid</code> value.
     *
     * @param userid The new Userid value.
     */
    public final void setUserid(final long userid) {
	this.userid = userid;
    }

    /**
     * Get the <code>Jobtitle</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getJobtitle() {
	return jobtitle;
    }

    /**
     * Set the <code>Jobtitle</code> value.
     *
     * @param jobtitle The new Jobtitle value.
     */
    public final void setJobtitle(final String jobtitle) {
	this.jobtitle = jobtitle;
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
     * Get the <code>Jobtype</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getJobtype() {
	return jobtype;
    }

    /**
     * Set the <code>Jobtype</code> value.
     *
     * @param jobtype The new Jobtype value.
     */
    public final void setJobtype(final String jobtype) {
	this.jobtype = jobtype;
    }

    /**
     * Get the <code>Job</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getJob() {
	return job;
    }

    /**
     * Set the <code>Job</code> value.
     *
     * @param job The new Job value.
     */
    public final void setJob(final String job) {
	this.job = job;
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
	return new InventoryJob();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getJobtitle(), String.valueOf(getJobid()) );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof InventoryJob ) {
	    InventoryJob f = (InventoryJob)obj;
	    return (f.getJobid() == this.getJobid());
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getJobid()).hashCode();
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
