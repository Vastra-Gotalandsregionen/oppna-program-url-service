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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import se.vgregion.urlservice.repository.BookmarkRepository;
import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.repository.LongUrlRepository;
import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.repository.UserRepository;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Owner;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.UrlWithHash;

public class DefaultUrlServiceServiceTest {

    private static final String DOMAIN = "foo.vgregion.se";
    private static final String GLOBAL_HASH = "a9b9f0";
    private static final String HASH = "74a88b";
    private static final String USERNAME = "roblu";
    private static final URI LONG_URL = URI.create("http://example.com");
    private DefaultUrlServiceService urlService = new DefaultUrlServiceService();

    private LongUrl longUrl = new LongUrl(LONG_URL, GLOBAL_HASH);
    private Owner owner = new Owner(USERNAME);
    private List<Keyword> emptyKeywords = new ArrayList<Keyword>();
    private LongUrlRepository longUrlRepository = mock(LongUrlRepository.class); 
    private BookmarkRepository bookmarkRepository = mock(BookmarkRepository.class);
    private RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
    private KeywordRepository keywordRepository = mock(KeywordRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    
    private Keyword kw1 = new Keyword("kw1");
    private Keyword kw2 = new Keyword("kw2");

    private List<String> keywordNames = Arrays.asList(kw1.getName(), kw2.getName()); 
    private List<Keyword> keywords = Arrays.asList(kw1, kw2); 
    
    @Before
    public void before() {
        urlService.setDomain(DOMAIN);
        urlService.setLongUrlRepository(longUrlRepository);
        urlService.setShortLinkRepository(bookmarkRepository);
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        urlService.setKeywordRepository(keywordRepository);
        urlService.setUserRepository(userRepository);
        
        when(userRepository.findByName(owner.getName())).thenReturn(owner);
    }
    
    @Test
    public void shortenNonExistingUrl() {
        Bookmark link = urlService.shorten(LONG_URL, owner);
        
        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
        Assert.assertEquals(0, link.getKeywords().size());
    }

    @Test
    public void shortenWithSlug() {
        Bookmark link = urlService.shorten(LONG_URL, owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
        Assert.assertEquals(0, link.getKeywords().size());
    }

    @Test
    @Ignore
    public void shortenExistingWithOwnerAndUpdatedKeywords() {
        KeywordRepository keywordRepository = mock(KeywordRepository.class);
        when(keywordRepository.find(kw1.getId())).thenReturn(kw1);
        when(keywordRepository.find(kw2.getId())).thenReturn(kw2);
        
        urlService.setKeywordRepository(keywordRepository);

        Owner owner = new Owner("test");

        Bookmark link = new Bookmark(HASH, longUrl, Arrays.asList(kw1), owner);

        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByLongUrl(LONG_URL, owner)).thenReturn(link);
        urlService.setShortLinkRepository(shortLinkRepository);

        Assert.assertEquals(owner, link.getOwner());
        
        link = urlService.shorten(LONG_URL, null, keywordNames, owner);

        Assert.assertEquals(2, link.getKeywords().size());
        Assert.assertEquals(kw1.getName(), link.getKeywords().get(0).getName());
        Assert.assertEquals(kw2.getName(), link.getKeywords().get(1).getName());
    }

    
    @Test
    public void shortenWithExistingKeywords() {
        KeywordRepository keywordRepository = mock(KeywordRepository.class);
        when(keywordRepository.findOrCreateKeywords(keywordNames)).thenReturn(keywords);
        urlService.setKeywordRepository(keywordRepository);

        Bookmark link = urlService.shorten(LONG_URL, null, keywordNames, owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
        Assert.assertEquals(2, link.getKeywords().size());
        Assert.assertEquals(kw1.getName(), link.getKeywords().get(0).getName());
        Assert.assertEquals(kw2.getName(), link.getKeywords().get(1).getName());
    }


    @Test
    public void shortenWithNewKeywords() {
        KeywordRepository keywordRepository = mock(KeywordRepository.class);
        when(keywordRepository.findOrCreateKeywords(keywordNames)).thenReturn(keywords);
        urlService.setKeywordRepository(keywordRepository);
        
        Bookmark link = urlService.shorten(LONG_URL, null, keywordNames, owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
        Assert.assertEquals(2, link.getKeywords().size());
        
        Keyword actualKw1 = link.getKeywords().get(0);
        Keyword actualKw2 = link.getKeywords().get(1);
        Assert.assertEquals(kw1.getName(), actualKw1.getName());
        Assert.assertEquals(kw2.getName(), actualKw2.getName());
    }
    
    
    @Test
    public void shortenWithBlankSlug() {
        Bookmark link = urlService.shorten(LONG_URL, "", owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test
    public void shortenWithNullSlug() {
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));

        Bookmark link = urlService.shorten(LONG_URL, null, owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
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
    public void shortenWithLongUrlHashCollision() {
        LongUrlRepository longUrlRepository = mock(LongUrlRepository.class);
        when(longUrlRepository.findByHash(GLOBAL_HASH)).thenReturn(new LongUrl(LONG_URL, GLOBAL_HASH));
        urlService.setLongUrlRepository(longUrlRepository);
        
        Bookmark link = urlService.shorten(LONG_URL, owner);

        // since the hash collides, we should get a longer hash
        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH + "4", link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test
    public void shortenWithBookmarkHashCollision() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByHash(HASH, owner)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);
        
        Bookmark link = urlService.shorten(LONG_URL, owner);

        // since the hash collides, we should get a longer hash
        Assert.assertEquals(HASH + "7", link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
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
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
    }

    
    
    @Test(expected=IllegalArgumentException.class)
    public void shortenInvalidUrl() {
        urlService.shorten(URI.create("dummy:/invalid"), owner);
    }

    @Test
    public void expandExistingHash() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByHash(HASH, owner)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);
        
        Bookmark link = urlService.expand(HASH, owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
    }

    @Test
    public void expandExistingGlobalShortUrl() throws URISyntaxException {
        when(longUrlRepository.findByHash(GLOBAL_HASH)).thenReturn(new LongUrl(LONG_URL, GLOBAL_HASH));

        UrlWithHash link = urlService.expandPath(URI.create("http://" + DOMAIN + "/b/" + GLOBAL_HASH));

        Assert.assertEquals(GLOBAL_HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getUrl());
    }

    @Test
    public void expandExistingUserShortUrl() throws URISyntaxException {
    	when(bookmarkRepository.findByHash(HASH, owner)).thenReturn(new Bookmark(HASH, longUrl, owner));
    	
    	UrlWithHash link = urlService.expandPath(URI.create("http://" + DOMAIN + "/u/" + owner.getName() + "/b/" + HASH));
    	
    	Assert.assertEquals(HASH, link.getHash());
    	Assert.assertEquals(LONG_URL, link.getUrl());
    }
    
    @Test
    public void expandNonExistingHash() {
        Assert.assertNull(urlService.expand(HASH, owner));
    }
    
    @Test
    public void lookupExistingUrl() {
        when(bookmarkRepository.findByLongUrl(LONG_URL, owner)).thenReturn(new Bookmark(HASH, longUrl, owner));

        Bookmark link = urlService.lookup(LONG_URL, owner);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LONG_URL, link.getLongUrl().getUrl());
        Assert.assertEquals(GLOBAL_HASH, link.getLongUrl().getHash());
        Assert.assertEquals(owner, link.getOwner());
    }


    @Test
    public void lookupNonExistingUrl() {
        Assert.assertNull(urlService.lookup(LONG_URL, owner));
    }

    @Test
    public void redirectOnRedirectRule() {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(DOMAIN, "/foo", LONG_URL.toString())));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));
        
        
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertEquals(LONG_URL, uri);
    }

    @Test
    public void redirectOnRedirectRuleWithPlaceHolder() {
    	RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
    	when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(DOMAIN, "/foo(.+)", "http://example.com/{1}")));
    	urlService.setRedirectRuleRepository(redirectRuleRepository);
    	
    	urlService.setShortLinkRepository(mock(BookmarkRepository.class));
    	
    	
    	URI uri = urlService.redirect(DOMAIN, "foobar");
    	
    	Assert.assertEquals(URI.create("http://example.com/bar"), uri);
    }
    
    @Test
    public void redirectOnRedirectRuleNullDomain() {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(null, "/foo", LONG_URL.toString())));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));
        
        
        URI uri = urlService.redirect(null, "foo");
        
        Assert.assertEquals(LONG_URL, uri);
    }

    @Test
    public void redirectOnRedirectRuleDomainNotMatching() {
        RedirectRuleRepository redirectRuleRepository = mock(RedirectRuleRepository.class);
        when(redirectRuleRepository.findAll()).thenReturn(Arrays.asList(new RedirectRule(DOMAIN, "/foo", LONG_URL.toString())));
        urlService.setRedirectRuleRepository(redirectRuleRepository);
        
        urlService.setShortLinkRepository(mock(BookmarkRepository.class));
        
        
        Assert.assertNull(urlService.redirect("dummy", "foo"));
    }

    
    @Test
    public void redirectOnShortLink() {
        urlService.setRedirectRuleRepository(mock(RedirectRuleRepository.class));
        when(bookmarkRepository.findByHash(HASH, owner)).thenReturn(new Bookmark(HASH, longUrl, owner));
        
        URI uri = urlService.redirect(DOMAIN, "/u/" + owner.getName() + "/b/" + HASH);

        Assert.assertEquals(LONG_URL, uri);
    }
    
    @Test
    public void redirectOnShortLinkDomainNoMatching() {
        BookmarkRepository shortLinkRepository = mock(BookmarkRepository.class);
        when(shortLinkRepository.findByHash(HASH, owner)).thenReturn(new Bookmark(HASH, longUrl, owner));
        urlService.setShortLinkRepository(shortLinkRepository);

        Assert.assertNull(urlService.redirect("dummy", "dummy"));
    }

    
    @Test
    public void redirectNoneMatching() {
        URI uri = urlService.redirect(DOMAIN, "foo");

        Assert.assertNull(uri);
    }
    
}
