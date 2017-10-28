package com.emd.simbiom.rest;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <code>UserNotFoundException</code> signals an invalid user.
 *
 * Created: Tue Feb 10 12:50:19 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException( String userId ) {
	super("could not find user '" + userId + "'.");
    }

}

