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

import se.vgregion.urlservice.types.Application;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Owner;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.UrlWithHash;

/**
 * Service for handling short link, modelled after the bit.ly version 3 API
 * (http://code.google.com/p/bitly-api/wiki/ApiDocumentation).
 */
public interface UrlServiceService {

    /**
     * Shorten a long URL to a short link. If the long URL already exists, 
     * the already existing short link will be returned.
     * @param url The long URL, required.
     * @param Owner The owner of the bookmark
     * @return The {@link Bookmark} representing the long URL
     * @throws URISyntaxException
     */
    Bookmark shorten(URI url, Owner owner);

    /**
     * Shorten a long URL to a short link. If the long URL already exists, 
     * the already existing short link will be returned.
     * @param url The long URL, required.
     * @param hash The desired hash
     * @param owner The owner of the bookmark
     * @return The {@link Bookmark} representing the long URL
     * @throws URISyntaxException
     */
    Bookmark shorten(URI url, String hash, Owner owner);

    Bookmark shorten(URI url, String hash, Collection<String> keywordIds, Owner owner);
    
    /**
     * Expand a short link (e.g. http://s.vgregion.se/abc) 
     * to the matching long URL or bookmark. 
     * @param shortUrlOrHash The short URL, required
     * @return The {@link LongUrl} or {@link Bookmark} for the short URL. Null if the
     *   short URL/hash is unknown.
     * @throws URISyntaxException
     */
    UrlWithHash expandPath(URI shortUrl);

    UrlWithHash expandPath(String path);

    /**
     * Expand a hash (e.g. "abc") 
     * to the matching long URL. 
     * @param shortUrlOrHash The hash, required
     * @return The {@link Bookmark} containing the long URL. Null if the
     *   short URL/hash is unknown.
     * @throws URISyntaxException
     */
    LongUrl expandGlobal(String hash);
    
    Bookmark expand(String hash, Owner owner);

    /**
     * Find the matching hash for a long URL.  
     * @param url The long URL
     * @return The {@link Bookmark} containing the hash. Null if
     *   the long URL is unknown
     * @throws URISyntaxException
     */
    Bookmark lookup(URI url, Owner owner);

    /**
     * Given a path, will return the URI to where the user should be redirected. 
     * Handles all types of redirects.
     * @param path The path part of the request URL
     * @return The URI to where the user should be redirected.
     */
    URI redirect(String domain, String path);
    
    Owner getUser(String vgrId);
    
    List<Keyword> getAllKeywords();

    void createRedirectRule(RedirectRule rule);

    Collection<Owner> findAllUsers();

    Collection<RedirectRule> findAllRedirectRules();

    void removeRedirectRule(UUID id);

    Bookmark updateBookmark(String hash, String newHash, Collection<String> keywordNames, Owner owner);
    
    Application getApplication(String apikey);
    
    void createApplication(Application application);

    Collection<Application> findAllApplications();

    void removeApplication(UUID id);

}