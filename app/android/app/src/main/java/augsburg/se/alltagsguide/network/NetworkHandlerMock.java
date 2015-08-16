package augsburg.se.alltagsguide.network;

import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.common.Information;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.utilities.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
                                    )),
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
