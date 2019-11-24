--
-- Creates the Biobank sample inventory database
--

--
-- t_sample
--   main sample entry
--
drop table if exists t_sample;
create table t_sample(
  sampleid   varchar(36) primary key,
  samplename varchar(50),
  typeid      bigint,
  stamp      bigint,
  trackid    bigint,
  created    timestamp
);
create index i_sam_sna on t_sample (samplename);
create index i_sam_sty on t_sample (typeid);
create index i_sam_tid on t_sample (trackid);

--
-- t_sample_type
--   sample type e.g. plasma, biopsy etc.
--
drop table if exists t_sample_type;
create table t_sample_type(
  typeid     bigint primary key,
  typename  varchar(80),
  created   timestamp
); 
create index i_sty_typ on t_sample_type (typename);

insert into t_sample_type (typeid, typename) values ( 1, 'unknown' );

--
-- t_sample_lookup
--   sample type lookup
--
drop table if exists t_sample_lookup;
create table t_sample_lookup(
  typekey   varchar(80) primary key,
  typename  varchar(80)
); 
create index i_stl_typ on t_sample_lookup (typename);

insert into t_sample_lookup values ( 'wbc', 'white blood cells' );
insert into t_sample_lookup values ( 'edta', 'plasma' );

insert into t_sample_lookup values ( 'dna', 'DNA' );
insert into t_sample_lookup values ( 'btdna', 'DNA' );
insert into t_sample_lookup values ( 'rna', 'rna' );
insert into t_sample_lookup values ( 'slide', 'FFPE Tissue Slide' );
insert into t_sample_lookup values ( 'slides', 'FFPE Tissue Slide' );
insert into t_sample_lookup values ( 'stained', 'FFPE Tissue Slide' );
insert into t_sample_lookup values ( 'unstained', 'FFPE Tissue Slide' );
insert into t_sample_lookup values ( 'block', 'FFPE Tissue Block' );
insert into t_sample_lookup values ( 'blocks', 'FFPE Tissue Block' );
insert into t_sample_lookup values ( 'biopsy', 'Biopsy' );
insert into t_sample_lookup values ( 'biopsies', 'Biopsy' );
insert into t_sample_lookup values ( 'whole blood', 'Whole Blood' );
insert into t_sample_lookup values ( 'blood', 'Whole Blood' );
insert into t_sample_lookup values ( 'plasma', 'Plasma' );
insert into t_sample_lookup values ( 'serum', 'Serum' );
insert into t_sample_lookup values ( 'urine', 'Urine' );
insert into t_sample_lookup values ( 'cell', 'Cells' );
insert into t_sample_lookup values ( 'circulating markers backup', 'Plasma' );
insert into t_sample_lookup values ( 'molecular markers', 'Plasma' );
insert into t_sample_lookup values ( 'molecular markers backup', 'Plasma' );
insert into t_sample_lookup values ( 'pk samples', 'Serum' );


--
-- t_sample_parent
--   sample parent - child relationship
--
drop table if exists t_sample_parent;
create table t_sample_parent(
  poid      varchar(36),
  coid      varchar(36),
  trackid   bigint
);
create index i_spa_soi on t_sample_parent (poid);
create index i_spa_cid on t_sample_parent (coid);
create index i_spa_tid on t_sample_parent (trackid);

--
-- t_sample_group
--   a grouping of samples e.g. pbmc samples
--
drop table if exists t_sample_group;
create table t_sample_group(
  groupid     bigint primary key,
  groupname   varchar(50),
  status      varchar(30)
);
create index i_sgr_gna on t_sample_group (groupname);

--
-- t_sample_groupmember
--   sample group membership (n:m), trackid is used to track the sample's group
--   membership. trackid is equivalent to a timestamp (nanosecs since 1.1.1970)
--
drop table if exists t_sample_groupmember;
create table t_sample_groupmember(
  sampleid        varchar(36),
  groupid     bigint,
  trackid     bigint
);
create index i_sgm_soi on t_sample_groupmember (sampleid);
create index i_sgm_gid on t_sample_groupmember (groupid);
create index i_sgm_tid on t_sample_groupmember (trackid);

--
-- t_sample_tracker
--   tracks changes to sample membership in groups, studies, accessions etc.
--   previd referes to the previous state's trackid
--   item describes the context (group, study etc.)
--   content holds an xml representation of the items to be tracked
--
drop table if exists t_sample_tracker;
create table t_sample_tracker(
  trackid   bigint primary key,
  modified  timestamp,
  previd    bigint,
  item      varchar(30),
  activity  varchar(50),
  uid       bigint,
  remark    varchar(254),
  content   text
); 
create index i_tra_pid on t_sample_tracker (previd);
create index i_tra_ite on t_sample_tracker (item);
create index i_tra_uid on t_sample_tracker (uid);

