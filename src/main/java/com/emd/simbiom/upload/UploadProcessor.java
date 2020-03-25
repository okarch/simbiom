package com.emd.simbiom.upload;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import java.net.URL;

import java.sql.SQLException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
// import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.config.XmlFactoryConfiguration;

// import com.emd.simbiom.dao.SampleInventoryDAO;
import com.emd.simbiom.dao.InventoryFactory;
import com.emd.simbiom.dao.SampleInventory;

import com.emd.simbiom.job.InventoryJob;

import com.emd.simbiom.model.SampleType;
import com.emd.simbiom.model.Study;
import com.emd.simbiom.model.Subject;

import com.emd.simbiom.util.Period;

/**
 * <code>UploadProcessor</code> assembles the templates in a given directory 
 * and produces an aggregate document.
 *
 * @author <a href="mailto:Oliver.Karch@merck.de>Oliver Karch</a>
 *
 */
public class UploadProcessor {
    private ToolManager    toolManager;
    private VelocityEngine vEngine;

    private static UploadProcessor uploadProcessor;

    private static Log log = LogFactory.getLog(UploadProcessor.class);

    private static final String LOGGER_NAME      = "CONSOLE";
    private static final String TOOLS_CONFIG     = "tools.xml";

    private static final String CTXT_UPLOAD_BATCH = "upload";
    private static final String CTXT_INPUT        = "uploadContent";

    private UploadProcessor() {
    }

    /**
     * Creates or retrieves the current <code>UploadProcessor</code> instance.
     *
     * @return an <code>UploadProcessor</code> instance.
     */
    public static UploadProcessor getInstance() {
	if( uploadProcessor == null )
	    uploadProcessor = new UploadProcessor();
	return uploadProcessor;
    }

    private ToolManager getToolManager() {
	if( toolManager == null ) {
	    toolManager = new ToolManager( false, true );
	    try {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream( TOOLS_CONFIG );
		if( is != null ) {
		    XmlFactoryConfiguration cfg = new XmlFactoryConfiguration();
		    cfg.read( is );
		    is.close();
		    toolManager.configure( cfg );
		    log.debug( "Configured context tools from "+TOOLS_CONFIG );
		}
		else
		    log.warn( "Cannot configure tools" );
	    }
	    catch( IOException ioe ) {
		log.warn( "Cannot configure tools: "+((ioe.getMessage()!=null)?ioe.getMessage():"") );
	    }
	}
	return toolManager;
    }

    private Context createVelocityContext( UploadBatch upd, 
					   InventoryUploadTemplate templ,
					   Map context ) {
	ToolManager manager = getToolManager();
	if( manager == null )
	    return new VelocityContext( context );
	ToolContext tc = manager.createContext();

	// add additional tools, e.g. FileUtils
	tc.put( "files", FileUtils.class );
	tc.put( "strings", StringUtils.class );
	tc.put( "dates", DateUtils.class );
	tc.put( "dateFormats", DateFormatUtils.class );
	tc.put( "words", WordUtils.class );

	// specific tools
	tc.put( "samples", SampleType.class );
	tc.put( "subjects", Subject.class );
	tc.put( "studies", Study.class );
	tc.put( "periods", Period.class );
	tc.put( "systems", System.class );

	tc.put( "db", InventoryFactory.getInstance().getSampleInventory() );
	tc.put( "upload", upd );
	if( templ != null )
	    tc.put( "template", templ );

	// add additional context variables
	tc.putAll( context );

	return tc;
    }    

    // private Properties extractVelocityProperties( Map context ) {
    // 	Properties props = new Properties();
    // 	String rLoader = null;
    // 	if( (rLoader = (String)context.get( "resource.loader" )) == null )
    // 	    rLoader = "file";
    // 	props.put( "resource.loader", rLoader );
    // 	rLoader = rLoader + ".";
    // 	Iterator<Map.Entry> it = context.entrySet().iterator();
    // 	while( it.hasNext() ) {
    // 	    Map.Entry me = it.next();
    // 	    if( (me.getKey() != null) &&
    // 		(me.getKey().toString().startsWith( rLoader )) )
    // 		props.put( me.getKey().toString(), me.getValue() );
    // 	}
    // 	return props;
    // }

