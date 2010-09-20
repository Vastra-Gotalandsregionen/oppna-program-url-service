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

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.ShortLinkRepository;
import se.vgregion.urlservice.types.ShortLink;

@Service
public class DefaultUrlServiceService implements UrlServiceService {

    private final Logger log = LoggerFactory.getLogger(DefaultUrlServiceService.class);
    
    private static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[] {"http", "https"});

    private static final int INITIAL_HASH_LENGTH = 6;
    
    private ShortLinkRepository shortLinkRepository;
    
    public DefaultUrlServiceService() {
        log.info("Created {}", DefaultUrlServiceService.class.getName());
    }

    /** 
     * {@inheritDoc}
     */
    @Transactional(readOnly = false)
    public ShortLink shorten(String urlString) throws URISyntaxException {
        URI url = new URI(urlString);
        
        if(WHITELISTED_SCHEMES.contains(url.getScheme())) {
            ShortLink link = shortLinkRepository.findByUrl(urlString);
            
            if(link != null) {
                return link;
            } else {
                String md5 = DigestUtils.md5Hex(urlString);
                
                int length = INITIAL_HASH_LENGTH;
                String hash = md5.substring(0, length);
                
                // check that the hash does not already exist
                while(shortLinkRepository.findByHash(hash) != null) {
                    length++;
                    
                    if(length > md5.length()) {
                        // should never happen...
                        throw new RuntimeException("Failed to generate hash");
                    }
                    
                    hash = md5.substring(0, length);
                }
                
                ShortLink newLink = new ShortLink();
                newLink.setHash(hash);
                newLink.setUrl(urlString);
                
                newLink = shortLinkRepository.persist(newLink);
                return newLink;
            }
        } else {
            throw new URISyntaxException(urlString, "Scheme not allowed");
        }
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ShortLink expand(String shortUrlOrHash) throws URISyntaxException {
        String hash;
        if(shortUrlOrHash.startsWith("http://")) {
            hash = shortUrlOrHash.substring(shortUrlOrHash.lastIndexOf('/') + 1);
        } else {
            hash = shortUrlOrHash;
        }
        return shortLinkRepository.findByHash(hash);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ShortLink lookup(String url) throws URISyntaxException {
        return shortLinkRepository.findByUrl(url);
    }

    public ShortLinkRepository getShortLinkRepository() {
        return shortLinkRepository;
    }

    @Resource
    public void setShortLinkRepository(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }



}
