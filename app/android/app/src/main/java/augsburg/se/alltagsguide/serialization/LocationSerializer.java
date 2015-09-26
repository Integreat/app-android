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

import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.common.Page;

/**
 * Created by Daniel-L on 20.09.2015.
 */
public class LocationSerializer implements JsonDeserializer<List<Location>> {

    @Override
    public List<Location> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Location> locations = parseLocations(json.getAsJsonArray());
        printLocations(locations);
        return locations;
    }

    private List<Location> parseLocations(final JsonArray jsonPages) {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < jsonPages.size(); i++) {
            locations.add(Location.fromJson(jsonPages.get(i).getAsJsonObject()));
        }
        return locations;
    }

    private void printLocations(final List<Location> locations) {
        for (Location location : locations) {
            printLocation(location);
        }
    }


    private void printLocation(final Location location) {
        System.out.println(location.getName() + "|" + location.getPath() + "|" + location.getColor());
    }
}
