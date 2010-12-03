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
import java.util.Enumeration;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.StaticRedirect;

/**
 * Controller for showing a basic web GUI for shorting link.
 *
 */
@Controller
public class AdminGuiController {

    private final Logger log = LoggerFactory.getLogger(AdminGuiController.class);

    @Resource
    private UrlServiceService urlServiceService;

    
    public AdminGuiController() {
        log.info("Created {}", AdminGuiController.class.getName());
    }

    @RequestMapping(value="/admin", method=RequestMethod.GET)
    public ModelAndView index() throws IOException {
        ModelAndView mav = new ModelAndView("admin/index");
        
        mav.addObject("redirectRules", urlServiceService.findAllRedirectRules());
        mav.addObject("staticRedirects", urlServiceService.findAllStaticRedirects());
        
        return mav;
    }

    @RequestMapping(value="/admin/redirectrules", method=RequestMethod.POST)
    public ModelAndView updateRedirectRules(HttpServletRequest request) throws IOException {
        ModelAndView mav = new ModelAndView("redirect:../admin");
        
        if(request.getParameter("add") != null) {
            // adding a new rule
            String domain = request.getParameter("domain");
            if(StringUtils.isEmpty(domain)) {
                domain = null;
            }
            String pattern = request.getParameter("pattern");
            String url = request.getParameter("url");

            if(StringUtils.isNotEmpty(pattern) && StringUtils.isNotEmpty(url)) {
                log.debug("Adding redirect rule with pattern \"{}\" and URL \"{}\"", pattern, url);
                try { 
                    urlServiceService.createRedirectRule(new RedirectRule(domain, pattern, url));
                } catch(RuntimeException e) {
                    throw e;
                }
            } else {
                // handle validation error
                System.out.println("Validation error");
            }
        } else {
            UUID deletedId = findDeletedId(request);
            if(deletedId != null) {
                log.debug("Deleting redirect rule {}", deletedId);
                urlServiceService.removeRedirectRule(deletedId);
            }
        }
        
        return mav;
    }

    @RequestMapping(value="/admin/staticredirects", method=RequestMethod.POST)
    public ModelAndView updateStaticRedirects(HttpServletRequest request) throws IOException {
        ModelAndView mav = new ModelAndView("redirect:../admin");
        
        if(request.getParameter("add") != null) {
            // adding a new rule
            String domain = request.getParameter("domain");
            String path = request.getParameter("pattern");
            String url = request.getParameter("url");
            
            if(StringUtils.isNotEmpty(domain) && StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(url)) {
                log.debug("Adding static redirect with path \"{}\" and URL \"{}\"", path, url);
                try { 
                    urlServiceService.createStaticRedirect(new StaticRedirect(domain, path, url));
                } catch(RuntimeException e) {
                    // TODO do not ignore
                }
            }
        } else {
            UUID deletedId = findDeletedId(request);
            if(deletedId != null) {
                log.debug("Deleting static redirect {}", deletedId);
                urlServiceService.removeStaticRedirect(deletedId);
            }
        }
        
        return mav;
    }

    
    private UUID findDeletedId(HttpServletRequest request) {
        Enumeration names = request.getParameterNames();
        while(names.hasMoreElements()) {
            String name = (String) names.nextElement();
            if(name.startsWith("delete-")) {
                return UUID.fromString(name.substring(7));
            }
        }
        
        return null;
    }

    public UrlServiceService getUrlServiceService() {
        return urlServiceService;
    }

    public void setUrlServiceService(UrlServiceService urlServiceService) {
        this.urlServiceService = urlServiceService;
    }
}
