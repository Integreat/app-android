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

import augsburg.se.alltagsguide.common.Location;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class LocationSerializer implements JsonDeserializer<List<Location>> {
    @NonNull
    @Override
    public List<Location> deserialize(@NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()){
            return new ArrayList<>();
        }
        List<Location> locations = parseLocations(json.getAsJsonArray());
        printLocations(locations);
        return locations;
    }

    @NonNull
    private List<Location> parseLocations(@NonNull final JsonArray jsonPages) {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < jsonPages.size(); i++) {
            locations.add(Location.fromJson(jsonPages.get(i).getAsJsonObject()));
        }
        return locations;
    }

    private void printLocations(@NonNull final List<Location> locations) {
        for (Location location : locations) {
            printLocation(location);
        }
    }


    private void printLocation(@NonNull final Location location) {
        System.out.println(location.getName() + "|" + location.getPath() + "|" + location.getColor());
    }
}
