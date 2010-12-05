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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.types.Keyword;


public class ShortenGuiTest {

    private static final URI LONG_URL = URI.create("http://example.com");
    private static final URI SHORT_LINK_PREFIX = URI.create("http://s.vgregion.se");
    
    private MockUrlServiceService urlServiceService = new MockUrlServiceService();
    private ShortenGuiController controller = new ShortenGuiController(urlServiceService, SHORT_LINK_PREFIX);
    
    private User user = new User("roblu", "password", true, true, true, true, Arrays.asList(new GrantedAuthorityImpl("ROLE_USER")));
    private Authentication authentication = new TestingAuthenticationToken(user, "credentials");
    
    @Before
    public void before() {

    }
    
    @Test
    public void shortenLongUrl() throws IOException {
        ModelAndView  mav = controller.index(LONG_URL, null, null, authentication);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals(LONG_URL, mav.getModel().get("longUrl"));
        Assert.assertNull(mav.getModel().get("slug"));
        Assert.assertEquals("http://s.vgregion.se/foo", mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }

    @Test
    public void shortenLongUrlWithSlug() throws IOException {
        ModelAndView  mav = controller.index(LONG_URL, "slug", null, authentication);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals(LONG_URL, mav.getModel().get("longUrl"));
        Assert.assertEquals("slug", mav.getModel().get("slug"));
        Assert.assertEquals("http://s.vgregion.se/slug", mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }

    @Test
    public void shortenLongUrlWithSlugAndOwner() throws IOException {
        ModelAndView mav = controller.index(LONG_URL, "slug", null, authentication);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals(LONG_URL, mav.getModel().get("longUrl"));
        Assert.assertEquals("slug", mav.getModel().get("slug"));
        Assert.assertEquals("http://s.vgregion.se/slug", mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }

    @Test
    public void shortenLongUrlWithKeywords() throws IOException {
        List<Keyword> keywords = urlServiceService.getAllKeywords(); 
        List<UUID> keywordIds = Arrays.asList(keywords.get(0).getId(), keywords.get(1).getId()); 
        List<String> keywordNames = Arrays.asList(keywords.get(0).getName(), keywords.get(1).getName()); 
        
        ModelAndView mav = controller.index(LONG_URL, "slug", keywordNames, authentication);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals(LONG_URL, mav.getModel().get("longUrl"));
        Assert.assertEquals("slug", mav.getModel().get("slug"));
        Assert.assertEquals(keywords, mav.getModel().get("keywords"));
        Assert.assertEquals(keywordIds, mav.getModel().get("selectedKeywords"));
        Assert.assertEquals("http://s.vgregion.se/slug", mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }

    
    @Test
    public void shortenInvalidLongUrl() throws IOException {
        URI invalidUrl = URI.create("dummy://example.com");
        ModelAndView  mav = controller.index(invalidUrl, null, null, authentication);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertEquals(invalidUrl, mav.getModel().get("longUrl"));
        Assert.assertNull(mav.getModel().get("slug"));
        Assert.assertNotNull(mav.getModel().get("error"));
    }

    
    @Test
    public void show() throws IOException {
        ModelAndView  mav = controller.index(null, null, null, authentication);
        
        Assert.assertEquals("shorten", mav.getViewName());
        Assert.assertNull(mav.getModel().get("longUrl"));
        Assert.assertNull(mav.getModel().get("slug"));
        Assert.assertNull(mav.getModel().get("shortUrl"));
        Assert.assertNull(mav.getModel().get("error"));
    }
    
    @After
    public void after() {
        SecurityContextImpl ctx = new SecurityContextImpl();
        SecurityContextHolder.setContext(ctx);

    }

}
