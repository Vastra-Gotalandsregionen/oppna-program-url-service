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
import java.util.UUID;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.AbstractJpaRepository;
import se.vgregion.urlservice.repository.LongUrlRepository;
import se.vgregion.urlservice.types.LongUrl;
    
@Repository
public class JpaLongUrlRepository extends AbstractJpaRepository<LongUrl, UUID, UUID> implements LongUrlRepository {
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public LongUrl find(UUID id) {
        try {
            return (LongUrl) entityManager.createQuery("select l from LongUrl l where l.id = :id")
            .setParameter("id", id)
            .getSingleResult();
            
        } catch(NoResultException e) {
            return null;
        }
    }
    
    /**
     * Find link by hash.
     */
    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public LongUrl findByHash(String hash) {
        try {
            return (LongUrl) entityManager.createQuery("select l from " + type.getSimpleName() + " l where l.hash = :hash")
                .setParameter("hash", hash)
                .getSingleResult();
            
        } catch(NoResultException e) {
            return null;
        }

    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public LongUrl findByUrl(URI url) {
        try {
            return (LongUrl) entityManager.createQuery("select l from LongUrl l where l.url = :url")
                .setParameter("url", url.toString())
                .getSingleResult();
            
        } catch(NoResultException e) {
            return null;
        }
    }

}