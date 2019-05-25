package org.noses.games.homedefense.game;

import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;

@Getter
public class Aimer {
    double latitude;
    double longitude;
    HomeDefenseGame parent;

    double angle;

    public Aimer(HomeDefenseGame parent, double latitude, double longitude) {
        this.parent = parent;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double aim(PhysicalObject target) {
        double enemyLong = target.getLongitude();
        double enemyLat = target.getLatitude();

        double angle = Math.atan2(getLongitude() - enemyLong,
                getLatitude() - enemyLat);
        angle = 360 - (180 - (angle * (180 / Math.PI)));

        //System.out.println ("Aiming "+angle+" deg at "+new Point(enemyLat, enemyLong)+" for enemy at "+closestEnemy.getBoundingBox());
        return angle;
    }



}
