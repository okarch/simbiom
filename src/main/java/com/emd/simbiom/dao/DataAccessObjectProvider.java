package com.emd.simbiom.dao;

/**
 * Describe interface DataAccessObjectProvider here.
 *
 *
 * Created: Wed Aug 22 19:55:55 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface DataAccessObjectProvider {

    /**
     * Returns an instance of the <code>SampleInventory</code> API.
     *
     * @return the API instance.
     */
    public SampleInventory getSampleInventory();
}
