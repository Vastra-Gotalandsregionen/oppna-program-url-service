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

package se.vgregion.urlservice.repository;

import java.net.URI;
import java.util.UUID;

import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.urlservice.types.Bookmark;
import se.vgregion.urlservice.types.User;
    
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID, UUID> {

    /**
     * Find link by hash or slug.
     */
    Bookmark findByHash(String hash, boolean includeSlugs);

    /**
     * Find link by URL.
     */
    Bookmark findByLongUrl(URI longUrl, User owner);
}