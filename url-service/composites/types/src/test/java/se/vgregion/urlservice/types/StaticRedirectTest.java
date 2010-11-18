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

import org.junit.Assert;
import org.junit.Test;

public class StaticRedirectTest {

    private static final String DOMAIN = "foo.vgregion.se";

    private static final String URL = "http://example.com";
    
    @Test(expected=IllegalArgumentException.class)
    public void nullDomainNotAllowed() {
        new StaticRedirect(null, "foo", URL);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullPatterNotAllowed() {
        new StaticRedirect(DOMAIN, null, URL);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullUrlNotAllowed() {
        new StaticRedirect(DOMAIN, "foo", null);
    }
    
    @Test
    public void matchesSimple() {
        StaticRedirect rule = new StaticRedirect(DOMAIN, "foo", URL);
        
        Assert.assertTrue(rule.matches(DOMAIN, "foo"));
        Assert.assertFalse(rule.matches(DOMAIN, "foox"));
        Assert.assertFalse(rule.matches(DOMAIN, "foo/x"));
        Assert.assertFalse(rule.matches(DOMAIN, "xfoo"));
        Assert.assertFalse(rule.matches(DOMAIN, "bar"));
    }

    @Test
    public void doNotMatchIncorrectDomain() {
        StaticRedirect rule = new StaticRedirect(DOMAIN, "foo", URL);
        
        Assert.assertFalse(rule.matches("dummy", "foo"));
        Assert.assertFalse(rule.matches(null, "foox"));
    }

}
