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

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import se.vgregion.urlservice.repository.KeywordRepository;
import se.vgregion.urlservice.types.Keyword;

@Controller
public class KeywordController {

    @Resource
    private KeywordRepository keywordRepository;
    
    protected KeywordController() {
    }

    public KeywordController(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    @RequestMapping(value="/keywords", params="prefix", method=RequestMethod.GET)
    public @ResponseBody List<Keyword> lookupKeywordsByPrefix(@RequestParam("prefix") String keywordPrefix) {
        return keywordRepository.findByNamePrefix(keywordPrefix);
    }
}
