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

import javax.persistence.PersistenceException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.ShortLinkRepository;
import se.vgregion.urlservice.types.ShortLink;

@ContextConfiguration("classpath:services-test.xml")
public class JpaShortLinkRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private ShortLinkRepository dao;
    
    private ShortLink link1;
    
    @Before
    public void setup() {
        dao = applicationContext.getBean(ShortLinkRepository.class);
        link1 = dao.persist(new ShortLink("foo1", "http://example.com/1", "http://short"));
    }
    
    @Test
    @Transactional
    @Rollback
    public void findByPk() {
        ShortLink loaded = dao.findByPrimaryKey(link1.getId());
        
        Assert.assertEquals(link1.getHash(), loaded.getHash());
        Assert.assertEquals(link1.getLongUrl(), loaded.getLongUrl());
    }

    @Test
    @Transactional
    @Rollback
    public void findByHash() {
        ShortLink loaded = dao.findByHash(link1.getHash());
        
        Assert.assertEquals(link1.getHash(), loaded.getHash());
        Assert.assertEquals(link1.getLongUrl(), loaded.getLongUrl());
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
        Assert.assertNull(dao.findByLongUrl("dummy"));
    }
    
    @Test
    @Transactional
    @Rollback
    public void findByLongUrl() {
        ShortLink loaded = dao.findByLongUrl(link1.getLongUrl());
        
        Assert.assertEquals(link1.getHash(), loaded.getHash());
        Assert.assertEquals(link1.getLongUrl(), loaded.getLongUrl());
    }

    @Test(expected=PersistenceException.class)
    @Transactional
    @Rollback
    public void duplicateHashNotAllowed() {
        dao.persist(new ShortLink(link1.getHash(), "http://dummy", "http://short"));
    }

}
