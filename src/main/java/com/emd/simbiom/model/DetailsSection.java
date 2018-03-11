package com.emd.simbiom.model;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.emd.util.ClassUtils;
import com.emd.util.Stringx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Describe class DetailsSection here.
 *
 *
 * Created: Sun Feb  4 09:38:07 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class DetailsSection {
    private String sectionName;

    // private TreeSet<String> paths;
    private TreeMap<String,Properties> entries;

    private static Log log = LogFactory.getLog(DetailsSection.class);

    private final static String CDATA_START = "<![CDATA[";
    private final static String CDATA_END   = "]]>";

    public DetailsSection( String sectionName ) {
	this.sectionName = sectionName;
	// this.paths = new TreeSet<String>();
	this.entries = new TreeMap<String,Properties>();
    }

    
    // private String sortablePath( String path ) {
    // 	String pStr = path;
    // 	StringBuilder stb = new StringBuilder();
    // 	int k = 0;
    // 	int l = 0;
    // 	int nParts = 0;
    // 	while( (l < pStr.length()) && 
    // 	       ((k = pStr.indexOf( "[", l )) > l) ) {
    // 	    if( l > 0 ) {
    // 		stb.append( ";" );
    // 		nParts++;
    // 	    }
    // 	    stb.append( pStr.substring( l, k ) );
    // 	    l = k+1;
    // 	}

    // 	int sortn = 999 - nParts;
    // 	return String.valueOf(sortn)+";"+stb.toString();
    // }

    /**
     * Adds object properties to a section.
     *
     * @param path the access path.
     * @param obj the object to be represented.
     */
    public void addProperties( String path, Object obj ) {
	Properties props = null;
	if( obj != null ) { 
	    props = ClassUtils.toProperties( obj );
	    // paths.add( sortablePath(path) );
	}
	else
	    props = new Properties();
	entries.put( path, props );
    }

    /**
     * Get the <code>SectionName</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getSectionName() {
	return sectionName;
    }

    /**
     * Set the <code>SectionName</code> value.
     *
     * @param sectionName The new SectionName value.
     */
    public final void setSectionName(final String sectionName) {
	this.sectionName = sectionName;
    }

    private boolean containsMetaChars( String cont ) {
	if( cont.indexOf( CDATA_START ) >= 0 )
	    return false;
	int k = -1;
	if( (k = cont.indexOf( '&' )) >= 0 ) {
	    int l = -1;
	    if( (l = cont.indexOf(';',k)) < k )
		return true;
	    if( l-k > 4 )
		return true;
	}
	return (( cont.indexOf( '>' ) >= 0 ) ||
		( cont.indexOf( '<' ) >= 0 ));
    }

    private void renderProperties( StringBuilder stb, String path ) {
     	Properties props = entries.get( path );
     	if( props == null )
     	    return;
	
	Set<String> pNames = props.stringPropertyNames();
	for( String pName : pNames ) {
	    String pVal = props.getProperty( pName );
	    stb.append( "<property name=\"" );
	    stb.append( pName );
	    stb.append( "\"" );
	    if( pVal == null )
		stb.append( "/>\n" );
	    else {
		stb.append( ">" );
		boolean needsEsc = containsMetaChars( pVal );
		if( needsEsc )
		    stb.append( CDATA_START );
		stb.append( pVal );
		if( needsEsc )
		    stb.append( CDATA_END );
		stb.append( "</property>\n" );
	    }
	}
    }

    private void closeTag( StringBuilder stb, String[] lastPath, String[] currentPath ) {
	if( (lastPath == null) || (lastPath.length <= 0) ||
	    (lastPath.length < currentPath.length) )
	    return;

	// xxx0
	// xxx0/yyy0
	// xxx0/yyy1
	// xxx1

	for( int i = lastPath.length-1; i >= currentPath.length-1; i-- ) {
	    String lTag = Stringx.before( lastPath[i], "[" );	
	    log.debug( "Closing tag: "+lTag );
	    stb.append( "</" );
	    stb.append( lTag );
	    stb.append( ">\n" );	    
	}
    }

    private void closeAllTags( StringBuilder stb, String[] lastPath ) {
	if( (lastPath == null) || (lastPath.length <= 0) )
	    return;

	for( int i = lastPath.length-1; i >= 0; i-- ) {
	    String lTag = Stringx.before( lastPath[i], "[" );	
	    stb.append( "</" );
	    stb.append( lTag );
	    stb.append( ">\n" );	    
	}
    }

    private void openTag( StringBuilder stb, String[] currentPath ) {
	if( (currentPath == null) || (currentPath.length <= 0) )
	    return;
	String cTag = Stringx.before( currentPath[currentPath.length-1], "[" );
	stb.append( "<" );
	stb.append( cTag );
	stb.append( ">" );
    }
	
    /**
     * Returns an XML fragment from the current set of properties.
     *
     * @return a (potentially empty) XML fragment.
     */
    public String toXml() {
	StringBuilder stb = new StringBuilder();
	boolean sectionWritten = false;
	Set<String> paths = entries.keySet();
	if( paths.size() > 0 ) {
	    stb.append( "<" );
	    stb.append( getSectionName() );
	    stb.append( ">\n" );
	    sectionWritten = true;
	}
	String[] currentPath = null;
	String[] lastPath = null;
	for( String path : paths ) {
	    log.debug( "Current path: "+path );
	    currentPath = path.split( "/" );
	    closeTag( stb, lastPath, currentPath );
	    openTag( stb, currentPath );	    
	    renderProperties( stb, path );
	    lastPath = currentPath;
	}
	closeAllTags( stb, lastPath );
	if( sectionWritten ) {
	    stb.append( "</" );
	    stb.append( getSectionName() );
	    stb.append( ">\n" );
	    sectionWritten = true;
	}
	return stb.toString();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object obj) {
	if( obj instanceof DetailsSection ) {
	    DetailsSection f = (DetailsSection)obj;
	    return (f.getSectionName().equals( this.getSectionName() ));
	}
	return false;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
	return this.getSectionName().hashCode();
    }

	    
}
