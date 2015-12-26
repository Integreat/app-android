/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.persistence.resources;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.List;

/**
 * Describes how to store, load or get-an-update-for a particular set of
 * data.
 *
 * @param <E> type of item
 */
public interface PersistableNetworkResource<E> extends PersistableResource<E> {
    /**
     * Request the data directly from the GitHub API, rather than attempting to
     * load it from the DB cache.
     *
     * @return list of items
     * @throws IOException
     */
    @NonNull
    List<E> request() throws IOException;

    /**
     * Determines if a update should be made
     *
     * @return true, if update is required, false otherwise
     */
    boolean shouldUpdate();

    void loadedFromNetwork();
}