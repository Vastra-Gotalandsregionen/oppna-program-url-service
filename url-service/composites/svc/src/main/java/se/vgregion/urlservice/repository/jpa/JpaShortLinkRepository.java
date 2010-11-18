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

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.DefaultJpaRepository;
import se.vgregion.urlservice.repository.ShortLinkRepository;
import se.vgregion.urlservice.types.ShortLink;
    
@Repository
public class JpaShortLinkRepository extends DefaultJpaRepository<ShortLink> implements ShortLinkRepository {
    
    public JpaShortLinkRepository() {
        setType(ShortLink.class);
    }
    
    /**
     * Find link by hash.
     */
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public ShortLink findByHash(String hash) {
        try {
            return (ShortLink)entityManager.createQuery("select l from ShortLink l where l.pattern = :pattern")
                .setParameter("pattern", hash).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }

    }

    @Override
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public ShortLink findByLongUrl(String longUrl) {
        try {
            return (ShortLink)entityManager.createQuery("select l from ShortLink l where l.url = :url")
            .setParameter("url", longUrl).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }   
}