--
-- t_sample_user
--   sample user
--
drop table if exists t_sample_user;
create table t_sample_user(
  userid    bigint primary key,
  muid      varchar(20),
  username  varchar(80),
  apikey    varchar(20),
  email     varchar(80),
  active    varchar(5),
  roles     bigint,
  created   timestamp
);
create index i_sus_muid on t_sample_user (muid);
create index i_sus_una on t_sample_user (username);
create index i_sus_api on t_sample_user (apikey);
create index i_sus_ema on t_sample_user (email);

insert into t_sample_user values( 0, 'm01061', 'Oliver Karch', '220466', 'Oliver.K.Karch@merckgroup.com', 'true', 1, '2015-02-20 01:01:01' );
insert into t_sample_user values( 1, 'guest', 'Guest', '', 'bmdm@merckgroup.com', 'true', 2, '2015-02-20 01:01:01' );

--
-- t_sample_treatment
--   molecule the sample has been treated with
--
drop table if exists t_sample_treatment;
create table t_sample_treatment(
  treatid     bigint primary key,
  treatment   varchar(80),
  treatdesc   varchar(254)
);
create index i_tre_tre on t_sample_treatment (treatment);

insert into t_sample_treatment (treatid, treatment) values ( 0, 'Unknown' );
insert into t_sample_treatment (treatid, treatment) values ( 1, 'Untreated' );
insert into t_sample_treatment (treatid, treatment) values ( 2, 'DMSO' );
insert into t_sample_treatment (treatid, treatment) values ( 3, 'Stimulated' );
insert into t_sample_treatment (treatid, treatment) values ( 4, 'Treated' );
insert into t_sample_treatment (treatid, treatment) values ( 5, 'Collected' );
insert into t_sample_treatment (treatid, treatment) values ( 6, 'Packaged' );
insert into t_sample_treatment (treatid, treatment) values ( 7, 'Unpacked' );

--
-- t_sample_study
--   study / trial annotation
--
drop table if exists t_sample_study;
create table t_sample_study(
  studyid     bigint primary key,
  studyname   varchar(50),
  started     timestamp,
  expire      timestamp,
  status      varchar(30)
);
create index i_stu_nam on t_sample_study (studyname);
create index i_stu_exp on t_sample_study (expire);

--
-- t_sample_studymember
--   sample study association
--
drop table if exists t_sample_studymember;
create table t_sample_studymember(
  sampleid        varchar(36),
  studyid     bigint,
  trackid     bigint
);
create index i_ssm_soi on t_sample_studymember (sampleid);
create index i_ssm_ssi on t_sample_studymember (studyid);
create index i_ssm_tid on t_sample_studymember (trackid);

--
-- t_sample_accession
--   sample accession codes, barcodes, ids etc. assigned by other labs
--
drop table if exists t_sample_accession;
create table t_sample_accession(
  sampleid        varchar(36),
  accession   varchar(50),
  acctype     varchar(30),
  orgid       bigint,
  trackid     bigint
);
create index i_sac_soi on t_sample_accession (sampleid);
create index i_sac_acc on t_sample_accession (accession);
create index i_sac_aty on t_sample_accession (acctype);
create index i_sac_org on t_sample_accession (orgid);
create index i_sac_tid on t_sample_accession (trackid);

--
-- t_sample_organization
--   sample accession codes, barcodes, ids etc. assigned by other labs
--
drop table if exists t_sample_organization;
create table t_sample_organization(
  orgid       bigint primary key,
  orgname     varchar(50),
  siteid      varchar(20),
  countryid   smallint(5) unsigned,
  orgtype     varchar(50)
);
create index i_sor_nam on t_sample_organization (orgname);
create index i_sor_sit on t_sample_organization (siteid);
create index i_sor_cou on t_sample_organization (countyid);
create index i_sor_typ on t_sample_organization (orgtype);

insert into t_sample_organization (orgid,orgname,countryid,orgtype) values( 0, 'Unknown origin', 0, 'unknown' );
insert into t_sample_organization (orgid,orgname,countryid,orgtype) values( -2, 'Merck Biopharma', 0, 'sponsor' );

