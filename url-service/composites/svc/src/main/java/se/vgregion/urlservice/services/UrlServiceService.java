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

package se.vgregion.urlservice.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.ShortLink;
import se.vgregion.urlservice.types.StaticRedirect;
import se.vgregion.urlservice.types.User;

/**
 * Service for handling short link, modelled after the bit.ly version 3 API
 * (http://code.google.com/p/bitly-api/wiki/ApiDocumentation).
 */
public interface UrlServiceService {

    /**
     * Shorten a long URL to a short link. If the long URL already exists, 
     * the already existing short link will be returned.
     * @param url The long URL, required.
     * @return The {@link ShortLink} representing the long URL
     * @throws URISyntaxException
     */
    ShortLink shorten(String url) throws URISyntaxException;

    /**
     * Shorten a long URL to a short link. If the long URL already exists, 
     * the already existing short link will be returned.
     * @param url The long URL, required.
     * @param hash The desired hash
     * @return The {@link ShortLink} representing the long URL
     * @throws URISyntaxException
     */
    ShortLink shorten(String url, String hash) throws URISyntaxException;

    /**
     * Shorten a long URL to a short link. If the long URL already exists, 
     * the already existing short link will be returned.
     * @param url The long URL, required.
     * @param hash The desired hash
     * @param owner The user which owns of the link
     * @return The {@link ShortLink} representing the long URL
     * @throws URISyntaxException
     */
    ShortLink shorten(String url, String hash, User owner) throws URISyntaxException;

    ShortLink shorten(String url, String hash, Collection<UUID> keywordIds, User owner) throws URISyntaxException;
    
    /**
     * Expand a short link (e.g. http://s.vgregion.se/abc) 
     * to the matching long URL. 
     * @param shortUrlOrHash The short URL, required
     * @return The {@link ShortLink} containing the long URL. Null if the
     *   short URL/hash is unknown.
     * @throws URISyntaxException
     */
    ShortLink expand(String shortUrl) throws URISyntaxException;

    /**
     * Expand a hash (e.g. "abc") 
     * to the matching long URL. 
     * @param shortUrlOrHash The hash, required
     * @return The {@link ShortLink} containing the long URL. Null if the
     *   short URL/hash is unknown.
     * @throws URISyntaxException
     */
    ShortLink expand(String domain, String hash) throws URISyntaxException;

    /**
     * Find the matching hash for a long URL.  
     * @param url The long URL
     * @return The {@link ShortLink} containing the hash. Null if
     *   the long URL is unknown
     * @throws URISyntaxException
     */
    ShortLink lookup(String url) throws URISyntaxException;

    /**
     * Given a path, will return the URI to where the user should be redirected. 
     * Handles all types of redirects.
     * @param path The path part of the request URL
     * @return The URI to where the user should be redirected.
     */
    URI redirect(String domain, String path);
    
    User getUser(String vgrId);
    
    List<Keyword> getAllKeywords();

    void createRedirectRule(RedirectRule rule);

    void createStaticRedirect(StaticRedirect redirect);

    Collection<StaticRedirect> findAllStaticRedirects();

    Collection<RedirectRule> findAllRedirectRules();

    void removeRedirectRule(UUID id);

    void removeStaticRedirect(UUID id);
}