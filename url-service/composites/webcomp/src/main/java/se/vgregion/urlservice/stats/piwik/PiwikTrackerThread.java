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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.urlservice.stats.piwik.DefaultPiwikClient.Hit;

class PiwikTrackerThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(PiwikTrackerThread.class);
	
	private static final int API_VERSION = 1;
	
	private String piwikBase;
	private String siteId;
	private LinkedBlockingQueue<Hit> hits;
	
	private Random rnd = new Random();
	
	public PiwikTrackerThread(String piwikBase, String siteId, LinkedBlockingQueue<Hit> hits) {
		super("Piwik tracker");
		
		this.piwikBase = piwikBase;
		this.siteId = siteId;
		this.hits = hits;
	}
	
	@Override
	public void run() {
		while(trackOneHit());
	}

	protected boolean trackOneHit() {
		try {
			Hit hit = hits.take();
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("idsite", siteId);
			parameters.put("rec", 1);
			parameters.put("apiv", API_VERSION);
			parameters.put("url", encode(hit.getUrl()));
			parameters.put("urlref", encode(hit.getReferrer()));
			parameters.put("action_name", encode(hit.getTitle()));
			parameters.put("rand", rnd.nextInt(9999999));
			
			String query = buildQuery(parameters);
			
			URL piwikUrl;
			piwikUrl = new URL(piwikBase + "?" + query);

			// best effort, if it doesn't work we'll just ignore the error
			URLConnection conn = openConnection(piwikUrl);

			if(hit.getUserAgent() != null) {
				conn.setRequestProperty("User-agent", hit.getUserAgent());
			}
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setAllowUserInteraction(false);
			
			log.debug("Tracking to Piwik with URL: {}", piwikUrl);
			conn.connect();
			conn.getInputStream().close();
		} catch (InterruptedException e) {
			log.debug("Stopping Piwik tracker", e);
			return false;
		} catch (Exception e) {
			log.warn("Failed to track request in Piwik", e);
		}
		
		return true;
	}
	
	protected URLConnection openConnection(URL url) throws IOException {
		return url.openConnection();
	}
	
	private String buildQuery(Map<String, Object> parameters) {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, Object> parameter : parameters.entrySet()) {
			if(sb.length() > 0) sb.append("&");
			sb.append(parameter.getKey()).append("=").append(parameter.getValue());
		}
		return sb.toString();
	}

	private String encode(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// not very likely...
			throw new RuntimeException(e);
		}
	}
}