package augsburg.se.alltagsguide.network;

import android.content.Context;
import android.os.Handler;

import com.google.inject.Inject;

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
import roboguice.RoboGuice;


public class NetworkServiceMock implements NetworkService {

    /**
     * Time the response should wait until it's executed (to simulate network).
     */
    private static final int TIME_TO_SLEEP = 1500;

    @Inject
    private ColorManager cm;

    @Inject
    public NetworkServiceMock(Context context) {
        RoboGuice.injectMembers(context, this);
    }

    @Override
    public List<Category> getContents(Language language, Location location) {
        List<Category> categories = new ArrayList<>();
        Category welcome = new Category(0, "Willkommen in Augsburg",
                "Die Stadt Augsburg und Ihre Kommunen",
                new ArrayList<>(Arrays.asList(
                        new Article("Bürgeramt", "Das Bürgeramt befindet sich in XYZ",
                                String.format("/location/%s/%s/images/burgeramt.png", location.getName(),
                                        language.getShortName()),
                                "www.augsburg.de"),
                        new Article("Stadtplan", "Hier sehen sie keine Karte")
                )),
                null, 0);
        categories.add(welcome);

        welcome.setSubCategories(new ArrayList<Category>());

        Category arrival = new Category(1, "Ankunftsinformation",
                "Ein erster Einstieg bei Ihrer Ankunft",
                null,
                new ArrayList<>(Arrays.asList(
                        new Category(2, "Unterbringung",
                                "So hausen Sie",
                                new ArrayList<>(Arrays.asList(
                                        new Article("Hausordnung", "Wir trennen Müll, um durch Recycling " +
                                                "die Umwelt zu schonen. Werfen Sie Papier und Kartons in den Papiermüll " +
                                                "und ihre Dosen in dafür vorgesehene Container. Diese finden Sie ....")
                                )), null, 2),
                        new Category(3, "Sozialamt",
                                "Termine, Informationen und Dokumente", 2),
                        new Category(4, "Asylberatung",
                                "Anliegen", 2)
                )), 1);
        welcome.getSubCategories().add(arrival);

        Category emergency = new Category(5, "Notrufnummern",
                "Polizei, Krankenwagen, Feuerwehr,...",
                new ArrayList<>(Arrays.asList(new Article("Notfall",
                        "Ausschließlich bei einem Notfall (akute Gesundheitsbedrohung!) dürfen Sie auch ohne Behandlungsschein " +
                                "zum Krankenhaus oder Arzt gehen. Dort müssen Sie nachweisen, dass Sie Asylsuchender sind " +
                                "und die Kosten über das Sozialamt abgerechnet werden.\n" +
                                "\n" +
                                "Polizei - 110\n" +
                                "Feuerwehr, Notarzt - 112\n" +
                                "\n" +
                                "Bitte beachten Sie die 5 W's..."))), null, 1);
        welcome.getSubCategories().add(emergency);

        Category publicHolidays = new Category(6, "Feiertage und Öffnungszeiten", "Geschäfte, Kiosk und Tankstellen",
                new ArrayList<>(Arrays.asList(new Article("Öffnungszeiten", "Die gewöhnlichen Öffnungszeiten von Geschäften in Augsburg sind " +
                        "von Montag bis Samstag von 08:00 Uhr bis 20:00 Uhr. " +
                        "Bei kleineren Geschäften sind diese Zeiten oftmals kürzer, ggf. auch mit einer Mittagspause " +
                        "in der das Geschäft geschlossen ist.\n" +
                        "An Sonn- und Feiertagen sind die beinahe alle Geschäfte geschlossen.\n" +
                        "\n" +
                        "Hier finden Sie eine Übersicht über die Feiertage in Augsburg: ..."))), null, 1);
        welcome.getSubCategories().add(publicHolidays);

        Category learning =
                new Category(7, "Deutsch lernen in Augsburg", "Tandem-Partner, Dolmetscher, Sprachschulen", 1);
        welcome.getSubCategories().add(learning);

        Category translating =
                new Category(8, "Inanspruchnahme von Dolmetschern/Übersetzern", "Schwierigkeiten bei inoffizieller Übersetzung: ...", 1);
        welcome.getSubCategories().add(translating);

        Category kids =
                new Category(9, "Kinder und Familie", "Kindergeld, Kindergarten,...", 1);
        welcome.getSubCategories().add(kids);

        Category telecommunication =
                new Category(10, "Telekommunikation", "Handyvertrag, Festnetzanschluss, ...", 1);
        welcome.getSubCategories().add(telecommunication);
        return categories;
    }


    @Override
    public List<Location> getAvailableLocations() {

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
        return sendDelayed(locations);
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
        return sendDelayed(languages);
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

    private <T> T sendDelayed(final T response) {
        try {
            Thread.sleep(TIME_TO_SLEEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }


    @Override
    public Boolean isServerAlive() {
        return sendDelayed(true);
    }


    @Override
    public List<Article> getContent(@Path("language") Language language, @Path("location") Location location, @Path("contentid") String contentId) {
        return getContents(language, location).get(0).getArticles();
    }
}
