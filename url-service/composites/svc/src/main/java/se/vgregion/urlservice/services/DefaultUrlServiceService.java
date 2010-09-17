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
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import se.vgregion.urlservice.types.ShortLink;

@Service
public class DefaultUrlServiceService implements UrlServiceService {

    private final Logger log = LoggerFactory.getLogger(DefaultUrlServiceService.class);
    
    private static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[] {"http", "https"});
    
    public DefaultUrlServiceService() {
        log.info("Created {}", DefaultUrlServiceService.class.getName());
    }

    /* (non-Javadoc)
     * @see se.vgregion.urlservice.services.UrlServiceService#shorten(java.lang.String)
     */
    public ShortLink shorten(String urlString) throws URISyntaxException {
        URI url = new URI(urlString);
        
        if(WHITELISTED_SCHEMES.contains(url.getScheme())) {
            ShortLink link = new ShortLink();
            link.setHash("def");
            link.setUrl(urlString);
            return link;
        } else {
            throw new URISyntaxException(urlString, "Scheme not allowed");
        }
    }

    @Override
    public ShortLink expand(String shortUrlOrHash) throws URISyntaxException {
        // TODO Auto-generated method stub
        return null;
    }
}
