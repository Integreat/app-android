/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

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
