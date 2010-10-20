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
import se.vgregion.urlservice.types.RedirectRule;
    
public class MockRedirectRuleRepository implements RedirectRuleRepository {

    @Override
    public void clear() {
    }

    @Override
    public List<RedirectRule> findByNamedQuery(String queryName, Map<String, ? extends Object> args) {
        return null;
    }

    @Override
    public List<RedirectRule> findByNamedQuery(String queryName, Object[] args) {
        return null;
    }

    @Override
    public RedirectRule findInstanceByNamedQuery(String queryName, Object[] args) {
        return null;
    }

    @Override
    public RedirectRule findInstanceByNamedQuery(String queryName, Map<String, ? extends Object> args) {
        return null;
    }

    @Override
    public RedirectRule findByPrimaryKey(Long pk) {
        return null;
    }

    @Override
    public void removeByPrimaryKey(Long pk) {
        
    }

    @Override
    public boolean contains(RedirectRule entity) {
        return false;
    }

    @Override
    public RedirectRule find(Long id) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<RedirectRule> findAll() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void flush() {
        
    }

    @Override
    public RedirectRule merge(RedirectRule object) {
        return null;
    }

    @Override
    public RedirectRule persist(RedirectRule object) {
        return object;
    }

    @Override
    public void refresh(RedirectRule object) {
        
    }

    @Override
    public void remove(RedirectRule object) {
        
    }

    @Override
    public void remove(Long id) {
        
    }

    @Override
    public RedirectRule store(RedirectRule entity) {
        return null;
    }
}