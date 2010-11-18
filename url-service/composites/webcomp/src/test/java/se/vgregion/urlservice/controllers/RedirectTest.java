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
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;


public class RedirectTest {

    private RedirectController controller = new RedirectController(new MockUrlServiceService());
    private MockHttpServletResponse response = new MockHttpServletResponse();
    
    @Test
    public void redirectWithExistingHash() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://s.vgregion.se/foo");
        request.setServerName("s.vgregion.se");
        request.setPathInfo("/foo");
        ModelAndView mav = controller.redirect(request, response);
        
        Assert.assertEquals(301, response.getStatus());
        Assert.assertEquals("http://example.com", response.getHeader("Location"));
        Assert.assertEquals("http://example.com", mav.getModel().get("longUrl"));
    }

    @Test
    public void redirectWithNonExistingHash() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://s.vgregion.se/dummy");
        request.setPathInfo("/dummy");
        ModelAndView mav = controller.redirect(request, response);
        
        Assert.assertEquals(404, response.getStatus());
        Assert.assertNull(mav);
    }

    
    @Test
    public void redirectWithRedirectRule() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "http://s.vgregion.se/bar");
        request.setPathInfo("/bar");
        ModelAndView mav = controller.redirect(request, response);
        
        Assert.assertEquals(301, response.getStatus());
        Assert.assertEquals("http://google.com", response.getHeader("Location"));
        Assert.assertEquals("http://google.com", mav.getModel().get("longUrl"));
    }

}
