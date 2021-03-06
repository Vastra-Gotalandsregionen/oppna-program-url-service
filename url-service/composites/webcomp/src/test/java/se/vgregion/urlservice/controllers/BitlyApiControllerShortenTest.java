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

import java.io.IOException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class BitlyApiControllerShortenTest {

    private static final URI SHORT_LINK_PREFIX = URI.create("http://s.vgregion.se");
    private BitlyApiController controller = new BitlyApiController(new MockUrlServiceService(), SHORT_LINK_PREFIX);
    private static final URI LONG_URL = URI.create("http://example.com");
    private static final String GLOBAL_HASH = "abcdef";
    private static final String API_KEY = "123456";
    private MockHttpServletResponse response = new MockHttpServletResponse();
    
    @Test
    public void jsonResponse() throws IOException {
        controller.shorten(LONG_URL, "json", API_KEY, response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(
                        "{\"status_code\":200," +
                        "\"status_txt\":\"OK\"," +
        		"\"data\":{" +
        		"\"url\":\"http://s.vgregion.se/u/test/b/foo\"," +
        		"\"hash\":\"foo\"," +
        		"\"global_hash\":\"" + GLOBAL_HASH + "\"," +
        		"\"long_url\":\"http://example.com\"," +
        		"\"new_hash\":0}" +
        		"}", response.getContentAsString());
    }

    @Test
    public void xmlResponse() throws IOException {
        controller.shorten(LONG_URL, "xml", API_KEY, response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(
                        "<response>" +
                        "<status_code>200</status_code>" +
                        "<status_txt>OK</status_txt>" +
                        "<data>" +
                        "<url>http://s.vgregion.se/u/test/b/foo</url>" +
                        "<hash>foo</hash>" +
                        "<global_hash>" + GLOBAL_HASH + "</global_hash>" +
                        "<long_url>http://example.com</long_url>" +
                        "<new_hash>0</new_hash>" +
                        "</data></response>", response.getContentAsString());
    }

    
    @Test
    public void txtResponse() throws IOException {
        controller.shorten(LONG_URL, "txt", API_KEY, response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("foo", response.getContentAsString());
    }

    @Test
    public void unknownFormatMustNotBeAllowed() throws IOException {
        controller.shorten(LONG_URL, "unknown", API_KEY, response);
        
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void invalidUrlMustBeRefused() throws IOException {
        controller.shorten(URI.create("dummy:/invalid"), "txt", API_KEY, response);
        
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void httpUrlShouldBeAllowed() throws IOException {
        controller.shorten(LONG_URL, "txt", API_KEY, response);
        
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void HttpsUrlShouldBeAllowed() throws IOException {
        controller.shorten(URI.create("https://example.com"), "txt", API_KEY, response);
        
        Assert.assertEquals(200, response.getStatus());
    }
    
    @Test
    public void invalidApiKey() throws IOException {
        controller.shorten(LONG_URL, "txt", "dummy", response);

        Assert.assertEquals(500, response.getStatus());
    }


}
