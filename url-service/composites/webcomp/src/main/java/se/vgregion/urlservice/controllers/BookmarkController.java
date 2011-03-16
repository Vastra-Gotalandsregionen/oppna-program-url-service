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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Owner;

/**
 * Controller for showing a basic web GUI for shorting link.
 *
 */
@Controller
public class BookmarkController {

    private final Logger log = LoggerFactory.getLogger(BookmarkController.class);

    @Resource(name="domain")
    private String shortLinkPrefix;
    
    @Resource
    private UrlServiceService urlServiceService;

    public BookmarkController() {
        log.info("Created {}", BookmarkController.class.getName());
    }

    public BookmarkController(UrlServiceService urlServiceService, URI shortLinkPrefix) {
        this();
        this.shortLinkPrefix = shortLinkPrefix.toString();
        this.urlServiceService = urlServiceService;
    }
    
    @PostConstruct
    public void postConstruct() {
        // check shortlink prefix
        if(!this.shortLinkPrefix.endsWith("/")) {
            this.shortLinkPrefix += "/";
        }        
    }
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public ModelAndView index() throws IOException {
        Collection<Owner> users = urlServiceService.findAllUsers();

        ModelAndView mav = new ModelAndView("index");
        
        mav.addObject("users", users);
        return mav;
    }


    @RequestMapping(value="/u/{username}/b", method=RequestMethod.GET)
    public ModelAndView bookmarks(@PathVariable(value="username") String username,
            Authentication authentication) throws IOException {
        
        Owner user = urlServiceService.getUser(username);
        if(user == null) {
            throw new ResourceNotFoundException("Unknown user");
        }

        ModelAndView mav = new ModelAndView("bookmarks/index");
        
        Owner loggedInUser = getUser(authentication);
        mav.addObject("username", username);
        mav.addObject("user", loggedInUser);
        mav.addObject("owner", user.equals(loggedInUser));

        mav.addObject("bookmarks", user.getShortLinks());
        return mav;
    }

    
    @RequestMapping(value="/b/new", method=RequestMethod.GET)
    public ModelAndView newBookmark(Authentication authentication) {
        ModelAndView mav = new ModelAndView("bookmarks/edit");
        
        mav.addObject("user", getUser(authentication));
        
        return mav;
    }
    
    @RequestMapping(value="/b/new", method=RequestMethod.POST)
    public ModelAndView create(@RequestParam(value="longurl") URI longUrl, @RequestParam(value="slug", required=false) String slug, 
            @RequestParam(value="keywords", required=false) String keywordNameString, Authentication authentication) throws IOException {
        
        Owner user = getUser(authentication);
        if(user == null) {
            throw new ForbiddenException();
        }
        
        try {
            List<String> keywordNames = parseKeywordNames(keywordNameString);

            Bookmark shortLink = urlServiceService.shorten(longUrl, slug, keywordNames, user);

            return new ModelAndView("redirect:/u/" +  user.getName() + "/b/" + shortLink.getHash() + "/edit");
        } catch (IllegalArgumentException e) {
            // TODO add validation support
            throw e;
        }
    }

    private List<String> parseKeywordNames(String keywordNameString) {
        List<String> keywordNames = new ArrayList<String>();

        if(StringUtils.isEmpty(keywordNameString)) {
            return keywordNames;
        }
        
        if(keywordNameString != null) {
            keywordNames = Arrays.asList(keywordNameString.split("[\\s,;]+"));
        }
        return keywordNames;
    }

    @RequestMapping(value="/u/{username}/b/{hash}/qr", method=RequestMethod.GET)
    public ModelAndView qr(@PathVariable(value="hash") String hash, @PathVariable(value="username") String username) {
        ModelAndView mav = new ModelAndView("bookmarks/qr");
        
        Bookmark bookmark = urlServiceService.expand(hash);
        if(bookmark != null) {
            mav.addObject("shortUrl", shortLinkPrefix + "u/" + username + "/b/" + hash);
            return mav;
        } else {
            throw new ResourceNotFoundException("Unknown bookmark");
        }
    }
    
