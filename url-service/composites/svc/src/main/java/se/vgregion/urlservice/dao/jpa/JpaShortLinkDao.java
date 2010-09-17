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

package se.vgregion.urlservice.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.urlservice.dao.ShortLinkDao;
import se.vgregion.urlservice.types.ShortLink;
    
@Repository("shortLinkDao")
@Transactional(readOnly = true)
public class JpaShortLinkDao implements ShortLinkDao {
    
    @PersistenceContext
    private EntityManager em;

    /**
     * Find link.
     */
    public ShortLink find(long id) {
        return em.find(ShortLink.class, id);
    }

    /**
     * Find link by hash.
     */
    public ShortLink findByHash(String hash) {
        return (ShortLink)em.createNamedQuery("findByHash")
                .setParameter("hash", hash).getSingleResult();
    }

    
    /**
     * Saves link.
     */
    @Transactional(readOnly = false)
    public ShortLink save(ShortLink link) {
        return em.merge(link);
    }

    /**
     * Deletes link.
     */
    @Transactional(readOnly = false)
    public void delete(ShortLink link) {
        em.remove(em.merge(link));
    }   
}