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
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.services.UrlServiceService;

/**
 * Controller for showing a basic web GUI for shorting link.
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

    @RequestMapping(value="/shorten")
    public ModelAndView index(@RequestParam(value="longurl", required=false) String longUrl, @RequestParam(value="slug", required=false) String slug) throws IOException {
        ModelAndView mav = new ModelAndView("shorten");
        if(longUrl != null) {
            mav.addObject("longUrl", longUrl);
            try {
                String shortUrl = urlServiceService.shorten(longUrl, slug).getShortUrl();
                mav.addObject("shortUrl", shortUrl);
                mav.addObject("slug", slug);
            } catch (URISyntaxException e) {
                mav.addObject("error", "Felaktig address, måste börja med \"http://\" eller \"https://\"");
            }
        }
        
        return mav;
    }
}
