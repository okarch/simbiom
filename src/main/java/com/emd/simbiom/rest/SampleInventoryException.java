package com.emd.simbiom.rest;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <code>SampleInventoryEception</code> signals invalid access to the database.
 *
 * Created: Tue Feb 10 12:50:19 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SampleInventoryException extends RuntimeException {

    public SampleInventoryException( Throwable t ) {
	super( t );
    }
    public SampleInventoryException( String msg ) {
	super( msg );
    }

}

