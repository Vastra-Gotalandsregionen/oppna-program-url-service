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

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.types.Keyword;


public class BookmarkControllerTest {

    private static final URI LONG_URL = URI.create("http://example.com");
    private static final URI SHORT_LINK_PREFIX = URI.create("http://s.vgregion.se");
    
    private MockUrlServiceService urlServiceService = new MockUrlServiceService();
    private BookmarkController controller = new BookmarkController(urlServiceService, SHORT_LINK_PREFIX);
    
    private User user = new User("roblu", "password", true, true, true, true, Arrays.asList(new GrantedAuthorityImpl("ROLE_USER")));
    private Authentication authentication = new TestingAuthenticationToken(user, "credentials");
    
    @Before
    public void before() {

    }
    
    @Test
    public void shortenLongUrl() throws IOException {
        ModelAndView  mav = controller.create(LONG_URL, null, null, authentication);
        
        Assert.assertEquals("redirect:/u/roblu/b/foo/edit", mav.getViewName());
    }

    @Test
    public void shortenLongUrlWithSlug() throws IOException {
        ModelAndView  mav = controller.create(LONG_URL, "slug", null, authentication);
        
        Assert.assertEquals("redirect:/u/roblu/b/slug/edit", mav.getViewName());
    }

    @Test
    public void shortenLongUrlWithSlugAndOwner() throws IOException {
        ModelAndView mav = controller.create(LONG_URL, "slug", null, authentication);
        
        Assert.assertEquals("redirect:/u/roblu/b/slug/edit", mav.getViewName());
    }

    @Test
    public void shortenLongUrlWithKeywords() throws IOException {
        List<Keyword> keywords = urlServiceService.getAllKeywords(); 
        List<String> keywordNames = Arrays.asList(keywords.get(0).getName(), keywords.get(1).getName()); 
        String keywordNameString = StringUtils.join(keywordNames, " ");
        
        ModelAndView mav = controller.create(LONG_URL, "slug", keywordNameString, authentication);
        
        Assert.assertEquals("redirect:/u/roblu/b/slug/edit", mav.getViewName());
    }

    
    @Test(expected=IllegalArgumentException.class)
    public void shortenInvalidLongUrl() throws IOException {
        URI invalidUrl = URI.create("dummy://example.com");
        controller.create(invalidUrl, null, null, authentication);
    }

    @Test
    public void redirectWithExistingHash() throws IOException {
    	MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        ModelAndView mav = controller.redirect("foo", user.getUsername(), request, response);
        
        Assert.assertEquals(301, response.getStatus());
        Assert.assertEquals("http://example.com", response.getHeader("Location"));
        Assert.assertEquals("http://example.com", mav.getModel().get("longUrl"));
    }

    @Test
    public void redirectWithNonExistingHash() throws IOException {
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	MockHttpServletResponse response = new MockHttpServletResponse();
        ModelAndView mav = controller.redirect("dummy", user.getUsername(), request, response);
        
        Assert.assertEquals(404, response.getStatus());
        Assert.assertNull(mav);
    }

    
    @After
    public void after() {
        SecurityContextImpl ctx = new SecurityContextImpl();
        SecurityContextHolder.setContext(ctx);

    }

}
