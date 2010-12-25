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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.types.Keyword;

@ContextConfiguration({"classpath:spring/services-common.xml", "classpath:test.xml"})
public class JpaKeywordRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private KeywordRepository dao;
    
    private Keyword keyword1;
    
    @Before
    @Transactional
    public void setup() {
        dao = applicationContext.getBean(KeywordRepository.class);
        keyword1 = dao.persist(new Keyword("kw1"));
        dao.flush();
    }
    
    @Test
    @Transactional
    @Rollback
    public void find() {
        Keyword loaded = dao.find(keyword1.getId());
        
        Assert.assertEquals(keyword1.getId(), loaded.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void findByName() {
        Keyword loaded = dao.findByName(keyword1.getName());
        
        Assert.assertEquals(keyword1.getId(), loaded.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void findOrCreateKeywords() {
        List<Keyword> loaded = dao.findOrCreateKeywords(Arrays.asList(keyword1.getName(), "kw2"));
        
        Assert.assertEquals(2, loaded.size());
        Assert.assertEquals(keyword1, loaded.get(0));
        Assert.assertEquals("kw2", loaded.get(1).getName());
    }

    @Test
    @Transactional
    @Rollback
    public void findByNamePrefix() {
        List<Keyword> loaded = dao.findByNamePrefix("k");
        
        Assert.assertEquals(1, loaded.size());
        Assert.assertEquals(keyword1, loaded.get(0));
    }

    @Test
    @Transactional
    @Rollback
    public void findByNameFullPrefix() {
        List<Keyword> loaded = dao.findByNamePrefix(keyword1.getName());
        
        Assert.assertEquals(1, loaded.size());
        Assert.assertEquals(keyword1, loaded.get(0));
    }

    @Test
    @Transactional
    @Rollback
    public void findByNameNonMatchingPrefix() {
        List<Keyword> loaded = dao.findByNamePrefix("c");
        
        Assert.assertEquals(0, loaded.size());
    }

}
