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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import se.vgregion.portal.core.domain.patterns.entity.AbstractEntity;

@Entity
public class ShortLink extends AbstractEntity<ShortLink, Long> {

    @Id
    @GeneratedValue
    private long id;
    
    @Column(unique=true, nullable=false)
    private String hash;
    
    @Column(nullable=false)
    private String url;

    public ShortLink() {
    }

    public ShortLink(String hash, String url) {
        this.hash = hash;
        this.url = url;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
    
}
