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

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import se.vgregion.portal.core.infrastructure.persistence.jpa.JpaRepository;
import se.vgregion.urlservice.repository.ShortLinkRepository;
import se.vgregion.urlservice.types.ShortLink;
    
@Repository
public class JpaShortLinkRepository extends JpaRepository<ShortLink, Long> implements ShortLinkRepository {
    
    @PersistenceContext
    private EntityManager em;

    public JpaShortLinkRepository() {
        super(ShortLink.class);
    }
    
    /**
     * Find link by hash.
     */
    public ShortLink findByHash(String hash) {
        try {
            return (ShortLink)em.createQuery("select l from ShortLink l where l.hash = :hash")
                .setParameter("hash", hash).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }

    }

    @Override
    public ShortLink findByUrl(String url) {
        try {
            return (ShortLink)em.createQuery("select l from ShortLink l where l.url = :url")
            .setParameter("url", url).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }   
}