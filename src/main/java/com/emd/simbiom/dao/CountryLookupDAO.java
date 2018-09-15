package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Country;

import com.emd.util.Stringx;


/**
 * <code>CountryLookupDAO</code> implements the <code>CountryLookup</code> API.
 *
 * Created: Thu Aug 21 07:40:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class CountryLookupDAO extends BasicDAO implements CountryLookup {

    private static Log log = LogFactory.getLog(CountryLookupDAO.class);

    private static final String STMT_COUNTRY_BY_ID       = "biobank.country.findById";
    private static final String STMT_COUNTRY_BY_NAME     = "biobank.country.findByName";


    public CountryLookupDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return new String[0];
    }

    /**
     * Returns a country entry by name.
     *
     * @param countryName the name of country.
     *
     * @return an (potentially empty) array of <code>Country</code> objects.
     */
    public Country[] findCountryByName( String countryName ) throws SQLException {
	String cName = Stringx.getDefault( countryName, "" ).trim();
	if( cName.length() <= 0 )
	    cName = "%";
	
	PreparedStatement pstmt = getStatement( STMT_COUNTRY_BY_NAME );
     	pstmt.setString( 1, cName.toLowerCase()+"%" );

     	ResultSet res = pstmt.executeQuery();

     	List<Country> fl = new ArrayList<Country>();
     	Iterator it = TableUtils.toObjects( res, new Country() );
	Country rr = null;
	while( it.hasNext() ) {
	    rr = (Country)it.next();
	    fl.add( rr );
	}	       
	res.close();
	popStatement( pstmt );

	Country[] countries = new Country[ fl.size() ];
	return (Country[])fl.toArray( countries );
    }

    /**
     * Returns the country entry by id.
     *
     * @param countryId the id of the country.
     *
     * @return the <code>Country</code> object (or null).
     */
    public Country findCountryById( int countryId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_COUNTRY_BY_ID );
	pstmt.setInt( 1, countryId );
	ResultSet res = pstmt.executeQuery();
	Country sType = null;
	if( res.next() ) 
	    sType = (Country)TableUtils.toObject( res, new Country() ); 
     	res.close();
	popStatement( pstmt );
	return sType;
    }

}
