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
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import org.springframework.util.Assert;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@javax.persistence.Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Owner extends AbstractEntity<UUID> {

    @SuppressWarnings("unused")
    @Id
    private UUID id;
    
    @Column(nullable=false, unique=true)
    private String name;
    
    @OneToMany
    private Collection<Bookmark> bookmarks = new HashSet<Bookmark>();
    
    protected Owner() {
    }

    public Owner(String name) {
        Assert.hasText(name);

        this.id = UUID.randomUUID();
        this.name = name;
    }

    @Override
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public Collection<Bookmark> getShortLinks() {
        return bookmarks;
    }
    
    public void addShortLink(Bookmark shortLink) {
        bookmarks.add(shortLink);
    }
}
