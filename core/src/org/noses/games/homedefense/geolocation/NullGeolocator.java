package org.noses.games.homedefense.geolocation;

import org.noses.games.homedefense.geometry.Point;

public class NullGeolocator extends Geolocator {

    @Override
    public Point getGeolocation() {
        return null;
    }
}
