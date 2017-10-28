package com.emd.simbiom.model;

/**
 * Trackable items interface.
 *
 * Created: Tue Feb  3 09:24:25 2015
 *
 * @author <a href="mailto:m01061@tonga.bci.merck.de">Oliver Karch</a>
 * @version 1.0
 */
public interface Trackable {

    /**
     * Get the <code>Trackid</code> value.
     *
     * @return a <code>long</code> value
     */
    public long getTrackid();

    /**
     * Get the <code>Item</code> value.
     *
     * @return a <code>String</code> value
     */
    public String getItem();

    /**
     * Get the <code>Content</code> value.
     *
     * @return a <code>String</code> value
     */
    public String toContent();

}
