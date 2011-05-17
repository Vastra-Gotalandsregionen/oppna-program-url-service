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
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Redirect which matches a domain statically and uses a regular expression
 * to match the path  
 *
 */
@Entity
@Table(uniqueConstraints=
    @UniqueConstraint(columnNames={"domain", "pattern"})
    )

public class RedirectRule extends AbstractRedirect<RedirectRule> {

    /* Make JPA happy */
    protected RedirectRule() {
    }

    public RedirectRule(String domain, String pattern, URI url) {
        super(domain, pattern, url);
    }
    
    public boolean matches(String domain, String path) {
        if(!domainMatches(domain)) return false;
        
        Pattern regex = Pattern.compile(getPattern());
        
        return regex.matcher(path).matches();
    }
}