--
-- t_sample_subject
--   the origin of the sample, typically a patient
--
drop table if exists t_sample_subject;
create table t_sample_subject(
  donorid     bigint primary key,
  studyid     bigint,
  subjectid   varchar(50),
  species     varchar(30),
  taxon       bigint,
  orgid       bigint,
  age         int,
  gender      varchar(2),
  ethnicity   varchar(50),
  usubjid     varchar(60),
  enrolled    timestamp
);
create index i_sub_stu on t_sample_subject (studyid);
create index i_sub_sid on t_sample_subject (subjectid);
create index i_sub_tax on t_sample_subject (taxon);
create index i_sub_uid on t_sample_subject (usubjid);

--
-- t_sample_donor
--   associates the sample with a donor
-- 
drop table if exists t_sample_donor;
create table t_sample_donor(
  sampleid        varchar(36),
  donorid     bigint,
  trackid     bigint
);
create index i_sdo_sid on t_sample_donor (sampleid);
create index i_sdo_did on t_sample_donor (donorid);
create index i_sdo_tid on t_sample_donor (trackid);

--
-- t_sample_time
--   the timepoint the sample has been taken
--
drop table if exists t_sample_time;
create table t_sample_time(
  timeid      bigint primary key,
  orgid       bigint,
  visit       varchar(50),
  cycle      varchar(10),
  day        int,
  hour       float,
  dosage      varchar(50),
  quantity    float,
  unit        varchar(10)
);
create index i_sti_oid on t_sample_time (orgid);
create index i_sti_vis on t_sample_time (visit);

--
-- t_sample_processing
--   captures how the sample has been processed 
--   (optionally linked to a specific sampling timepoint)
--
drop table if exists t_sample_processing;
create table t_sample_processing(
  sampleid        varchar(36),
  treatid     bigint,
  timeid      bigint,
  step        int,
  processed   timestamp,
  trackid     bigint
);
create index i_spc_sid on t_sample_processing (sampleid);
create index i_spc_trd on t_sample_processing (treatid);
create index i_spc_tmd on t_sample_processing (timeid);
create index i_spc_pro on t_sample_processing (processed);
create index i_spc_tid on t_sample_processing (trackid);

--
-- t_sample_container
--   the container used to store the sample(s)
--
drop table if exists t_sample_container;
create table t_sample_container(
  containerid     bigint primary key,
  cname           varchar(50),
  ctype           varchar(20),
  capacity        float,
  unit            varchar(10)
);
create index i_cnt_nam on t_sample_container (cname);

--
-- t_sample_containment
--   the association of a sample to a container or a container to another one
--
drop table if exists t_sample_containment;
create table t_sample_containment(
  containerid     bigint,
  parentid        bigint,
  location        varchar(50),
  sampleid            varchar(36),
  trackid         bigint
);
create index i_cont_cid on t_sample_containment (containerid);
create index i_cont_sid on t_sample_containment (sampleid);
create index i_cont_pid on t_sample_containment (parentid);
create index i_cont_loc on t_sample_containment (location);
create index i_cont_tid on t_sample_containment (trackid);

--
-- t_sample_relocation
--   data on sample movement
--
drop table if exists t_sample_relocation;
create table t_sample_relocation(
  relocid       bigint primary key,
  fromorgid     bigint,
  toorgid       bigint,
  containerid   bigint,
  sampleid      varchar(36),
  sent          timestamp,
  received      timestamp,
  trackid       bigint
);
create index i_rel_for on t_sample_relocation (fromorgid);
create index i_rel_tor on t_sample_relocation (toorgid);
create index i_rel_con on t_sample_relocation (containerid);
create index i_rel_sid on t_sample_relocation (sampleid);
create index i_rel_tid on t_sample_relocation (trackid);

--
-- t_sample_tissue
--   tissue characteristics, the sample assignment and additional properties
--   are held in the property tables
--
drop table if exists t_sample_tissue;
create table t_sample_tissue(
   tissueid       bigint primary key,
   tissue         varchar(128)
);
create index i_tis_tissue on t_sample_tissue (tissue);

--
-- t_sample_condition
--   sample conditions, the sample assignment and additional properties
--   are held in the property tables
--
drop table if exists t_sample_condition;
create table t_sample_condition(
   condid        bigint primary key,
   specification varchar(50)
);
create index i_cond_cond on t_sample_condition (specification);


--
-- Legal and other restrictions related to sample usage
--

