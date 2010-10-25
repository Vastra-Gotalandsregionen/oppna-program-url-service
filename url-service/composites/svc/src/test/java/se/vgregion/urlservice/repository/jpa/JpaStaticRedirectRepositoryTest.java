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

import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.types.StaticRedirect;


public class JpaStaticRedirectRepositoryTest {

    private ApplicationContext ctx = new ClassPathXmlApplicationContext("services-test.xml");
    private StaticRedirectRepository dao = ctx.getBean(StaticRedirectRepository.class);
    
    private StaticRedirect redirect1;
    
    @Before
    public void setup() {
        redirect1 = dao.persist(new StaticRedirect("foo", "http://example.com/1"));
    }
    
    @Test
    public void findByPk() {
        StaticRedirect loaded = dao.findByPrimaryKey(redirect1.getId());
        
        Assert.assertEquals(redirect1.getPath(), loaded.getPath());
        Assert.assertEquals(redirect1.getUrl(), loaded.getUrl());
    }

    @Test
    public void findByPath() {
        StaticRedirect loaded = dao.findByPath(redirect1.getPath());
        
        Assert.assertEquals(redirect1.getPath(), loaded.getPath());
        Assert.assertEquals(redirect1.getUrl(), loaded.getUrl());
    }

    @Test
    public void findNonExistingByHash() {
        Assert.assertNull(dao.findByPath("dummy"));
    }

    @Test(expected=PersistenceException.class)
    public void duplicateHashNotAllowed() {
        dao.persist(new StaticRedirect(redirect1.getPath(), "http://dummy"));
    }

}
