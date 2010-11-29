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

package se.vgregion.urlservice.services;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.repository.ShortLinkRepository;
import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.ShortLink;
import se.vgregion.urlservice.types.StaticRedirect;
import se.vgregion.urlservice.types.User;

public class DefaultUrlServiceServiceTest {

    private static final String DOMAIN = "foo.vgregion.se";
    private static final String SHORT_URL = "http://short";
    private static final String HASH = "foo";
    private static final String LONG_URL = "http://example.com";
    private DefaultUrlServiceService urlService = new DefaultUrlServiceService();

    @Before
    public void before() {
        urlService.setDomain(DOMAIN);
    }
    
    @Test
    public void shortenNonExistingUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        ShortLink link = urlService.shorten(LONG_URL);

        Assert.assertEquals("a9b9f0", link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    @Test
    public void shortenWithSlug() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        ShortLink link = urlService.shorten(LONG_URL, "my_slug");

        Assert.assertEquals("my_slug", link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    @Test
    public void shortenWithSlugAndOwner() throws URISyntaxException {
        User owner = new User("test");
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        ShortLink link = urlService.shorten(LONG_URL, "my_slug", owner);

        Assert.assertEquals("test/my_slug", link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    
    @Test
    public void shortenWithBlankSlug() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        ShortLink link = urlService.shorten(LONG_URL, "");

        Assert.assertEquals("a9b9f0", link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    @Test
    public void shortenWithNullSlug() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        ShortLink link = urlService.shorten(LONG_URL, null);

        Assert.assertEquals("a9b9f0", link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    @Test
    public void shortenWithTooShortSlug() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        ShortLink link = urlService.shorten(LONG_URL, "foo");

        Assert.assertEquals("foo9f0", link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    @Test(expected=IllegalArgumentException.class)
    public void shortenSlugWithSlash() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        urlService.shorten(LONG_URL, "f/sdsddsoo");
    }

    @Test(expected=IllegalArgumentException.class)
    public void shortenSlugWithPercentage() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        urlService.shorten(LONG_URL, "f%20sdsddsoo");
    }

    @Test(expected=IllegalArgumentException.class)
    public void shortenSlugWithWhitespace() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        urlService.shorten(LONG_URL, "f sdsddsoo");
    }

    @Test
    public void shortenWithSlugCollision() throws URISyntaxException {
        ShortLinkRepository shortLinkRepository = mock(ShortLinkRepository.class);
        when(shortLinkRepository.findByHash(eq(DOMAIN), eq("my_slug"))).thenReturn(new ShortLink(DOMAIN, "a9b9f0", "http://someurl", SHORT_URL));
        when(shortLinkRepository.findByHash(eq(DOMAIN), eq("my_slug4"))).thenReturn(null);
        urlService.setShortLinkRepository(shortLinkRepository);

        ShortLink link = urlService.shorten(LONG_URL, "my_slug");

        Assert.assertEquals("my_slug4", link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    
    @Test
    public void shortenWithHashCollision() throws URISyntaxException {
        ShortLinkRepository shortLinkRepository = mock(ShortLinkRepository.class);
        when(shortLinkRepository.findByHash(DOMAIN, "a9b9f0")).thenReturn(new ShortLink(DOMAIN, "a9b9f0", "http://someurl", SHORT_URL));
        when(shortLinkRepository.findByHash(DOMAIN, "a9b9f04")).thenReturn(null);
        urlService.setShortLinkRepository(shortLinkRepository);
        
        ShortLink link = urlService.shorten(LONG_URL);

        // since the hash collides, we should get a longer hash
        Assert.assertEquals("a9b9f04", link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    
    @Test
    public void shortenExistingUrl() throws URISyntaxException {
        ShortLinkRepository shortLinkRepository = mock(ShortLinkRepository.class);
        when(shortLinkRepository.findByLongUrl(anyString())).thenReturn(new ShortLink(DOMAIN, HASH, LONG_URL, SHORT_URL));
        urlService.setShortLinkRepository(shortLinkRepository);

        ShortLink link = urlService.shorten(LONG_URL);

        Assert.assertEquals(HASH, link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    @Test(expected=URISyntaxException.class)
    public void shortenInvalidUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        urlService.shorten("invalid");
    }

    @Test
    public void expandExistingHash() throws URISyntaxException {
        ShortLinkRepository shortLinkRepository = mock(ShortLinkRepository.class);
        when(shortLinkRepository.findByHash(eq(DOMAIN), anyString())).thenReturn(new ShortLink(DOMAIN, HASH, LONG_URL, SHORT_URL));
        urlService.setShortLinkRepository(shortLinkRepository);

        
        ShortLink link = urlService.expand(DOMAIN, HASH);

        Assert.assertEquals(HASH, link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    @Test
    public void expandExistingShortUrl() throws URISyntaxException {
        ShortLinkRepository shortLinkRepository = mock(ShortLinkRepository.class);
        when(shortLinkRepository.findByHash(eq(DOMAIN), anyString())).thenReturn(new ShortLink(DOMAIN, HASH, LONG_URL, SHORT_URL));
        urlService.setShortLinkRepository(shortLinkRepository);

        
        ShortLink link = urlService.expand("http://" + DOMAIN + "/" + HASH);

        Assert.assertEquals(HASH, link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    
    @Test
    public void expandNonExistingHash() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        Assert.assertNull(urlService.expand(HASH));
    }
    
    
    @Test
    public void lookupExistingUrl() throws URISyntaxException {
        ShortLinkRepository shortLinkRepository = mock(ShortLinkRepository.class);
        when(shortLinkRepository.findByLongUrl(anyString())).thenReturn(new ShortLink(DOMAIN, HASH, LONG_URL, SHORT_URL));
        urlService.setShortLinkRepository(shortLinkRepository);

        ShortLink link = urlService.lookup(LONG_URL);

        Assert.assertEquals(HASH, link.getPattern());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }


    @Test
    public void lookupNonExistingUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));

        Assert.assertNull(urlService.lookup(LONG_URL));
    }

    @Test
    public void redirectOnRedirectRule() throws URISyntaxException {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(DOMAIN, "foo", LONG_URL)));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));
        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertEquals(LONG_URL, uri.toString());
    }

    @Test
    public void redirectOnRedirectRuleNullDomain() throws URISyntaxException {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(null, "foo", LONG_URL)));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));
        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        
        URI uri = urlService.redirect(null, "foo");
        
        Assert.assertEquals(LONG_URL, uri.toString());
    }

    @Test
    public void redirectOnRedirectRuleDomainNotMatching() throws URISyntaxException {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(DOMAIN, "foo", LONG_URL)));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));
        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        
        Assert.assertNull(urlService.redirect("dummy", "foo"));
    }

    
    @Test
    public void redirectOnShortLink() throws URISyntaxException {
        urlService.setRedirectRuleRepository(mock(RedirectRuleRepository.class));
        
        ShortLinkRepository shortLinkRepository = mock(ShortLinkRepository.class);
        when(shortLinkRepository.findByHash(eq(DOMAIN), anyString())).thenReturn(new ShortLink(DOMAIN, "foo", LONG_URL, SHORT_URL));
        urlService.setShortLinkRepository(shortLinkRepository);

        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertEquals(LONG_URL, uri.toString());
    }
    
    @Test
    public void redirectOnShortLinkDomainNoMatching() throws URISyntaxException {
        urlService.setRedirectRuleRepository(mock(RedirectRuleRepository.class));
        
        ShortLinkRepository shortLinkRepository = mock(ShortLinkRepository.class);
        when(shortLinkRepository.findByHash(eq(DOMAIN), anyString())).thenReturn(new ShortLink(DOMAIN, "foo", LONG_URL, SHORT_URL));
        urlService.setShortLinkRepository(shortLinkRepository);

        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        Assert.assertNull(urlService.redirect("dummy", "foo"));
    }

    
    @Test
    public void redirectOnStaticRedirect() throws URISyntaxException {
        urlService.setRedirectRuleRepository(mock(RedirectRuleRepository.class));
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));
        
        StaticRedirectRepository staticRedirectRepository = mock(StaticRedirectRepository.class);
        when(staticRedirectRepository.findByPath(eq(DOMAIN), anyString())).thenReturn(new StaticRedirect(DOMAIN, "foo", LONG_URL));
        urlService.setStaticRedirectRepository(staticRedirectRepository);
        
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertEquals(LONG_URL, uri.toString());
    }

    @Test
    public void redirectOnStaticRedirectDomainNotMatching() throws URISyntaxException {
        urlService.setRedirectRuleRepository(mock(RedirectRuleRepository.class));
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));
        
        StaticRedirectRepository staticRedirectRepository = mock(StaticRedirectRepository.class);
        when(staticRedirectRepository.findByPath(eq(DOMAIN), anyString())).thenReturn(new StaticRedirect(DOMAIN, "foo", LONG_URL));
        urlService.setStaticRedirectRepository(staticRedirectRepository);
        
        Assert.assertNull(urlService.redirect("dummy", "foo"));
    }

    
    @Test
    public void redirectNoneMatching() throws URISyntaxException {
        urlService.setRedirectRuleRepository(mock(RedirectRuleRepository.class));
        urlService.setShortLinkRepository(mock(ShortLinkRepository.class));
        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));

        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertNull(uri);
    }
    
}
