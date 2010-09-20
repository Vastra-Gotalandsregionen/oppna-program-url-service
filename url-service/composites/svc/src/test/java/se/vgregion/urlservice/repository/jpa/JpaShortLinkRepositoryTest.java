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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.urlservice.repository.ShortLinkRepository;
import se.vgregion.urlservice.types.ShortLink;


public class JpaShortLinkRepositoryTest {

    private ApplicationContext ctx = new ClassPathXmlApplicationContext("services-test.xml");
    private ShortLinkRepository dao = ctx.getBean(ShortLinkRepository.class);
    
    private ShortLink link1;
    
    @Before
    public void setup() {
        link1 = dao.persist(new ShortLink("foo1", "http://example.com/1"));
    }
    
    @Test
    public void findByPk() {
        ShortLink loaded = dao.findByPk(link1.getId());
        
        Assert.assertEquals(link1.getHash(), loaded.getHash());
        Assert.assertEquals(link1.getUrl(), loaded.getUrl());
    }

    @Test
    public void findByHash() {
        ShortLink loaded = dao.findByHash(link1.getHash());
        
        Assert.assertEquals(link1.getHash(), loaded.getHash());
        Assert.assertEquals(link1.getUrl(), loaded.getUrl());
    }

    @Test
    public void findNonExistingByHash() {
        Assert.assertNull(dao.findByHash("dummy"));
    }

    @Test
    public void findNonExistingByUrl() {
        Assert.assertNull(dao.findByUrl("dummy"));
    }
    
    @Test
    public void findByUrl() {
        ShortLink loaded = dao.findByUrl(link1.getUrl());
        
        Assert.assertEquals(link1.getHash(), loaded.getHash());
        Assert.assertEquals(link1.getUrl(), loaded.getUrl());
    }

    @Test(expected=PersistenceException.class)
    public void duplicateHashNotAllowed() {
        dao.persist(new ShortLink(link1.getHash(), "http://dummy"));
    }

}
