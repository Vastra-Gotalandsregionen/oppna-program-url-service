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
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.repository.ShortLinkRepository;
import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.repository.UserRepository;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.ShortLink;
import se.vgregion.urlservice.types.StaticRedirect;
import se.vgregion.urlservice.types.User;

@Service
public class DefaultUrlServiceService implements UrlServiceService {

    private final Logger log = LoggerFactory.getLogger(DefaultUrlServiceService.class);
    
    private static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[] {"http", "https"});

    private static final int INITIAL_HASH_LENGTH = 6;
    
    private static final Pattern HASH_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");
    
    private String domain;
    
    private ShortLinkRepository shortLinkRepository;
    private RedirectRuleRepository redirectRuleRepository;
    private StaticRedirectRepository staticRedirectRepository;
    private UserRepository userRepository;
    
    public DefaultUrlServiceService() {
        log.info("Created {}", DefaultUrlServiceService.class.getName());
    }

    
    /** 
     * {@inheritDoc}
     */
    @Transactional
    public ShortLink shorten(String urlString) throws URISyntaxException {
        return shorten(urlString, null);
    }
    
    /** 
     * {@inheritDoc}
     */
    @Transactional
    public ShortLink shorten(String urlString, String hash) throws URISyntaxException {
        return shorten(urlString, hash, null);
    }
    
    /** 
     * {@inheritDoc}
     */
    @Transactional
    public ShortLink shorten(String urlString, String hash, User owner) throws URISyntaxException {
        URI url = new URI(urlString);
        
        if(WHITELISTED_SCHEMES.contains(url.getScheme())) {
            ShortLink link = shortLinkRepository.findByLongUrl(urlString);
            
            if(link != null) {
                // shortlink already exists, return as is
                return link;
            } else {
                String md5 = DigestUtils.md5Hex(urlString);
                
                int length = INITIAL_HASH_LENGTH;
                
                if(StringUtils.isBlank(hash)) {
                    hash = md5.substring(0, length);
                } else {
                    if(!HASH_PATTERN.matcher(hash).matches()) {
                        throw new IllegalArgumentException("Hash contains invalid characters");
                    }
                }
                
                if(hash.length() < INITIAL_HASH_LENGTH) {
                    hash = hash + md5.substring(hash.length(), length);
                }
                
                if(owner != null) {
                    hash = owner.getVgrId() + "/" + hash;
                }
                
                // check that the hash does not already exist
                while(shortLinkRepository.findByHash(domain, hash) != null) {
                    length++;
                    
                    if(length > md5.length()) {
                        // should never happen...
                        throw new RuntimeException("Failed to generate hash");
                    }
                    
                    hash += md5.substring(length-1, length);
                }
                
                ShortLink newLink = new ShortLink(domain, hash, urlString, domain + hash, owner);
                
                
                
                shortLinkRepository.persist(newLink);
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
    public ShortLink expand(String domain, String hash) {
        return shortLinkRepository.findByHash(domain, hash);
    }
    
    @Override
    public ShortLink expand(String shortUrl) throws URISyntaxException {
        URI url = URI.create(shortUrl);
        String domain = url.getHost();
        if(url.getPort() > 0) {
            domain += ":" + url.getPort();
        }
        
        int lastSlash = url.getPath().lastIndexOf('/');
        String hash;
        if(lastSlash == -1) {
            hash = url.getPath();
        } else {
            hash = url.getPath().substring(lastSlash);
        }
        return expand(domain, hash);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public URI redirect(String domain, String path) {
        ShortLink shortLink = expand(domain, path);
        // first try short links
        if(shortLink != null) {
            return URI.create(shortLink.getUrl());
        } else {
            // next, try static redirects
            StaticRedirect redirect = staticRedirectRepository.findByPath(domain, path);

            if(redirect != null) {
                return URI.create(redirect.getUrl());
            } else {
                // finally, check redirect rules
                Collection<RedirectRule> rules = redirectRuleRepository.findAll();
                
                for(RedirectRule rule : rules) {
                    if(rule.matches(domain, path)) {
                        return URI.create(rule.getUrl());
                    }
                }
                
                return null;
            }
        }
    }

    
    /** 
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ShortLink lookup(String url) throws URISyntaxException {
        return shortLinkRepository.findByLongUrl(url);
    }

    public ShortLinkRepository getShortLinkRepository() {
        return shortLinkRepository;
    }

    @Resource
    public void setShortLinkRepository(ShortLinkRepository shortLinkRepository) {
        this.shortLinkRepository = shortLinkRepository;
    }
    
    public RedirectRuleRepository getRedirectRuleRepository() {
        return redirectRuleRepository;
    }

    @Resource
    public void setRedirectRuleRepository(RedirectRuleRepository redirectRuleRepository) {
        this.redirectRuleRepository = redirectRuleRepository;
    }

    public StaticRedirectRepository getStaticRedirectRepository() {
        return staticRedirectRepository;
    }

    @Resource
    public void setStaticRedirectRepository(StaticRedirectRepository staticRedirectRepository) {
        this.staticRedirectRepository = staticRedirectRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    @Resource
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String getDomain() {
        return domain;
    }

    @Resource(name="domain")
    public void setDomain(String domain) {
        if(!domain.endsWith("/")) {
            domain += "/";
        }
        this.domain = domain;
    }


    @Override
    @Transactional
    public User getUser(String vgrId) {
        User user = userRepository.find(vgrId);
        if(user == null) {
            user = new User(vgrId);
            userRepository.persist(user);
        }
        return user;
    }



}
