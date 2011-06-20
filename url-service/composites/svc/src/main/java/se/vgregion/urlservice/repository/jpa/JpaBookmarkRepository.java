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
import se.vgregion.urlservice.repository.BookmarkRepository;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.Owner;
    
@Repository
public class JpaBookmarkRepository extends AbstractJpaRepository<Bookmark, UUID, UUID> implements BookmarkRepository {
    
    public JpaBookmarkRepository() {
        setType(Bookmark.class);
    }
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public Bookmark find(UUID id) {
        try {
            return (Bookmark) entityManager.createQuery("select l from " + type.getSimpleName() + " l where l.id = :id")
            .setParameter("id", id)
            .getSingleResult();
            
        } catch(NoResultException e) {
            return null;
        }
    }
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public Bookmark findByHash(String hash, Owner owner) {
        try {
            // TODO could have multiple matches, handle
            String query = "select l from " + type.getSimpleName() + " l where l.hash = :hash and l.owner.id = :owner";
            
            return (Bookmark) entityManager.createQuery(query)
            .setParameter("hash", hash)
            .setParameter("owner", owner.getId())
            .getSingleResult();
            
        } catch(NoResultException e) {
            return null;
        }
        
    }
    
    @Override
    @Transactional(propagation=Propagation.MANDATORY, readOnly=true)
    public Bookmark findByLongUrl(URI longUrl, Owner owner) {
        try {
            return (Bookmark)entityManager.createQuery("select l from " + type.getSimpleName() + " l where l.owner.name = :name " +
            		"and l.longUrl.url = :longurl")
	    .setParameter("name", owner.getName())
            .setParameter("longurl", longUrl.toString())
            .getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }

}