package org.noses.games.homedefense.geolocation;

import org.noses.games.homedefense.geometry.Point;

public class AndroidGeolocator extends Geolocator {
    Point geoLocation;

    boolean hasLive = false;

    public void setLive(boolean live) {
        this.hasLive = live;
    }

    public void setGeoLocation(Point point) {
        this.geoLocation = point;
        updateLocation();
    }

    @Override
    public boolean hasLiveGeolocation() {
        return hasLive;
    }



    @Override
    public Point getGeolocation() {
        return geoLocation;
    }
}
