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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class BookmarkTest {

    private static final String HASH = "12345";
    private static final LongUrl LONGURL = new LongUrl(URI.create("http://example.com"));
    private static final User OWNER = new User("roblu");
    private static final List<Keyword> KEYWORDS = Arrays.asList(new Keyword("kw1"));
    
    @Test
    public void cstr() {
        Bookmark bookmark = new Bookmark(HASH, LONGURL, KEYWORDS, OWNER);
        
        Assert.assertNotNull(bookmark.getId());
        Assert.assertEquals(HASH, bookmark.getHash());
        Assert.assertEquals(LONGURL, bookmark.getLongUrl());
        Assert.assertEquals(KEYWORDS, bookmark.getKeywords());
        Assert.assertEquals(OWNER, bookmark.getOwner());
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void nullHashNotAllowed() {
        new Bookmark(null, LONGURL, KEYWORDS, OWNER);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullLongUrlNotAllowed() {
        new Bookmark(HASH, null, KEYWORDS, OWNER);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullOwnerNotAllowed() {
        new Bookmark(HASH, LONGURL, KEYWORDS, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullKeywordsNotAllowed() {
        new Bookmark(HASH, LONGURL, null, OWNER);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullKeywordElementNotAllowed() {
        new Bookmark(HASH, LONGURL, Arrays.asList(new Keyword("kw2"), null), OWNER);
    }

}