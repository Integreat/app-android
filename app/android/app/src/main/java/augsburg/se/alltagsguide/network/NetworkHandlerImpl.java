package augsburg.se.alltagsguide.network;

import java.util.List;

import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public class NetworkHandlerImpl implements NetworkHandler {
    @Override
    public List<Content> getContent(Language language, Location location) {
        return null;
    }

    @Override
    public List<Content> getContent(String language, String location) {
        return null;
    }

    @Override
    public List<Location> getAvailableLocations() {
        return null;
    }

    @Override
    public List<Language> getAvailableLanguages(Location location) {
        return null;
    }
}
