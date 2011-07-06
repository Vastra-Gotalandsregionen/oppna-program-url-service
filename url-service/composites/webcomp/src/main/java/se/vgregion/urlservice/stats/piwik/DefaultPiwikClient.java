/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.urlservice.stats.piwik;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import se.vgregion.urlservice.stats.StatsTracker;



/**
 * Client to report clicks in Piwik. Handles tracking asynchronously so 
 * that performance for the user following a redirect should be unaffected 
 */
public class DefaultPiwikClient implements StatsTracker {

    private final Logger log = LoggerFactory.getLogger(DefaultPiwikClient.class);
	
    private static final int QUEUE_DEPTH = 10000;
	
	private LinkedBlockingQueue<Hit> hits = new LinkedBlockingQueue<Hit>(QUEUE_DEPTH);
	
	public static class Hit {
		private String url;
		private String referrer;
		private String title;
		private String userAgent;

		public Hit(String url, String referrer, String title, String userAgent) {
			this.url = url;
			this.referrer = referrer;
			this.title = title;
			this.userAgent = userAgent;
		}

		public String getUrl() {
			return url;
		}

		public String getReferrer() {
			return referrer;
		}

		public String getTitle() {
			return title;
		}

		public String getUserAgent() {
			return userAgent;
		}
	}
	
	public DefaultPiwikClient(String piwikBase, String siteId) {
		Assert.hasText(piwikBase, "piwikBase must be provided");
		Assert.hasText(siteId, "siteId must be provided");
		
		// start the async handler
		new PiwikTrackerThread(piwikBase, siteId, hits).start();
	}
	
	public void track(String url, String referrer, String title, String userAgent) {
		Assert.hasText(url, "url must be provided");
		Assert.hasText(referrer, "referrer must be provided");
		Assert.hasText(title, "title must be provided");
		
		Hit hit = new Hit(url, referrer, title, userAgent);
		
		// offer this hit to the queue, should the queue be full, 
		// this hit will be dropped. This is as not to flood the queue
		// in the case of much traffic. Handling redirects are more important
		// than the stats
		hits.offer(hit);
	}

}
