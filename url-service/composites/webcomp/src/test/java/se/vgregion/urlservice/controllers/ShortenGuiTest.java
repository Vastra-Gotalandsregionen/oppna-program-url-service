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
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.ModelAndView;


public class ShortenGuiTest {

    private ShortenGuiController controller = new ShortenGuiController(new MockUrlServiceService());
    
    @Test
    public void shortenLongUrl() throws IOException {
        ModelAndView  mav = controller.index("http://example.com", null);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals("http://example.com", mav.getModel().get("longUrl"));
        Assert.assertNull(mav.getModel().get("slug"));
        Assert.assertEquals("http://s.vgregion.se/foo", mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }

    @Test
    public void shortenLongUrlWithSlug() throws IOException {
        ModelAndView  mav = controller.index("http://example.com", "slug");
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals("http://example.com", mav.getModel().get("longUrl"));
        Assert.assertEquals("slug", mav.getModel().get("slug"));
        Assert.assertEquals("http://s.vgregion.se/slug", mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }

    @Test
    public void shortenLongUrlWithSlugAndOwner() throws IOException {
        SecurityContextImpl ctx = new SecurityContextImpl();
        User user = new User("roblu", "password", true, true, true, true, Arrays.asList(new GrantedAuthorityImpl("ROLE_USER")));
        Authentication authentication = new TestingAuthenticationToken(user, "credentials");
        ctx.setAuthentication(authentication);
        SecurityContextHolder.setContext(ctx);
        
        ModelAndView mav = controller.index("http://example.com", "slug");
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals("http://example.com", mav.getModel().get("longUrl"));
        Assert.assertEquals("slug", mav.getModel().get("slug"));
        Assert.assertEquals("http://s.vgregion.se/roblu/slug", mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }

    
    @Test
    public void shortenInvalidLongUrl() throws IOException {
        ModelAndView  mav = controller.index("dummy://example.com", null);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals("dummy://example.com", mav.getModel().get("longUrl"));
        Assert.assertNull(mav.getModel().get("slug"));
        Assert.assertNotNull(mav.getModel().get("error"));
    }

    
    @Test
    public void show() throws IOException {
        ModelAndView  mav = controller.index(null, null);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertNull(mav.getModel().get("longUrl"));
        Assert.assertNull(mav.getModel().get("slug"));
        Assert.assertNull(mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }

}
