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

package se.vgregion.urlservice.services;

import se.vgregion.urlservice.dao.ShortLinkRepository;
import se.vgregion.urlservice.types.ShortLink;
    
public class MockShortLinkRepository implements ShortLinkRepository {
    
    /**
     * Find link.
     */
    public ShortLink find(long id) {
        return null;
    }

    /**
     * Find link by hash.
     */
    public ShortLink findByHash(String hash) {
        return null;
    }

    
    /**
     * Saves link.
     */
    public ShortLink save(ShortLink link) {
        return link;
    }

    /**
     * Deletes link.
     */
    public void delete(ShortLink link) {
    }

    @Override
    public ShortLink findByUrl(String url) {
        return null;
    }   
}