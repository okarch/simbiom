package com.emd.simbiom.rest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * <code>SampleInventoryREST</code> provides web endpoint of the sample inventory REST service.
 *
 * Created: Fri Feb  6 18:40:01 2015
 *
 * @author <a href="mailto:okarch@cuba.site">Oliver Karch</a>
 * @version 1.0
 */
@ComponentScan({"com.emd.simbiom.rest","com.emd.simbiom.feeds"})
@EnableAutoConfiguration
public class SampleInventoryREST {

    public static void main(String[] args) {
        SpringApplication.run(SampleInventoryREST.class, args);
    }
}

