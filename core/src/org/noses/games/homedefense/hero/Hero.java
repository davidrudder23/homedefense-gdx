package org.noses.games.homedefense.hero;

import lombok.Getter;
import org.noses.games.homedefense.game.*;
import org.noses.games.homedefense.geolocation.GeolocationListener;
import org.noses.games.homedefense.geometry.Point;

public class Hero extends Actor implements PhysicalObject, GeolocationListener {

    @Getter
    double longitude;

    @Getter
    double latitude;

    public Hero(MapScreen parent, Point location) {
        super(parent);
        addState("attack", true, "hero/Healer-M-01.png", 24, 32, 0.03, true);

        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void updateLocation(Point newLocation) {
        longitude = newLocation.getLongitude();
        latitude = newLocation.getLatitude();
    }
}
