package com.emd.simbiom.model;

import java.sql.Timestamp;

import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>SampleType</code> represents a sample type (e.g. blood, serum etc.).
 *
 * Created: Mon Feb  2 18:16:00 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class SampleType implements Copyable {
    private long      typeid;
    private String    typename;
    private Timestamp created;

    private static final String[] SAMPLE_TYPES = {
	"unknown",
	"serum",
	"tissue",
	"blood",
	"plasma"
     };

    /**
     * Creates a new <code>sampleType</code> object.
     */
    public SampleType() {
	this.typename = "";
	this.typeid = -1L;
	this.created = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Creates a sample type from the given type name.
     *
     * @param sType the type name.
     * @return a <code>SampleType</code> object.
     */
    public static SampleType getInstance( String sType ) {
	SampleType st = new SampleType();
	String stName = Stringx.getDefault(sType,"").trim().toLowerCase();
	st.setTypeid( DataHasher.hash( stName.getBytes() ) );
	st.setTypename( stName );
	return st;
    }

    /**
     * Maps a sample type to a known sample type.
     *
     * @param sType the type name.
     * @return a mapped sample type.
     */
    public static String mapType( String sType ) {
	if( (sType != null) && (sType.trim().length() > 0) ) {
	    for( int i = 0; i < SAMPLE_TYPES.length; i++ ) {
		String st = sType.trim().toLowerCase();
		if( st.indexOf(SAMPLE_TYPES[i]) >= 0 )
		    return SAMPLE_TYPES[i];
	    }
	}
	return SAMPLE_TYPES[0];
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
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new SampleType();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return Stringx.getDefault( getTypename(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof SampleType ) {
	    SampleType f = (SampleType)obj;
	    return (f.getTypename().equals( this.getTypename() ));
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return this.getTypename().hashCode();
    }

}
