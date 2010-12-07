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

import se.vgregion.urlservice.repository.LongUrlRepository;
import se.vgregion.urlservice.repository.BookmarkRepository;
import se.vgregion.urlservice.types.Keyword;
import se.vgregion.urlservice.types.LongUrl;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.User;

@ContextConfiguration({"classpath:spring/services-common.xml", "classpath:test.xml"})
public class JpaLongUrlRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String HASH = "abcdef";
    private static final URI URL = URI.create("http://example.com");

    private LongUrlRepository dao;
    
    private LongUrl longUrl1;
    
    @Before
    public void setup() {
        dao = applicationContext.getBean(LongUrlRepository.class);
        longUrl1 = dao.persist(new LongUrl(URL, HASH));
        dao.flush();
    }
    
    @Test
    @Transactional
    @Rollback
    public void find() {
        LongUrl loaded = dao.find(longUrl1.getId());
        
        Assert.assertEquals(longUrl1.getUrl(), loaded.getUrl());
    }

    @Test
    @Transactional
    @Rollback
    public void findByHash() {
        LongUrl loaded = dao.findByHash(longUrl1.getHash());
        
        Assert.assertEquals(HASH, loaded.getHash());
        Assert.assertEquals(URL, loaded.getUrl());
    }

    @Test
    @Transactional
    @Rollback
    public void findNonExistingByHash() {
        Assert.assertNull(dao.findByHash("dummy"));
    }


    
    @Test
    @Transactional
    @Rollback
    public void findNonExistingByLongUrl() {
        Assert.assertNull(dao.findByUrl(URI.create("http://dummy")));
    }
    
    @Test
    @Transactional
    @Rollback
    public void findByUrl() {
        LongUrl loaded = dao.findByUrl(longUrl1.getUrl());
        
        Assert.assertEquals(longUrl1.getUrl(), loaded.getUrl());
    }

    @Test(expected=PersistenceException.class)
    @Transactional
    @Rollback
    public void duplicateUrlNotAllowed() {
        dao.persist(new LongUrl(URL, "ghj"));
        dao.flush();
    }

    @Test(expected=PersistenceException.class)
    @Transactional
    @Rollback
    public void duplicateHashNotAllowed() {
        dao.persist(new LongUrl(URI.create("http://google.com"), HASH));
        dao.flush();
    }

}
