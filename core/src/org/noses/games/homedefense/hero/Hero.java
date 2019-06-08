package org.noses.games.homedefense.hero;

import lombok.Getter;
import org.noses.games.homedefense.game.Animation;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.PhysicalObject;
import org.noses.games.homedefense.geolocation.GeolocationListener;
import org.noses.games.homedefense.geometry.Point;

public class Hero extends Animation implements PhysicalObject, ClockTickHandler, GeolocationListener {

    @Getter
    double longitude;

    @Getter
    double latitude;

    public Hero(MapScreen parent) {
        super(parent, "hero/Healer-M-01.png", 24, 32, 0.03, true);

        longitude = parent.getHome().getLongitude();
        latitude = parent.getHome().getLatitude();
    }

    @Override
    public void updateLocation(Point newLocation) {
        longitude = newLocation.getLongitude();
        latitude = newLocation.getLatitude();
    }
}
