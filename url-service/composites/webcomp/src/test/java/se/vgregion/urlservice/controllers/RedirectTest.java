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


public class RedirectTest {

    private RedirectController controller = new RedirectController();
    private MockHttpServletResponse response = new MockHttpServletResponse();
    
    @Before
    public void setup() {
        controller.setUrlServiceService(new MockUrlServiceService());
    }


    @Test
    public void redirectWithExistingHash() throws IOException {
        controller.redirect("foo", response);
        
        Assert.assertEquals(301, response.getStatus());
        Assert.assertEquals("http://example.com", response.getHeader("Location"));
    }

    @Test
    public void redirectWithNonExistingHash() throws IOException {
        controller.redirect("dummy", response);
        
        Assert.assertEquals(404, response.getStatus());
    }

}
