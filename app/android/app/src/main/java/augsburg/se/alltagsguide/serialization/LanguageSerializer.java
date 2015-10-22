package augsburg.se.alltagsguide.serialization;

import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.common.Language;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class LanguageSerializer implements JsonDeserializer<List<Language>> {


    @NonNull
    @Override
    public List<Language> deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Language> languages = parseLanguages(json.getAsJsonArray());
        printLanguages(languages);
        return languages;
    }

    @NonNull
    private List<Language> parseLanguages(@NonNull final JsonArray jsonPages) {
        List<Language> Languages = new ArrayList<>();
        for (int i = 0; i < jsonPages.size(); i++) {
            Languages.add(Language.fromJson(jsonPages.get(i).getAsJsonObject()));
        }
        return Languages;
    }

    private void printLanguages(@NonNull final List<Language> Languages) {
        for (Language Language : Languages) {
            printLanguage(Language);
        }
    }


    private void printLanguage(@NonNull final Language Language) {
        System.out.println(Language.getName() + "|" + Language.getShortName() + "|" + Language.getIconPath());
    }
}
