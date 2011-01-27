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

import se.vgregion.urlservice.repository.ApplicationRepository;
import se.vgregion.urlservice.types.Application;

@ContextConfiguration({"classpath:spring/services-common.xml", "classpath:test.xml"})
public class JpaApplicationRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private ApplicationRepository dao;
    
    private Application application1;
    
    @Before
    @Transactional
    public void setup() {
        dao = applicationContext.getBean(ApplicationRepository.class);
        application1 = dao.persist(new Application("app1"));
        dao.flush();
    }
    
    @Test
    @Transactional
    @Rollback
    public void find() {
        Application loaded = dao.find(application1.getId());
        
        Assert.assertEquals(application1.getId(), loaded.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void findByName() {
        Application loaded = dao.findByName(application1.getName());
        
        Assert.assertEquals(application1.getId(), loaded.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void findByApiKey() {
        Application loaded = dao.findByApiKey(application1.getApikey());
        
        Assert.assertEquals(application1.getId(), loaded.getId());
    }
    

}
