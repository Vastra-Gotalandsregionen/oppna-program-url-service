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

import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.types.StaticRedirect;

@ContextConfiguration("classpath:services-test.xml")
public class JpaStaticRedirectRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String DOMAIN = "foo.vgregion.se";
    
    private StaticRedirectRepository dao;
    
    private StaticRedirect redirect1;
    
    @Before
    @Transactional
    public void setup() {
        dao = applicationContext.getBean(StaticRedirectRepository.class);
        redirect1 = dao.persist(new StaticRedirect(DOMAIN, "foo", "http://example.com/1"));
        dao.flush();
    }
    
    @Test
    @Transactional
    @Rollback
    public void findByPk() {
        StaticRedirect loaded = dao.findByPrimaryKey(redirect1.getId());
        
        Assert.assertEquals(redirect1.getPattern(), loaded.getPattern());
        Assert.assertEquals(redirect1.getUrl(), loaded.getUrl());
    }

    @Test
    @Transactional
    @Rollback
    public void findByPath() {
        StaticRedirect loaded = dao.findByPath(DOMAIN, redirect1.getPattern());
        
        Assert.assertEquals(redirect1.getPattern(), loaded.getPattern());
        Assert.assertEquals(redirect1.getUrl(), loaded.getUrl());
    }

    @Test
    @Transactional
    @Rollback
    public void findByPathWithNullDomain() {
        StaticRedirect loaded = dao.findByPath(null, redirect1.getPattern());
        
        Assert.assertEquals(redirect1.getPattern(), loaded.getPattern());
        Assert.assertEquals(redirect1.getUrl(), loaded.getUrl());
    }

    @Test
    @Transactional
    @Rollback
    public void findNonExistingByHash() {
        Assert.assertNull(dao.findByPath(DOMAIN, "dummy"));
    }

    @Test(expected=PersistenceException.class)
    @Transactional
    @Rollback
    public void duplicateHashNotAllowed() {
        dao.persist(new StaticRedirect(DOMAIN, redirect1.getPattern(), "http://dummy"));
        dao.flush();
    }

}
