package com.emd.simbiom.dao;

/**
 * TableUtils provides various static functions to deal with database tables.
 *
 * Created: Sun Jan 11 20:52:39 2015
 *
 * @author <a href="mailto:">Oliver</a>
 * @version 1.0
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.QueryRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.util.ClassUtils;
import com.emd.util.Copyable;
import com.emd.util.Stringx;

public class TableUtils {

    private static Log log = LogFactory.getLog(TableUtils.class);

    private TableUtils() { }

    /**
     * Checks if a table exists
     *
     * @param runner the <code>QueryRunner</code> object.
     * @param tableName the table name to check.
     * @return true if it exists
     */
    public static boolean tableExists( Connection con, String tableName ) {
	
	ResultSetHandler<Boolean> h = new ResultSetHandler<Boolean>() {
	    public Boolean handle(ResultSet rs) throws SQLException {
		return new Boolean(true);
	    }
	};

	try {
	    QueryRunner runner = new QueryRunner();
	    Boolean rc = runner.query( con, "select 1 from "+tableName, h );
	    return rc.booleanValue();
	}
	catch( SQLException sqe ) {
	}
	return false;
    }

    /**
     * Drops a table exists
     *
     * @param runner the <code>QueryRunner</code> object.
     * @param tableName the table to drop.
     * @return true if table has been dropped.
     */
    public static boolean dropTable( Connection con, String tableName ) {
	try {
	    QueryRunner runner = new QueryRunner();
	    int updates = runner.update( con, "drop table "+tableName );
	    return (updates >= 0);
	}
	catch( SQLException sqe ) { 
	    log.warn( sqe );
	}
	return false;
    }

    /**
     * Creates a table if it is not existing already. 
     *
     * @param runner the <code>QueryRunner</code> object.
     * @param tableName the table name to check.
     * @param colDef the column definition, e.g. "(mycol1 int primary key, mycol2 varchar(10))".
     * @param append if set to true the table will not be created when it is existing already.
     * @return true if table has been created successfully (or existed already in the case of append set to true).
     */
    public static boolean createTable( Connection con, 
				       String tableName, 
				       String colDef,
				       boolean append ) {
	if( append && tableExists( con, tableName) ) {
	    log.debug( "table "+tableName+" exists already." );
	    return true;
	}
	log.debug( "dropping table "+tableName );
	dropTable( con, tableName );

	try {
	    QueryRunner runner = new QueryRunner();
	    int updates = runner.update( con, "create table "+tableName+colDef );
	    log.debug( "Created table "+tableName );
	    return true;
	}
	catch( SQLException sqe ) { 
	    log.error( sqe );
	}
	return false;
    }

    /**
     * Indexes a table using the given index spec.
     *
     * @param runner the <code>QueryRunner</code> object.
     * @param tableName the table name to check.
     * @param colDef the column definition, e.g. "(mycol1 int primary key, mycol2 varchar(10))".
     * @param append if set to true the table will not be created when it is existing already.
     * @return true if table has been created successfully (or existed already in the case of append set to true).
     */
    public static boolean indexTable( Connection con,
				      String tableName, 
				      String colDef,
				      boolean unique ) {
	StringBuilder stb = new StringBuilder( "create ");
	if( unique )
	    stb.append( "unique " );
	stb.append( "index i_" );
	stb.append( Stringx.strtrunc(UUID.randomUUID().toString().replace( '-', '_' ),25) );
	stb.append( " on " );
	stb.append( tableName );
	stb.append( colDef );

	try {
	    QueryRunner runner = new QueryRunner();
	    int updates = runner.update( con, stb.toString() );
	    return true;
	}
	catch( SQLException sqe ) { 
	    log.error( sqe );
	}
	return false;
    }

    /**
     * Inserts a list of rows in a batch using the given insert statement.
     *
     * @param runner the <code>QueryRunner</code> object.
     * @param insertStmt the statement to use.
     * @param rows a list of rows.
     *
     * @return the number of rows inserted.
     */
    public static int[] batchInsert( QueryRunner runner, 
				     String insertStmt, 
				     List rows )
	throws SQLException {

	if( rows.size() <= 0 )
	    return new int[0];

	Object obj = rows.get(0);
	int nCol = 1;
	if( obj.getClass().isArray() ) {
	    Object[] cols = (Object[])obj;
	    nCol = cols.length;
	}
	Object[][] params = new Object[rows.size()][nCol];
	Iterator it = rows.iterator();
	for( int i = 0; it.hasNext(); i++ ) {
	    obj = it.next();
	    if( obj.getClass().isArray() ) {
		Object[] cols = (Object[])obj;
		for( int j = 0; j < nCol; j++ )
		    params[i][j] = cols[j];
	    }
	    else
		params[i][0] = obj;
	}
	int[] bCount = runner.batch( insertStmt, params );
	return bCount;
    }

    /**
     * Returns the query result as a list of objects using the
     * given object as prototype.
     *
     * @param runner the <code>QueryRunner</code> object.
     * @param queryStmt the query to use.
     * @return a list of result rows mapped to objects.
     */
    public static List query( Connection con,
			      String queryStmt,
			      Object prototype )
	throws SQLException {

	final Object proto = prototype;

	ResultSetHandler<List> h = new ResultSetHandler<List>() {
	    public List handle(ResultSet rs) throws SQLException {
		List results = new ArrayList();

		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		
		Class pClass = null;
		if( proto != null ) {
		    if( proto instanceof String ) {
			try {
			    pClass = Class.forName( proto.toString() );
			}
			catch( ClassNotFoundException cnfe ) {
			    log.warn( "Class lookup failed: "+proto );
			    pClass = proto.getClass();
			}
		    }
		    else
			pClass = proto.getClass();
		}

		while( rs.next() ) {
		    
		    // create the result object

		    Object[] row = null;
		    Object rowObj = null;
		    if( proto == null ) {
			row = new Object[cols];
			results.add( row );
		    }
		    else if( proto instanceof Copyable ) {
			try {
			    rowObj = ((Copyable)proto).copy();
			    results.add( rowObj );
			}
			catch( CloneNotSupportedException cns ) {
			    log.warn( "Cannot clone "+rowObj );
			    break;
			}
		    }
		    else {
			try {
			    rowObj = pClass.newInstance();
			    results.add( rowObj );
			}
			catch( InstantiationException iex ) {
			    log.error( iex );
			    break;
			}
			catch( IllegalAccessException iaex ) {
			    log.error( iaex );
			    break;
			}
		    }

		    // iterate over columns and call ther setter methods

		    for( int i = 1; i <= cols; i++ ) {
			String colName = meta.getColumnName( i );
			Object resObj = rs.getObject( i );
			if( row != null )
			    row[i-1] = resObj;
			else 
			    ClassUtils.set( rowObj, colName, resObj );
		    }
		}
    
		return results;
	    }
	};
	
	QueryRunner runner = new QueryRunner();
	return runner.query( con, queryStmt, h );
    }

    /**
     * Returns the query result as a list of objects using the
     * given object as prototype.
     *
     * @param res the <code>ResultSet</code> to wrap into objects.
     * @param queryStmt the query to use.
     * @return a list of result rows mapped to objects.
     */
    public static Iterator toObjects( ResultSet rs, Object proto )
	throws SQLException {

	return new ResultObjectIterator( rs, proto );
    }

    /**
     * Produces an object from the current result set cursor.
     *
     * @param res the <code>ResultSet</code> to wrap into objects.
     * @param proto a prototype object.
     * @return an initialized object.
     */
    public static Object toObject( ResultSet rs, Object proto )
	throws SQLException {
	
	Class pClass = null;
	if( proto != null ) {
	    if( proto instanceof String ) {
		try {
		    pClass = Class.forName( proto.toString() );
		}
		catch( ClassNotFoundException cnfe ) {
		    log.warn( "Class lookup failed: "+proto );
		    pClass = proto.getClass();
		}
	    }
	    else
		pClass = proto.getClass();
	}

	ResultSetMetaData meta = rs.getMetaData();
	int cols = meta.getColumnCount();
	Object[] row = null;
	Object rowObj = null;
	if( proto == null ) {
	    row = new Object[cols];
	}
	else if( proto instanceof Copyable ) {
	    try {
		rowObj = ((Copyable)proto).copy();
	    }
	    catch( CloneNotSupportedException cns ) {
		log.warn( "Cannot clone "+rowObj );
		throw new NoSuchElementException();
	    }
	}
	else {
	    try {
		rowObj = pClass.newInstance();
	    }
	    catch( InstantiationException iex ) {
		log.error( iex );
		throw new NoSuchElementException();
	    }
	    catch( IllegalAccessException iaex ) {
		log.error( iaex );
		throw new NoSuchElementException();
	    }
	}

	// iterate over columns and call ther setter methods

	for( int i = 1; i <= cols; i++ ) {
	    String colName = meta.getColumnName( i );
	    Object resObj = rs.getObject( i );
	    if( row != null )
		row[i-1] = resObj;
	    else 
		ClassUtils.set( rowObj, colName, resObj );
	}
	return ((rowObj != null)?rowObj:row);
    }

}

