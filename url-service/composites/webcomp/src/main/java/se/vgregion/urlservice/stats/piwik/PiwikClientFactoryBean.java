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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import se.vgregion.urlservice.stats.StatsTracker;



/**
 * Client to report clicks in Piwik
 *
 */
public class PiwikClientFactoryBean implements FactoryBean<StatsTracker> {

	private String piwikBase;
	private String siteId;
	
	public PiwikClientFactoryBean(String piwikBase, String siteId) {
		this.piwikBase = piwikBase;
		this.siteId = siteId;
	}
	
	public static class NoopStatsTracker implements StatsTracker {
		@Override
		public void track(String url, String referrer, String title,
				String userAgent) {
			// do nothing
		}
	}

	@Override
	public StatsTracker getObject() throws Exception {
		// check that piwikBase and siteId is configured
		if(isConfigured(piwikBase) && isConfigured(siteId)) {
			return new DefaultPiwikClient(piwikBase, siteId);
		} else {
			return new NoopStatsTracker();
		}
	}
	
	private boolean isConfigured(String s) {
		// string must be non-blank and not contain unresolved Spring expressions
		return !StringUtils.isBlank(s) && !s.contains("${");
	}

	@Override
	public Class<?> getObjectType() {
		return StatsTracker.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
