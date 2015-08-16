package augsburg.se.alltagsguide.network;

import java.util.List;

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Content;

/**
 * Created by Daniel-L on 28.07.2015.
 */
public interface NetworkHandler {
    List<Content> getContent(Language language, Location location);

    List<Content> getContent(String language, String location);

    List<Location> getAvailableLocations();

    List<Language> getAvailableLanguages(Location location);

}
