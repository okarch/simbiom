package com.emd.simbiom.model;

import java.io.File;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.emd.simbiom.dao.DocumentLoader;
import com.emd.simbiom.util.DataHasher;

import com.emd.util.Copyable;
import com.emd.util.Stringx;

/**
 * <code>StorageDocument</code> holds information about a storage related document. 
 *
 * Created: Tue Mar 26 18:21:04 2019
 *
 * @author <a href="mailto:okarch@deda1infr005.localdomain">Oliver</a>
 * @version 1.0
 */
public class StorageDocument implements Copyable {
    private long documentid;
    private long projectid;
    private long documentsize;

    private Timestamp created;
    private Timestamp filedate;

    private String title;
    private String mime;
    private String md5sum;

    private File file;

    private DocumentLoader documentLoader;

    /**
     * Creates a new storage document.
     */
    public StorageDocument() {
	this.setDocumentid( DataHasher.hash( UUID.randomUUID().toString().getBytes() ) );
	this.setCreated( new Timestamp( (new Date()).getTime() ));
	this.setProjectid( 0L );
    }

    /**
     * Creates a new <code>StorageDocument</code> from the given file.
     *
     * @param file the file.
     * @param md5 the md5sum calculated from the content.
     */ 
    public static StorageDocument fromFile( File file, String md5 ) {
	StorageDocument sDoc = new StorageDocument();
	sDoc.setMd5sum( md5 );
	sDoc.setTitle( file.getName() );
	sDoc.setFiledate( new Timestamp( file.lastModified() ) );
	sDoc.setDocumentsize( file.length() );
	sDoc.setFile( file );
	return sDoc;
    }

    /**
     * Get the <code>Documentid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getDocumentid() {
	return documentid;
    }

    /**
     * Set the <code>Documentid</code> value.
     *
     * @param documentid The new Documentid value.
     */
    public final void setDocumentid(final long documentid) {
	this.documentid = documentid;
    }

    /**
     * Get the <code>Projectid</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getProjectid() {
	return projectid;
    }

    /**
     * Set the <code>Projectid</code> value.
     *
     * @param projectid The new Projectid value.
     */
    public final void setProjectid(final long projectid) {
	this.projectid = projectid;
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
     * Get the <code>Filedate</code> value.
     *
     * @return a <code>Timestamp</code> value
     */
    public final Timestamp getFiledate() {
	return filedate;
    }

    /**
     * Set the <code>Filedate</code> value.
     *
     * @param filedate The new Filedate value.
     */
    public final void setFiledate(final Timestamp filedate) {
	this.filedate = filedate;
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
     * Get the <code>Documentsize</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getDocumentsize() {
	return documentsize;
    }

    /**
     * Set the <code>Documentsize</code> value.
     *
     * @param documentsize The new Documentsize value.
     */
    public final void setDocumentsize(final long documentsize) {
	this.documentsize = documentsize;
    }

    /**
     * Get the <code>Mime</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getMime() {
	return mime;
    }

    /**
     * Set the <code>Mime</code> value.
     *
     * @param mime The new Mime value.
     */
    public final void setMime(final String mime) {
	this.mime = mime;
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
     * Get the <code>File</code> value.
     *
     * @return a <code>File</code> value
     */
    public final File getFile() {
	return file;
    }

    /**
     * Set the <code>File</code> value.
     *
     * @param file The new File value.
     */
    public final void setFile(final File file) {
	this.file = file;
    }

    /**
     * Get the <code>DocumentLoader</code> value.
     *
     * @return a <code>DocumentLoader</code> value
     */
    public final DocumentLoader getDocumentLoader() {
	return documentLoader;
    }

    /**
     * Set the <code>DocumentLoader</code> value.
     *
     * @param documentLoader The new DocumentLoader value.
     */
    public final void setDocumentLoader(final DocumentLoader documentLoader) {
	this.documentLoader = documentLoader;
    }

    /**
     * Creates and returns a copy of this object. 
     *
     * @return a copy of the implementing object.
     */
    public Object copy() throws CloneNotSupportedException {
	return new StorageDocument();
    }

    /**
     * Returns a human readable string.
     *
     * @return the donor's name
     */
    public String toString() {
	return Stringx.getDefault( getTitle(), "" );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof StorageDocument ) {
	    StorageDocument f = (StorageDocument)obj;
	    return (f.getDocumentid() == this.getDocumentid() );
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return String.valueOf(this.getDocumentid()).hashCode();
    }

}
