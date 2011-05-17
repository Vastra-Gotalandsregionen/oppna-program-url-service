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
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.Validate;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

/**
 * Abstract class for all types of redirects. 
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractRedirect<T extends se.vgregion.dao.domain.patterns.entity.Entity<UUID>> extends AbstractEntity<UUID> {

    @Id
    protected UUID id;

    @Column
    private String domain;
    
    @Column(nullable=false)
    private String pattern;
    
    @Column(nullable=false)
    private String url;

    public AbstractRedirect() {
    }

    public AbstractRedirect(String domain, String pattern, URI url) {
        this.id = UUID.randomUUID();
        
        Validate.notEmpty(pattern, "pattern can not be empty");
        Validate.notNull(url, "url can not be null");
        
        this.domain = domain;
        this.pattern = pattern;
        this.url = url.toString();
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getDomain() {
        return domain;
    }
    public String getPattern() {
        return pattern;
    }
    public URI getUrl() {
        return URI.create(url);
    }
    
    protected boolean domainMatches(String otherDomain) {
        if (domain == null && otherDomain == null) {
            return true;
        } else if(domain != null) {
            String canonicalDomain = canonicalDomain(domain);
            String canonicalOtherDomain = canonicalDomain(otherDomain);
            return canonicalDomain.equals(canonicalOtherDomain);
        } else {
            return false;
        }
    }

    private String canonicalDomain(String domainToFix) {
        if(domainToFix == null) return null;
        
        if(domainToFix.endsWith("/")) {
            return domainToFix.substring(0, domainToFix.length() - 1);
        } else {
            return domainToFix;
        }
    }
    
    public abstract boolean matches(String domain, String path);
}
