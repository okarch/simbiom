package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Treatment;

/**
 * <code>TreatmentsDAO</code> implements the <code>Treatments</code> API.
 *
 * Created: Mon Aug 27 08:07:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface Treatments {

    /**
     * Returns a list of treatments.
     *
     * @param treatment the treatment to search.
     *
     * @return the Treatment objects.
     */
    public Treatment[] findTreatment( String treatment ) throws SQLException;

    /**
     * Creates a new treatment entry.
     *
     * @param treatment the treatment name.
     * @param desc the treatment description.
     *
     * @return the <code>Treatment</code> object.
     */
    public Treatment createTreatment( String treatment, String desc ) throws SQLException;

}
