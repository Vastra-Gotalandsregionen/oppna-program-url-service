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
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class LongUrlTest {

    private static final URI URL = URI.create("http://example.com");

    @Test
    public void cstr() {
        LongUrl longUrl = new LongUrl(URL);
        Assert.assertNotNull(longUrl.getId());
        Assert.assertEquals(URL, longUrl.getUrl());
        
        // must not be null
        Assert.assertEquals(0, longUrl.getBookmarks().size());
    }
    
    @Test
    public void addBookmark() {
        LongUrl longUrl = new LongUrl(URL);
        
        // bookmark addded on longUrl in constructor 
        Bookmark bookmark = new Bookmark("123", longUrl, Arrays.asList(new Keyword("kw1")), new User("roblu"));
        
        Assert.assertEquals(1, longUrl.getBookmarks().size());
    }

    @Test
    public void addBookmarkIdempotent() {
        LongUrl longUrl = new LongUrl(URL);
        
        Bookmark bookmark = new Bookmark("123", longUrl, Arrays.asList(new Keyword("kw1")), new User("roblu"));

        // adding this an extra time
        longUrl.addBookmark(bookmark);
        
        Assert.assertEquals(1, longUrl.getBookmarks().size());
    }


    
    @Test(expected=IllegalArgumentException.class)
    public void nullUrlNotAllowed() {
        new LongUrl(null);
    }

}