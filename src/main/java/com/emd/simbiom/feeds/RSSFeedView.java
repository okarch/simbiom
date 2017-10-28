package com.emd.simbiom.feeds;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Item;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class RSSFeedView extends AbstractRssFeedView {
    protected void buildFeedMetadata(Map model, Channel feed, HttpServletRequest request) {

        feed.setLink("biobank.merckgroup.com");
        feed.setTitle("Sample Inventory - Latest Samples Check In");
        feed.setDescription("Samples which have been checked into the sample inventory");

        List<FeedContent> fdList = (List<FeedContent>) model.get("feedContent");

	for (FeedContent fdCont : fdList) {
	    Date date = fdCont.getPublished();

	    if ((feed.getLastBuildDate() == null) || (date.compareTo(feed.getLastBuildDate()) > 0)) {
		feed.setLastBuildDate(date);
	    }
	}

    }

    protected List buildFeedItems(Map model, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        List<FeedContent> fdList = (List<FeedContent>) model.get("feedContent");
        List<Item> items = new ArrayList<Item>(fdList.size());

	for (FeedContent fdCont : fdList) {
	    Item item = new Item();
	    String date = String.format("%1$tY-%1$tm-%1$td", fdCont.getPublished());
	    item.setTitle( fdCont.getTitle() );
	    item.setPubDate( fdCont.getPublished() );
	    item.setLink( fdCont.getLink() );

	    item.setAuthor( "Sample Inventory" );

	    items.add(item);
	}

        return items;
    }
}
