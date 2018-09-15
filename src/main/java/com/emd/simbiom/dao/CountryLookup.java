package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Country;


/**
 * <code>CountryLookup</code> specifies access to country information.
 *
 * Created: Thu Aug 21 17:40:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface CountryLookup {

    /**
     * Returns a country entry by name.
     *
     * @param countryName the name of country.
     *
     * @return an (potentially empty) array of <code>Country</code> objects.
     */
    public Country[] findCountryByName( String countryName ) throws SQLException;

    /**
     * Returns the country entry by id.
     *
     * @param countryId the id of the country.
     *
     * @return the <code>Country</code> object (or null).
     */
    public Country findCountryById( int countryId ) throws SQLException;
}
