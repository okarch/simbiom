package com.emd.simbiom.model;

import java.math.BigInteger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.emd.util.Copyable;
import com.emd.util.Stringx;
// import com.emd.util.ZipCoder;

/**
 * <code>DocumentContent</code> holds the zipcoded content as it is stored in the datamodel.
 *
 * Created: Mon Apr  1 18:12:45 2019
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class DocumentContent implements Copyable {
    private String md5sum;
    private String upload;

    public DocumentContent() {
	this.md5sum = "";
    }

    /**
     * Get the <code>Upload</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getUpload() {
	return upload;
    }

    /**
     * Set the <code>Upload</code> value.
     *
     * @param upload The new Upload value.
     */
    public final void setUpload(final String upload) {
	this.upload = upload;
    }

    /**
     * Get the <code>Md5sum</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getMd5sum() {
	return md5sum;
    }

    /**
     * Set the <code>Md5sum</code> value.
     *
     * @param md5sum The new Md5sum value.
     */
    public final void setMd5sum(final String md5sum) {
	this.md5sum = md5sum;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new DocumentContent();
    }

    /**
     * Returns a human readable string.
     *
     * @return the facet's name
     */
    public String toString() {
	return getMd5sum();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof DocumentContent ) {
	    DocumentContent f = (DocumentContent)obj;
	    return (f.getMd5sum().equals(this.getMd5sum() ));
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return getMd5sum().hashCode();
    }

}
