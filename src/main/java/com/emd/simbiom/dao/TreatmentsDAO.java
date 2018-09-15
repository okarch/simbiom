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
public class TreatmentsDAO extends BasicDAO implements Treatments {

    private static Log log = LogFactory.getLog(TreatmentsDAO.class);

    private static final String STMT_TREAT_BY_NAME       = "biobank.treatment.findByName";
    private static final String STMT_TREAT_INSERT        = "biobank.treatment.insert";

    private static final String[] entityNames = new String[] {
	"treatment"
    };

    public TreatmentsDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns a list of treatments.
     *
     * @param treatment the treatment to search.
     *
     * @return the Treatment objects.
     */
    public Treatment[] findTreatment( String treatment ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_TREAT_BY_NAME );
	pstmt.setString( 1, treatment );
	pstmt.setString( 2, treatment );

     	ResultSet res = pstmt.executeQuery();
     	List<Treatment> fl = new ArrayList<Treatment>();
     	Iterator it = TableUtils.toObjects( res, new Treatment() );
	while( it.hasNext() ) {
	    fl.add( (Treatment)it.next() );
	}	       
	res.close();
	popStatement( pstmt );

     	Treatment[] facs = new Treatment[ fl.size() ];
     	return (Treatment[])fl.toArray( facs );	
    }

    /**
     * Creates a new treatment entry.
     *
     * @param treatment the treatment name.
     * @param desc the treatment description.
     *
     * @return the <code>Treatment</code> object.
     */
    public Treatment createTreatment( String treatment, String desc ) throws SQLException {
	Treatment treat = new Treatment();
	treat.setTreatment( treatment );
	treat.setTreatdesc( desc );

 	PreparedStatement pstmt = getStatement( STMT_TREAT_INSERT );
	pstmt.setLong( 1, treat.getTreatid() );
	pstmt.setString( 2, treat.getTreatment() );
	pstmt.setString( 3, treat.getTreatdesc() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	return treat;
    }

}
