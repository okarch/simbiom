package com.emd.simbiom.model;

import java.sql.Timestamp;

/**
 * <code>Age</code> defines als sorst of ages.
 *
 * Created: Tue Mar 24 22:14:48 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
public class Age {
    private long base;
    private long distance;

    private int ageType;
    private int operator;

    private static final String[] STMT_SAMPLE_BY_AGE     = {
	"biobank.sample.findByCreatedEqual",
	"biobank.sample.findByCreatedNewer",
	"biobank.sample.findByCreatedOlder",
	"biobank.sample.findByCollectedEqual",
	"biobank.sample.findByCollectedNewer",
	"biobank.sample.findByCollectedOlder"
    };

    public static final int CREATED = 0;
    public static final int COLLECTED = 1;

    public static final int EQUAL = 0;
    public static final int NEWER = 1;
    public static final int OLDER = 2;
    
    private Age( int ageAttr, long baseTime ) {
	this.base = baseTime;
	this.distance = 0;
	this.ageType = ageAttr;
	this.operator = EQUAL;
    }

    private Age( int ageAttr ) {
	this( ageAttr, System.currentTimeMillis() );
    }

    /**
     * Creates an age instance refering to sample registration date
     *
     * @param base the date base to be used.
     * @return an age instance.
     */
    public static Age created( long base ) {
	return new Age( CREATED, base );
    }

    public static Age created() {
	return new Age( CREATED );
    }

    public static Age collected( long base ) {
	return new Age( COLLECTED, base );
    }

    public static Age collected() {
	return new Age( COLLECTED );
    }

    public Age newerThan( long time ) {
	distance = time;
	operator = NEWER;
	return this;
    }

    public Age olderThan( long time ) {
	distance = time;
	operator = OLDER;
	return this;
    }

    /**
     * Get the <code>Base</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getBase() {
	return base;
    }

    /**
     * Set the <code>Base</code> value.
     *
     * @param base The new Base value.
     */
    public final void setBase(final long base) {
	this.base = base;
    }

    /**
     * Get the <code>Distance</code> value.
     *
     * @return a <code>long</code> value
     */
    public final long getDistance() {
	return distance;
    }

    /**
     * Set the <code>Distance</code> value.
     *
     * @param distance The new Distance value.
     */
    public final void setDistance(final long distance) {
	this.distance = distance;
    }

    public Timestamp getTimestamp() {
	return new Timestamp( base-distance );
    }

    /**
     * Returns the appropriate SQL statement name.
     *
     * @return The name of the SQL statement.
     */
    public String getStatementName() {
	return STMT_SAMPLE_BY_AGE[(ageType*3)+operator];
    }

}
