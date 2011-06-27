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

package se.vgregion.urlservice.types;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;

public class RedirectRuleTest {

    private static final String DOMAIN = "http://foo.vgregion.se";
    private static final String URL = "http://example.com";
    
    @Test
    public void nullDomain() {
        new RedirectRule(null, "/foo", URL);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullPatterNotAllowed() {
        new RedirectRule(DOMAIN, null, URL);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullUrlNotAllowed() {
        new RedirectRule(DOMAIN, "/foo", null);
    }
    
    @Test
    public void matchesSimple() {
        RedirectRule rule = new RedirectRule(DOMAIN, "/foo", URL);
        
        Assert.assertTrue(rule.matches(DOMAIN, "/foo"));
        Assert.assertFalse(rule.matches(DOMAIN, "/foox"));
        Assert.assertFalse(rule.matches(DOMAIN, "/foo/x"));
        Assert.assertFalse(rule.matches(DOMAIN, "/xfoo"));
        Assert.assertFalse(rule.matches(DOMAIN, "/bar"));
    }

    @Test
    public void matchesWildcard() {
        RedirectRule rule = new RedirectRule(DOMAIN, "/foo.*", URL);
        
        Assert.assertTrue(rule.matches(DOMAIN, "/foo"));
        Assert.assertTrue(rule.matches(DOMAIN, "/foox"));
        Assert.assertTrue(rule.matches(DOMAIN, "/foo/x"));
        Assert.assertFalse(rule.matches(DOMAIN, "/xfoo"));
        Assert.assertFalse(rule.matches(DOMAIN, "/bar"));
    }

    @Test
    public void doNotMatchIncorrectDomain() {
        RedirectRule rule = new RedirectRule(DOMAIN, "/foo.*", URL);
        
        Assert.assertFalse(rule.matches("dummy", "foo"));
        Assert.assertFalse(rule.matches(null, "foox"));
    }

    @Test
    public void resolveNoGroups() {
    	RedirectRule rule = new RedirectRule(DOMAIN, "/foo", "http://example.com/bar");
    	
    	Assert.assertEquals(URI.create("http://example.com/bar"), rule.resolve("/foo"));
    }
    
    @Test
    public void resolveSimpleGroup() {
    	RedirectRule rule = new RedirectRule(DOMAIN, "/(fo)o", "http://example.com/b{1}ar");
    	
    	Assert.assertEquals(URI.create("http://example.com/bfoar"), rule.resolve("/foo"));
    }
    
    @Test
    public void resolveMultiGroup() {
    	RedirectRule rule = new RedirectRule(DOMAIN, "/(fo)(o)", "http://example.com/b{1}a{2}r");
    	
    	Assert.assertEquals(URI.create("http://example.com/bfoaor"), rule.resolve("/foo"));
    }
    
    @Test
    public void resolveMissingGroup() {
    	RedirectRule rule = new RedirectRule(DOMAIN, "/(fo)(o)", "http://example.com/ba{2}r");
    	
    	Assert.assertEquals(URI.create("http://example.com/baor"), rule.resolve("/foo"));
    }

    @Test
    public void resolveExtraGroup() {
    	RedirectRule rule = new RedirectRule(DOMAIN, "/(fo)(o)", "http://example.com/b{3}a{2}r");
    	
    	// non-matched placeholder should be removed
    	Assert.assertEquals(URI.create("http://example.com/baor"), rule.resolve("/foo"));
    }
    

}
