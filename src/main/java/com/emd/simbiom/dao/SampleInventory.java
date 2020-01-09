package com.emd.simbiom.dao;

/**
 * Describe class SampleInventory here.
 *
 *
 * Created: Fri Aug 10 21:12:42 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface SampleInventory extends DataAccessCleaner,
					 StorageBudget, 
					 StorageCost, 
					 CountryLookup, 
					 PropertySets,
					 UserLookup,
					 UploadManagement,
					 Organizations,
					 Studies,
					 Treatments,
					 RestrictionManagement,
					 SampleProcesses,
					 Samples,
					 Participants,
					 Accessions,
					 SampleReports,
                                         Tasks {

}
