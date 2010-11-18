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

/**
 * Controller implementing the bit.ly version 3 API (http://code.google.com/p/bitly-api/wiki/ApiDocumentation)
 *
 */
@Controller
@RequestMapping("/api/v3")
public class BitlyApiController {

    private final Logger log = LoggerFactory.getLogger(BitlyApiController.class);

    @Resource
    private UrlServiceService urlServiceService;

    public BitlyApiController() {
        log.info("Created {}", BitlyApiController.class.getName());
    }

    public BitlyApiController(UrlServiceService urlServiceService) {
        this();
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
                data.put("url", link.getShortUrl());
                data.put("hash", link.getPattern());
                data.put("global_hash", link.getPattern());
                data.put("long_url", link.getUrl());
                data.put("new_hash", 0);

                treeMapper.writeValue(writer, root);
            } else if ("xml".equals(format)) {
                Element root = new Element("response");
                root.appendChild(createElement("status_code", "200"));
                root.appendChild(createElement("status_txt", "OK"));
                Element data = new Element("data");
                data.appendChild(createElement("url", link.getShortUrl()));
                data.appendChild(createElement("hash", link.getPattern()));
                data.appendChild(createElement("global_hash", link.getPattern()));
                data.appendChild(createElement("long_url", link.getUrl()));
                data.appendChild(createElement("new_hash", "0"));

                root.appendChild(data);

                writer.write(root.toXML());
            } else if ("txt".equals(format)) {
                writer.write(link.getPattern());
            } else {
                sendUnknownFormatError(response);
            }
        } catch (URISyntaxException e) {
            sendInvalidUriError(response);
        }
    }

    @RequestMapping("/expand")
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
                        node.put("hash", link.getPattern());
                        node.put("short_url", link.getShortUrl());
                        node.put("global_hash", link.getPattern());
                        node.put("long_url", link.getUrl());
                        node.put("user_hash", link.getPattern());
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
                        entry.appendChild(createElement("hash", link.getPattern()));
                        entry.appendChild(createElement("short_url", link.getShortUrl()));
                        entry.appendChild(createElement("global_hash", link.getPattern()));
                        entry.appendChild(createElement("long_url", link.getUrl()));
                        entry.appendChild(createElement("user_hash", link.getPattern()));

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

    @RequestMapping("/lookup")
    public void lookup(@RequestParam(value = "url") List<String> urls,
            @RequestParam(value = "format", defaultValue = "json") String format, HttpServletResponse response)
            throws IOException {

        try {
            List<ShortLink> links = new ArrayList<ShortLink>();
            if(urls != null) {
                for (String shortUrl : urls) {
                    links.add(urlServiceService.expand(shortUrl));
                }
            }
    
            if (links.isEmpty()) {
                // nothing to expand
                response.sendError(500, "MISSING_ARG_URL");
            } else {

                PrintWriter writer = response.getWriter();

                if ("json".equals(format)) {
                    ObjectMapper mapper = new ObjectMapper();
                    ObjectNode root = mapper.createObjectNode();
                    root.put("status_code", 200);
                    root.put("status_txt", "OK");
                    ObjectNode data = root.putObject("data");
                    ArrayNode array = data.putArray("lookup");

                    for (ShortLink link : links) {
                        ObjectNode node = mapper.createObjectNode();
                        node.put("short_url", link.getShortUrl());
                        node.put("global_hash", link.getPattern());
                        node.put("long_url", link.getUrl());
                        array.add(node);
                    }

                    mapper.writeValue(writer, root);
                } else if ("xml".equals(format)) {
                    Element root = new Element("response");
                    root.appendChild(createElement("status_code", "200"));
                    root.appendChild(createElement("status_txt", "OK"));
                    Element data = new Element("data");
                    for (ShortLink link : links) {
                        Element entry = new Element("lookup");
                        entry.appendChild(createElement("short_url", link.getShortUrl()));
                        entry.appendChild(createElement("global_hash", link.getPattern()));
                        entry.appendChild(createElement("long_url", link.getUrl()));

                        data.appendChild(entry);
                    }

                    root.appendChild(data);

                    writer.write(root.toXML());
                } else {
                    sendUnknownFormatError(response);
                }
            }
        } catch (URISyntaxException e) {
            sendInvalidUriError(response);
        }
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
