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

package se.vgregion.urlservice.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.urlservice.types.ShortLink;


public class ShortLinkDaoTest {

    @Test
    public void test() {
        
        ApplicationContext ctx = new ClassPathXmlApplicationContext("services-test.xml");
        
        ShortLink link = create();
        
        ShortLinkDao dao = ctx.getBean(ShortLinkDao.class);
        link = dao.save(link);
        System.out.println(link.getId());
        
        try {
            dao.save(create());
        } catch(Exception e) {
            System.out.println(e.getCause().getClass());
        }
        
        ShortLink link2 = dao.find(1);
        System.out.println(link2.getHash());

        System.out.println(dao.findByHash("abc").getUrl());
    }

    private ShortLink create() {
        ShortLink link = new ShortLink();
        link.setHash("abc");
        link.setUrl("http://example.com");
        return link;
    }
}
