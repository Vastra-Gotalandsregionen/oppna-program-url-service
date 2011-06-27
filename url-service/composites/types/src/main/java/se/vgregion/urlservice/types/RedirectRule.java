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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
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

	@Transient
	private Pattern regex;
	
    /* Make JPA happy */
    protected RedirectRule() {
    }

    public RedirectRule(String domain, String pattern, String url) {
        super(domain, pattern, url);
    }

    private void compileRegex() {
        if(regex == null) {
        	regex = Pattern.compile(getPattern());
        }
    }
    
    public boolean matches(String domain, String path) {
        if(!domainMatches(domain)) return false;
        
        compileRegex();
        
        return regex.matcher(path).matches();
    }
    
    public URI resolve(String path) {
    	compileRegex();
    	
    	String url = getUrl();
    	
    	Matcher matcher = regex.matcher(path);
    	if(matcher.matches()) {
    		for(int i = 0; i<matcher.groupCount() + 1; i++) {
    			url = url.replace("{" + i + "}", matcher.group(i));
    		}
    		
    		// remove the extra placeholders
    		url = url.replaceAll("\\{\\d+\\}", "");
    		
    		return URI.create(url);
    	} else {
    		throw new IllegalArgumentException("path does not match pattern, verify with matches() first");
    	}
    }
}
