package augsburg.se.alltagsguide.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.common.Information;
import augsburg.se.alltagsguide.utilities.Objects;


public class NetworkHandlerMock implements NetworkHandler {

    @Override
    public List<Content> getContent(Language language, Location location) {
        return Arrays.asList(
                new Content("Wilkommen in Augsburg",
                        "Ein erster Einstieg in die Stadt Augsburg und Ihre Kommunen",
                        Arrays.asList(
                                new Information("Bürgeramt", "Das Bürgeramt befindet sich in XYZ",
                                        String.format("/location/%s/%s/images/burgeramt.png", location.getName(),
                                                language.getShortName()),
                                        "www.augsburg.de"),
                                new Information("Stadtplan", "Hier sehen sie keine Karte")
                        ),
                        null),

                new Content("Ankunftsinformation",
                        "Ein erster Einstieg bei Ihrer Ankunft",
                        null,
                        Arrays.asList(
                                new Content("Unterbringung",
                                        "So hausen Sie",
                                        Arrays.asList(
                                                new Information("Hausordnung", "Wir trennen Müll, um durch Recycling " +
                                                        "die Umwelt zu schonen. Werfen Sie Papier und Kartons in den Papiermüll " +
                                                        "und ihre Dosen in dafür vorgesehene Container. Diese finden Sie ....")
                                        ), null),
                                new Content("Termin Sozialamt",
                                        "Kommen Sie bitte am 03.10. zu Ihrem Sozialamt in der Münchner Straße 3."),
                                new Content("Asylberatung",
                                        "Wir sind für Sie da! Gerne kümmern wir uns um ...")
                        )),

                new Content("Notrufnummern",
                        "Ausschließlich bei einem Notfall (akute Gesundheitsbedrohung!) dürfen Sie auch ohne Behandlungsschein " +
                                "zum Krankenhaus oder Arzt gehen. Dort müssen Sie nachweisen, dass Sie Asylsuchender sind " +
                                "und die Kosten über das Sozialamt abgerechnet werden.\n" +
                                "\n" +
                                "Polizei - 110\n" +
                                "Feuerwehr, Notarzt - 112\n" +
                                "\n" +
                                "Bitte beachten Sie die 5 W's..."),

                new Content("Feiertage und Öffnungszeiten", "Die gewöhnlichen Öffnungszeiten von Geschäften in Augsburg sind " +
                        "von Montag bis Samstag von 08:00 Uhr bis 20:00 Uhr. " +
                        "Bei kleineren Geschäften sind diese Zeiten oftmals kürzer, ggf. auch mit einer Mittagspause " +
                        "in der das Geschäft geschlossen ist.\n" +
                        "An Sonn- und Feiertagen sind die beinahe alle Geschäfte geschlossen.\n" +
                        "\n" +
                        "Hier finden Sie eine Übersicht über die Feiertage in Augsburg: ..."),

                new Content("Deutsch lernen in Augsburg", "Augsburg spricht man \"Augschburg\""),

                new Content("Inanspruchnahme von Dolmetschern/Übersetzern", "Schwierigkeiten bei inoffizieller Übersetzung: ..."),

                new Content("Kinder und Familie", "..."),

                new Content("Telekommunikation", "...")
        );
    }

    @Override
    public List<Content> getContent(String language, String location) {
        return getContent(new Language(null, null, language), new Location(null, location));
    }


    @Override
    public List<Location> getAvailableLocations() {
        List<Location> locations = new ArrayList<>();
        String[] locationNames = {"Augsburg", "Berlin", "Düsseldorf", "Freiburg", "München"};
        String[] locationPictures = {
                "http://www.ina-sic.de/bilder/default/augsburg2.png",
                "http://www.briefmarkenverein-berliner-baer.de/foto-berliner-baer/berliner-baer-wappen.gif",
                "http://www.designtagebuch.de/wp-content/uploads/mediathek//duesseldorf-marke-logo.png",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/81/Wappen_Freiburg_im_Breisgau.svg/140px-Wappen_Freiburg_im_Breisgau.svg.png",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Muenchen_Kleines_Stadtwappen.svg/818px-Muenchen_Kleines_Stadtwappen.svg.png"};

        for (int i = 0; i < locationNames.length; i++) {
            locations.add(new Location(locationPictures[i], locationNames[i]));
        }
        return locations;
    }

    @Override
    public List<Language> getAvailableLanguages(Location location) {
        Language english = new Language("https://upload.wikimedia.org/wikipedia/en/thumb/a/ae/Flag_of_the_United_Kingdom.svg/100px-Flag_of_the_United_Kingdom.svg.png", "English", "en");
        Language german = new Language("https://upload.wikimedia.org/wikipedia/en/thumb/b/ba/Flag_of_Germany.svg/100px-Flag_of_Germany.svg.png", "Deutsch", "de");
        Language spanish = new Language("https://upload.wikimedia.org/wikipedia/en/thumb/9/9a/Flag_of_Spain.svg/100px-Flag_of_Spain.svg.png", "Espanol", "es");
        Language frensh = new Language("https://upload.wikimedia.org/wikipedia/en/thumb/c/c3/Flag_of_France.svg/100px-Flag_of_France.svg.png", "Francais", "fr");

        List<Language> languages = new ArrayList<>();
        languages.add(german);
        languages.add(frensh);
        languages.add(english);
        languages.add(spanish);
        return languages;
    }


}
