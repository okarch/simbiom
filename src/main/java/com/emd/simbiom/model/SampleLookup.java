package com.emd.simbiom.model;


import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * Describe class SampleLookup here.
 *
 *
 * Created: Sat Apr 28 18:39:13 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class SampleLookup {
    private String typename;
    private String typekey;

    public SampleLookup() {
	this.typekey = "";
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
     * Get the <code>Typekey</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getTypekey() {
	return typekey;
    }

    /**
     * Set the <code>Typekey</code> value.
     *
     * @param typekey The new Typekey value.
     */
    public final void setTypekey(final String typekey) {
	this.typekey = typekey;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof SampleLookup ) {
	    SampleLookup f = (SampleLookup)obj;
	    return (f.getTypekey().equals( this.getTypekey() ));
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return this.getTypekey().hashCode();
    }

}
