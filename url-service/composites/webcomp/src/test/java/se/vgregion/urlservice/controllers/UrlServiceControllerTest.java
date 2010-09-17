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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class UrlServiceControllerTest {

    UrlServiceController controller = new UrlServiceController();
    
    @Before
    public void setup() {
        controller.setUrlServiceService(new MockUrlServiceService());
    }


    @Test
    public void jsonResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        controller.shorten("http://example.com", "json", response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("{\"status_code\": 200,\"data\": " +
        		"{\"url\": \"http://example.com/foo\"," +
        		"\"hash\": \"foo\", " +
        		"\"global_hash\": \"foo\", " +
        		"\"long_url\": \"http://example.com\"," +
        		"\"new_hash\": 0}, " +
        		"\"status_txt\": \"OK\"}", response.getContentAsString());
    }
    
    @Test
    public void txtResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        controller.shorten("http://example.com", "txt", response);
        
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("foo", response.getContentAsString());
    }

    @Test
    public void unknownFormatMustNotBeAllowed() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        controller.shorten("http://example.com", "unknown", response);
        
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void invalidUrlMustBeRefused() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        controller.shorten("invalid", "txt", response);
        
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void httpUrlShouldBeAllowed() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        controller.shorten("http://example.com", "txt", response);
        
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void HttpsUrlShouldBeAllowed() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        controller.shorten("https://example.com", "txt", response);
        
        Assert.assertEquals(200, response.getStatus());
    }

}
