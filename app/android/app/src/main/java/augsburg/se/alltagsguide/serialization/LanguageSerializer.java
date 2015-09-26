package augsburg.se.alltagsguide.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import augsburg.se.alltagsguide.common.Language;
import augsburg.se.alltagsguide.common.Page;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class LanguageSerializer implements JsonDeserializer<List<Language>> {


    @Override
    public List<Language> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Language> Languages = parseLanguages(json.getAsJsonArray());
        printLanguages(Languages);
        return Languages;
    }

    private List<Language> parseLanguages(final JsonArray jsonPages) {
        List<Language> Languages = new ArrayList<>();
        for (int i = 0; i < jsonPages.size(); i++) {
            Languages.add(Language.fromJson(jsonPages.get(i).getAsJsonObject()));
        }
        return Languages;
    }

    private void printLanguages(final List<Language> Languages) {
        for (Language Language : Languages) {
            printLanguage(Language);
        }
    }


    private void printLanguage(final Language Language) {
        System.out.println(Language.getName() + "|" + Language.getShortName() + "|" + Language.getIconPath());
    }
}
