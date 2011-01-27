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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.Application;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.StaticRedirect;


public class AdminGuiControllerTest {

    private static final String PATTERN = "foo";
    private static final String DOMAIN = "vgregion.se";
    private static final URI URL = URI.create("http://google.com");
    private AdminGuiController controller = new AdminGuiController();
    
    @Test
    public void index() throws IOException {
        UrlServiceService urlServiceService = mock(UrlServiceService.class);
        
        List<RedirectRule> rules = Arrays.asList(new RedirectRule(DOMAIN, PATTERN, URL));
        when(urlServiceService.findAllRedirectRules()).thenReturn(rules);

        List<StaticRedirect> statics = Arrays.asList(new StaticRedirect(DOMAIN, PATTERN, URL));
        when(urlServiceService.findAllStaticRedirects()).thenReturn(statics);
        
        controller.setUrlServiceService(urlServiceService);

        
        ModelAndView mav = controller.index();
        
        Assert.assertEquals(rules, mav.getModel().get("redirectRules"));
        Assert.assertEquals(statics, mav.getModel().get("staticRedirects"));
    }
    
    @Test
    public void addRedirectRule() throws IOException {
        UrlServiceService urlServiceService = mock(UrlServiceService.class);
        controller.setUrlServiceService(urlServiceService);
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("add", "any value");
        request.addParameter("pattern", PATTERN);
        request.addParameter("url", URL.toString());
        
        controller.updateRedirectRules(request);

        verify(urlServiceService).createRedirectRule(Mockito.argThat(new TypeSafeMatcher<RedirectRule>() {
            @Override
            public boolean matchesSafely(RedirectRule rule) {
                return rule.getPattern().equals(PATTERN) && rule.getUrl().equals(URL);
            }
            @Override
            public void describeTo(Description arg0) {
            }}));

    }

    @Test
    public void deleteRedirectRule() throws IOException {
        UrlServiceService urlServiceService = mock(UrlServiceService.class);
        controller.setUrlServiceService(urlServiceService);
        
        UUID id = UUID.randomUUID();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("delete-" + id.toString(), "any value");
        
        controller.updateRedirectRules(request);

        verify(urlServiceService).removeRedirectRule(id);
    }

    @Test
    public void addStaticRedirect() throws IOException {
        UrlServiceService urlServiceService = mock(UrlServiceService.class);
        controller.setUrlServiceService(urlServiceService);
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("add", "any value");
        request.addParameter("domain", DOMAIN);
        request.addParameter("pattern", PATTERN);
        request.addParameter("url", URL.toString());
        
        controller.updateStaticRedirects(request);

        verify(urlServiceService).createStaticRedirect(Mockito.argThat(new TypeSafeMatcher<StaticRedirect>() {
            @Override
            public boolean matchesSafely(StaticRedirect rule) {
                return rule.getPattern().equals(PATTERN) && rule.getUrl().equals(URL);
            }

            @Override
            public void describeTo(Description arg0) {
            }}));
    }

    @Test
    public void deleteStaticRedirect() throws IOException {
        UrlServiceService urlServiceService = mock(UrlServiceService.class);
        controller.setUrlServiceService(urlServiceService);
        
        UUID id = UUID.randomUUID();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("delete-" + id.toString(), "any value");
        
        controller.updateStaticRedirects(request);

        verify(urlServiceService).removeStaticRedirect(id);
    }

    @Test
    public void addApplication() throws IOException {
        UrlServiceService urlServiceService = mock(UrlServiceService.class);
        controller.setUrlServiceService(urlServiceService);
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("add", "any value");
        request.addParameter("name", "App");
        
        controller.updateApplications(request);

        verify(urlServiceService).createApplication(Mockito.argThat(new TypeSafeMatcher<Application>() {
            @Override
            public boolean matchesSafely(Application app) {
                return app.getName().equals("App");
            }

            @Override
            public void describeTo(Description arg0) {
            }}));
    }

    @Test
    public void deleteApplication() throws IOException {
        UrlServiceService urlServiceService = mock(UrlServiceService.class);
        controller.setUrlServiceService(urlServiceService);
        
        UUID id = UUID.randomUUID();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("delete-" + id.toString(), "any value");
        
        controller.updateApplications(request);

        verify(urlServiceService).removeApplication(id);
    }

}
