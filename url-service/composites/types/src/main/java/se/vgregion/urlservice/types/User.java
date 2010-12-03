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

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.util.Assert;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
public class User extends AbstractEntity<String> {

    @Id
    private String vgrId;
    
    @OneToMany(fetch=FetchType.EAGER)
    private Collection<ShortLink> shortLinks = new ArrayList<ShortLink>();
    
    protected User() {
    }

    public User(String vgrId) {
        Assert.hasText(vgrId);
        
        this.vgrId = vgrId;
    }

    @Override
    public String getId() {
        return vgrId;
    }
    
    public String getVgrId() {
        return vgrId;
    }

    public Collection<ShortLink> getShortLinks() {
        return shortLinks;
    }
    
    public void addShortLink(ShortLink shortLink) {
        shortLinks.add(shortLink);
    }
}
