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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.repository.UserRepository;
import se.vgregion.urlservice.types.User;

@ContextConfiguration("classpath:services-test.xml")
public class JpaUserRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private UserRepository dao;
    
    private User user1;
    
    @Before
    @Transactional
    public void setup() {
        dao = applicationContext.getBean(UserRepository.class);
        user1 = dao.persist(new User("SE123-123"));
        dao.flush();
    }
    
    @Test
    @Transactional
    @Rollback
    public void find() {
        User loaded = dao.find(user1.getId());
        
        Assert.assertEquals(user1.getId(), loaded.getId());
    }
}
