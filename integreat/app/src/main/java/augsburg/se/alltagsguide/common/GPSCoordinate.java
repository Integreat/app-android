package augsburg.se.alltagsguide.common;


import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaSessionCompat;

import java.io.Serializable;

/**
 * Created by Amadeus on 06. Nov. 2015.
 */
public class GPSCoordinate implements Serializable {
    private static final double EARTH_RADIUS = 6371000.785; // Earth radius in meters

    private double latitude;
    private double longitude;

    public GPSCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Calculates the distance in meters between two GPS coordinates
     * @param gps
     * @return flight distance in meters
     */
    public double distanceTo(GPSCoordinate gps) {
        if (gps == null) return Double.POSITIVE_INFINITY;
        double dist = 0;

        // Convert angles from degree to radiant
        double dLat = (gps.getLatitude() - this.getLatitude()) * Math.PI / 180;
        double dLon = (gps.getLongitude() - this.getLongitude()) * Math.PI / 180;
        double lat1 = this.getLatitude() * Math.PI / 180;
        double lat2 = gps.getLatitude() * Math.PI / 180;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double angle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EARTH_RADIUS * angle;
        return d;
    }

    @NonNull
    public static GPSCoordinate fromLocation(@NonNull Location location) {
        return new GPSCoordinate(location.getLatitude(), location.getLongitude());
    }

    @Override
    public String toString() {
        return "GPS-Coordinate (Lat: "+getLatitude()+", Lon: "+getLongitude()+")";
    }
}
