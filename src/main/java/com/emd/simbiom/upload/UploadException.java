package com.emd.simbiom.upload;

/**
 * <code>UploadException</code> signals exceptional states while processing
 * templates
 *
 * Created: Mon Feb 16 21:22:18 2012
 *
 * @author <a href="mailto:Oliver.Karch@merckgroup.com"></a>
 * @version 0.1
 */

public class UploadException extends Exception {

    public UploadException( String msg ) {
	super( msg );
    }
    
}
