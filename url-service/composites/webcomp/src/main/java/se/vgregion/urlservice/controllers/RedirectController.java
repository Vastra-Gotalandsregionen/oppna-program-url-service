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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.services.UrlServiceService;

/**
 * Controller for handling redirects, e.g. a user doing a HTTP GET for http://s.vgregion.se/a4f6Bd 
 * this controller will redirect the user to the long URL for this short link.
 *
 */
@Controller
public class RedirectController {

    private final Logger log = LoggerFactory.getLogger(RedirectController.class);

    @Resource
    private UrlServiceService urlServiceService;

    public RedirectController() {
        log.info("Created {}", RedirectController.class.getName());
    }

    public RedirectController(UrlServiceService urlServiceService) {
        this();
        this.urlServiceService = urlServiceService;
    }

    /**
     * Handle redirects
     */
    @RequestMapping("/**")
    public ModelAndView redirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Redirecting");
        String path = request.getPathInfo().substring(1);
        URI uri = urlServiceService.redirect(path);
                
        if(uri != null) {
            ModelAndView mav = new ModelAndView("redirect");
            response.setStatus(301);
            response.setHeader("Location", uri.toString());
            
            mav.addObject("longUrl", uri.toString());

            return mav;
        } else {
            response.sendError(404);
            return null;
        }
    }
}
