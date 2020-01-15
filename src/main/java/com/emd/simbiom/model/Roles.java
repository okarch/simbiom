package com.emd.simbiom.model;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>Role</code> represents application specific roles encoded by a 64 bit value.
 *
 * Created: Sat Feb 21 10:24:14 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Roles {

    public static final long SUPER_USER       = 1L;
    public static final long SAMPLE_VIEW      = 2L;
    public static final long INVENTORY_UPLOAD = 4L;
    public static final long STORAGE_EDIT     = 8L;
    public static final long INVOICE_EDIT     = 16L;
    public static final long INVENTORY_REPORT = 32L;
    public static final long TASK_SCHEDULE    = 64L;
    
    private static final String[] roleNames = {
	"Super user",
	"View sample entries",
	"Upload to inventory",
	"Specify storage projects",
	"Handle invoices",
	"Inventory reports",
	"Schedule tasks"
    };

    private Roles() { }

    /**
     * Returns a human readable string.
     *
     * @return the role's name
     */
    public static String roleToString( long role ) {
	if( role == 0 )
	    return "Unknown";
	int idx = (int)Math.floor( (Math.log((double)role) / Math.log( 2D ) ) );
	return roleNames[ idx ];
    }

    /**
     * Returns all available roles in a human readble array of strings.
     *
     * @param role the roles encoded.
     * @return an (potentially empty) array of human readable roles.
     */
    public static String[] assignedRoles( long role ) {
	List<String> rList = new ArrayList<String>();
	for( int i = 0; i < roleNames.length; i++ ) {
	    long rIdx = (long)Math.floor(Math.pow( 2D, (double)i ));
	    if( (role & rIdx) == rIdx )
		rList.add( roleToString( rIdx ) );
	}
	String[] roles = new String[ rList.size() ];
	return (String[])rList.toArray( roles );
    }
    
}
