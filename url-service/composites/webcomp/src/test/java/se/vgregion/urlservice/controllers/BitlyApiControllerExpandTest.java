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

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.Application;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Owner;


public class BitlyApiControllerExpandTest {

    private static final URI SHORT_LINK_PREFIX = URI.create("http://s.vgregion.se");
    
    @Mock private UrlServiceService urlServiceService;
    
    private BitlyApiController controller;
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private static final String HASH = "987654";
    private static final String GLOBAL_HASH = "abcdef";
    private static final String API_KEY = "123456";
    private static final URI LONG_URI = URI.create("http://example.com");
    private static final Owner OWNER = new Owner("roblu");
    private static final Application APPLICATION = new Application("app");
    private static final LongUrl LONG_URL = new LongUrl(LONG_URI, GLOBAL_HASH);
    private static final Bookmark BOOKMARK = new Bookmark(HASH, LONG_URL, OWNER);

    @Before
    public void before() {
    	MockitoAnnotations.initMocks(this);

    	controller = new BitlyApiController(urlServiceService, SHORT_LINK_PREFIX);

    	when(urlServiceService.expandGlobal(GLOBAL_HASH)).thenReturn(LONG_URL);
    	when(urlServiceService.expand(HASH, OWNER)).thenReturn(BOOKMARK);
    	when(urlServiceService.expandPath(URI.create(SHORT_LINK_PREFIX + "/b/" + GLOBAL_HASH))).thenReturn(LONG_URL);
    	when(urlServiceService.expandPath(URI.create(SHORT_LINK_PREFIX + "/u/" + OWNER.getName() + "/b/" + HASH))).thenReturn(BOOKMARK);
    	when(urlServiceService.getApplication(API_KEY)).thenReturn(APPLICATION);
    	when(urlServiceService.getUser(OWNER.getName())).thenReturn(OWNER);
    }
    
    @Test
    public void jsonResponseGlobalHash() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://s.vgregion.se/b/" + GLOBAL_HASH)), null, "json", API_KEY, response);

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(
                        "{\"status_code\":200," +
                        "\"status_txt\":\"OK\"," +
                        "\"data\":{" +
                        "\"expand\":[{" +
                        "\"hash\":\"" + GLOBAL_HASH + "\"," +
                        "\"short_url\":\"http://s.vgregion.se/b/" + GLOBAL_HASH + "\"," +
                        "\"global_hash\":\"" + GLOBAL_HASH + "\"," +
                        "\"long_url\":\"http://example.com\"" +
                        "}]}}", response.getContentAsString());
    }

    @Test
    public void jsonResponseUserHash() throws IOException {
    	controller.expand(Arrays.asList(URI.create("http://s.vgregion.se/u/roblu/b/" + HASH)), null, "json", API_KEY, response);
    	
    	
    	Assert.assertEquals(200, response.getStatus());
    	Assert.assertEquals(
    			"{\"status_code\":200," +
    			"\"status_txt\":\"OK\"," +
    			"\"data\":{" +
    			"\"expand\":[{" +
    			"\"hash\":\"" + HASH + "\"," +
    			"\"short_url\":\"http://s.vgregion.se/b/" + GLOBAL_HASH + "\"," +
    			"\"global_hash\":\"" + GLOBAL_HASH + "\"," +
    			"\"long_url\":\"http://example.com\"," +
    			"\"user_hash\":\"" + HASH + "\"" +
    			"}]}}", response.getContentAsString());
    }

    @Test
    public void xmlResponse() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://s.vgregion.se/b/" + GLOBAL_HASH)), null, "xml", API_KEY, response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(
                "<response><status_code>200</status_code>" +
                "<status_txt>OK</status_txt>" +
                "<data><entry>" +
                "<hash>" + GLOBAL_HASH + "</hash>" +
                "<short_url>http://s.vgregion.se/b/" + GLOBAL_HASH + "</short_url>" +
                "<global_hash>" + GLOBAL_HASH + "</global_hash>" +
                "<long_url>http://example.com</long_url>" +
                "</entry></data></response>", response.getContentAsString());
    }

    
    @Test
    public void txtResponse() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://s.vgregion.se/b/" + GLOBAL_HASH)), null, "txt", API_KEY, response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("http://example.com", response.getContentAsString());
    }

    @Test
    public void unknownFormatMustNotBeAllowed() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://example.com")), null, "unknown", API_KEY, response);
        
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void noShortLinkNorHashMustBeRefused() throws IOException {
        controller.expand(null, null, "txt", API_KEY, response);
        
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void invalidApiKey() throws IOException {
        controller.expand(Arrays.asList(URI.create("http://s.vgregion.se/foo")), null, "json", "dummy", response);

        Assert.assertEquals(500, response.getStatus());
    }

}
