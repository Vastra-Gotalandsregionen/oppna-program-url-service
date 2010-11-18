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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import se.vgregion.urlservice.repository.RedirectRuleRepository;
import se.vgregion.urlservice.types.RedirectRule;

/**
 * Controller for showing a basic web GUI for shorting link.
 *
 */
@Component()
public class DemoDataPopulator {

    private final Logger log = LoggerFactory.getLogger(DemoDataPopulator.class);

    @Resource
    private RedirectRuleRepository redirectRuleRepository;

    public DemoDataPopulator() {
        log.info("Created {}", DemoDataPopulator.class.getName());
    }
    
    @PostConstruct
    public void createData() {
        redirectRuleRepository.persist(new RedirectRule("http://vgregion.se", "foo/.*", "http://google.com"));
    }
}
