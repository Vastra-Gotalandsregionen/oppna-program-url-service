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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.Application;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Owner;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.StaticRedirect;

public class MockUrlServiceService implements UrlServiceService {

    private final Logger log = LoggerFactory.getLogger(MockUrlServiceService.class);
    private static final String DOMAIN = "s.vgregion.se";
    private static final String URL_PREFIX = "http://s.vgregion.se/";
    private static final String GLOBAL_HASH = "abcdef";
    private static final String HASH = "foo";
    private static final String USERNAME = "roblu";
    private static final URI LONG_URL = URI.create("http://example.com");
    
    private static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[] {"http", "https"});
    
    private List<Keyword> keywords = Arrays.asList(new Keyword("kw1"), new Keyword("kw2"));
    
    public MockUrlServiceService() {
        log.info("Created {}", MockUrlServiceService.class.getName());
    }

    @Override
    public LongUrl expandGlobal(String hash) {
        if(hash.equals("foo")) {
            return new LongUrl(LONG_URL, GLOBAL_HASH);
        } else {
            return null;
        }
    }

    
    @Override
    public Bookmark expand(String hash) {
        if(hash.equals("foo")) {
            return new Bookmark(hash, new LongUrl(LONG_URL, GLOBAL_HASH), keywords, new Owner(USERNAME));
        } else {
            return null;
        }
    }
    
    @Override
    public Bookmark expand(URI shortUrl) throws URISyntaxException {
        return new Bookmark(HASH, new LongUrl(LONG_URL, GLOBAL_HASH), keywords, new Owner(USERNAME));
    }

    @Override
    public URI redirect(String domain, String path) {
        if(expand(path) != null) {
            return expand(path).getLongUrl().getUrl(); 
        } else if(path.equals("bar")) {
            return URI.create("http://google.com");
        } else {
            return null;
        }
    }


    @Override
    public Owner getUser(String vgrId) {
        return new Owner(vgrId);
    }

    @Override
    public List<Keyword> getAllKeywords() {
        return keywords;
    }

    @Override
    public void createRedirectRule(RedirectRule rule) {
        
    }

    @Override
    public void createStaticRedirect(StaticRedirect redirect) {
        
    }

    @Override
    public Collection<StaticRedirect> findAllStaticRedirects() {
        return null;
    }

    @Override
    public Collection<RedirectRule> findAllRedirectRules() {
        return null;
    }

    @Override
    public void removeRedirectRule(UUID id) {
        
    }

    @Override
    public void removeStaticRedirect(UUID id) {
        
    }

    @Override
    public Bookmark shorten(URI url, Owner owner) {
        return shorten(url, null, owner);
    }

    @Override
    public Bookmark shorten(URI url, String hash, Owner owner) {
        return shorten(url, hash, Collections.<String>emptyList(), owner);
    }

    @Override
    public Bookmark shorten(URI url, String hash, Collection<String> keywordNames, Owner owner) {
        if(WHITELISTED_SCHEMES.contains(url.getScheme())) {
            hash = (hash != null) ? hash : HASH; 
            
            return new Bookmark(hash, new LongUrl(url, GLOBAL_HASH), keywords, owner);
        } else {
            throw new IllegalArgumentException("Scheme not allowed");
        }
    }

    @Override
    public Bookmark lookup(URI url, Owner owner) {
        return new Bookmark(HASH, new LongUrl(url, GLOBAL_HASH), Collections.<Keyword>emptyList(), owner);
    }

    @Override
    public Bookmark updateBookmark(String hash, String slug, Collection<String> keywordNames) {
        return new Bookmark(hash, new LongUrl(LONG_URL, GLOBAL_HASH), Collections.<Keyword>emptyList(), new Owner(USERNAME));
    }

    @Override
    public void addHit(Bookmark bookmark) {
        
    }

    @Override
    public void addHit(LongUrl longUrl) {
        
    }

    @Override
    public Application getApplication(String apiKey) {
        if("123456".equals(apiKey)) {
            return new Application("test");
        } else {
            return null;
        }
    }

    @Override
    public void createApplication(Application application) {
        
    }

    @Override
    public Collection<Application> findAllApplications() {
        return null;
    }

    @Override
    public void removeApplication(UUID id) {
        
    }

    @Override
    public Collection<Owner> findAllUsers() {
        return null;
    }
}
