package com.emd.simbiom.feeds;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateFormatUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.emd.simbiom.dao.SampleInventoryDAO;

import com.emd.simbiom.model.Age;
import com.emd.simbiom.model.Sample;
import com.emd.simbiom.model.SampleType;
import com.emd.simbiom.model.Study;

/**
 * <code>FeedController</code> produces the feed content upon requests.
 */
@Controller
public class FeedController {

    private static final long   DEFAULT_150_DAYS = 150L * 24L * 60L * 60L * 1000L; // 150 days

    private static Log log = LogFactory.getLog(FeedController.class);

    private Sample[] getLatestSamples( long age ) {
	SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	Sample[] samples = null;
	try {
	    samples = sInv.findSampleByAge( Age.created().newerThan(age) );
	}
	catch( SQLException sqe ) {
	    log.error( sqe );
	    samples = null;
	}
	return ((samples == null)?(new Sample[0]):samples); 
    }

    private List<FeedContent> groupSamples( Sample[] samples ) {
	SampleInventoryDAO sInv = SampleInventoryDAO.getInstance();
	TreeMap<String,FeedContent> studySamples = new TreeMap<String,FeedContent>();
	Map<Long,String> sampleTypes = new HashMap<Long,String>();
	Map<Long,String> studyNames = new HashMap<Long,String>();
	for( int i=0; i < samples.length; i++ ) {
	    long studyId = samples[i].getStudyid();
	    long typeId = samples[i].getTypeid();
	    String calWeek = DateFormatUtils.format( samples[i].getCreated(), "yyyy-ww" );
	    String fcKey = calWeek+"."+String.valueOf( studyId )+"."+String.valueOf( typeId );
	    FeedContent fdCont = studySamples.get( fcKey );
	    if( fdCont == null ) {
		fdCont = new FeedContent( samples[i] );

		String sType = sampleTypes.get( new Long(typeId) );
		if( sType == null ) {
		    try {
			SampleType st = sInv.findSampleTypeById( typeId );
			sType = st.toString();
		    }
		    catch( SQLException sqe ) {
			log.error( sqe );
			sType = "unknown";
		    }
		    sampleTypes.put( new Long(typeId), sType );
		}		
		fdCont.setTypename( sType );

		String studyName = studyNames.get( new Long(studyId) );
		if( studyName == null ) {
		    try {
			Study st = sInv.findStudyById( studyId );
			studyName = st.toString();
		    }
		    catch( SQLException sqe ) {
			log.error( sqe );
			sType = "unknown";
		    }
		    studyNames.put( new Long(studyId), studyName );
		}		
		fdCont.setStudyname( studyName );
		
		studySamples.put( fcKey, fdCont );
	    }
	    fdCont.addSample(samples[i]);
	}

	List<FeedContent> feedCont = new ArrayList<FeedContent>();
	Map.Entry<String,FeedContent> me = null;
	while( (me = studySamples.pollLastEntry()) != null ) {
	    feedCont.add( me.getValue() );
	}
	return feedCont;
    }

    private void createFeedContent( Model model ) {
	Sample[] samples = getLatestSamples( DEFAULT_150_DAYS );
	List<FeedContent> fCont = groupSamples( samples );
        model.addAttribute("feedContent", fCont );
    }

    @RequestMapping("sample/latest/atomfeed")
    public String getAtomFeed(Model model) {
	createFeedContent( model );
        return "atomfeedtemplate";
    }

    @RequestMapping("sample/latest/rssfeed")
    public String getRSSFeed(Model model) {
	createFeedContent( model );

        return "rssfeedtemplate";
    }

    @RequestMapping("/sample/latest")
    public String getJSON(Model model) {
        List<Sample> tournamentList = new ArrayList<Sample>();
        model.addAttribute("feedContent", tournamentList);

        return "jsontournamenttemplate";
    }
}
