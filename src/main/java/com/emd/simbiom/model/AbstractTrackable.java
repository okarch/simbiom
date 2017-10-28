package com.emd.simbiom.model;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;

import com.emd.util.ClassUtils;

/**
 * <code>AbstractTrackable</code> specifies basic functionality of items which are trackable.
 *
 * Created: Fri Feb  6 09:16:16 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0 
 */
public class AbstractTrackable implements Trackable {
    private long trackid;
    private String item;

    private static final String ROOT_TAG      = "<rowset>";
    private static final String ROOT_END      = "</rowset>";
    private static final String ROW_TAG       = "<row>";
    private static final String ROW_END       = "</row>";

    protected AbstractTrackable( String iType ) {
	this.item = iType;
    }

    /**
     * Get the <code>Trackid</code> value.
     *
     * @return a <code>long</code> value
     */
    public long getTrackid() {
	return trackid;
    }

    /**
     * Set the <code>Trackid</code> value.
     *
     * @param trackid The new Trackid value.
     */
    public final void setTrackid(final long trackid) {
	this.trackid = trackid;
    }

    /**
     * Get the <code>Item</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getItem() {
	return item;
    }

    /**
     * Set the <code>Item</code> value.
     *
     * @param item The new Item value.
     */
    public final void setItem(final String item) {
     	this.item = item;
    }

    /**
     * Get the xml <code>Content</code> value.
     *
     * @return a <code>String</code> value
     */
    public String toContent() {
	Properties props = ClassUtils.toProperties( this );
	StringBuilder stb = new StringBuilder( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
	stb.append( ROOT_TAG );
	stb.append( '\n' );
	stb.append( ROW_TAG );
	stb.append( '\n' );

	Iterator it = props.keySet().iterator();
	while( it.hasNext() ) {
	    Object key = it.next();
	    if( (key != null) && (key.toString().trim().length() > 0) ) {
		Object val = props.get( key );
		stb.append( "<" );
		stb.append( key.toString() );
		stb.append( ">" );
		if( val != null )
		    stb.append( StringEscapeUtils.escapeXml(val.toString()) );
		stb.append( "</" );
		stb.append( key.toString() );
		stb.append( ">\n" );
	    }
	}
	    
	stb.append( ROW_END );
	stb.append( '\n' );
	stb.append( ROOT_END );

	return stb.toString();
    }

}
