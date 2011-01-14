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

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.Validate;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
public class Keyword extends AbstractEntity<UUID> {

    public static enum KeywordClass {
        WHITE,
        GREEN,
        GREY,
        BLACK
    }
    
    @Id
    private UUID id;
    
    @Column(nullable=false, unique=true)
    private String name;
    
    @Column
    private KeywordClass classification;
    
    protected Keyword() {
    }

    public Keyword(String name) {
        this(name, KeywordClass.GREY);
    }
    
    public Keyword(String name, KeywordClass classification) {
        this.id = UUID.randomUUID();
        
        Validate.notEmpty(name, "Name can not be empty");
        Validate.notNull(classification, "Classification can not be empty");
        this.name = name;
        this.classification = classification;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public KeywordClass getClassification() {
        return classification;
    }

    public void setClassification(KeywordClass classification) {
        this.classification = classification;
    }
}