--
-- t_restrict_rule
--   a general rule
--
drop table if exists t_restrict_rule;
create table t_restrict_rule(
   restrictid   bigint primary key,
   rule         varchar(20),
   restriction  varchar(128),
   datatype     varchar(20),
   propertyid   bigint
);
create index i_rru_rule on t_restrict_rule (rule);
create index i_rru_res on t_restrict_rule (restriction);
create index i_rru_pid on t_restrict_rule (propertyid);

--
-- t_restrict_apply
--   a rule applied to study / site scope
--
drop table if exists t_restrict_apply;
create table t_restrict_apply(
  applyid         bigint primary key,
  restrictid      bigint,
  studyid         bigint,
  orgid           bigint,
  propertyid      bigint
);
create index i_rap_rid on t_restrict_apply (restrictid);
create index i_rap_sid on t_restrict_apply (studyid);
create index i_rap_oid on t_restrict_apply (orgid);
create index i_rap_vid on t_restrict_apply (propertyid);

--
-- t_restrict_site
--   a rule applicable in scope of a site
--
drop table if exists t_restrict_site;
create table t_restrict_site(
   siterestrictid bigint primary key,
   restrictid     bigint,
   orgid          bigint
);
create index i_rsi_rid on t_restrict_site (restrictid);
create index i_rsi_oid on t_restrict_site (orgid);

--
-- t_restrict_value
--   holds the actual realiation of the restriction
--   valuerestrictid is either a siterestrictid or a studyrestrictid
--
drop table if exists t_restrict_value;
create table t_restrict_value(
   valuerestrictid bigint,
   valueid         bigint
);
create index i_rsv_rid on t_restrict_value (valuerestrictid);
create index i_rsv_vid on t_restrict_value (valueid);

   
--
-- Storage provider related tables
--

--
-- t_storage_project
--   the project created by the storage provider
-- 
drop table if exists t_storage_project;
create table t_storage_project(
  projectid      bigint primary key,
  title          varchar(128),
  created        timestamp
);
create index i_prj_title on t_storage_project (title);
create index i_prj_created on t_storage_project (created);

--
-- t_storage_document
--   the project documentation related to the storage project
-- 
drop table if exists t_storage_document;
create table t_storage_document(
  documentid     bigint primary key,
  projectid      bigint,
  created        timestamp,
  filedate       timestamp,
  documentsize   bigint,
  mime           varchar(128),
  title          varchar(255),
  md5sum         varchar(32)
);
create index i_sdoc_pid on t_storage_document (projectid);
create index i_sdoc_upd on t_storage_document (created);
create index i_sdoc_tit on t_storage_document (title);
create index i_sdoc_md5 on t_storage_document (md5sum);

--
-- t_storage_group
--   the sample group of a project as defined by the storage provider
-- 
drop table if exists t_storage_group;
create table t_storage_group(
  groupid        bigint primary key,
  projectid      bigint,
  groupname      varchar(128),
  groupref       varchar(50)
);
create index i_sgrp_pid on t_storage_group (projectid);
create index i_sgrp_name on t_storage_group (groupname);

--
-- t_storage_sample
--   the sample group of a project as defined by the storage provider
-- 
drop table if exists t_storage_sample;
create table t_storage_sample(
  sampleid    varchar(36),
  groupid     bigint
);
create index i_ssamp_sid on t_storage_sample (sampleid);
create index i_ssamp_gid on t_storage_sample (groupid);

--
-- t_storage_billing
--   the project's billing information
-- 
drop table if exists t_storage_billing;
create table t_storage_billing(
  billid         bigint primary key,
  projectid      bigint,
  purchase       varchar(30),
  projectcode    varchar(50),
  currency       varchar(3),
  total          float
);
create index i_bill_pid on t_storage_billing (projectid);
create index i_bill_po on t_storage_billing (purchase);
create index i_bill_pcode on t_storage_billing (projectcode);

--
-- t_storage_invoice
--   invoice tracking table
-- 
drop table if exists t_storage_invoice;
create table t_storage_invoice(
  invoiceid      bigint primary key,
  purchase       varchar(30),
  invoice        varchar(30),
  started        timestamp,
  ended          timestamp,
  verified       timestamp,
  approved       timestamp,
  currency       varchar(3),
  numsamples     float,
  amount         float,
  rejected       timestamp,
  reason         varchar(250),
  created        timestamp
);
create index i_inv_pid on t_storage_invoice (purchase);
create index i_inv_inv on t_storage_invoice (invoice);
create index i_inv_start on t_storage_invoice (started);
create index i_inv_end on t_storage_invoice (ended);
create index i_inv_num on t_storage_invoice (numsamples);
create index i_inv_amt on t_storage_invoice (amount);

