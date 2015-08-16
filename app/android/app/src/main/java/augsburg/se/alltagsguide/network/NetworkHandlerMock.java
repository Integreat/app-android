package augsburg.se.alltagsguide.network;

import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.common.Information;
import augsburg.se.alltagsguide.utilities.Objects;


public class NetworkHandlerMock implements NetworkHandler {

    @Override
    public List<Content> getContent(Language language, Location location) {
        List<Content> contents = new ArrayList<>();
        List<Information> information = new ArrayList<>();
        Information information1 = new Information("Bürgeramt", "Das Bürgeramt befindet sich in XYZ", String.format("/location/%s/%s/images/burgeramt.png", location.getName(), language.getShortName()), "www.augsburg.de");
        information.add(information1);
        Content content = new Content("Wilkommen in Augsburg", "Ein erster Einstieg in die Stadt Augsburg und Ihre Kommunen", information, null);
        contents.add(content);
        return contents;
    }

    @Override
    public List<Content> getContent(String language, String location) {
        return getContent(null, new Location(null, null));
    }


    @Override
    public List<Location> getAvailableLocations() {
        List<Location> locations = new ArrayList<>();
        String[] locationNames = {"Augsburg, Berlin, Düsseldorf, Freiburg, München, Ulm"};
        for (String location : locationNames) {
            locations.add(new Location(String.format("/location/%s/images/city.png", location), location));
        }
        return locations;
    }

    @Override
    public List<Language> getAvailableLanguages(Location location) {
        Language english = new Language(String.format("/location/%s/", location.getName()), "English", "en");
        Language german = new Language(String.format("/location/%s/", location.getName()), "Deutsch", "de");
        Language spanish = new Language(String.format("/location/%s/", location.getName()), "Espanol", "es");
        Language frensh = new Language(String.format("/location/%s/", location.getName()), "Francais", "fr");
        List<Language> languages = new ArrayList<>();
        if (Objects.equals(location.getIconPath(), "Augsburg")) {
            languages.add(german);
            languages.add(frensh);
        }
        languages.add(english);
        languages.add(spanish);
        return languages;
    }


}
