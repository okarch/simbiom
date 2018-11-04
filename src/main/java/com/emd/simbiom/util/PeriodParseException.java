package com.emd.simbiom.util;

/**
 * <code>PeriodParseException</code> signals exceptional states while parsing periods.
 *
 * Created: Mon Oct  8 13:22:18 2018
 *
 * @author <a href="mailto:Oliver.Karch@merckgroup.com"></a>
 * @version 0.1
 */

public class PeriodParseException extends Exception {

    public PeriodParseException( String msg ) {
	super( msg );
    }
    
}
