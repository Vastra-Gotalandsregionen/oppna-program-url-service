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
import java.io.PrintWriter;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.ShortLink;

@Controller
public class RedirectController {

    private final Logger log = LoggerFactory.getLogger(RedirectController.class);

    private UrlServiceService urlServiceService;

    public RedirectController() {
        log.info("Created {}", RedirectController.class.getName());
    }

    public UrlServiceService getUrlServiceService() {
        return urlServiceService;
    }

    @Resource
    public void setUrlServiceService(UrlServiceService urlServiceService) {
        this.urlServiceService = urlServiceService;
    }

    @RequestMapping("/{hash}")
    public void redirect(@PathVariable("hash") String hash, HttpServletResponse response) throws IOException {
        try {
            ShortLink link = urlServiceService.expand(hash);

            if (link != null) {
                response.setStatus(301);
                response.setHeader("Location", link.getUrl());
                
                PrintWriter writer = response.getWriter();
                
                // HTML5 goodness
                writer.write("<!DOCTYPE html>");
                writer.write("<html>");
                writer.write("<head>");
                writer.write("<title>Moved</title>");
                writer.write("</head>");
                writer.write("<body>");
                writer.write("<a href=\"" + link.getUrl() + "\">The requested URL has moved here.</a>");
                writer.write("</body>");
                writer.write("</html>");
            } else {
                response.sendError(404);
            }
        } catch (URISyntaxException e) {
            response.sendError(500);
        }
    }
}
