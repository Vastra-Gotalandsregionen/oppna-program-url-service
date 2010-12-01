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
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.ShortLink;
import se.vgregion.urlservice.types.User;

public class MockUrlServiceService implements UrlServiceService {

    private final Logger log = LoggerFactory.getLogger(MockUrlServiceService.class);
    private static final String DOMAIN = "s.vgregion.se";
    private static final String URL_PREFIX = "http://s.vgregion.se/";
    private static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[] {"http", "https"});
    
    private List<Keyword> keywords = Arrays.asList(new Keyword("kw1"), new Keyword("kw2"));
    
    public MockUrlServiceService() {
        log.info("Created {}", MockUrlServiceService.class.getName());
    }

    /* (non-Javadoc)
     * @see se.vgregion.urlservice.services.UrlServiceService#shorten(java.lang.String)
     */
    public ShortLink shorten(String urlString) throws URISyntaxException {
        return shorten(urlString, null);
    }
    
    @Override
    public ShortLink expand(String domain, String hash) {
        if(hash.equals("foo")) {
            return new ShortLink(DOMAIN, "foo", "http://example.com", URL_PREFIX + "foo");
        } else {
            return null;
        }
    }
    
    public ShortLink expand(String shortUrl) {
        if(shortUrl.equals("http://s.vgregion.se/foo")) {
            return new ShortLink(DOMAIN, "foo", "http://example.com", URL_PREFIX + "foo");
        } else {
            return null;
        }
    }

    @Override
    public ShortLink lookup(String url) throws URISyntaxException {
        return new ShortLink(DOMAIN, "foo", url, URL_PREFIX + "foo");
    }

    @Override
    public ShortLink shorten(String urlString, String hash) throws URISyntaxException {
        return shorten(urlString, hash, null);
    }

    @Override
    public URI redirect(String domain, String path) {
        if(expand(domain, path) != null) {
            return URI.create(expand(domain, path).getUrl()); 
        } else if(path.equals("bar")) {
            return URI.create("http://google.com");
        } else {
            return null;
        }
    }

    @Override
    public ShortLink shorten(String urlString, String hash, User owner) throws URISyntaxException {
        return shorten(urlString, hash, null, owner);
    }
    
    @Override
    public ShortLink shorten(String urlString, String hash, Collection<UUID> keywordIds, User owner)
            throws URISyntaxException {
        URI url = new URI(urlString);
        
        if(WHITELISTED_SCHEMES.contains(url.getScheme())) {
            hash = (hash != null) ? hash : "foo"; 
            if(owner != null) {
                hash = owner.getVgrId() + "/" + hash;
            }
            
            return new ShortLink(DOMAIN, hash, urlString, URL_PREFIX + hash, null, owner);
        } else {
            throw new URISyntaxException(urlString, "Scheme not allowed");
        }
    }


    @Override
    public User getUser(String vgrId) {
        return new User(vgrId);
    }

    @Override
    public List<Keyword> getAllKeywords() {
        return keywords;
    }


}
