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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.repository.LongUrlRepository;
import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.repository.BookmarkRepository;
import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.StaticRedirect;
import se.vgregion.urlservice.types.User;

public class DefaultUrlServiceServiceTest {

    private static final String DOMAIN = "foo.vgregion.se";
    private static final String HASH = "foo";
    private static final String USERNAME = "roblu";
    private static final URI LONG_URL = URI.create("http://example.com");
    private DefaultUrlServiceService urlService = new DefaultUrlServiceService();

    private LongUrl longUrl = new LongUrl(LONG_URL);
    private User owner = new User(USERNAME);
    private List<Keyword> emptyKeywords = new ArrayList<Keyword>(); 
    
    @Before
    public void before() {
        urlService.setDomain(DOMAIN);
        urlService.setLongUrlRepository(mock(LongUrlRepository.class));
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));
        urlService.setRedirectRuleRepository(mock(RedirectRuleRepository.class));
        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
    }
    
    @Test
    public void shortenNonExistingUrl() {
        Bookmark link = urlService.shorten(LONG_URL, owner);
        
        Assert.assertEquals("a9b9f0", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
        Assert.assertEquals(0, link.getKeywords().size());
    }

    @Test
    public void shortenWithSlug() {
        Bookmark link = urlService.shorten(LONG_URL, "my_slug", owner);

        Assert.assertEquals("my_slug", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
        Assert.assertEquals(0, link.getKeywords().size());
    }

    @Test
    @Ignore
    public void shortenExistingWithOwnerAndUpdatedKeywords() {
        Keyword kw1 = new Keyword("kw1");
        Keyword kw2 = new Keyword("kw2");
        
        KeywordRepository keywordRepository = mock(KeywordRepository.class);
        when(keywordRepository.find(kw1.getId())).thenReturn(kw1);
        when(keywordRepository.find(kw2.getId())).thenReturn(kw2);
        
        urlService.setKeywordRepository(keywordRepository);

        List<UUID> keywordIds2 = Arrays.asList(kw1.getId(), kw2.getId()); 

        
        User owner = new User("test");

        Bookmark link = new Bookmark(HASH, longUrl, Arrays.asList(kw1), owner);

        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByLongUrl(LONG_URL, owner)).thenReturn(link);
        urlService.setShortLinkRepository(shortLinkRepository);

        Assert.assertEquals(owner, link.getOwner());
        
        link = urlService.shorten(LONG_URL, "my_slug", keywordIds2, owner);

        Assert.assertEquals(2, link.getKeywords().size());
        Assert.assertEquals(kw1.getName(), link.getKeywords().get(0).getName());
        Assert.assertEquals(kw2.getName(), link.getKeywords().get(1).getName());
    }

    
    @Test
    public void shortenWithKeywords() {
        Keyword kw1 = new Keyword("kw1");
        Keyword kw2 = new Keyword("kw2");
        
        KeywordRepository keywordRepository = mock(KeywordRepository.class);
        when(keywordRepository.find(kw1.getId())).thenReturn(kw1);
        when(keywordRepository.find(kw2.getId())).thenReturn(kw2);
        
        urlService.setKeywordRepository(keywordRepository);

        List<UUID> keywordIds = Arrays.asList(kw1.getId(), kw2.getId()); 
        
        Bookmark link = urlService.shorten(LONG_URL, "my_slug", keywordIds, owner);

        Assert.assertEquals("my_slug", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
        Assert.assertEquals(2, link.getKeywords().size());
        Assert.assertEquals(kw1.getName(), link.getKeywords().get(0).getName());
        Assert.assertEquals(kw2.getName(), link.getKeywords().get(1).getName());
    }

    
    @Test
    public void shortenWithBlankSlug() {
        Bookmark link = urlService.shorten(LONG_URL, "", owner);

        Assert.assertEquals("a9b9f0", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test
    public void shortenWithNullSlug() {
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));

        Bookmark link = urlService.shorten(LONG_URL, null, owner);

        Assert.assertEquals("a9b9f0", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test
    public void shortenWithTooShortSlug() {
        Bookmark link = urlService.shorten(LONG_URL, "foo", owner);

        Assert.assertEquals("foo9f0", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test(expected=IllegalArgumentException.class)
    public void shortenSlugWithSlash() {
        urlService.shorten(LONG_URL, "f/sdsddsoo", owner);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shortenSlugWithPercentage() {
        urlService.shorten(LONG_URL, "f%20sdsddsoo", owner);
    }

    @Test(expected=IllegalArgumentException.class)
    public void shortenSlugWithWhitespace() {
        urlService.shorten(LONG_URL, "f sdsddsoo", owner);
    }

    @Test
    public void shortenWithSlugCollision() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        
        when(shortLinkRepository.findByHash("my_slug")).thenReturn(new Bookmark("my_slug", longUrl, emptyKeywords, owner));
        when(shortLinkRepository.findByHash("my_slug4")).thenReturn(null);
        urlService.setShortLinkRepository(shortLinkRepository);

        Bookmark link = urlService.shorten(LONG_URL, "my_slug", owner);

        Assert.assertEquals("my_slug4", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    
    @Test
    public void shortenWithHashCollision() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByHash("a9b9f0")).thenReturn(new Bookmark("a9b9f0", longUrl, owner));
        when(shortLinkRepository.findByHash("a9b9f04")).thenReturn(null);
        urlService.setShortLinkRepository(shortLinkRepository);
        
        Bookmark link = urlService.shorten(LONG_URL, owner);

        // since the hash collides, we should get a longer hash
        Assert.assertEquals("a9b9f04", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    
    @Test
    public void shortenExistingUrl() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByLongUrl(LONG_URL, owner)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);

        Bookmark link = urlService.shorten(LONG_URL, owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test(expected=IllegalArgumentException.class)
    public void shortenInvalidUrl() {
        urlService.shorten(URI.create("dummy:/invalid"), owner);
    }

    @Test
    public void expandExistingHash() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByHash(HASH)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);
        
        Bookmark link = urlService.expand(HASH);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test
    public void expandExistingShortUrl() throws URISyntaxException {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByHash(HASH)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);

        Bookmark link = urlService.expand(URI.create("http://" + DOMAIN + "/" + HASH));

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test
    public void expandNonExistingHash() {
        Assert.assertNull(urlService.expand(HASH));
    }
    
    @Test
    public void lookupExistingUrl() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByLongUrl(LONG_URL, owner)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);

        Bookmark link = urlService.lookup(LONG_URL, owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(owner, link.getOwner());
    }


    @Test
    public void lookupNonExistingUrl() {
        Assert.assertNull(urlService.lookup(LONG_URL, owner));
    }

    @Test
    public void redirectOnRedirectRule() {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(DOMAIN, "foo", LONG_URL)));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));
        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertEquals(LONG_URL, uri);
    }

    @Test
    public void redirectOnRedirectRuleNullDomain() {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(null, "foo", LONG_URL)));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));
        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        
        URI uri = urlService.redirect(null, "foo");
        
        Assert.assertEquals(LONG_URL, uri);
    }

    @Test
    public void redirectOnRedirectRuleDomainNotMatching() {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(DOMAIN, "foo", LONG_URL)));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));
        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        
        Assert.assertNull(urlService.redirect("dummy", "foo"));
    }

    
    @Test
    public void redirectOnShortLink() {
        urlService.setRedirectRuleRepository(mock(RedirectRuleRepository.class));
        
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByHash(HASH)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);

        urlService.setStaticRedirectRepository(mock(StaticRedirectRepository.class));
        
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertEquals(LONG_URL, uri);
    }
    
    @Test
    public void redirectOnShortLinkDomainNoMatching() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByHash(HASH)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);

        Assert.assertNull(urlService.redirect("dummy", "dummy"));
    }

    
    @Test
    public void redirectOnStaticRedirect() {
        StaticRedirectRepository staticRedirectRepository = mock(StaticRedirectRepository.class);
        when(staticRedirectRepository.findByPath(eq(DOMAIN), anyString())).thenReturn(new StaticRedirect(DOMAIN, "foo", LONG_URL));
        urlService.setStaticRedirectRepository(staticRedirectRepository);
        
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertEquals(LONG_URL, uri);
    }

    @Test
    public void redirectOnStaticRedirectDomainNotMatching() {
        StaticRedirectRepository staticRedirectRepository = mock(StaticRedirectRepository.class);
        when(staticRedirectRepository.findByPath(eq(DOMAIN), anyString())).thenReturn(new StaticRedirect(DOMAIN, "foo", LONG_URL));
        urlService.setStaticRedirectRepository(staticRedirectRepository);
        
        Assert.assertNull(urlService.redirect("dummy", "foo"));
    }

    
    @Test
    public void redirectNoneMatching() {
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertNull(uri);
    }
    
}
