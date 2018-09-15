package com.emd.simbiom.dao;

import java.sql.SQLException;

import com.emd.simbiom.model.Organization;
import com.emd.simbiom.model.Restriction;
import com.emd.simbiom.model.RestrictionRule;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.Study;

/**
 * <code>RestrictionManagement</code> specifies restriction management.
 *
 * Created: Wed Aug 22 21:10:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public interface RestrictionManagement {

    /**
     * Returns a restriction rule identified by the short rule name.
     *
     * @param rule the rule name.
     *
     * @return an array of <code>RestrictionRule</code> objects which can be empty.
     */
    public RestrictionRule[] findRestrictionRule( String rule ) throws SQLException;

    /**
     * Returns a restriction rule identified by id.
     *
     * @param ruleId the rule id.
     *
     * @return an array of <code>RestrictionRule</code> objects which can be empty.
     */
    public RestrictionRule findRestrictionRuleById( long ruleId ) throws SQLException;

    /**
     * Creates a new restriction rule with the given name.
     *
     * @param userId the user creating the restriction rule.
     * @param ruleName the rule name.
     * @param type the rule type.
     * @param property the referencing property name.
     *
     * @return a newly created <code>RestrictionRule</code> object.
     *
     * @exception SQLException in case of a duplicate rule has been found.
     */
    public RestrictionRule createRestrictionRule( long userId, String ruleName, String type, String property ) throws SQLException;

    /**
     * Strores the restriction rule.
     *
     * @param userId the user creating the restriction rule.
     * @param rule the rule.
     *
     * @return the updated <code>RestrictionRule</code> object.
     *
     * @exception SQLException in case rule is not existing.
     */
    public RestrictionRule storeRestrictionRule( long userId, RestrictionRule rule ) throws SQLException;

    /**
     * Searches for a restriction Returns a restriction rule identified by id.
     *
     * @param ruleId the rule id.
     *
     * @return an array of <code>RestrictionRule</code> objects which can be empty.
     */
    public Restriction findRestriction( Study study, RestrictionRule rule, Organization site ) 
	throws SQLException;
    
    /**
     * Assigns a restriction realization to a study (and optionally to a site)
     *
     * @param study the study.
     * @param rule the rule.
     * @param site the organization (study site), can be null.
     * @param restrictValue the restriction value (realization).
     *
     * @return the restriction assigned.
     */
    public Restriction assignRestriction( Study study, 
					  RestrictionRule rule, 
					  Organization site,
					  String restrictValue )
	throws SQLException;

    /**
     * Assigns a restriction realization to a study.
     *
     * @param study the study.
     * @param rule the rule.
     * @param restrictValue the restriction value (realization).
     *
     * @return the restriction assigned.
     */
    public Restriction assignRestriction( Study study, 
					  RestrictionRule rule, 
					  String restrictValue )
	throws SQLException;

    /**
     * Returns all restrictions applicable to a sample.
     *
     * @param sample the sample.
     *
     * @return an array of <code>Restriction</code> objects which can be empty.
     */
    public Restriction[] findSampleRestriction( Sample sample ) 
	throws SQLException;

}
