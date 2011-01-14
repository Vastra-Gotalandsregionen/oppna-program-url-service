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

package se.vgregion.urlservice.repository.jpa;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.persistence.PersistenceException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.repository.LongUrlRepository;
import se.vgregion.urlservice.repository.BookmarkRepository;
import se.vgregion.urlservice.repository.UserRepository;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.User;

@ContextConfiguration({"classpath:spring/services-common.xml", "classpath:test.xml"})
public class JpaBookmarkRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String GLOBAL_HASH = "abcdef";
    private static final String HASH = "12345";
    private static final String SLUG = "slug";

    private BookmarkRepository dao;
    
    private Bookmark bookmark1;
    
    @Before
    @Transactional
    public void setup() {
        User owner = new User("roblu");
        LongUrl longUrl = new LongUrl(URI.create("http://example.com"), GLOBAL_HASH);
        Keyword kw1 = new Keyword("kw1");
        List<Keyword> keywords = Arrays.asList(kw1);
        
        // the keyword, owner and LongUrl must be persisted first
        UserRepository userRepository = applicationContext.getBean(UserRepository.class);
        userRepository.persist(owner);
        userRepository.flush();
        
        LongUrlRepository longUrlRepository = applicationContext.getBean(LongUrlRepository.class);
        longUrlRepository.persist(longUrl);
        longUrlRepository.flush();
        
        KeywordRepository keywordRepository = applicationContext.getBean(KeywordRepository.class);
        keywordRepository.persist(kw1);
        keywordRepository.flush();

        
        Bookmark shortLink = new Bookmark(HASH, longUrl, keywords, SLUG, owner);

        dao = applicationContext.getBean(BookmarkRepository.class);
        
        bookmark1 = dao.persist(shortLink);
        //dao.flush();
    }
    
    @Test
    @Transactional
    @Rollback
    public void find() {
        Bookmark loaded = dao.find(bookmark1.getId());
        
        Assert.assertEquals(bookmark1.getHash(), loaded.getHash());
        Assert.assertEquals(bookmark1.getOwner(), loaded.getOwner());
    }

    @Test
    @Transactional
    @Rollback
    public void findByHash() {
        Bookmark loaded = dao.findByHash(HASH, false);
        
        Assert.assertEquals(bookmark1.getHash(), loaded.getHash());
        Assert.assertEquals(bookmark1.getOwner(), loaded.getOwner());
    }

    @Test
    @Transactional
    @Rollback
    public void findBySlug() {
        Bookmark loaded = dao.findByHash(SLUG, true);
        
        Assert.assertEquals(bookmark1.getHash(), loaded.getHash());
        Assert.assertEquals(bookmark1.getOwner(), loaded.getOwner());
    }

    
    @Test
    @Transactional
    @Rollback
    public void findNonExistingByHashOrSlug() {
        Assert.assertNull(dao.findByHash("dummy", true));
    }

    @Test
    @Transactional
    @Rollback
    public void findNonExistingByLongUrl() {
        User owner = new User("roblu");
        Assert.assertNull(dao.findByLongUrl(URI.create("http://dummy"), owner));
    }
    
    @Test
    @Transactional
    @Rollback
    public void findByLongUrl() {
        Bookmark loaded = dao.findByLongUrl(bookmark1.getLongUrl().getUrl(), bookmark1.getOwner());
        
        Assert.assertEquals(bookmark1.getHash(), loaded.getHash());
        Assert.assertEquals(bookmark1.getOwner(), loaded.getOwner());
    }

    @Test(expected=PersistenceException.class)
    @Transactional
    @Rollback
    public void duplicateHashNotAllowed() {
        User owner = new User("roblu");
        LongUrl otherLongUrl = new LongUrl(URI.create("http://dummy"), GLOBAL_HASH);
        List<Keyword> keywords = Arrays.asList(new Keyword("kw1"));
        
        dao.persist(new Bookmark(bookmark1.getHash(), otherLongUrl, keywords, owner));
        dao.flush();
    }

}