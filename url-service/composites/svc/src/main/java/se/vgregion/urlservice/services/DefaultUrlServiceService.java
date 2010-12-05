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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.repository.LongUrlRepository;
import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.repository.BookmarkRepository;
import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.repository.UserRepository;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.StaticRedirect;
import se.vgregion.urlservice.types.User;

@Service
public class DefaultUrlServiceService implements UrlServiceService {

    private final Logger log = LoggerFactory.getLogger(DefaultUrlServiceService.class);
    
    private static final List<String> WHITELISTED_SCHEMES = Arrays.asList(new String[] {"http", "https"});

    private static final int INITIAL_HASH_LENGTH = 6;
    
    private static final Pattern HASH_PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");
    
    private String domain;
    
    private BookmarkRepository shortLinkRepository;
    private RedirectRuleRepository redirectRuleRepository;
    private StaticRedirectRepository staticRedirectRepository;
    private UserRepository userRepository;
    private KeywordRepository keywordRepository;
    private LongUrlRepository longUrlRepository;
    
    public DefaultUrlServiceService() {
        log.info("Created {}", DefaultUrlServiceService.class.getName());
    }

    /** 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Bookmark shorten(URI url, User owner) {
        return shorten(url, null, Collections.EMPTY_LIST, owner);
    }
    
    /** 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Bookmark shorten(URI url, String hash, User owner) {
        return shorten(url, hash, Collections.EMPTY_LIST, owner);
    }
    
    /** 
     * {@inheritDoc}
     */
    @Transactional
    public Bookmark shorten(URI url, String hash, Collection<String> keywordNames, User owner) {
        if(WHITELISTED_SCHEMES.contains(url.getScheme())) {
            // does the LongUrl exist?
            LongUrl longUrl = longUrlRepository.findByUrl(url);
            
            if(longUrl == null) {
                // new LongUrl, create and persist
                longUrl = new LongUrl(url);
                longUrlRepository.persist(longUrl);
            }
            
            Bookmark link = shortLinkRepository.findByLongUrl(url, owner);
            if(link != null) {
                return link;
            } else {
                String md5 = DigestUtils.md5Hex("{" + owner.getUserName() + "}" + url.toString());
                
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
                
                // check that the hash does not already exist
                while(shortLinkRepository.findByHash(hash) != null) {
                    length++;
                    
                    if(length > md5.length()) {
                        // should never happen...
                        throw new RuntimeException("Failed to generate hash");
                    }
                    
                    hash += md5.substring(length-1, length);
                }
                
                List<Keyword> keywords = keywordRepository.findOrCreateKeywords(keywordNames);
                Bookmark newLink = new Bookmark(hash, longUrl, keywords, owner);
                
                shortLinkRepository.persist(newLink);
                return newLink;
            }
        } else {
            throw new IllegalArgumentException("Scheme not allowed: " + url.getScheme());
        }
    }


    /** 
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Bookmark expand(String hash) {
        return shortLinkRepository.findByHash(hash);
    }
    
    @Override
    public Bookmark expand(URI url) throws URISyntaxException {
        String domain = url.getHost();
        if(url.getPort() > 0) {
            domain += ":" + url.getPort();
        }
        
        int lastSlash = url.getPath().lastIndexOf('/');
        String hash;
        if(lastSlash == -1) {
            hash = url.getPath();
        } else {
            hash = url.getPath().substring(lastSlash + 1);
        }
        return expand(hash);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public URI redirect(String domain, String path) {
        Bookmark shortLink = expand(path);
        // first try short links
        if(shortLink != null) {
            return shortLink.getLongUrl().getUrl();
        } else {
            // next, try static redirects
            StaticRedirect redirect = staticRedirectRepository.findByPath(domain, path);

            if(redirect != null) {
                return redirect.getUrl();
            } else {
                // finally, check redirect rules
                Collection<RedirectRule> rules = redirectRuleRepository.findAll();
                
                for(RedirectRule rule : rules) {
                    if(rule.matches(domain, path)) {
                        return rule.getUrl();
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
    public Bookmark lookup(URI url, User owner) {
        return shortLinkRepository.findByLongUrl(url, owner);
    }

    public BookmarkRepository getShortLinkRepository() {
        return shortLinkRepository;
    }

    @Resource
    public void setShortLinkRepository(BookmarkRepository shortLinkRepository) {
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

    public KeywordRepository getKeywordRepository() {
        return keywordRepository;
    }

    @Resource
    public void setKeywordRepository(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }


    public LongUrlRepository getLongUrlRepository() {
        return longUrlRepository;
    }

    @Resource
    public void setLongUrlRepository(LongUrlRepository longUrlRepository) {
        this.longUrlRepository = longUrlRepository;
    }


    public String getDomain() {
        return domain;
    }

    @Resource(name="domain")
    public void setDomain(String domain) {
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


    @Override
    @Transactional(readOnly=true)
    public List<Keyword> getAllKeywords() {
        return keywordRepository.findAllInOrder();
    }

    @Override
    @Transactional    
    public void createRedirectRule(RedirectRule rule) {
        redirectRuleRepository.persist(rule);
    }

    @Override
    @Transactional    
    public void createStaticRedirect(StaticRedirect redirect) {
        staticRedirectRepository.persist(redirect);
    }

    @Override
    @Transactional    
    public Collection<RedirectRule> findAllRedirectRules() {
        return redirectRuleRepository.findAll();
    }

    @Override
    @Transactional    
    public Collection<StaticRedirect> findAllStaticRedirects() {
        return staticRedirectRepository.findAll();
    }

    @Override
    @Transactional    
    public void removeRedirectRule(UUID id) {
        redirectRuleRepository.remove(id);
    }

    
    @Override
    @Transactional    
    public void removeStaticRedirect(UUID id) {
        staticRedirectRepository.remove(id);
    }

}
