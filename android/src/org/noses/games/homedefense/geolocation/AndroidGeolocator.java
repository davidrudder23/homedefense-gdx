package org.noses.games.homedefense.geolocation;

import org.noses.games.homedefense.geometry.Point;

public class AndroidGeolocator extends Geolocator {
    Point geoLocation;

    public void setGeoLocation(Point point) {
        this.geoLocation = point;
        updateLocation();
    }

    @Override
    public Point getGeolocation() {
        return geoLocation;
    }
}
