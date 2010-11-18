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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.StringUtils;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractRedirect<T extends se.vgregion.dao.domain.patterns.entity.Entity<T, Long>> extends AbstractEntity<T, Long> {

    @Id
    @GeneratedValue
    protected Long id;

    @Column
    private String domain;
    
    @Column(nullable=false)
    private String pattern;
    
    @Column(nullable=false)
    private String url;

    public AbstractRedirect() {
    }

    public AbstractRedirect(String domain, String pattern, String url) {
        if(StringUtils.isEmpty(pattern)) {
            throw new IllegalArgumentException("Patter can not be empty");
        }
        if(StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("URL can not be empty");
        }
        
        this.domain = domain;
        this.pattern = pattern;
        this.url = url;
    }
    
    public Long getId() {
        return id;
    }
    
    public String getDomain() {
        return domain;
    }
    public String getPattern() {
        return pattern;
    }
    public String getUrl() {
        return url;
    }
    
    protected boolean domainMatches(String otherDomain) {
        if (domain == null && otherDomain == null) {
            return true;
        } else if(domain != null) {
            return domain.equals(otherDomain);
        } else {
            return false;
        }
    }
    
    public abstract boolean matches(String domain, String path);
}
