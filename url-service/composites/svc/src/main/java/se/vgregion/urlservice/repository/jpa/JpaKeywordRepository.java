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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.AbstractJpaRepository;
import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.types.Keyword;
    
@Repository
public class JpaKeywordRepository extends AbstractJpaRepository<Keyword, UUID, UUID> implements KeywordRepository {
    
    public JpaKeywordRepository() {
        super(Keyword.class);
    }
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public Keyword find(UUID id) {
        try {
            return (Keyword) entityManager.createQuery("select l from Keyword l where l.id = :id")
            .setParameter("id", id)
            .getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    @SuppressWarnings("unchecked")
    public List<Keyword> findAllInOrder() {
        try {
            return entityManager.createQuery("select l from Keyword l order by name")
            .getResultList();
        } catch(NoResultException e) {
            return Collections.emptyList();
        }
    }

    
}