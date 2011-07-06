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

package se.vgregion.urlservice.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * Client to report clicks in Piwik
 *
 */
public class DefaultPiwikClient implements StatsTracker {

    private final Logger log = LoggerFactory.getLogger(DefaultPiwikClient.class);
	
	private static final int API_VERSION = 1;
	
	private String piwikBase;
	private String siteId;
	
	private Random rnd = new Random();
	
	public DefaultPiwikClient(String piwikBase, String siteId) {
		Assert.hasText(piwikBase, "piwikBase must be provided");
		Assert.hasText(siteId, "siteId must be provided");
		this.piwikBase = piwikBase;
		this.siteId = siteId;
	}
	
	public void track(String url, String referrer, String title, String userAgent) {
		Assert.hasText(url, "url must be provided");
		Assert.hasText(referrer, "referrer must be provided");
		Assert.hasText(title, "title must be provided");
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idsite", siteId);
		parameters.put("rec", 1);
		parameters.put("apiv", API_VERSION);
		parameters.put("url", encode(url));
		parameters.put("urlref", encode(referrer));
		parameters.put("action_name", encode(title));
		parameters.put("rand", rnd.nextInt(9999999));
		
		String query = buildQuery(parameters);
		
		URL piwikUrl;
		try {
			piwikUrl = new URL(piwikBase + "?" + query);

			// best effort, if it doesn't work we'll just ignore the error
			URLConnection conn = piwikUrl.openConnection();
			if(userAgent != null) {
				conn.setRequestProperty("User-agent", userAgent);
			}
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setAllowUserInteraction(false);
			
			log.debug("Tracking to Piwik with URL: {}", piwikUrl);
			conn.connect();
			conn.getInputStream().close();
		} catch (IOException e) {
			log.warn("Failed to track request in Piwik", e);
		}
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
