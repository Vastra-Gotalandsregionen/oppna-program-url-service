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

import org.junit.Assert;
import org.junit.Test;

import se.vgregion.urlservice.controllers.PiwikClientFactoryBean.NoopStatsTracker;


public class PiwikClientFactoryBeanTest {

	private static final String URL = "http://example.com";
	private static final String SPRING_PATTERN = "${foo}";

	@Test
	public void testConfigured() throws Exception {
		PiwikClientFactoryBean factory = new PiwikClientFactoryBean(URL, "1");
		
		Assert.assertTrue(factory.getObject() instanceof DefaultPiwikClient);
	}

	@Test
	public void testNullBaseUrl() throws Exception {
		PiwikClientFactoryBean factory = new PiwikClientFactoryBean(null, "1");
		
		Assert.assertTrue(factory.getObject() instanceof NoopStatsTracker);
	}

	@Test
	public void testNullSiteId() throws Exception {
		PiwikClientFactoryBean factory = new PiwikClientFactoryBean(URL, null);
		
		Assert.assertTrue(factory.getObject() instanceof NoopStatsTracker);
	}
	
	@Test
	public void testBlankBaseUrl() throws Exception {
		PiwikClientFactoryBean factory = new PiwikClientFactoryBean("", "1");
		
		Assert.assertTrue(factory.getObject() instanceof NoopStatsTracker);
	}
	
	@Test
	public void testBlankSiteId() throws Exception {
		PiwikClientFactoryBean factory = new PiwikClientFactoryBean(URL, "");
		
		Assert.assertTrue(factory.getObject() instanceof NoopStatsTracker);
	}
	
	@Test
	public void testUnresolvedBaseUrl() throws Exception {
		PiwikClientFactoryBean factory = new PiwikClientFactoryBean(SPRING_PATTERN, "1");
		
		Assert.assertTrue(factory.getObject() instanceof NoopStatsTracker);
	}
	
	@Test
	public void testUnresolvedSiteId() throws Exception {
		PiwikClientFactoryBean factory = new PiwikClientFactoryBean(URL, SPRING_PATTERN);
		
		Assert.assertTrue(factory.getObject() instanceof NoopStatsTracker);
	}
	
}
