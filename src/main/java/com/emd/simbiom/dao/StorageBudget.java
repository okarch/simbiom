package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.StorageProject;

/**
 * <code>StorageBudget</code> specifies the API for budget operations.
 *
 * Created: Thu Aug  9 21:18:19 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface StorageBudget {

    /**
     * Creates a new storage project.
     *
     * @param project the project name (null or empty creates a new name based on the datetime).
     *
     * @return a newly created <code>StorageProject</code>
     */
    public StorageProject createStorageProject( String project ) 
	throws SQLException;

    /**
     * Returns the storage project with the given id.
     *
     * @param projectId the project id.
     * @return the storage project or null (if not existing).
     */
    public StorageProject findStorageProjectById( long projectId ) 
	throws SQLException;

    /**
     * Returns the storage projects matching the given name.
     *
     * @param projectTitle the project name which may include wildcards.
     * @return an potentially empty array of storage projects.
     */
    public StorageProject[] findStorageProject( String projectTitle ) 
	throws SQLException;

    /**
     * Stores the project including storage group.
     *
     * @param the project to store.
     * @return the stored projects.
     */
    public StorageProject storeStorageProject( StorageProject project ) 
	throws SQLException;

}
