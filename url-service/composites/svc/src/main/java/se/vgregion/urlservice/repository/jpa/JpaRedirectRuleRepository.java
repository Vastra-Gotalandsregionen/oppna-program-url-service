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

import java.util.UUID;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.AbstractJpaRepository;
import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.types.RedirectRule;
    
@Repository
public class JpaRedirectRuleRepository extends AbstractJpaRepository<RedirectRule, UUID, UUID> implements RedirectRuleRepository {

    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public RedirectRule find(UUID id) {
        try {
            return (RedirectRule) entityManager.createQuery("select l from RedirectRule l where l.id = :id")
            .setParameter("id", id)
            .getSingleResult();
            
        } catch(NoResultException e) {
            return null;
        }
    }
    
    
    /**
     * Find link by domain and pattern.
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public RedirectRule findByDomainAndPattern(String domain, String pattern) {
        try {
            return (RedirectRule) entityManager.createQuery("select l from " + type.getName() + " l where l.domain = :domain and l.pattern = :pattern")
                .setParameter("domain", domain)
                .setParameter("pattern", pattern)
                .getSingleResult();
        } catch(NoResultException e) {
            return null;
        }

    }

    
}