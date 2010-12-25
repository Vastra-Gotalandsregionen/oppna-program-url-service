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

package se.vgregion.urlservice.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.types.Keyword;


public class KeywordControllerTest {

    private KeywordController controller;
    
    @Before
    public void before() {
        KeywordRepository keywordRepository = Mockito.mock(KeywordRepository.class);
        Mockito.when(keywordRepository.findByNamePrefix("k")).thenReturn(Arrays.asList(new Keyword("kw1")));
        Mockito.when(keywordRepository.findByNamePrefix("kw1")).thenReturn(Arrays.asList(new Keyword("kw1")));
        
        controller = new KeywordController(keywordRepository);
    }
    
    @Test
    public void prefix() throws IOException {
        List<Keyword> keywords = controller.lookupKeywordsByPrefix("k");
        Assert.assertEquals(1, keywords.size());
        Assert.assertEquals("kw1", keywords.get(0).getName());
    }

    @Test
    public void fullPrefix() throws IOException {
        List<Keyword> keywords = controller.lookupKeywordsByPrefix("kw1");
        Assert.assertEquals(1, keywords.size());
        Assert.assertEquals("kw1", keywords.get(0).getName());
    }

    @Test
    public void prefixNotFound() throws IOException {
        List<Keyword> keywords = controller.lookupKeywordsByPrefix("x");
        Assert.assertEquals(0, keywords.size());
    }


}
