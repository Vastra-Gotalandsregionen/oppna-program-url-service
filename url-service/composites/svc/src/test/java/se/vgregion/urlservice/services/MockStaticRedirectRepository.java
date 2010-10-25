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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.repository.StaticRedirectRepository;
import se.vgregion.urlservice.types.RedirectRule;
import se.vgregion.urlservice.types.StaticRedirect;
    
public class MockStaticRedirectRepository implements StaticRedirectRepository {

    @Override
    public StaticRedirect findByPath(String path) {
        return null;
    }

    @Override
    public void clear() {
        
    }

    @Override
    public List<StaticRedirect> findByNamedQuery(String queryName, Map<String, ? extends Object> args) {
        return null;
    }

    @Override
    public List<StaticRedirect> findByNamedQuery(String queryName, Object[] args) {
        return null;
    }

    @Override
    public StaticRedirect findInstanceByNamedQuery(String queryName, Object[] args) {
        return null;
    }

    @Override
    public StaticRedirect findInstanceByNamedQuery(String queryName, Map<String, ? extends Object> args) {
        return null;
    }

    @Override
    public StaticRedirect findByPrimaryKey(Long pk) {
        return null;
    }

    @Override
    public void removeByPrimaryKey(Long pk) {
        
    }

    @Override
    public boolean contains(StaticRedirect entity) {
        return false;
    }

    @Override
    public StaticRedirect find(Long id) {
        return null;
    }

    @Override
    public Collection<StaticRedirect> findAll() {
        return null;
    }

    @Override
    public void flush() {
        
    }

    @Override
    public StaticRedirect merge(StaticRedirect object) {
        return null;
    }

    @Override
    public StaticRedirect persist(StaticRedirect object) {
        return null;
    }

    @Override
    public void refresh(StaticRedirect object) {
        
    }

    @Override
    public void remove(StaticRedirect object) {
        
    }

    @Override
    public void remove(Long id) {
        
    }

    @Override
    public StaticRedirect store(StaticRedirect entity) {
        return null;
    }

}