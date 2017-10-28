package com.emd.simbiom.model;

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

    private static final String[] roleNames = {
	"Super user",
	"Viewing sample entries",
	"Upload to inventory"
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
    
}
