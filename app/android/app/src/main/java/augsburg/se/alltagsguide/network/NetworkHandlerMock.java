package augsburg.se.alltagsguide.network;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.utilities.ColorManager;
import retrofit.Callback;
import retrofit.http.Path;


public class NetworkHandlerMock implements NetworkHandler {

    /**
     * Time the response should wait until it's executed (to simulate network).
     */
    private static final int TIME_TO_SLEEP = 1500;

    @Override
    public void getContents(Language language, Location location, Callback<List<Category>> cb) {
        sendDelayed(cb, Arrays.asList(
                new Category("Wilkommen in Augsburg",
                        "Die Stadt Augsburg und Ihre Kommunen",
                        Arrays.asList(
                                new Article("Bürgeramt", "Das Bürgeramt befindet sich in XYZ",
                                        String.format("/location/%s/%s/images/burgeramt.png", location.getName(),
                                                language.getShortName()),
                                        "www.augsburg.de"),
                                new Article("Stadtplan", "Hier sehen sie keine Karte")
                        ),
                        null),

                new Category("Ankunftsinformation",
                        "Ein erster Einstieg bei Ihrer Ankunft",
                        null,
                        Arrays.asList(
                                new Category("Unterbringung",
                                        "So hausen Sie",
                                        Arrays.asList(
                                                new Article("Hausordnung", "Wir trennen Müll, um durch Recycling " +
                                                        "die Umwelt zu schonen. Werfen Sie Papier und Kartons in den Papiermüll " +
                                                        "und ihre Dosen in dafür vorgesehene Container. Diese finden Sie ....")
                                        ), null),
                                new Category("Sozialamt",
                                        "Termine, Informationen und Dokumente"),
                                new Category("Asylberatung",
                                        "Anliegen")
                        )),

                new Category("Notrufnummern",
                        "Polizei, Krankenwagen, Feuerwehr,...",
                        Arrays.asList(new Article("Notfall",
                                "Ausschließlich bei einem Notfall (akute Gesundheitsbedrohung!) dürfen Sie auch ohne Behandlungsschein " +
                                        "zum Krankenhaus oder Arzt gehen. Dort müssen Sie nachweisen, dass Sie Asylsuchender sind " +
                                        "und die Kosten über das Sozialamt abgerechnet werden.\n" +
                                        "\n" +
                                        "Polizei - 110\n" +
                                        "Feuerwehr, Notarzt - 112\n" +
                                        "\n" +
                                        "Bitte beachten Sie die 5 W's...")), null),

                new Category("Feiertage und Öffnungszeiten", "Geschäfte, Kiosk und Tankstellen",
                        Arrays.asList(new Article("Öffnungszeiten", "Die gewöhnlichen Öffnungszeiten von Geschäften in Augsburg sind " +
                                "von Montag bis Samstag von 08:00 Uhr bis 20:00 Uhr. " +
                                "Bei kleineren Geschäften sind diese Zeiten oftmals kürzer, ggf. auch mit einer Mittagspause " +
                                "in der das Geschäft geschlossen ist.\n" +
                                "An Sonn- und Feiertagen sind die beinahe alle Geschäfte geschlossen.\n" +
                                "\n" +
                                "Hier finden Sie eine Übersicht über die Feiertage in Augsburg: ...")), null),

                new Category("Deutsch lernen in Augsburg", "Tandem-Partner, Dolmetscher, Sprachschulen"),

                new Category("Inanspruchnahme von Dolmetschern/Übersetzern", "Schwierigkeiten bei inoffizieller Übersetzung: ..."),

                new Category("Kinder und Familie", "Kindergeld, Kindergarten,..."),

                new Category("Telekommunikation", "Handyvertrag, Festnetzanschluss, ...")
        ));
    }


    @Override
    public void getAvailableLocations(Callback<List<Location>> cb) {

        ColorManager cm = ColorManager.getInstance();
        List<Location> locations = new ArrayList<>();
        String[] locationNames = {"Augsburg", "Berlin", "Düsseldorf", "Freiburg", "München"};
        String[] locationUrl = {"www.augsburg.de", "www.berlin.de", "www.düsseldorf.de", "www.freiburg.de", "www.münchen.de"};
        int[] locationColors = {cm.getColor(0), cm.getColor(3), cm.getColor(6), cm.getColor(9), cm.getColor(12)};
        String[] locationPictures = {
                "http://www.ina-sic.de/bilder/default/augsburg2.png",
                "http://www.briefmarkenverein-berliner-baer.de/foto-berliner-baer/berliner-baer-wappen.gif",
                "http://www.designtagebuch.de/wp-content/uploads/mediathek//duesseldorf-marke-logo.png",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/81/Wappen_Freiburg_im_Breisgau.svg/140px-Wappen_Freiburg_im_Breisgau.svg.png",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Muenchen_Kleines_Stadtwappen.svg/818px-Muenchen_Kleines_Stadtwappen.svg.png"};

        for (int i = 0; i < locationNames.length; i++) {
            locations.add(new Location(locationPictures[i], locationNames[i], locationUrl[i], locationColors[i]));
        }
        sendDelayed(cb, locations);
    }

    @Override
    public void getAvailableLanguages(Location location, Callback<List<Language>> cb) {
        Language english = new Language("https://upload.wikimedia.org/wikipedia/en/thumb/a/ae/Flag_of_the_United_Kingdom.svg/100px-Flag_of_the_United_Kingdom.svg.png", "English", "en");
        Language german = new Language("https://upload.wikimedia.org/wikipedia/en/thumb/b/ba/Flag_of_Germany.svg/100px-Flag_of_Germany.svg.png", "Deutsch", "de");
        Language spanish = new Language("https://upload.wikimedia.org/wikipedia/en/thumb/9/9a/Flag_of_Spain.svg/100px-Flag_of_Spain.svg.png", "Espanol", "es");
        Language frensh = new Language("https://upload.wikimedia.org/wikipedia/en/thumb/c/c3/Flag_of_France.svg/100px-Flag_of_France.svg.png", "Francais", "fr");

        List<Language> languages = new ArrayList<>();
        languages.add(german);
        languages.add(frensh);
        languages.add(english);
        languages.add(spanish);
        sendDelayed(cb, languages);
    }

    /**
     * Sends the response to the success-listener.
     *
     * @param <T>             Type of object, which is send to the successlistener
     * @param successListener listener which should receive the response
     * @param response        response which should be send to the listener
     */
    private <T> void sendDelayed(final Callback<T> successListener, final T response, int timeToSleep) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (successListener != null) {
                    successListener.success(response, null);
                }
            }
        }, timeToSleep);
    }

    /**
     * Sends the response to the success-listener.
     *
     * @param <T>             Type of object, which is send to the successlistener
     * @param successListener listener which should receive the response
     * @param response        response which should be send to the listener
     */
    private <T> void sendDelayed(final Callback<T> successListener, final T response) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (successListener != null) {
                    successListener.success(response, null);
                }
            }
        }, TIME_TO_SLEEP);
    }


    @Override
    public void isServerAlive(Callback<Boolean> callback) {
        sendDelayed(callback, true);
    }


    @Override
    public void getContent(@Path("language") Language language, @Path("location") Location location, @Path("contentid") String contentId, Callback<List<Category>> cb) {
        getContents(language, location, cb);
    }
}
