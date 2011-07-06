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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import se.vgregion.urlservice.stats.piwik.DefaultPiwikClient.Hit;


public class PiwikTrackerThreadTest {

	private static final String BASE_URL = "http://pwiki.com";
	private static final String URL = "http://example.com";
	private static final String REFERRER = "http://from.com";
	private static final String TITLE = "foo";
	private static final String USER_AGENT = "Some browser";
	private static final String SITE_ID = "1";
	private static final Hit HIT = new Hit(URL, REFERRER, TITLE, USER_AGENT);
	
	private LinkedBlockingQueue<Hit> hits = new LinkedBlockingQueue<Hit>();
	
	private URLConnection conn = Mockito.mock(URLConnection.class);
	private LinkedBlockingQueue<URL> urls = new LinkedBlockingQueue<URL>();
	
	private PiwikTrackerThread ppt = new PiwikTrackerThread(BASE_URL, SITE_ID, hits) {
		@Override
		protected URLConnection openConnection(java.net.URL url)
				throws IOException {
			urls.offer(url);
			return conn;
		}
	};
	

	
	@Before
	public void before() throws IOException {
		Mockito.when(conn.getInputStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
	}
	
	@Test
	public void test() throws InterruptedException, Exception {
		hits.add(HIT);
		
		Assert.assertTrue(ppt.trackOneHit());

		Mockito.verify(conn).setRequestProperty("User-agent", USER_AGENT);

		URL actualUrl = urls.poll();
		
		assertQuery(actualUrl, "idsite", "1");
		assertQuery(actualUrl, "apiv", "1");
		assertQuery(actualUrl, "rec", "1");
		assertQuery(actualUrl, "action_name", TITLE);
		assertQuery(actualUrl, "url", URLEncoder.encode(URL, "UTF-8"));
		assertQuery(actualUrl, "urlref", URLEncoder.encode(REFERRER, "UTF-8"));
	}
	
	@Test
	public void testIOException() throws InterruptedException, Exception {
		Mockito.doThrow(new IOException()).when(conn).connect();
		
		hits.add(HIT);
	
		// no exception should be propagated and should return true
		Assert.assertTrue(ppt.trackOneHit());
	}
	
	@Test
	public void testRuntimeException() throws InterruptedException, Exception {
		Mockito.doThrow(new RuntimeException()).when(conn).connect();
		
		hits.add(HIT);
		
		// no exception should be propagated and should return true
		Assert.assertTrue(ppt.trackOneHit());
	}
	
	private void assertQuery(URL url, String expectedName, String expectedValue) {
		Assert.assertTrue(Pattern.compile("[\\?\\&]" + expectedName + "\\=" + expectedValue).matcher(url.toString()).find());
	}
}
