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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.Owner;


public class RedirectTest {
	
	@Mock private UrlServiceService urlServiceService;
    
	private RedirectController controller;
    private MockHttpServletResponse response = new MockHttpServletResponse();
    
    private static final URI SHORT_LINK_PREFIX = URI.create("http://s.vgregion.se");
    private static final String HASH = "987654";
    private static final String GLOBAL_HASH = "abcdef";
    private static final URI LONG_URI = URI.create("http://example.com");
    private static final Owner OWNER = new Owner("roblu");
    
    @Before
    public void before() {
    	MockitoAnnotations.initMocks(this);

    	controller = new RedirectController(urlServiceService);

    	when(urlServiceService.redirect(SHORT_LINK_PREFIX.toString(), "/b/" + GLOBAL_HASH)).thenReturn(LONG_URI);
    	when(urlServiceService.redirect(SHORT_LINK_PREFIX.toString(), "/u/" + OWNER.getName() + "/b/" + HASH)).thenReturn(LONG_URI);

    	when(urlServiceService.redirect(SHORT_LINK_PREFIX.toString(), "/bar")).thenReturn(LONG_URI);
    }
    
    @Test
    public void redirectWithExistingGlobalHash() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", SHORT_LINK_PREFIX + "/b/" + GLOBAL_HASH);
        request.setServerName("s.vgregion.se");
        request.setPathInfo("/b/" + GLOBAL_HASH);
        ModelAndView mav = controller.redirect("/b/" + GLOBAL_HASH, request, response);
        
        Assert.assertEquals(301, response.getStatus());
        Assert.assertEquals("http://example.com", response.getHeader("Location"));
        Assert.assertEquals("http://example.com", mav.getModel().get("longUrl"));
    }

    @Test
    public void redirectWithExistingUserHash() throws IOException {
    	String path = "/u/" + OWNER.getName() + "/b/" + HASH;
    	MockHttpServletRequest request = new MockHttpServletRequest("GET", SHORT_LINK_PREFIX + path);
    	request.setServerName("s.vgregion.se");
    	request.setPathInfo(path);
    	ModelAndView mav = controller.redirect(path, request, response);
    	
    	Assert.assertEquals(301, response.getStatus());
    	Assert.assertEquals("http://example.com", response.getHeader("Location"));
    	Assert.assertEquals("http://example.com", mav.getModel().get("longUrl"));
    }

    @Test
    public void redirectWithNonExistingHash() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://s.vgregion.se/dummy");
        request.setPathInfo("/dummy");
        ModelAndView mav = controller.redirect("dummy", request, response);
        
        Assert.assertEquals(404, response.getStatus());
        Assert.assertNull(mav);
    }

    
    @Test
    public void redirectWithRedirectRule() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://s.vgregion.se/bar");
        request.setServerName("s.vgregion.se");
        request.setPathInfo("/bar");
        ModelAndView mav = controller.redirect("/bar", request, response);
        
        Assert.assertEquals(301, response.getStatus());
        Assert.assertEquals("http://example.com", response.getHeader("Location"));
        Assert.assertEquals("http://example.com", mav.getModel().get("longUrl"));
    }

}