drop table if exists t_storage_cost;
create table t_storage_cost(
  costid         bigint primary key,
  region         varchar(10),
  servicegroup   varchar(80),
  serviceitem    varchar(100),
  unit           varchar(30),
  frequency      varchar(20),
  price          float,  
  currency       varchar(3)
);
create index i_cos_region on t_storage_cost (region);
create index i_cos_servicegroup on t_storage_cost (servicegroup);
create index i_cos_serviceitem on t_storage_cost (serviceitem);

drop table if exists t_storage_costestimate;
create table t_storage_costestimate(
  estimateid     bigint primary key,
  projectname    varchar(80),
  region         varchar(10),
  created        timestamp,
  duration       int,
  total          float
);
create index i_ces_projectname on t_storage_costestimate (projectname);

drop table if exists t_storage_cost_item;
create table t_storage_cost_item(
  costitemid     bigint primary key,
  estimateid     bigint,
  itemtype       varchar(80),
  costid         bigint,
  itemcount      bigint
);
create index i_cit_estimateid on t_storage_cost_item (estimateid);
create index i_cit_itemtype on t_storage_cost_item (itemtype);
create index i_cit_costid on t_storage_cost_item (costid);

drop table if exists t_storage_cost_preference;
create table t_storage_cost_preference(
  preferenceid   bigint primary key,
  typename       varchar(80),
  region         varchar(10),
  rank           int,
  costtype       varchar(20),
  costid         bigint
);
create index i_cop_typename on t_storage_cost_preference (typename);
create index i_cop_region on t_storage_cost_preference (region);
create index i_cop_rank on t_storage_cost_preference (rank);
create index i_cop_ctype on t_storage_cost_preference (costtype);
create index i_cop_costid on t_storage_cost_preference (costid);


--
-- Inventory related tables, e.g. upload of data sheets
--

--
-- t_inventory_template
--   the upload template
-- 
drop table if exists t_inventory_template;
create table t_inventory_template(
  templateid     bigint primary key,
  templatename   varchar(80),
  template       text
);
create index i_inv_tna on t_inventory_template (templatename);

--
-- t_inventory_upload
--   the upload (raw) content typically delimited text
-- 
drop table if exists t_inventory_upload;
create table t_inventory_upload(
  uploadid       bigint primary key,
  templateid     bigint,
  uploaded       timestamp,
  userid         bigint,
  md5sum         varchar(32),
  upload         text
);
create index i_upl_tid on t_inventory_upload (templateid);
create index i_upl_uid on t_inventory_upload (userid);
create index i_upl_md5 on t_inventory_upload (md5sum);

--
-- t_inventory_raw
--   the upload (raw) content typically delimited text which
--   was moved to the archival area in case of multiple time
--   upload
-- 
drop table if exists t_inventory_raw;
create table t_inventory_raw(
  md5sum         varchar(32) primary key,
  upload         text
);

drop table if exists t_inventory_log;
create table t_inventory_log(
  logid        bigint primary key, 
  uploadid     bigint, 
  logstamp     timestamp, 
  level        varchar(10), 
  line         bigint, 
  message      varchar(254) 
);
create index i_log_uid on t_inventory_log( uploadid );
create index i_log_lst on t_inventory_log( logstamp );
create index i_log_lev on t_inventory_log( level );
create index i_log_lin on t_inventory_log( line );

--
-- t_inventory_output
--   captures the output of the template
-- 
drop table if exists t_inventory_output;
create table t_inventory_output(
  documentid     bigint primary key,
  uploadid       bigint,
  created        timestamp,
  filedate       timestamp,
  documentsize   bigint,
  mime           varchar(128),
  title          varchar(255),
  md5sum         varchar(32)
);
create index i_upout_uid on t_inventory_output (uploadid);
create index i_upout_created on t_inventory_output (created);
create index i_upout_tit on t_storage_document (title);
create index i_upout_md5 on t_inventory_output (md5sum);


--
-- General property tables
--

--
-- t_property
--   holds property definitions
--   e.g. a property can be linked to additional properties (e.g. can act as a qualifier)
--   or it can be linked to a study context i.e. proptype=study etc.
--
drop table if exists t_property;
create table t_property( 
  propertyid   bigint primary key, 
  propertyname varchar(50), 
  label        varchar(80), 
  typeid       bigint,
  unit         varchar(20),
  parentid     bigint,
  trackid      bigint
);
create index i_prop_name on t_property (propertyname);
create index i_prop_tyid on t_property (typeid);
create index i_prop_trid on t_property (trackid);

