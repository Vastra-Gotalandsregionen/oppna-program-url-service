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

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.DefaultJpaRepository;
import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.types.ShortLink;
import se.vgregion.urlservice.types.StaticRedirect;
    
@Repository
public class JpaStaticRedirectRepository extends DefaultJpaRepository<StaticRedirect> implements StaticRedirectRepository {
    
    public JpaStaticRedirectRepository() {
        setType(StaticRedirect.class);
    }
    
    /**
     * Find link by hash.
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.REQUIRED, readOnly=true)
    public StaticRedirect findByPath(String domain, String path) {
        try {
            // TODO is this the desired behavior, with null domain, any matching link will be returned
            if(domain == null) {
                List<StaticRedirect> links = entityManager.createQuery("select l from StaticRedirect l where l.pattern = :pattern")
                    .setParameter("pattern", path)
                    .getResultList();
                if(links.isEmpty()) {
                    return null;
                } else {
                    return links.get(0);
                }
            } else {
                return (StaticRedirect) entityManager.createQuery("select l from StaticRedirect l where l.domain = :domain and l.pattern = :pattern")
                    .setParameter("domain", domain)
                    .setParameter("pattern", path)
                    .getSingleResult();
            }
        } catch(NoResultException e) {
            return null;
        }

    }
}