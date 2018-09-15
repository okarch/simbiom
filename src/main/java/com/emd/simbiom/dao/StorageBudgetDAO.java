package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.StorageGroup;
import com.emd.simbiom.model.StorageProject;

import com.emd.util.Stringx;

/**
 * <code>StorageBudgetDAO</code> implements the <code>StorageBudget</code> API.
 *
 * Created: Thu Aug  9 21:40:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class StorageBudgetDAO extends BasicDAO implements StorageBudget {

    private static Log log = LogFactory.getLog(StorageBudgetDAO.class);

    private static final String STMT_PROJECT_BY_ID       = "biobank.project.findById";
    private static final String STMT_PROJECT_BY_NAME     = "biobank.project.findByTitle";
    private static final String STMT_PROJECT_INSERT      = "biobank.project.insert";
    private static final String STMT_PROJECT_UPDATE      = "biobank.project.update";

    private static final String STMT_GROUP_DELETE        = "biobank.group.deleteAll";
    private static final String STMT_GROUP_INSERT        = "biobank.group.insert";

    public static final String[] entityNames = new String[] {
	"project",
	"group",
	"invoice"
    };

    public StorageBudgetDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Creates a new storage project.
     *
     * @param project the project name (null or empty creates a new name based on the datetime).
     *
     * @return a newly created <code>StorageProject</code>
     */
    public StorageProject createStorageProject( String project ) 
	throws SQLException {

	StorageProject prj = new StorageProject();
	String pName = Stringx.getDefault( project, "").trim();
	if( pName.length() <= 0 )
	    pName = "Untitled "+Stringx.currentDateString("MMM dd, yyyy");
	prj.setTitle( pName );

	PreparedStatement pstmt = getStatement( STMT_PROJECT_INSERT );
	pstmt.setLong( 1, prj.getProjectid() );
	pstmt.setString( 2, prj.getTitle() );
	pstmt.setTimestamp( 3, prj.getCreated() );

     	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Storage project created: "+prj );

	return prj;
    }

    /**
     * Returns the storage project with the given id.
     *
     * @param projectId the project id.
     * @return the storage project or null (if not existing).
     */
    public StorageProject findStorageProjectById( long projectId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_PROJECT_BY_ID );
     	pstmt.setLong( 1, projectId );

     	ResultSet res = pstmt.executeQuery();

     	// List<CostItem> fl = new ArrayList<CostItem>();
	StorageProject project = null;
	while( res.next() ) {
	    if( project == null )
		project = (StorageProject)TableUtils.toObject( res, new StorageProject() );
	    StorageGroup sGrp = (StorageGroup)TableUtils.toObject( res, new StorageGroup() );
	    project.addStorageGroup( sGrp );
	}	       
	res.close();
	popStatement( pstmt );

	return project;
    }

    /**
     * Returns the storage projects matching the given name.
     *
     * @param projectTitle the project name which may include wildcards.
     * @return an potentially empty array of storage projects.
     */
    public StorageProject[] findStorageProject( String projectTitle ) throws SQLException {
 	PreparedStatement pstmt = getStatement( STMT_PROJECT_BY_NAME );
	String st = Stringx.getDefault(projectTitle,"").trim().toLowerCase();
	if( st.length() <= 0 )
	    st = "%";
     	pstmt.setString( 1, st );

     	ResultSet res = pstmt.executeQuery();

	List<StorageProject> fl = new ArrayList<StorageProject>();
	StorageProject lastProject = null;
	while( res.next() ) {
	    StorageProject project = (StorageProject)TableUtils.toObject( res, new StorageProject() );
	    if( (lastProject == null) || !(lastProject.equals(project)) ) {
		lastProject = project;
		fl.add( lastProject );
	    }
	    StorageGroup sGrp = (StorageGroup)TableUtils.toObject( res, new StorageGroup() );
	    lastProject.addStorageGroup( sGrp );
	}	       
	res.close();
	popStatement( pstmt );
	
	StorageProject[] projs = new StorageProject[ fl.size() ];
	return (StorageProject[])fl.toArray( projs );
    }
    
    /**
     * Stores the project including storage group.
     *
     * @param the project to store.
     * @return the stored projects.
     */
    public StorageProject storeStorageProject( StorageProject project ) throws SQLException {
	StorageProject prj = findStorageProjectById( project.getProjectid() ); 
	if( prj == null )
	    throw new SQLException( "Storgae project does not exist: "+project.getProjectid() );

	PreparedStatement pstmt = getStatement( STMT_PROJECT_UPDATE );
	pstmt.setLong( 3, project.getProjectid() );
	pstmt.setString( 1, project.getTitle() );
	pstmt.setTimestamp( 2, project.getCreated() );
     	pstmt.executeUpdate();
	popStatement( pstmt );

	// remove storage groups (if any)

	pstmt = getStatement( STMT_GROUP_DELETE );
	pstmt.setLong( 1, project.getProjectid() );
     	pstmt.executeUpdate();
	popStatement( pstmt );	

	log.debug( "Storage groups removed from project: "+project );

	// repopulate storage groups

	StorageGroup[] grps = project.getStorageGroups();
	pstmt = getStatement( STMT_GROUP_INSERT );
	for( int i = 0; i < grps.length; i++ ) {
	    pstmt.setLong( 1, grps[i].getGroupid() );
	    pstmt.setLong( 2, project.getProjectid() );
	    pstmt.setString( 3, grps[i].getGroupname() );
	    pstmt.setString( 4, grps[i].getGroupref() );
	    pstmt.executeUpdate();
	}
	popStatement( pstmt );	

	log.debug( "Storage project updated: "+project );

	return project;
    }

}