    // private VelocityEngine getVelocityEngine( Properties props ) 
    private VelocityEngine getVelocityEngine() 
	throws UploadException {

	if( vEngine == null ) {
	    // Properties vProps = extractVelocityProperties( props );

	    // log.debug( "Creating velocity engine using properties: "+vProps );

	    vEngine = new VelocityEngine();
 	    vEngine.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
 				 "org.apache.velocity.runtime.log.Log4JLogChute" );
 	    vEngine.setProperty("runtime.log.logsystem.log4j.logger",
 				LOGGER_NAME);
 	    vEngine.setProperty("directive.set.null.allowed", "true" );

	    log.debug( "Velocity engine logging setup done." );
	    try {
		// vEngine.init( vProps );
		vEngine.init();
		log.debug( "Velocity engine initialized." );
	    }
	    catch( Exception ex ) {
		log.error( ex );
		throw new UploadException( ex.getMessage() );
	    }
	}
	return vEngine;
    }

    // private Properties createVelocityProperties( File templDir, Map context ) {
    // 	Properties props = new Properties();

    // 	StringBuilder stb = new StringBuilder( System.getProperty( "user.dir" ) );
    // 	if( templDir.isFile() ) {
    // 	    File parent = templDir.getParentFile();
    // 	    if( parent != null ) {
    // 		stb.append( ", " );
    // 		stb.append( parent );
    // 	    }
    // 	}
    // 	else {
    // 	    stb.append( ", " );
    // 	    stb.append( templDir.getAbsoluteFile() );
    // 	}
	    
    // 	props.put("file.resource.loader.path", stb.toString() );
    // 	props.put("file.resource.loader.modificationCheckInterval", "120" );
    // 	props.put("file.resource.loader.cache", "true" );

    // 	if( context != null ) {
    // 	    String rLoader = null;
    // 	    if( (rLoader = (String)context.get( "resource.loader" )) == null )
    // 		rLoader = "file";
    // 	    props.put( "resource.loader", rLoader );
    // 	    props.putAll( context );
    // 	}
    // 	else
    // 	    props.put( "resource.loader", "file" );

    // 	return props;
    // }

    private String toTemplate( String tCont ) {
	return tCont.trim().replace( "\\n", "\n" );
    }

    /**
     * Process the template(s).
     *
     * @param templDir a directory or file where the template is located.
     * @param outDir a directory or file where the output should be written to. 
     * This can be null (current directory is used instead)
     * @param context a map of properties for use inside the velocity context.
     * @exception TemplateException signals abnormal behavior.
     */
    public void processUpload( InventoryUploadTemplate template, long uploadId, Map context ) 
	throws UploadException {

	UploadBatch upd = template.getUploadBatch( uploadId );
	if( upd == null )
	    throw new UploadException( "Cannot retrieve upload batch: "+uploadId );	

	VelocityEngine ve = getVelocityEngine();

	Context vc = createVelocityContext( upd, template, context );

	String tCont = toTemplate( template.getTemplate() );
	log.debug( "Template:\n"+tCont );

	try {
	    StringWriter sw = new StringWriter();
	    if( !ve.evaluate( vc, sw, template.getTemplatename(), tCont ) )
		log.error( "Cannot transform template "+template.getTemplatename() );
	    sw.flush();
	    String logCont = sw.toString();
	    sw.close();
	    log.debug( "Upload document produced:\n"+sw.toString() );
	}
	catch( Exception ex ) {
	    log.error( ex );
	    throw new UploadException( ex.getMessage() );
	}
    }

    private UploadBatch createUploadBatch( Map context ) {
	UploadBatch upd = (UploadBatch)context.get( CTXT_UPLOAD_BATCH );
	if( upd != null )
	    return upd;
	Object updInput = context.get( CTXT_INPUT );
	String updText = null;
	if(  updInput != null ) {
	    updText = updInput.toString();
	}
	upd = new UploadBatch();
	if( updText != null ) 
	    upd.setUpload( updText );

	return upd;
    }

    /**
     * Process the job template.
     *
     * @param templDir a directory or file where the template is located.
     * @param outDir a directory or file where the output should be written to. 
     * This can be null (current directory is used instead)
     * @param context a map of properties for use inside the velocity context.
     * @exception TemplateException signals abnormal behavior.
     */
    public void processJob( InventoryJob job, Map context ) 
	throws UploadException {

	UploadBatch upd = createUploadBatch( context );

	VelocityEngine ve = getVelocityEngine();
	Context vc = createVelocityContext( upd, null, context );

	String tCont = job.getJob();
	log.debug( "Template:\n"+tCont );
	String title = job.getJobtitle();

	try {
	    StringWriter sw = new StringWriter();
	    if( !ve.evaluate( vc, sw, title, tCont ) )
		log.error( "Cannot transform template "+title );
	    sw.flush();
	    String logCont = sw.toString();
	    sw.close();
	    log.debug( "Upload document produced:\n"+sw.toString() );
	}
	catch( Exception ex ) {
	    log.error( ex );
	    throw new UploadException( ex.getMessage() );
	}
    }

}