    @RequestMapping(value="/b/{hash}/qr", method=RequestMethod.GET)
    public ModelAndView qrGlobal(@PathVariable(value="hash") String hash) {
        ModelAndView mav = new ModelAndView("bookmarks/qr");
        
        LongUrl longUrl = urlServiceService.expandGlobal(hash);
        if(longUrl != null) {
            mav.addObject("shortUrl", shortLinkPrefix + "b/" + hash);
            return mav;
        } else {
            throw new ResourceNotFoundException("Unknown bookmark");
        }
    }
    
    
    @RequestMapping(value="/u/{username}/b/{hash}/edit", method=RequestMethod.GET)
    public ModelAndView edit(@PathVariable(value="hash") String hash, @PathVariable(value="username") String username, 
            Authentication authentication) throws IOException {
        ModelAndView mav = new ModelAndView("bookmarks/edit");
        mav.addObject("edit", true);

        Owner user = getUser(authentication);
        mav.addObject("user", getUser(authentication));
        
        if(user == null || !username.equals(user.getName())) {
            throw new ForbiddenException();
        }
        
        mav.addObject("userid", user.getName());
        
        Bookmark bookmark = urlServiceService.expand(hash);
        if(bookmark != null) {
            mav.addObject("longUrl", bookmark.getLongUrl().getUrl());
            
            mav.addObject("shortUrl",  createShortLink(user, bookmark));
            mav.addObject("globalShortUrl", shortLinkPrefix + "b/" + bookmark.getLongUrl().getHash());

            List<String> storedKeywordNames = new ArrayList<String>();
            if(bookmark.getKeywords() != null) {
                for(Keyword keyword : bookmark.getKeywords()) {
                    storedKeywordNames.add(keyword.getName());
                }
            }
            mav.addObject("selectedKeywords", StringUtils.join(storedKeywordNames, " "));
            
            mav.addObject("slug", bookmark.getSlug());
            return mav;
        } else {
            throw new ResourceNotFoundException("Unknown bookmark");
        }
    }

    private String createShortLink(Owner user, Bookmark bookmark) {
        String shortLink = shortLinkPrefix + "u/" + user.getName() + "/b/";
        if(bookmark.getSlug() != null && bookmark.getSlug().length() > 0) {
            shortLink += bookmark.getSlug();
        } else {
            shortLink += bookmark.getHash();
        }
        return shortLink;
    }

    @RequestMapping(value="/u/{username}/b/{hash}/edit", method=RequestMethod.POST)
    public ModelAndView update(@PathVariable(value="hash") String hash, @PathVariable(value="username") String username,
            @RequestParam(value="keywords") String keywordNameString, @RequestParam(value="slug", required=false) String slug, 
            Authentication authentication) throws IOException {
        Owner user = getUser(authentication);
        if(user == null || !username.equals(user.getName())) {
            throw new ForbiddenException();
        }
        
        List<String> keywordNames = parseKeywordNames(keywordNameString);
        Bookmark bookmark = urlServiceService.updateBookmark(hash, slug, keywordNames);
        if(bookmark != null) {
            return new ModelAndView("redirect:edit");
        } else {
            throw new ResourceNotFoundException("Unknown bookmark");
        }
    }

    
    private Owner getUser(Authentication authentication) {
        if(authentication != null) {
            Object principal = authentication.getPrincipal();
            if(principal instanceof org.springframework.security.core.userdetails.User) {
                String userName = ((org.springframework.security.core.userdetails.User) principal).getUsername();
                
                return urlServiceService.getUser(userName);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    @RequestMapping(value="/b/{globalHash}")
    public ModelAndView redirectGlobal(@PathVariable(value="globalHash") String globalHash, HttpServletResponse response) throws IOException { 
        LongUrl longUrl = urlServiceService.expandGlobal(globalHash);
        
        if(longUrl != null) {
            ModelAndView mav = new ModelAndView("redirect");
            response.setStatus(301);
            response.setHeader("Location", longUrl.getUrl().toString());
            
            mav.addObject("longUrl", longUrl.getUrl().toString());

            return mav;
        } else {
            response.sendError(404);
            return null;
        }
    }

    @RequestMapping(value="/u/{username}/b/{hash}")
    public ModelAndView redirect(@PathVariable(value="hash") String hash, HttpServletResponse response) throws IOException { 
        Bookmark bookmark = urlServiceService.expand(hash);
        
        if(bookmark != null) {
            ModelAndView mav = new ModelAndView("redirect");
            response.setStatus(301);
            response.setHeader("Location", bookmark.getLongUrl().getUrl().toString());
            
            mav.addObject("longUrl", bookmark.getLongUrl().getUrl().toString());

            return mav;
        } else {
            response.sendError(404);
            return null;
        }
    }

    @RequestMapping(value="/login")
    public ModelAndView login() throws IOException { 
        ModelAndView mav = new ModelAndView("login");
        return mav;
    }

    
}
