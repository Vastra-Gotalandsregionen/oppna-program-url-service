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

import java.util.ArrayList;
import java.util.Collection;
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
    
@Repository("keywordRepository")
public class JpaKeywordRepository extends AbstractJpaRepository<Keyword, UUID, UUID> implements KeywordRepository {
    
    public JpaKeywordRepository() {
        super(Keyword.class);
    }
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public Keyword find(UUID id) {
        try {
            return (Keyword) entityManager.createQuery("select l from " + type.getSimpleName() + " l where l.id = :id")
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
            return entityManager.createQuery("select l from " + type.getSimpleName() + " l order by name")
            .getResultList();
        } catch(NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public Keyword findByName(String name) {
        try {
            return (Keyword) entityManager.createQuery("select l from " + type.getSimpleName() + " l where l.name = :name")
            .setParameter("name", name)
            .getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }

    @Transactional(propagation=Propagation.MANDATORY)
    public List<Keyword> findOrCreateKeywords(Collection<String> keywordNames) {
        List<Keyword> keywords = new ArrayList<Keyword>();
        if(keywordNames != null) {
            for(String keywordName : keywordNames) {
                Keyword keyword = findByName(keywordName);
                
                if(keyword == null) {
                    if(keywordName.trim().length() > 0) {
                        keyword = new Keyword(keywordName);
                        persist(keyword);
                        keywords.add(keyword);
                    }
                } else {
                    keywords.add(keyword);
                }
            }
        }
        return keywords;
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS, readOnly=true)
    @SuppressWarnings("unchecked")
    public List<Keyword> findByNamePrefix(String prefix) {
        try {
            return entityManager.createQuery("select l from " + type.getSimpleName() + " l " +
            		"where name LIKE :prefix " +
            		"order by name")
            .setParameter("prefix", prefix + "%")
            .getResultList();
        } catch(NoResultException e) {
            return Collections.emptyList();
        }
    }

    
}