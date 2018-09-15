package com.emd.simbiom.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>InventoryFactory</code> creates a new sample inventory data access.
 *
 * Created: Tue Aug 14 19:41:18 2018
 *
 * @author <a href="mailto:okarch@linux">Oliver</a>
 * @version 1.0
 */
public class InventoryFactory implements InvocationHandler, DataAccessObjectProvider {
    private DatabaseDAO database;
    private Map<String,BasicDAO> dataObjects;
    private SampleInventory sampleInventory;
    private DataAccessObject genericDAO;

    private static InventoryFactory inventoryFactory;

    private static Log log = LogFactory.getLog(InventoryFactory.class);

    public InventoryFactory() {
	this.dataObjects = new HashMap<String,BasicDAO>();
	this.sampleInventory = null;
    }

    /**
     * Returns the instance of the InventoryFactory
     * @return an instance of InventoryFactory. 
     */
    public static InventoryFactory getInstance() {
	if( inventoryFactory == null )
	    inventoryFactory = new InventoryFactory();
	return inventoryFactory;
    }

    private synchronized void registerDAOs() {
	dataObjects.clear();
	List sClasses = ClassUtils.getAllInterfaces( SampleInventory.class );
	log.debug( "Number of interfaces: "+sClasses.size() );
	DatabaseDAO db = getDatabase();
	for( Object clazz : sClasses ) {
	    log.debug( "Interface object: "+clazz );
	    if( (clazz != null) && (clazz instanceof Class) ) {
		String daoName = ((Class)clazz).getName()+"DAO";
		try {
		    Class daoClazz = Class.forName( daoName );
		    Constructor cons = daoClazz.getConstructor( DatabaseDAO.class );
		    if( cons != null ) {
			BasicDAO dao = (BasicDAO)cons.newInstance( db );
			dao.initDAO();
			dao.setDataAccessObjectProvider( this );
			Method[] meths = dao.getClass().getMethods();
			for( int i = 0; i < meths.length; i++ ) {
			    if( meths[i].toString().indexOf( daoName ) > 0 ) {
				dataObjects.put( meths[i].toString(), dao );
				log.debug( "DAO API registering "+meths[i].toString() );
			    }
			}
		    }
		    else
			log.error( "Appropriate constructor not found. Check implementation!" );
		}
		catch( ClassNotFoundException cnfe ) {
		    log.warn( "Cannot find "+daoName );
		}
		catch( NoSuchMethodException nsme ) {
		    log.warn( "Cannot find appropriate constructor "+daoName );
		}
		catch( InstantiationException ine ) {
		    log.error( ine );
		}
		catch( IllegalAccessException iae ) {
		    log.warn( iae );
		}
		catch( InvocationTargetException ite ) {
		    log.warn( ite );
		}
		catch( SQLException sqe ) {
		    log.error( sqe );
		}
	    }
	}

	// create a no-op data access object to fulfill generic calls

	genericDAO = new DataAccessObject( db );
	try {
	    genericDAO.initDAO();
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	}
	genericDAO.setDataAccessObjectProvider( this );
    }

    private BasicDAO findDAO( Method method ) {
	String mName = method.toString().replace( " abstract ", " ");
	mName = mName.replace( "."+method.getName(), "DAO."+method.getName() );
	// log.debug( "Find method "+mName );
	// return dataObjects.get( method.toString() );
	return dataObjects.get( mName );
    }

    /**
     * Get the <code>Database</code> value.
     *
     * @return a <code>DatabaseDAO</code> value
     */
    public final DatabaseDAO getDatabase() {
	return database;
    }

    /**
     * Set the <code>Database</code> value.
     *
     * @param database The new Database value.
     */
    public synchronized final void setDatabase(final DatabaseDAO database) {
	this.database = database;
	registerDAOs();
	this.sampleInventory = null;
    }

    private synchronized void initDatabase() {
	if( this.database == null ) 
	    setDatabase( DatabaseDAO.getInstance() );
    }

    /**
     * Creates a new API to access the sample inventory.
     *
     * @return the sample inventory API.
     */
    public SampleInventory createSampleInventory() {
	initDatabase();
	return (SampleInventory)Proxy.newProxyInstance( SampleInventory.class.getClassLoader(),
							new Class[] { SampleInventory.class },
							this );
    }

    /**
     * Returns an instance of the <code>SampleInventory</code> API.
     *
     * @return the API instance.
     */
    public SampleInventory getSampleInventory() {
	if( sampleInventory == null )
	    sampleInventory = createSampleInventory();
	return sampleInventory;
    }

    /**
     * Processes a method invocation on a proxy instance and returns the result. 
     * This method will be invoked on an invocation handler when a method is invoked 
     * on a proxy instance that it is associated with.
     *
     * @param proxy the proxy instance that the method was invoked on
     * @param method the Method instance corresponding to the interface method invoked on the proxy instance.
     * @param args an array of objects containing the values of the arguments passed in the method invocation on the proxy instance, or null if interface method takes no arguments. 
     * @return the value to return from the method invocation on the proxy instance.
     */
    public Object invoke(Object proxy, Method method, Object[] args)
	throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

	Object ret = null;
	BasicDAO dao = findDAO( method );
	if( dao == null ) {
	    log.warn( "Cannot find DAO implementing "+method );
	    ret = method.invoke( genericDAO, args );
	}
	else {
	    // log.debug( "DAO resolving method \""+method+"\": "+dao );
	    ret = method.invoke( dao, args );
	}

	return ret;
    }

}
