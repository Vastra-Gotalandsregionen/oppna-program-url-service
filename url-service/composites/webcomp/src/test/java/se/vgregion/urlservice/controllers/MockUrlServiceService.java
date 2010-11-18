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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.ShortLink;

@Service
public class MockUrlServiceService implements UrlServiceService {

    private final Logger log = LoggerFactory.getLogger(MockUrlServiceService.class);
    private static final String URL_PREFIX = "http://s.vgregion.se/";
    private static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[] {"http", "https"});
    
    public MockUrlServiceService() {
        log.info("Created {}", MockUrlServiceService.class.getName());
    }

    /* (non-Javadoc)
     * @see se.vgregion.urlservice.services.UrlServiceService#shorten(java.lang.String)
     */
    public ShortLink shorten(String urlString) throws URISyntaxException {
        return shorten(urlString, null);
    }
    
    public ShortLink expand(String hashOrShortUrl) {
        if(hashOrShortUrl.equals("foo") ||
                hashOrShortUrl.equals("http://s.vgregion.se/foo")) {
            return new ShortLink("foo", "http://example.com", URL_PREFIX + "foo");
        } else {
            return null;
        }
    }

    @Override
    public ShortLink lookup(String url) throws URISyntaxException {
        return new ShortLink("foo", url, URL_PREFIX + "foo");
    }

    @Override
    public ShortLink shorten(String urlString, String hash) throws URISyntaxException {
        URI url = new URI(urlString);
        
        if(WHITELISTED_SCHEMES.contains(url.getScheme())) {
            hash = (hash != null) ? hash : "foo"; 
            
            return new ShortLink(hash, urlString, URL_PREFIX + hash);
        } else {
            throw new URISyntaxException(urlString, "Scheme not allowed");
        }

    }

    @Override
    public URI redirect(String path) {
        if(expand(path) != null) {
            return URI.create(expand(path).getUrl()); 
        } else if(path.equals("bar")) {
            return URI.create("http://google.com");
        } else {
            return null;
        }
    }
}
