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

import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import se.vgregion.urlservice.types.ShortLink;

public class UrlServiceServiceTest {

    private static final String SHORT_URL = "http://short";
    private static final String HASH = "foo";
    private static final String LOGN_URL = "http://example.com";
    private DefaultUrlServiceService urlService = new DefaultUrlServiceService();

    @Test
    public void shortenNonExistingUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository());

        ShortLink link = urlService.shorten(LOGN_URL);

        Assert.assertEquals("a9b9f0", link.getHash());
        Assert.assertEquals(LOGN_URL, link.getLongUrl());
    }

    @Test
    public void shortenWithSlug() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository());

        ShortLink link = urlService.shorten(LOGN_URL, "my_slug");

        Assert.assertEquals("my_slug", link.getHash());
        Assert.assertEquals(LOGN_URL, link.getLongUrl());
    }

    @Test
    public void shortenWithSlugCollision() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository() {

            @Override
            public ShortLink findByHash(String hash) {
                // make sure the first hash collides
                if("my_slug".equals(hash)) {
                    return new ShortLink("my_slug", "http://someurl", SHORT_URL);
                } else {
                    return null;
                }
            }
        });


        ShortLink link = urlService.shorten(LOGN_URL, "my_slug");

        Assert.assertEquals("my_slug4", link.getHash());
        Assert.assertEquals(LOGN_URL, link.getLongUrl());
    }

    
    @Test
    public void shortenWithHashCollision() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository() {

            @Override
            public ShortLink findByHash(String hash) {
                // make sure the first hash collides
                if("a9b9f0".equals(hash)) {
                    return new ShortLink("a9b9f0", "http://someurl", SHORT_URL);
                } else {
                    return null;
                }
            }
        });

        ShortLink link = urlService.shorten(LOGN_URL);

        // since the hash collides, we should get a longer hash
        Assert.assertEquals("a9b9f04", link.getHash());
        Assert.assertEquals(LOGN_URL, link.getLongUrl());
    }

    
    @Test
    public void shortenExistingUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository() {
            @Override
            public ShortLink findByLongUrl(String url) {
                return new ShortLink(HASH, url, SHORT_URL);
            }
            
        });

        ShortLink link = urlService.shorten(LOGN_URL);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LOGN_URL, link.getLongUrl());
    }

    @Test(expected=URISyntaxException.class)
    public void shortenInvalidUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository());

        urlService.shorten("invalid");
    }

    @Test
    public void expandExistingHash() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository() {
            @Override
            public ShortLink findByHash(String hash) {
                return new ShortLink(hash, LOGN_URL, SHORT_URL);
            }
        });

        ShortLink link = urlService.expand(HASH);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LOGN_URL, link.getLongUrl());
    }

    @Test
    public void expandExistingShortUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository() {
            @Override
            public ShortLink findByHash(String hash) {
                return new ShortLink(hash, LOGN_URL, SHORT_URL);
            }
        });

        ShortLink link = urlService.expand("http://s.vgregion.se/foo");

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LOGN_URL, link.getLongUrl());
    }

    
    @Test
    public void expandNonExistingHash() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository());

        Assert.assertNull(urlService.expand(HASH));
    }
    
    
    @Test
    public void lookupExistingUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository() {
            @Override
            public ShortLink findByLongUrl(String url) {
                return new ShortLink(HASH, url, SHORT_URL);
            }
        });

        ShortLink link = urlService.lookup(LOGN_URL);

        Assert.assertEquals(HASH, link.getHash());
        Assert.assertEquals(LOGN_URL, link.getLongUrl());
    }


    @Test
    public void lookupNonExistingUrl() throws URISyntaxException {
        urlService.setShortLinkRepository(new MockShortLinkRepository());

        Assert.assertNull(urlService.lookup(LOGN_URL));
    }

}
