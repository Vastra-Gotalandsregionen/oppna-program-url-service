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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.ShortLink;

@Controller
public class UrlServiceController {

    private final Logger log = LoggerFactory.getLogger(UrlServiceController.class);

    private UrlServiceService urlServiceService;
    
    public UrlServiceController() {
        log.info("Created {}", UrlServiceController.class.getName());
    }

    public UrlServiceService getUrlServiceService() {
        return urlServiceService;
    }

    @Resource
    public void setUrlServiceService(UrlServiceService urlServiceService) {
        this.urlServiceService = urlServiceService;
    }

    @RequestMapping("/shorten")
    public void shorten(@RequestParam("longUrl") String url, @RequestParam(value="format",defaultValue="json" ) String format, 
            HttpServletResponse response) throws IOException {
        try {
            ShortLink link = urlServiceService.shorten(url);

            PrintWriter writer = response.getWriter();
            
            if("json".equals(format)) {
                writer.write("{");
                writer.write("\"status_code\": 200,");
                writer.write("\"data\": {");
                writer.write("\"url\": \"http://example.com/" + link.getHash() + "\",");
                writer.write("\"hash\": \"" + link.getHash() + "\", ");
                writer.write("\"global_hash\": \"" + link.getHash() + "\", ");
                writer.write("\"long_url\": \"" + link.getUrl() + "\",");
                writer.write("\"new_hash\": 0");
                writer.write("}, ");
                writer.write("\"status_txt\": \"OK\"");
                writer.write("}");
            } else if("txt".equals(format)) {
                writer.write(link.getHash());
            } else {
                response.sendError(404, "Unknown format: " + format);
            }
        } catch (URISyntaxException e) {
            response.sendError(500, "Invalid longUrl");
        }
    }
    
}
