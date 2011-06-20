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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.Validate;
import org.hibernate.annotations.Index;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(uniqueConstraints=
    @UniqueConstraint(columnNames={"owner_id", "hash"})
    )
public class Bookmark extends AbstractEntity<UUID> implements UrlWithHash {

    @Id
    private UUID id;
    
    @Column(nullable=false)
    @Index(name="bookmark_hash")
    private String hash;

    @ManyToOne(optional=false)
    private LongUrl longUrl;
    
    @ManyToOne(optional=false)
    private Owner owner;
    
    @ManyToMany
    private List<Keyword> keywords = Collections.emptyList();
    
    protected Bookmark() {
    }

    public Bookmark(String hash, LongUrl longUrl, Owner owner) {
        this(hash, longUrl, Collections.<Keyword>emptyList(), owner);
    }
    
    public Bookmark(String hash, LongUrl longUrl, List<Keyword> keywords, Owner owner) {
        this.id = UUID.randomUUID();
        
        Validate.notEmpty(hash, "hash can not be empty");
        Validate.notNull(longUrl, "longUrl can not be null");
        Validate.notNull(owner, "owner can not be null");
        Validate.noNullElements(keywords, "Keyword element can not be null");
        
        this.hash = hash;
        this.longUrl = longUrl;
        this.keywords = keywords;
        this.owner = owner;
        
        longUrl.addBookmark(this);
        owner.addShortLink(this);
    }
    
    @Override
    public UUID getId() {
        return id;
    }

    public Owner getOwner() {
        return owner;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
    
    public LongUrl getLongUrl() {
        return longUrl;
    }
    
	@Override
	public URI getUrl() {
		return longUrl.getUrl();
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
