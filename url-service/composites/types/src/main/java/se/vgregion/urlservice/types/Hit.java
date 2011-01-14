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

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
public class Hit extends AbstractEntity<UUID> {

    @Id
    private UUID id;
    
    @Column
    private UUID bookmarkId;

    @Column
    private UUID longUrlId;

    @Column
    private long timestamp;

    public Hit(UUID bookmarkId, UUID longUrlId, long timestamp) {
        this.id = UUID.randomUUID();
        this.bookmarkId = bookmarkId;
        this.longUrlId = longUrlId;
        this.timestamp = timestamp;
    }
    
    @Override
    public UUID getId() {
        return id;
    }

    public UUID getBookmarkId() {
        return bookmarkId;
    }

    public UUID getLongUrlId() {
        return longUrlId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
