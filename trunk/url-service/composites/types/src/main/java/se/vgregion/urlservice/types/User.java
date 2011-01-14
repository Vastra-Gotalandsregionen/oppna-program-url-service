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

package se.vgregion.urlservice.types;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.util.Assert;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
public class User extends AbstractEntity<String> {

    @Id
    private String username;
    
    @OneToMany
    private Collection<Bookmark> bookmarks = new HashSet<Bookmark>();
    
    protected User() {
    }

    public User(String vgrId) {
        Assert.hasText(vgrId);
        
        this.username = vgrId;
    }

    @Override
    public String getId() {
        return username;
    }
    
    public String getUserName() {
        return username;
    }

    public Collection<Bookmark> getShortLinks() {
        return bookmarks;
    }
    
    public void addShortLink(Bookmark shortLink) {
        bookmarks.add(shortLink);
    }
}