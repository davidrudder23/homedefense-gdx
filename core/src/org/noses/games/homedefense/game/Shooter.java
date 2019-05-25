package org.noses.games.homedefense.game;

import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Point;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter
public class Shooter {
    double latitude;
    double longitude;
    HomeDefenseGame parent;

    double angle;

    public Shooter(HomeDefenseGame parent, double latitude, double longitude) {
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
