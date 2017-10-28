package com.emd.simbiom.dao;

import java.io.InputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.SQLException;
import java.sql.Timestamp;

import javax.json.Json;
import javax.json.stream.JsonParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Sample;

import com.emd.util.Stringx;

/**
 * <code>FullContentSearch</code> provides full content search functionality based on solr.
 *
 * Created: Fri Jun 19 12:06:51 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class FullContentSearch {
    private String url;

    private static final Map<String,FullContentSearch> contentServers = new HashMap<String,FullContentSearch>();

    private static Log log = LogFactory.getLog(FullContentSearch.class);

    private static final String DEFAULT_URL = "http://localhost:7272/solr/biobank";

    private static final int MAX_PAGES = 20;   // 20000 samples emergency break

    /**
     * Creates a new module for full content search.
     */
    public FullContentSearch() {
    }

    /**
     * Retrieves a unique content search server per server name.
     *
     * @param server the server name.
     * @return the allocated search module.
     */
    public synchronized static FullContentSearch getInstance( String server ) {
	FullContentSearch fcs = contentServers.get( server );
	if( fcs == null ) {
	    fcs = new FullContentSearch();
	    contentServers.put( server, fcs );
	}
	return fcs;
    }

    private URL createQuery( String query, int page ) throws SQLException {
	int start = page * 100;
	String urlSt = String.format( "%s/select?q=%s&wt=json&start=%d&rows=100", 
				      Stringx.getDefault(getUrl(),DEFAULT_URL), 
				      query,
				      start );
	URL qUrl = null;
	try {
	    qUrl = new URL( urlSt );
	}
	catch( MalformedURLException mfex ) {
	    log.error( mfex );
	    throw new SQLException( "Invalid query: "+urlSt );
	}
	return qUrl;
    }

// calls to the method next() result in parse events at the specified locations below (marked in bold):

//  {START_OBJECT
//    "firstName"KEY_NAME: "John"VALUE_STRING, "lastName"KEY_NAME: "Smith"VALUE_STRING, "age"KEY_NAME: 25VALUE_NUMBER,
//    "phoneNumber"KEY_NAME : [START_ARRAY
//        {START_OBJECT "type"KEY_NAME: "home"VALUE_STRING, "number"KEY_NAME: "212 555-1234"VALUE_STRING }END_OBJECT,
//        {START_OBJECT "type"KEY_NAME: "fax"VALUE_STRING, "number"KEY_NAME: "646 555-4567"VALUE_STRING }END_OBJECT
//     ]END_ARRAY
//  }END_OBJECT

 // {"category":"blood","id":"001e665f-cd8d-4f1c-8cd5-445ef4af748f","last_modified":"2015-05-15T18:18:20Z","name":"blood 2015-05-15 06:18:20","orgtype":["central laboratory","clinical site"],"organization":["Covance","EMR62242-004 study site 0202"],"acctype":["primary"],"accession":["Y343030"],"expire":["2025-05-12T18:17:37Z"],"started":["2015-05-15T18:17:37Z"],"title":["EMR62242-004"],"species":["human"],"subjectid":["02021007"],"taxon":["9606"],"processed":["2011-10-07T00:00:00Z"],"treatment":["Collected"],"site":["0202"],"_version_":1505196887462903808}

    private String unfold( Map<String,List<String>> props, String pName, String def ) {
	List<String> vals = props.get( pName );
	if( (vals == null) || (vals.size() <= 0) )
	    return def;
	String val = vals.get(0);
	return ((val != null)?val:def);
    }
    private long unfoldLong( Map<String,List<String>> props, String pName, long def ) {
	List<String> vals = props.get( pName );
	if( (vals == null) || (vals.size() <= 0) )
	    return def;
	String val = vals.get(0);
	return Stringx.toLong( val, def );
    }
    private long unfoldDatetime( Map<String,List<String>> props, String pName, long def ) {
	List<String> vals = props.get( pName );
	if( (vals == null) || (vals.size() <= 0) )
	    return def;
	String val = vals.get(0);
	long dt = Stringx.parseDate( val, "yyyy-MM-dd'T'HH:mm:ss'Z'" );
	return ((dt >= 0L)?dt:def);
    }

    /**
     * Query the samples and related entities.
     *
     * @param query the query string.
     * @return a list of samples associated with the query.
     */
    public List<Sample> querySamples( String query ) throws SQLException {

	int page = 0;
	boolean finished = false;

	List<Sample> samples = new ArrayList<Sample>();

	do {
	    if( page > MAX_PAGES )
		break;

	    URL qURL = createQuery( query, page );
	    log.debug( "Querying "+qURL );

	    int docCount = 0;

	    try {
		InputStream ins = qURL.openStream();

		JsonParser parser = Json.createParser(ins);
		log.debug( "Parser created: "+parser );

		boolean inDocSection = false;
		boolean inDocument = false;
		Sample sample = null;
		Map<String,List<String>> properties = new HashMap<String,List<String>>();
		List vals = null;
		while( parser.hasNext() ) {
		    JsonParser.Event e = parser.next();
		    if( inDocSection ) {
			if( e == JsonParser.Event.END_OBJECT ) {
			    if( (sample != null) && (vals != null) && (vals.size() > 0) ) {
				// log.debug( "Sample properties: "+properties );

				String val = unfold( properties, "id", null );
				if( val != null )
				    sample.setSampleid( val );
				val = unfold( properties, "name", null );
				if( val != null )
				    sample.setSamplename( val );

				long tid = unfoldLong( properties, "typeid", 0L );
				sample.setTypeid( tid );

				tid = unfoldLong( properties, "stamp", 0L );
				sample.setStamp( tid );

				tid = unfoldLong( properties, "studyid", 0L );
				sample.setStudyid( tid );

				tid = unfoldDatetime( properties, "last_modified", 1L );
				sample.setCreated( new Timestamp( tid ) );

				samples.add( sample );

				docCount++;
				sample = null;
			    }
			    inDocument = false;
			    continue;
			}
			else if( e == JsonParser.Event.START_OBJECT ) {
			    inDocument = true;
			    sample = new Sample();
			    properties.clear();
			    continue;
			}
		    }
		    else if( (e == JsonParser.Event.KEY_NAME) && (parser.getString().equals("docs")) ) {
			parser.next();
			inDocSection = true;
			docCount = 0;
			continue;
		    }
		    if( (inDocument) && (e == JsonParser.Event.KEY_NAME) ) {
			String lastProp = parser.getString();
			vals = new ArrayList<String>();
			properties.put( lastProp, vals );
		    }
		    else if( (inDocument) && 
			     ((e == JsonParser.Event.VALUE_STRING) || 
			      (e == JsonParser.Event.VALUE_NUMBER)) ) {
			vals.add( parser.getString() );
		    }
		}

		ins.close();
	    }
	    catch( IOException ioe ) {
		log.error( ioe );
		throw new SQLException( "I/O exception occured: "+Stringx.getDefault(ioe.getMessage(),"") );
	    }
	    if( docCount < 100 )
		finished = true;
	    else
		page++;
	}
	while( !finished );

	return samples;
    }

    /**
     * Get the <code>Url</code> value.
     *
     * @return a <code>String</code> value
     */
    public final String getUrl() {
	return url;
    }

    /**
     * Set the <code>Url</code> value.
     *
     * @param url The new Url value.
     */
    public final void setUrl(final String url) {
	this.url = url;
    }

}
