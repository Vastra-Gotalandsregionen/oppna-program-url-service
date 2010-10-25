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

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
public class StaticRedirect extends AbstractEntity<StaticRedirect, Long> {

    @Id
    @GeneratedValue
    private long id;
    
    @Column(unique=true, nullable=false)
    private String path;
    
    @Column(nullable=false)
    private String url;

    public StaticRedirect() {
    }

    public StaticRedirect(String path, String url) {
        this.path = path;
        this.url = url;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getPath() {
        return path;
    }
    public String getUrl() {
        return url;
    }
    
    public boolean matches(String other) {      
        return path.equals(other);
    }
}
