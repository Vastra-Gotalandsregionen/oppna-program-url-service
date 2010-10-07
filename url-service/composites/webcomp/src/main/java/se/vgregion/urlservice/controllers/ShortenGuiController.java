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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.urlservice.services.UrlServiceService;

/**
 * Controller for showing a basic web GUI for shorting link.
 * TODO does not currently use a view framework 
 *
 */
@Controller
public class ShortenGuiController {

    private final Logger log = LoggerFactory.getLogger(ShortenGuiController.class);

    @Resource
    private UrlServiceService urlServiceService;

    public ShortenGuiController() {
        log.info("Created {}", ShortenGuiController.class.getName());
    }

    public ShortenGuiController(UrlServiceService urlServiceService) {
        this();
        this.urlServiceService = urlServiceService;
    }

    @RequestMapping(value="/")
    public void index(@RequestParam(value="longurl", required=false) String longUrl, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        
        // HTML5 goodness
        writer.write("<!DOCTYPE html>");
        writer.write("<html>");
        writer.write("<head>");
        writer.write("<title>Förkorta länk</title>");
        writer.write("</head>");
        writer.write("<body>");
        writer.write("<form action=''>");
        
        if(longUrl != null) {
            writer.write("<input name='longurl' value='" + longUrl + "'>");
        } else {
            writer.write("<input name='longurl' value=''>");
        }
        
        writer.write("<input type='submit' value='Förkorta länk'>");
        writer.write("</form>");
        
        if(longUrl != null) {
            try {
                String shortUrl = urlServiceService.shorten(longUrl).getShortUrl();
                writer.write("<p><a href=\"" + shortUrl + "\">" + shortUrl + "</a></p>");
            } catch (URISyntaxException e) {
                writer.write("<p>Felaktig address, måste börja med \"http://\" eller \"https://\"</p>");
            }
        }
        
        writer.write("<p><a href=\"javascript:location.href='http://localhost:8080/url-service-url-service-module-web/?longurl='+encodeURIComponent(location.href)\">Förkorta länk</a>, drag denna länk till dina bokmärken för att enkelt skapa korta länkar</p>");
        
        writer.write("</body>");
        writer.write("</html>");
        
    }
}
