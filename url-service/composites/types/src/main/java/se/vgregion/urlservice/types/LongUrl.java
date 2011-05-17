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

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang.Validate;
import org.hibernate.annotations.Index;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
public class LongUrl extends AbstractEntity<UUID> {

    @Id
    private UUID id;
    
    @Column(nullable=false, unique=true)
    @Index(name="url")
    private String url;
    
    @Column(nullable=false, unique=true)
    @Index(name="hash")
    private String hash;
    
    @OneToMany
    private Collection<Bookmark> bookmarks = new HashSet<Bookmark>();

    protected LongUrl() {
    }

    public LongUrl(URI url, String hash) {
        id = UUID.randomUUID();
        Validate.notNull(url, "url can not be null");
        Validate.notNull(hash, "hash can not be null");
        
        this.url = url.toString();
        this.hash = hash;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public URI getUrl() {
        return URI.create(url);
    }
    
    public String getHash() {
        return hash;
    }

    public Collection<Bookmark> getBookmarks() {
        return Collections.unmodifiableCollection(bookmarks);
    }

    public void addBookmark(Bookmark bookmark) {
        bookmarks.add(bookmark);
    }
    
    
}
