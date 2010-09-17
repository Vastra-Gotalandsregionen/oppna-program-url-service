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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import nu.xom.Element;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.urlservice.services.UrlServiceService;
import se.vgregion.urlservice.types.ShortLink;

@Controller
public class UrlServiceController {

    private final Logger log = LoggerFactory.getLogger(UrlServiceController.class);

    private String urlPrefix = "http://s.vgregion.se/";

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
    public void shorten(@RequestParam("longUrl") String url,
            @RequestParam(value = "format", defaultValue = "json") String format, HttpServletResponse response)
            throws IOException {
        try {
            ShortLink link = urlServiceService.shorten(url);

            PrintWriter writer = response.getWriter();

            if ("json".equals(format)) {
                ObjectMapper treeMapper = new ObjectMapper();
                ObjectNode root = treeMapper.createObjectNode();
                root.put("status_code", 200);
                root.put("status_txt", "OK");
                ObjectNode data = root.putObject("data");
                data.put("url", createFullShortUrl(link));
                data.put("hash", link.getHash());
                data.put("global_hash", link.getHash());
                data.put("long_url", link.getUrl());
                data.put("new_hash", 0);

                treeMapper.writeValue(writer, root);
            } else if ("xml".equals(format)) {
                Element root = new Element("response");
                root.appendChild(createElement("status_code", "200"));
                root.appendChild(createElement("status_txt", "OK"));
                Element data = new Element("data");
                data.appendChild(createElement("url", createFullShortUrl(link)));
                data.appendChild(createElement("hash", link.getHash()));
                data.appendChild(createElement("global_hash", link.getHash()));
                data.appendChild(createElement("long_url", link.getUrl()));
                data.appendChild(createElement("new_hash", "0"));

                root.appendChild(data);

                writer.write(root.toXML());
            } else if ("txt".equals(format)) {
                writer.write(link.getHash());
            } else {
                sendUnknownFormatError(response);
            }
        } catch (URISyntaxException e) {
            sendInvalidUriError(response);
        }
    }

    @RequestMapping("/shorten")
    public void expand(@RequestParam(value = "shortUrl", required = false) List<String> shortUrls,
            @RequestParam(value = "hash", required = false) List<String> hashes,
            @RequestParam(value = "format", defaultValue = "json") String format, HttpServletResponse response)
            throws IOException {

        try {
            List<ShortLink> links = new ArrayList<ShortLink>();
            if(shortUrls != null) {
                for (String shortUrl : shortUrls) {
                    links.add(urlServiceService.expand(shortUrl));
                }
            }
            if(hashes != null) {
                for (String hash : hashes) {
                    links.add(urlServiceService.expand(hash));
                }
            }
    
            if (links.isEmpty()) {
                // nothing to expand
                response.sendError(500, "MISSING_ARG_SHORTURL_OR_HASH");
            } else {

                PrintWriter writer = response.getWriter();

                if ("json".equals(format)) {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode root = mapper.createObjectNode();
                    root.put("status_code", 200);
                    root.put("status_txt", "OK");
                    ObjectNode data = root.putObject("data");
                    ArrayNode array = data.putArray("expand");

                    for (ShortLink link : links) {
                        ObjectNode node = mapper.createObjectNode();
                        node.put("hash", link.getHash());
                        node.put("short_url", createFullShortUrl(link));
                        node.put("global_hash", link.getHash());
                        node.put("long_url", link.getUrl());
                        node.put("user_hash", link.getHash());
                        array.add(node);
                    }

                    mapper.writeValue(writer, root);
                } else if ("xml".equals(format)) {
                    Element root = new Element("response");
                    root.appendChild(createElement("status_code", "200"));
                    root.appendChild(createElement("status_txt", "OK"));
                    Element data = new Element("data");
                    for (ShortLink link : links) {
                        Element entry = new Element("entry");
                        entry.appendChild(createElement("hash", link.getHash()));
                        entry.appendChild(createElement("short_url", createFullShortUrl(link)));
                        entry.appendChild(createElement("global_hash", link.getHash()));
                        entry.appendChild(createElement("long_url", link.getUrl()));
                        entry.appendChild(createElement("user_hash", link.getHash()));

                        data.appendChild(entry);
                    }

                    root.appendChild(data);

                    writer.write(root.toXML());
                } else if ("txt".equals(format)) {
                    if (links.size() > 1) {
                        response.sendError(500, "TOO_MANY_EXPAND_PARAMETERS");
                    } else {
                        writer.write(links.get(0).getUrl());
                    }
                } else {
                    sendUnknownFormatError(response);
                }
            }
        } catch (URISyntaxException e) {
            sendInvalidUriError(response);
        }
    }


    private String createFullShortUrl(ShortLink link) {
        return urlPrefix + link.getHash();
    }

    private Element createElement(String name, String text) {
        Element elm = new Element(name);
        elm.appendChild(text);
        return elm;
    }

    private void sendUnknownFormatError(HttpServletResponse response) throws IOException {
        response.sendError(500, "INVALID_ARG_FORMAT");
    }

    private void sendInvalidUriError(HttpServletResponse response) throws IOException {
        response.sendError(500, "INVALID_URI");
    }
}
