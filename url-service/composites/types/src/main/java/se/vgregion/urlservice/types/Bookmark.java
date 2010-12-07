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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang.Validate;

import com.sun.org.apache.bcel.internal.generic.LUSHR;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
public class Bookmark extends AbstractEntity<UUID> {

    @Id
    private UUID id;
    
    @Column(nullable=false, unique=true)
    private String hash;

    @Column
    private String slug;

    @ManyToOne(optional=false)
    private LongUrl longUrl;
    
    @ManyToOne(optional=false)
    private User owner;
    
    @ManyToMany
    private List<Keyword> keywords = Collections.emptyList();
    
    protected Bookmark() {
    }

    public Bookmark(String hash, LongUrl longUrl, User owner) {
        this(hash, longUrl, Collections.<Keyword>emptyList(), owner);
    }
    
    public Bookmark(String hash, LongUrl longUrl, List<Keyword> keywords, User owner) {
        this(hash, longUrl, keywords, null, owner);
    }
    
    public Bookmark(String hash, LongUrl longUrl, List<Keyword> keywords, String slug, User owner) {
        this.id = UUID.randomUUID();
        
        Validate.notEmpty(hash, "hash can not be empty");
        Validate.notNull(longUrl, "longUrl can not be null");
        Validate.notNull(owner, "owner can not be null");
        Validate.noNullElements(keywords, "Keyword element can not be null");
        
        this.hash = hash;
        this.longUrl = longUrl;
        this.keywords = keywords;
        this.slug = slug;
        this.owner = owner;
        
        longUrl.addBookmark(this);
        owner.addShortLink(this);
    }

    @Override
    public UUID getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getHash() {
        return hash;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public LongUrl getLongUrl() {
        return longUrl;
    }

    public List<Keyword> getKeywords() {
        if(keywords != null) {
            return Collections.unmodifiableList(keywords);
        } else {
            return null;
        }
    }

    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }

}
