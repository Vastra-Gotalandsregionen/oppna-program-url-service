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

import java.net.URISyntaxException;

import se.vgregion.urlservice.types.ShortLink;

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
     * Expand a short link (e.g. http://s.vgregion.se/abc) or hash (e.g. "abc") 
     * to the matching long URL. 
     * @param shortUrlOrHash The short URL or the hash, required
     * @return The {@link ShortLink} containing the long URL. Null if the
     *   short URL/hash is unknown.
     * @throws URISyntaxException
     */
    ShortLink expand(String shortUrlOrHash) throws URISyntaxException;

    /**
     * Find the matching hash for a long URL.  
     * @param url The long URL
     * @return The {@link ShortLink} containing the hash. Null if
     *   the long URL is unknown
     * @throws URISyntaxException
     */
    ShortLink lookup(String url) throws URISyntaxException;

}