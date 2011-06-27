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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.types.RedirectRule;

@ContextConfiguration({"classpath:spring/services-common.xml", "classpath:test.xml"})
public class JpaRedirectRuleRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String DOMAIN = "foo.vgregion.se";
    private static final String HASH = "foo";
    private static final URI LONG_URL = URI.create("http://example.com");

    private RedirectRuleRepository dao;
    
    private RedirectRule rule1;
    
    @Before
    public void setup() {
        dao = applicationContext.getBean(RedirectRuleRepository.class);
        rule1 = dao.persist(new RedirectRule(DOMAIN, HASH, LONG_URL.toString()));
    }
    
    @Test
    @Transactional
    @Rollback
    public void find() {
        RedirectRule loaded = dao.find(rule1.getId());
        
        Assert.assertEquals(HASH, loaded.getPattern());
        Assert.assertEquals(LONG_URL.toString(), loaded.getUrl());
    }
}
