package org.noses.games.homedefense.geolocation;

import org.noses.games.homedefense.geometry.Point;

public class AndroidGeolocator implements Geolocator {
    Point geoLocation;

    public void setGeoLocation(Point point) {
        this.geoLocation = point;
    }

    @Override
    public Point getGeolocation() {

        System.out.println("Getting location " + geoLocation);
        return geoLocation;
    }
}
