package com.emd.simbiom.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
// import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
// import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.model.Organization;
import com.emd.simbiom.model.Property;
import com.emd.simbiom.model.PropertySet;
import com.emd.simbiom.model.PropertyType;
import com.emd.simbiom.model.Restriction;
import com.emd.simbiom.model.RestrictionRule;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.Study;

import com.emd.util.Stringx;

/**
 * <code>RestrictionManagementDAO</code> implements the <code>RestrictionManagement</code> API.
 *
 * Created: Wed Aug 22 20:50:31 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class RestrictionManagementDAO extends BasicDAO implements RestrictionManagement {

    private static Log log = LogFactory.getLog(RestrictionManagementDAO.class);

    private static final String STMT_RESTRICT_BY_ID      = "biobank.restrict.findById";
    private static final String STMT_RESTRICT_BY_RULE    = "biobank.restrict.findByRule";
    private static final String STMT_RESTRICT_BY_SAMPLE  = "biobank.restrictapply.findBySample";
    private static final String STMT_RESTRICT_BY_STUDY   = "biobank.restrictapply.findRestriction";
    private static final String STMT_RESTRICT_INSERT     = "biobank.restrict.insert";
    private static final String STMT_RESTRICT_UPDATE     = "biobank.restrict.update";

    private static final String STMT_RESTASSIGN_INSERT   = "biobank.restrictapply.insert";
    private static final String STMT_RESTASSIGN_UPDATE   = "biobank.restrictapply.update";

    private static final String TYPE_ITEM_LIST           = "choice";

    private static final long TYPE_NUMERIC               = 2L; // as defined in biobank.sql

    private static final String[] entityNames = new String[] {
	"restrict",
	"restrictapply"
    };


    public RestrictionManagementDAO( DatabaseDAO db ) {
	super( db );
    }

    public String[] getEntityNames() {
	return entityNames;
    }

    /**
     * Returns a restriction rule identified by the short rule name.
     *
     * @param rule the rule name.
     *
     * @return an array of <code>RestrictionRule</code> objects which can be empty.
     */
    public RestrictionRule[] findRestrictionRule( String rule ) throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_RESTRICT_BY_RULE );
     	pstmt.setString( 1, Stringx.getDefault(rule,"") );

     	ResultSet res = pstmt.executeQuery();
     	List<RestrictionRule> fl = new ArrayList<RestrictionRule>();
     	Iterator it = TableUtils.toObjects( res, new RestrictionRule() );
	RestrictionRule rr = null;
	while( it.hasNext() ) {
	    rr = (RestrictionRule)it.next();
	    fl.add( rr );
	    if( rr.getParentid() != 0 ) {
		PropertySet pSet = getDAO().findPropertySetById( rr.getParentid() );
		if( pSet != null )
		    rr.addChoices( pSet.getItems() );
	    }
	}	       
	res.close();
	popStatement( pstmt );

	RestrictionRule[] rules = new RestrictionRule[ fl.size() ];
	return (RestrictionRule[])fl.toArray( rules );
    }

    /**
     * Returns a restriction rule identified by id.
     *
     * @param ruleId the rule id.
     *
     * @return an array of <code>RestrictionRule</code> objects which can be empty.
     */
    public RestrictionRule findRestrictionRuleById( long ruleId ) throws SQLException {
	PreparedStatement pstmt = getStatement( STMT_RESTRICT_BY_ID );
     	pstmt.setLong( 1, ruleId );

     	ResultSet res = pstmt.executeQuery();
     	RestrictionRule rule = null;
     	if( res.next() ) 
     	    rule = (RestrictionRule)TableUtils.toObject( res, new RestrictionRule() );
     	res.close();
	popStatement( pstmt );
	if( (rule != null) && (rule.getParentid() != 0L) ) {
	    PropertySet pSet = getDAO().findPropertySetById( rule.getParentid() );
	    if( pSet != null )
		rule.addChoices( pSet.getItems() );
	}
	return rule;
    }

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
    public RestrictionRule createRestrictionRule( long userId, String ruleName, String type, String property ) throws SQLException {
	String rName = Stringx.getDefault( ruleName, "" ).trim();
	if( rName.length() <= 0 )
	    throw new SQLException( "Empty rule name cannot be created." );

	RestrictionRule[] rules = findRestrictionRule( rName );
	if( rules.length > 0 )
	    throw new SQLException( "Rule \""+Stringx.getDefault(rName,"")+"\" exists already." );

	String pName = Stringx.getDefault( property, "" ).trim();
	if( pName.length() <= 0 )
	    throw new SQLException( "Property name is invalid." );

	RestrictionRule rule = new RestrictionRule();
	rule.setRule( rName );
	rule.setRestriction( "" );
	rule.setDatatype( "" );

	String tName = Stringx.getDefault( type, "" ).trim().toLowerCase();
	if( tName.length() <= 0 )
	    tName = "unknown";

	PropertyType pType = getDAO().findTypeByName( tName );
	if( pType == null ) 
	    pType = getDAO().createType( tName, type );

	log.debug( "Type to be used: "+pType.getTypename() );
	
	Property[] props = getDAO().findPropertyByName( property, pType.getTypename() );
        log.debug( props.length+" existing properties for \""+property+"\" type: "+pType.getTypename() );
 
	Property prop = null;
	if( props.length <= 0 ) {
	    prop = new Property();
	    prop.setPropertyname( property );
	    prop.setLabel( property );
	    prop.setTypeid( pType.getTypeid() );
	    prop.setTypename( pType.getTypename() );
	    prop = getDAO().storeProperty( userId, prop );
	    log.debug( "Property stored: "+prop );
	}
	else {
	    prop = props[0];
	    log.debug( "Property used: "+prop );
	}
	rule.setPropertyid( prop.getPropertyid() );
	rule.setPropertyname( prop.getPropertyname() );
	rule.setLabel( prop.getLabel() );
	rule.setUnit( prop.getUnit() );
	rule.setTypeid( pType.getTypeid() );
	rule.setTypename( prop.getTypename() );
	rule.setParentid( prop.getParentid() );

	PreparedStatement pstmt = getStatement( STMT_RESTRICT_INSERT );

	pstmt.setLong( 1, rule.getRestrictid() );
	pstmt.setString( 2, rule.getRule() );
	pstmt.setString( 3, rule.getRestriction() );
	pstmt.setString( 4, rule.getDatatype() );
	pstmt.setLong( 5, rule.getPropertyid() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "New rule created: "+String.valueOf(rule.getRestrictid())+" "+rule.toString() );

	return rule;
    }

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
    public RestrictionRule storeRestrictionRule( long userId, RestrictionRule rule ) throws SQLException {
	RestrictionRule restr = findRestrictionRuleById( rule.getRestrictid() );
	if( restr == null )
	    throw new SQLException( "Rule "+String.valueOf(rule.getRestrictid())+" does not exist." );

	Property prop = getDAO().findPropertyById( rule.getPropertyid() );
	if( prop == null )
	    throw new SQLException( "Rule \""+rule.toString()+"\" property is invalid: "+String.valueOf(rule.getPropertyid()) );

	String pName = Stringx.getDefault( rule.getPropertyname(), prop.getPropertyname() ).trim();
	if( pName.length() <= 0 )
	    throw new SQLException( "Rule \""+rule.toString()+"\" property name is invalid: "+String.valueOf(rule.getPropertyid()) );

	String tName = Stringx.getDefault( rule.getTypename(), "" ).trim().toLowerCase();
	log.debug( "Rule "+rule.getRule()+" property: "+pName+" type created: "+tName );

	if( tName.length() <= 0 )
	    tName = "unknown";
	PropertyType pType = getDAO().findTypeByName( tName );
	if( pType == null )
	    pType = getDAO().createType( tName );

	prop.setPropertyname( pName );
	prop.setLabel( pName );
	prop.setTypeid( pType.getTypeid() );

	rule.setPropertyname( prop.getPropertyname() );
	rule.setLabel( prop.getLabel() );
	rule.setUnit( prop.getUnit() );
	rule.setTypeid( prop.getTypeid() );
	rule.setTypename( pType.getTypename() );
	rule.setParentid( prop.getParentid() );

	String[] items = rule.getChoices();

	long pSetId = rule.getParentid();
	PropertySet pSet = null;
	if( (pSetId == 0L) && (items.length > 0) ) {
	    String setName = rule.toString()+"_choices";
	    pSet = getDAO().createPropertySet( setName, TYPE_ITEM_LIST );
	    pSetId = pSet.getListid();
	}
	else if( pSetId != 0L ) {
	    pSet = getDAO().findPropertySetById( pSetId );
	}

	if( pSet != null ) {
	    pSet.clearProperties();
	    long tid = rule.getTypeid();
	    for( int i = 0; i < items.length; i++ ) {
		Property[] props = getDAO().findPropertyByName( items[i] );
		Property cProp = null;
		if( props.length <= 0 ) {
		    cProp = new Property();
		    cProp.setPropertyname( Stringx.strtrunc( items[i], 50) );
		    cProp.setLabel( Stringx.strtrunc(items[i], 80) );
		    cProp.setTypeid( tid );
		}
		else {
		    for( int j = 0; j < props.length; j++ ) {
			if( props[j].getTypeid() == tid ) {
			    cProp = props[j];
			    break;
			}
		    }
		    if( cProp == null )
			cProp = props[0];
		}
		pSet.addProperty( cProp );
	    }
	    pSet = getDAO().storePropertySet( userId, pSet );

	    rule.setParentid( pSet.getListid() );
	    prop.setParentid( pSet.getListid() );
	}	

	prop = getDAO().storeProperty( userId, prop );

	PreparedStatement pstmt = getStatement( STMT_RESTRICT_UPDATE );

	pstmt.setString( 1, rule.getRule() );
	pstmt.setString( 2, rule.getRestriction() );
	pstmt.setString( 3, rule.getDatatype() );
	pstmt.setLong( 4, rule.getPropertyid() );
	pstmt.setLong( 5, rule.getRestrictid() );

	pstmt.executeUpdate();
	popStatement( pstmt );

	log.debug( "Rule updated: "+String.valueOf(rule.getRestrictid())+" "+rule.toString() );

	return rule;
    }

    /**
     * Searches for a restriction Returns a restriction rule identified by id.
     *
     * @param ruleId the rule id.
     *
     * @return an array of <code>RestrictionRule</code> objects which can be empty.
     */
    public Restriction findRestriction( Study study, RestrictionRule rule, Organization site ) 
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_RESTRICT_BY_STUDY );
	long siteId = 0L;
	if( site != null ) 
	    siteId = site.getOrgid();
	
	pstmt.setLong( 1, rule.getRestrictid() );
	pstmt.setLong( 2, study.getStudyid() );
	pstmt.setLong( 3, siteId );

     	ResultSet res = pstmt.executeQuery();
	
     	Restriction restrict = null;
     	if( res.next() ) 
     	    restrict = (Restriction)TableUtils.toObject( res, new Restriction() );
     	res.close();
	popStatement( pstmt );

	if( restrict != null ) {
	    restrict.setRestrictionRule( findRestrictionRuleById( restrict.getRestrictid() ) );
	    restrict.setStudy( getDAO().findStudyById( restrict.getStudyid() ) );
	    if( restrict.getOrgid() != 0L )
		restrict.setOrganization( getDAO().findOrganizationById( restrict.getOrgid() ) );
	    restrict.setProperty( getDAO().findPropertyById( restrict.getPropertyid() ) );
	}
	return restrict;
    }
    
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
	throws SQLException {

	if( (study == null) || (rule == null)  || (restrictValue == null) )
	    throw new SQLException( "Cannot assign restriction as mandatory information is missing" );

	Restriction restrict = findRestriction( study, rule, site );
	boolean updRestrict = true;
	if( restrict == null ) {
	    restrict = new Restriction();
	    restrict.setRestrictid( rule.getRestrictid() );
	    restrict.setRestrictionRule( rule );
	    restrict.setStudyid( study.getStudyid() );
	    restrict.setStudy( study );
	    if( site != null ) {
		restrict.setOrgid( site.getOrgid() );
		restrict.setOrganization( site );
	    }
	    updRestrict = false;
	}

	log.debug( "Rule "+rule+" choice list: "+rule.getParentid() );

	PropertySet pSet = getDAO().findPropertySetById( rule.getParentid() );
	boolean valueAssigned = false;
	if( pSet != null ) {
	    Property[] props = pSet.getProperties( restrictValue );
	    if( props.length > 0 ) {
		restrict.setPropertyid( props[0].getPropertyid() );
		restrict.setProperty( props[0] );
		valueAssigned = true;
		log.debug( "Property label used: "+restrictValue );
	    }	    
	    else {
		log.warn( "Restriction value \""+restrictValue+"\" not found in list" );
	    }
	}
	else if( rule.getParentid() != 0L ) {
	    log.warn( "Choice list not found: "+rule.getParentid() );
	}

	if( !valueAssigned ) {
	    Property prop = getDAO().findPropertyById( rule.getPropertyid() );
	    if( prop == null )
		throw new SQLException( "Restriction property id invalid: "+rule.getPropertyid() );
	    restrict.setPropertyid( prop.getPropertyid() );
	    if( !prop.toString().equals( restrictValue ) ) {
		if( prop.getTypeid() == TYPE_NUMERIC ) {
		    restrict.setNumvalue( Stringx.toDouble( restrictValue, 0d ) );
		    prop = getDAO().assignPropertyValue( prop, restrict.getNumvalue() );
		}
		else {
		    restrict.setCharvalue( restrictValue );
		    prop = getDAO().assignPropertyValue( prop, restrict.getCharvalue() );
		}
		restrict.setProperty( prop );
	    }
	}

	int nn = 1;
	PreparedStatement pstmt = null;
	if( updRestrict ) {
	    pstmt = getStatement( STMT_RESTASSIGN_UPDATE );
	    pstmt.setLong( 5, restrict.getApplyid() );
	}
	else {
	    pstmt = getStatement( STMT_RESTASSIGN_INSERT );
	    pstmt.setLong( nn, restrict.getApplyid() );
	    nn++;
	}

	pstmt.setLong( nn, restrict.getRestrictid() );
	nn++;
	pstmt.setLong( nn, restrict.getStudyid() );
	nn++;
	pstmt.setLong( nn, restrict.getOrgid() );
	nn++;
	pstmt.setLong( nn, restrict.getPropertyid() );

	pstmt.executeUpdate();
	popStatement( pstmt );
	
	return restrict;
    }

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
	throws SQLException {

	return assignRestriction( study, rule, null, restrictValue );
    }

    /**
     * Returns all restrictions applicable to a sample.
     *
     * @param sample the sample.
     *
     * @return an array of <code>Restriction</code> objects which can be empty.
     */
    public Restriction[] findSampleRestriction( Sample sample ) 
	throws SQLException {

	PreparedStatement pstmt = getStatement( STMT_RESTRICT_BY_SAMPLE );
	// long siteId = 0L;
	// if( site != null ) 
	//     siteId = site.getOrgid();
	
	// pstmt.setLong( 1, study.getStudyid() );
	pstmt.setString( 1, sample.getSampleid() );

     	ResultSet res = pstmt.executeQuery();

     	List<Restriction> fl = new ArrayList<Restriction>();
     	Iterator it = TableUtils.toObjects( res, new Restriction() );
	while( it.hasNext() ) {
	    Restriction restrict = (Restriction)it.next();
	    restrict.setRestrictionRule( findRestrictionRuleById( restrict.getRestrictid() ) );
	    restrict.setStudy( getDAO().findStudyById( restrict.getStudyid() ) );
	    if( restrict.getOrgid() != 0L )
	 	restrict.setOrganization( getDAO().findOrganizationById( restrict.getOrgid() ) );
	    restrict.setProperty( getDAO().findPropertyById( restrict.getPropertyid() ) );
	    fl.add( restrict );
	}
	res.close();
	popStatement( pstmt );

	Restriction[] restricts = new Restriction[ fl.size() ];
	return (Restriction[])fl.toArray( restricts );
    }

}
