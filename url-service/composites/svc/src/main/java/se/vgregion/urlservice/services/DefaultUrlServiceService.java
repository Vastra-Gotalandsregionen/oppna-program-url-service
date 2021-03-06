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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.ApplicationRepository;
import se.vgregion.urlservice.repository.BookmarkRepository;
import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.repository.LongUrlRepository;
import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.repository.UserRepository;
import se.vgregion.urlservice.types.Application;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Owner;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.UrlWithHash;

@Service
public class DefaultUrlServiceService implements UrlServiceService {

	private final Logger log = LoggerFactory
			.getLogger(DefaultUrlServiceService.class);

	private static final List<String> WHITELISTED_SCHEMES = Arrays
			.asList(new String[] { "http", "https" });

	private static final int INITIAL_HASH_LENGTH = 6;

	private static final Pattern HASH_PATTERN = Pattern
			.compile("[a-zA-Z0-9_-]+");

	private String domain;

	private BookmarkRepository shortLinkRepository;
	private RedirectRuleRepository redirectRuleRepository;
	private UserRepository userRepository;
	private KeywordRepository keywordRepository;
	private LongUrlRepository longUrlRepository;
	private ApplicationRepository applicationRepository;

	public DefaultUrlServiceService() {
		log.info("Created {}", DefaultUrlServiceService.class.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public Bookmark shorten(URI url, Owner owner) {
		return shorten(url, null, Collections.EMPTY_LIST, owner);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public Bookmark shorten(URI url, String hash, Owner owner) {
		return shorten(url, hash, Collections.EMPTY_LIST, owner);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public Bookmark shorten(URI url, String hash,
			Collection<String> keywordNames, Owner owner) {
		if (WHITELISTED_SCHEMES.contains(url.getScheme())) {
			// does the LongUrl exist?
			LongUrl longUrl = longUrlRepository.findByUrl(url);

			if (longUrl == null) {
				// new LongUrl, create and persist
				String md5 = DigestUtils.md5Hex(url.toString());

				int length = INITIAL_HASH_LENGTH;

				String globalHash = md5.substring(0, length);
				// check that the hash does not already exist
				while (longUrlRepository.findByHash(globalHash) != null) {
					length++;

					if (length > md5.length()) {
						// should never happen...
						throw new RuntimeException("Failed to generate hash");
					}

					globalHash += md5.substring(length - 1, length);
				}

				longUrl = new LongUrl(url, globalHash);
				longUrlRepository.persist(longUrl);
			}

			Bookmark link = shortLinkRepository.findByLongUrl(url, owner);
			if (link != null) {
				return link;
			} else {
				String md5 = DigestUtils.md5Hex("{" + owner.getName() + "}"
						+ url.toString());

				int length = INITIAL_HASH_LENGTH;

				if (!StringUtils.isEmpty(hash)
						&& !HASH_PATTERN.matcher(hash).matches()) {
					throw new IllegalArgumentException(
							"Hash contains invalid characters");
				}

				if (StringUtils.isEmpty(hash)) {
					hash = md5.substring(0, length);
				}

				// check that the hash does not already exist
				while (shortLinkRepository.findByHash(hash, owner) != null) {
					length++;

					if (length > md5.length()) {
						// should never happen...
						throw new RuntimeException("Failed to generate hash");
					}

					hash += md5.substring(length - 1, length);
				}

				List<Keyword> keywords = findOrCreateKeywords(keywordNames);
				Bookmark newLink = new Bookmark(hash, longUrl, keywords, owner);

				shortLinkRepository.persist(newLink);
				return newLink;
			}
		} else {
			throw new IllegalArgumentException("Scheme not allowed: "
					+ url.getScheme());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public Bookmark updateBookmark(String hash, String newHash,
			Collection<String> keywordNames, Owner owner) {
		Bookmark bookmark = shortLinkRepository.findByHash(hash, owner);

		if (bookmark != null) {
			if (!StringUtils.isEmpty(newHash)) {
				bookmark.setHash(newHash);
			}

			bookmark.setKeywords(findOrCreateKeywords(keywordNames));
			return bookmark;
		} else {
			return null;
		}

	}

	@Transactional
	private List<Keyword> findOrCreateKeywords(Collection<String> keywordNames) {
		return keywordRepository.findOrCreateKeywords(keywordNames);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public LongUrl expandGlobal(String hash) {
		return longUrlRepository.findByHash(hash);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Bookmark expand(String hash, Owner owner) {
		return shortLinkRepository.findByHash(hash, owner);
	}

	private final Pattern USER_SHORTLINK_PATTERN = Pattern
			.compile("/u/([a-zA-Z0-9]+)/b/([a-zA-Z0-9]+)$");
	private final Pattern GLOBAL_SHORTLINK_PATTERN = Pattern
			.compile("/b/([a-zA-Z0-9]+)$");

	@Override
	public UrlWithHash expandPath(URI url) {
		String domain = url.getHost();
		if (url.getPort() > 0) {
			domain += ":" + url.getPort();
		}
		
		return expandPath(url.getPath());
	}
	
	@Override
	public UrlWithHash expandPath(String path) {
		Matcher userShortLinkMatcher = USER_SHORTLINK_PATTERN.matcher(path);
		Matcher globalShortLinkMatcher = GLOBAL_SHORTLINK_PATTERN.matcher(path);
		if (userShortLinkMatcher.find()) {
			Owner owner = userRepository.findByName(userShortLinkMatcher.group(1));
			String hash = userShortLinkMatcher.group(2);

			if (owner != null) {
				return expand(hash, owner);
			}
		} else if (globalShortLinkMatcher.find()) {
			String hash = globalShortLinkMatcher.group(1);
			return expandGlobal(hash);
		} else {
			// not found
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public URI redirect(String domain, String path) {
		// first try short links
		UrlWithHash urlWithHash = expandPath(path);
		if (urlWithHash != null) {
			return urlWithHash.getUrl();
		} else {
			String pathWithLeadingSlash = (path.startsWith("/")) ? path : "/" + path;
			// next, try a perfect matching rule. This is a performance optimization for when
			// large number of static redirects are used
			RedirectRule directRedirect= redirectRuleRepository.findByDomainAndPattern(domain, pathWithLeadingSlash);

			if (directRedirect != null) {
				return URI.create(directRedirect.getUrl());
			} else {
				// finally, check redirect rules
				Collection<RedirectRule> rules = redirectRuleRepository
						.findAll();

				for (RedirectRule rule : rules) {
					if (rule.matches(domain, pathWithLeadingSlash)) {
						return rule.resolve(pathWithLeadingSlash);
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
	public Bookmark lookup(URI url, Owner owner) {
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
	public void setRedirectRuleRepository(
			RedirectRuleRepository redirectRuleRepository) {
		this.redirectRuleRepository = redirectRuleRepository;
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

	public ApplicationRepository getApplicationRepository() {
		return applicationRepository;
	}

	@Resource
	public void setApplicationRepository(
			ApplicationRepository applicationRepository) {
		this.applicationRepository = applicationRepository;
	}

	public String getDomain() {
		return domain;
	}

	@Resource(name = "domain")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	@Transactional
	public Owner getUser(String username) {
		Owner user = userRepository.findByName(username);
		if (user == null) {
			user = new Owner(username);
			userRepository.persist(user);
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true)
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
	public Collection<RedirectRule> findAllRedirectRules() {
		return redirectRuleRepository.findAll();
	}

	@Override
	@Transactional
	public void removeRedirectRule(UUID id) {
		redirectRuleRepository.remove(id);
	}

	@Override
	@Transactional
	public Application getApplication(String apiKey) {
		return applicationRepository.findByApiKey(apiKey);
	}

	@Override
	@Transactional
	public void createApplication(Application application) {
		applicationRepository.persist(application);
	}

	@Override
	@Transactional
	public Collection<Application> findAllApplications() {
		return applicationRepository.findAll();
	}

	@Override
	@Transactional
	public void removeApplication(UUID id) {
		applicationRepository.remove(id);
	}

	@Override
	public Collection<Owner> findAllUsers() {
		return userRepository.findAll();
	}
}
