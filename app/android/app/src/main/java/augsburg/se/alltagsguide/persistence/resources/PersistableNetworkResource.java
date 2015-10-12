package augsburg.se.alltagsguide.persistence.resources;

import java.io.IOException;
import java.util.List;

/**
 * Describes how to store, load or request-an-update-for a particular set of
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
    List<E> request() throws IOException;

    /**
     * Determines if a update should be made
     *
     * @return true, if update is required, false otherwise
     */
    boolean shouldUpdate();

}