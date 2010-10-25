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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.types.RedirectRule;


public class JpaRedirectRuleRepositoryTest {

    private ApplicationContext ctx = new ClassPathXmlApplicationContext("services-test.xml");
    private RedirectRuleRepository dao = ctx.getBean(RedirectRuleRepository.class);
    
    private RedirectRule rule1;
    
    @Before
    public void setup() {
        rule1 = dao.persist(new RedirectRule("foo", "http://example.com/1"));
    }
    
    @Test
    public void findByPk() {
        RedirectRule loaded = dao.findByPrimaryKey(rule1.getId());
        
        Assert.assertEquals(rule1.getPattern(), loaded.getPattern());
        Assert.assertEquals(rule1.getUrl(), loaded.getUrl());
    }
}
