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

package se.vgregion.urlservice.services;

import java.util.List;
import java.util.Map;

import se.vgregion.urlservice.repository.ShortLinkRepository;
import se.vgregion.urlservice.types.ShortLink;
    
public class MockShortLinkRepository implements ShortLinkRepository {
    
    /**
     * Find link by hash.
     */
    public ShortLink findByHash(String hash) {
        return null;
    }

    @Override
    public ShortLink findByUrl(String url) {
        return null;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean contains(ShortLink entity) {
        return false;
    }

    @Override
    public void deleteByPk(Long pk) {
        
    }

    @Override
    public List<ShortLink> findAll() {
        return null;
    }

    @Override
    public List<ShortLink> findByNamedQuery(String queryName, Map<String, ? extends Object> args) {
        return null;
    }

    @Override
    public List<ShortLink> findByNamedQuery(String queryName, Object[] args) {
        return null;
    }

    @Override
    public ShortLink findByPk(Long pk) {
        return null;
    }

    @Override
    public ShortLink findInstanceByNamedQuery(String queryName, Object[] args) {
        return null;
    }

    @Override
    public ShortLink findInstanceByNamedQuery(String queryName, Map<String, ? extends Object> args) {
        return null;
    }

    @Override
    public void flush() {
        
    }

    @Override
    public ShortLink merge(ShortLink object) {
        return null;
    }

    @Override
    public ShortLink persist(ShortLink object) {
        return object;
    }

    @Override
    public void refresh(ShortLink object) {
    }

    @Override
    public void removeEntity(ShortLink object) {
    }

    @Override
    public ShortLink store(ShortLink entity) {
        return null;
    }   
}