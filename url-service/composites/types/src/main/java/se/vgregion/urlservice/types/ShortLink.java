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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.util.Assert;

@Entity
@Table(uniqueConstraints=
    @UniqueConstraint(columnNames={"domain", "pattern"})
    )

public class ShortLink extends AbstractRedirect<ShortLink> {

    @Column(nullable=false)
    private String shortUrl;

    @ManyToOne(optional=true)
    private User owner;
    
    @ManyToMany
    private List<Keyword> keywords = Collections.emptyList();
    
    public ShortLink() {
    }

    public ShortLink(String domain, String hash, String longUrl, String shortUrl) {
        super(domain, hash, longUrl);
        
        Assert.hasLength(domain, "domain can not be empty");
        Assert.hasLength(shortUrl, "shortUrl can not be empty");
        
        this.shortUrl = shortUrl;
    }

    public ShortLink(String domain, String hash, String longUrl, String shortUrl, List<Keyword> keywords, User owner) {
        super(domain, hash, longUrl);
        
        Assert.hasLength(domain, "domain can not be empty");
        Assert.hasLength(shortUrl, "shortUrl can not be empty");
        
        this.shortUrl = shortUrl;
        this.keywords = keywords;
        this.owner = owner;
    }

    
    public String getShortUrl() {
        return shortUrl;
    }

    public User getOwner() {
        return owner;
    }

    public List<Keyword> getKeywords() {
        if(keywords != null) {
            return Collections.unmodifiableList(keywords);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean matches(String domain, String path) {
        if(!domainMatches(domain)) return false;
        
        return getPattern().equals(path);
    }
}