--
-- t_property_type
--   represents the type of a property
--
drop table if exists t_property_type;
create table t_property_type( 
  typeid       bigint primary key, 
  typename     varchar(50), 
  label        varchar(80)
);
create index i_ptype_nam on t_property_type (typename);

--
-- basic parameter types
--
insert into t_property_type values( 0, 'unknown', 'Unknown' ); 
insert into t_property_type values( 1, 'text', 'Text' ); 
insert into t_property_type values( 2, 'number', 'Number' ); 
insert into t_property_type values( 3, 'date', 'Date' ); 
insert into t_property_type values( 4, 'string', 'String' ); 

--
-- specific list types
--
insert into t_property_type values( 5, 'header', 'Column header' ); 
insert into t_property_type values( 6, 'choice', 'Item list' ); 
insert into t_property_type values( 7, 'report', 'Reports' ); 

--
-- t_property_list
--   container to handles sets of properties
--
drop table if exists t_property_list;
create table t_property_list( 
  listid       bigint primary key,
  listname     varchar(80),
  typeid       bigint
);
create index i_plist_nam on t_property_list (listname);
create index i_plist_typ on t_property_list (typeid);

--
-- t_property_member
--   assigns a property to a list of properties
--
drop table if exists t_property_member;
create table t_property_member( 
  listid       bigint,
  propertyid   bigint,
  rank         int, 
  display      varchar(5)
);
create index i_pmem_lid on t_property_member (listid);
create index i_pmem_pid on t_property_member (propertyid);
create index i_pmem_rank on t_property_member (rank);

--
-- t_property_column
--   extended column properties of an attribute
--
drop table if exists t_property_column;
create table t_property_column( 
  columnid     bigint primary key,
  propertyid   bigint,
  dbformat     varchar(50),
  informat     varchar(254),
  outformat    varchar(254),
  columnsize   int,
  digits       int,
  minoccurs    int,
  maxoccurs    int,
  mandatory    varchar(5)
);
create index i_pcol_pid on t_property_column (propertyid);

--
-- t_property_value
--   a property's value
--
drop table if exists t_property_value;
create table t_property_value(
  valueid    bigint primary key,
  propertyid bigint,
  charvalue  varchar(254),
  numvalue   double,
  rank       int
);
create index i_spv_pid on t_property_value (propertyid);
create index i_spv_ran on t_property_value (rank);
create index i_spv_cva on t_property_value (charvalue);
create index i_spv_nva on t_property_value (numvalue);

--
-- t_sample_property
--   sample properties (name value pairs)
--   proptype provides information about the property's context
--
drop table if exists t_sample_property;
create table t_sample_property(
  sampleid       varchar(36),
  propertyid     bigint
);
create index i_spr_sid on t_sample_property (sampleid);
create index i_spr_pid on t_sample_property (propertyid);

--
-- t_donor_property
--   donor properties (name value pairs)
--
drop table if exists t_donor_property;
create table t_donor_property(
  donorid       bigint,
  propertyid    bigint
);
create index i_don_did on t_donor_property (donorid);
create index i_don_pid on t_donor_property (propertyid);

--
-- t_sample_report
--   sample details report
--
drop table if exists t_sample_report;
create table t_sample_report(
  sampleid       varchar(36) primary key,
  created        timestamp,
  details        text
);
create index i_sre_created on t_sample_report (created);


-- create index i_spr_iid on t_sample_property (instanceid);

--   proptype   varchar(20),
--   instanceid bigint,
--   propname   varchar(50),
--   unit       varchar(10)
-- ); 
-- create index i_spr_typ on t_sample_property (proptype);
-- create index i_spr_iid on t_sample_property (instanceid);

--
-- t_sample_property_value
--   a property's value
--
-- drop table if exists t_sample_property_value;
-- create table t_sample_property_value(
--   valid      bigint primary key,
--   propid     bigint,
--   sampleid       varchar(36),
--   charvalue  varchar(254),
--   numvalue   double,
--   rank       int
-- );
-- create index i_spv_pid on t_sample_property_value (propid);
-- create index i_spv_sid on t_sample_property_value (sampleid);
-- create index i_spv_ran on t_sample_property_value (rank);
-- create index i_spv_cva on t_sample_property_value (charvalue);
-- create index i_spv_nva on t_sample_property_value (numvalue);
