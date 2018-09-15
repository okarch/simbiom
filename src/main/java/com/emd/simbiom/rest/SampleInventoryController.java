package com.emd.simbiom.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

// import com.emd.simbiom.dao.SampleInventoryDAO;
import com.emd.simbiom.dao.SampleInventory;
import com.emd.simbiom.dao.InventoryFactory;

import com.emd.simbiom.model.CostEstimate;
import com.emd.simbiom.model.CostSample;
import com.emd.simbiom.model.Roles;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleType;
import com.emd.simbiom.model.User;

import com.emd.simbiom.upload.InventoryUploadTemplate;
import com.emd.simbiom.upload.UploadBatch;
import com.emd.simbiom.upload.UploadException;
import com.emd.simbiom.upload.UploadProcessor;

import com.emd.util.Stringx;

/**
 * <code>SampleInventoryController</code> implements the REST controller of the sample inventory.
 *
 * Created: Fri Feb  6 17:49:26 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
@RestController
public class SampleInventoryController {

    private static Log log = LogFactory.getLog(SampleInventoryController.class);


    // public SampleInventoryController() {

    // }

    @RequestMapping("/sample")
    public Sample getSamples(@RequestParam(value="type", defaultValue="blood") String type) {
	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	SampleType sType = SampleType.getInstance( type );
        return Sample.getInstance( sType );
    }

    @RequestMapping("/sample/type")
    public SampleType[] getSampleTypes(@RequestParam(value="name", defaultValue="") String name) {
	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	
	try {
	    SampleType[] tList = sInv.findSampleTypeByNameAll( name );
	    log.debug( "Number of sample types matching \""+name+"\": "+tList.length );
	    return tList;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
    }

    @RequestMapping("/sample/type/map")
    public SampleType[] mapSampleTypes(@RequestParam(value="name", defaultValue="") String name) {
	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	
	try {
	    SampleType[] tList = sInv.mapSampleType( name );
	    log.debug( "Mapping sample type name \""+name+"\": "+tList.length+" entries found" );
	    return tList;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
    }

    @RequestMapping( value = "/sample/type/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public SampleType createSampleType(@RequestBody SampleType input) {
    // ResponseEntity<?> registerTemplate(@RequestBody InventoryUploadTemplate input) {

	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	try {
	    log.debug( "Creating sample type: "+input );
	    SampleType newT = sInv.createSampleType( input.getTypename() );
	    return newT;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
	    
        // return accountRepository.findByUsername(userId)
        //         .map(account -> {
        //                     Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

        //                     HttpHeaders httpHeaders = new HttpHeaders();

        //                     Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
        //                     httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

        //                     return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
        //                 }
        //         ).get();
    }

    @RequestMapping("/inventory/template")
    public InventoryUploadTemplate[] getUploadTemplates(@RequestParam(value="name", defaultValue="") String name) {
	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	
	try {
	    InventoryUploadTemplate[] tList = sInv.findTemplateByName( name );
	    log.debug( "Number of templates matching \""+name+"\": "+tList.length );
	    return tList;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
    }

    @RequestMapping( value = "/inventory/template/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public InventoryUploadTemplate registerTemplate(@RequestBody InventoryUploadTemplate input) {
    // ResponseEntity<?> registerTemplate(@RequestBody InventoryUploadTemplate input) {

	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();

	try {
	    log.debug( "Creating inventory upload template: "+input );
	    InventoryUploadTemplate newT = sInv.storeTemplate( input );
	    return newT;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
	    
        // return accountRepository.findByUsername(userId)
        //         .map(account -> {
        //                     Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

        //                     HttpHeaders httpHeaders = new HttpHeaders();

        //                     Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
        //                     httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

        //                     return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
        //                 }
        //         ).get();
    }

    @RequestMapping( value = "/inventory/template/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public InventoryUploadTemplate updateTemplate(@RequestBody InventoryUploadTemplate input) {

	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	try {
	    log.debug( "Updating inventory upload template: "+input );
	    InventoryUploadTemplate newT = sInv.updateTemplateByName( input );
	    return newT;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
	    
        // return accountRepository.findByUsername(userId)
        //         .map(account -> {
        //                     Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

        //                     HttpHeaders httpHeaders = new HttpHeaders();

        //                     Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");
        //                     httpHeaders.setLocation(URI.create(forOneBookmark.getHref()));

        //                     return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
        //                 }
        //         ).get();
    }

    @RequestMapping(value="/inventory/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
	return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value="/inventory/upload/{apiKey}/{templatename}", method=RequestMethod.POST)
    public InventoryUploadTemplate handleFileUpload( @PathVariable String apiKey,
						     @PathVariable String templatename, 
						     @RequestParam("file") MultipartFile file) {

	User user = validateApiKey( apiKey, Roles.INVENTORY_UPLOAD );

     	// find template

     	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
     	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
     	InventoryUploadTemplate templ = null;
     	try {
     	    InventoryUploadTemplate[] tList = sInv.findTemplateByName( templatename );
     	    if( tList.length <= 0 )
     		throw new SampleInventoryException( "Unknown template: \""+templatename+"\"" );
     	    templ = tList[0];
     	    log.info( "Start upload using template: "+templ.getTemplatename() );
	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	    throw new SampleInventoryException( sqe );
     	}

     	// upload the file

     	String updContent = null;
	if (!file.isEmpty()) {
	    try {
     		File tempUpload = File.createTempFile( "simbiom", null );
     		log.debug( "Temporary file created: "+tempUpload );
     		InputStream ins = file.getInputStream(); 
     		BufferedOutputStream outs = new BufferedOutputStream(new FileOutputStream(tempUpload));
     		IOUtils.copy( ins, outs );
     		ins.close();
     		outs.close();

     		updContent = FileUtils.readFileToString( tempUpload );
	    } 
     	    catch (Exception e) {
     		log.error( e );
		throw new SampleInventoryException( "Upload failed: "+Stringx.getDefault( e.getMessage(), "unknown reason" ) );
	    }
	} 
     	else {
     	    throw new SampleInventoryException( "Upload failed: empty file" );
	}

     	// register upload with the template

     	UploadBatch uBatch = new UploadBatch();
     	uBatch.setTemplateid( templ.getTemplateid() );
     	if( updContent != null )
     	    uBatch.setUpload( updContent );
     	uBatch.setUploaded( new Timestamp(System.currentTimeMillis()) );
     	uBatch.setUserid( 0L );

     	templ.addUploadBatch( uBatch );

     	try {
     	    templ = sInv.storeTemplate( templ );
     	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	    throw new SampleInventoryException( sqe );
     	}

     	log.info( "Upload batch registered: "+uBatch.getUploadid()+" content length: "+updContent.length() );
	
     	// launch upload process

     	UploadProcessor uProcessor = UploadProcessor.getInstance();
     	try {

	    Map ctxt = new HashMap();
	    ctxt.put( "user", user );

     	    uProcessor.processUpload( templ, uBatch.getUploadid(), ctxt );
     	}
     	catch( UploadException sqe ) {
     	    log.error( sqe );
     	    throw new SampleInventoryException( sqe );
     	}
	
     	return templ;
    }

    @RequestMapping("/cost/sample")
    public CostSample[] getSampleCosts(@RequestParam(value="type", defaultValue="") String type ) {
	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	
	try {
	    CostSample[] tList = sInv.findCostBySampleType( type );
	    log.debug( "Number of cost types matching \""+type+"\": "+tList.length );
	    return tList;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
    }

    @RequestMapping("/cost/estimate/create")
    public CostEstimate getCostEstimate(@RequestParam(value="project", defaultValue="") String project ) {
	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	
	try {
	    CostEstimate estimate = sInv.createCostEstimate( project );
	    log.debug( "Estimate created: "+estimate );
	    return estimate;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
    }

    @RequestMapping( value = "/cost/estimate/add/{estimateId}/{itemCount}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public CostEstimate addCostItem( @PathVariable long estimateId,
				     @PathVariable long itemCount, 
				     @RequestBody CostSample item ) {

	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	try {
	    CostEstimate estimate = sInv.addCostItem( estimateId, item, itemCount );
	    log.debug( "Added costs to estimate. Update: "+estimate );
	    return estimate;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
    }

    @RequestMapping( value = "/cost/estimate/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE )
    public CostEstimate updateCostEstimate(@RequestBody CostEstimate input) {
	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	try {
	    CostEstimate estimate = sInv.updateCostEstimate( input );
	    log.debug( "Estimate updated: "+estimate );
	    return estimate;
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    throw new SampleInventoryException( sqe );
	}
    }

    private void validateUser(String userId) {
	
	throw new UserNotFoundException(userId);
    }

    private User validateApiKey( String apiKey, long reqRole ) {
     	// SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
     	SampleInventory sInv = InventoryFactory.getInstance().getSampleInventory();
	User user = null;
     	try {
	    user = sInv.findUserByApikey( apiKey );	    
     	}
     	catch( SQLException sqe ) {
     	    log.error( sqe );
     	}

	if( user == null )
	    throw new UserNotFoundException( "Cannot access user information" );

	if( !user.hasRole( reqRole ) )
	    throw new UserNotFoundException( "User "+user+" requires "+Roles.roleToString( reqRole ) );
	return user;
    }

}