class ResultObjectIterator implements Iterator {
    private ResultSetMetaData meta;
    private int               cols;
    private ResultSet         rs;
    private Class             pClass;
    private Object            proto;
    private boolean           nextReady;

    private static Log log = LogFactory.getLog(ResultObjectIterator.class);

    ResultObjectIterator( ResultSet rs, Object proto )
	throws SQLException {

	meta = rs.getMetaData();
	cols = meta.getColumnCount();
		
	pClass = null;
	if( proto != null ) {
	    if( proto instanceof String ) {
		try {
		    pClass = Class.forName( proto.toString() );
		}
		catch( ClassNotFoundException cnfe ) {
		    log.warn( "Class lookup failed: "+proto );
		    pClass = proto.getClass();
		}
	    }
	    else
		pClass = proto.getClass();
	}
	this.rs = rs;
	this.proto = proto;
	nextReady = false;
    }

    /**
     * Returns true if the iteration has more elements. 
     * (In other words, returns true if next would return an element rather than throwing an exception.)
     *
     * @return true if the iterator has more elements.
     */
    public boolean hasNext() {
	try {
	    nextReady = rs.next();
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    nextReady = false;
	}
	return nextReady;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration. 
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Object next() {
	if( nextReady ) {
	    Object[] row = null;
	    Object rowObj = null;
	    if( proto == null ) {
		row = new Object[cols];
	    }
	    else if( proto instanceof Copyable ) {
		try {
		    rowObj = ((Copyable)proto).copy();
		}
		catch( CloneNotSupportedException cns ) {
		    log.warn( "Cannot clone "+rowObj );
		    throw new NoSuchElementException();
		}
	    }
	    else {
		try {
		    rowObj = pClass.newInstance();
		}
		catch( InstantiationException iex ) {
		    log.error( iex );
		    throw new NoSuchElementException();
		}
		catch( IllegalAccessException iaex ) {
		    log.error( iaex );
		    throw new NoSuchElementException();
		}
	    }

	    // iterate over columns and call ther setter methods

	    try {
		for( int i = 1; i <= cols; i++ ) {
		    String colName = meta.getColumnName( i );
		    Object resObj = rs.getObject( i );
		    if( row != null )
			row[i-1] = resObj;
		    else 
			ClassUtils.set( rowObj, colName, resObj );
		}
		return ((rowObj != null)?rowObj:row);
	    }
	    catch( SQLException sqe ) {
		throw new NoSuchElementException( sqe.getMessage() );
	    }
	}
	else
	    throw new NoSuchElementException();
    }

    /**
     * Removes from the underlying collection the last element returned by the iterator (optional operation).
     */
    public void remove() {
	throw new UnsupportedOperationException();
    }
}
