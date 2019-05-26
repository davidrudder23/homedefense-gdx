package org.noses.games.homedefense.game;

import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Point;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        if (target == null) {
            return 0;
        }
        
        double enemyLong = target.getLongitude();
        double enemyLat = target.getLatitude();

        double angle = Math.atan2(getLongitude() - enemyLong,
                getLatitude() - enemyLat);
        angle = 360 - (180 - (angle * (180 / Math.PI)));

        //System.out.println ("Aiming "+angle+" deg at "+new Point(enemyLat, enemyLong)+" for enemy at "+closestEnemy.getBoundingBox());
        return angle;
    }

    public Enemy findClosestEnemy(List<Enemy> enemies) {
        // find enemy
        if ((enemies == null) || (enemies.size() == 0)) {
            return null;
        }


        Collections.sort(enemies, new Comparator<Enemy>() {
            @Override
            public int compare(Enemy a, Enemy b) {
                Point locationA = a.getLocation();
                Point locationB = b.getLocation();

                double homeCalc = (getLatitude() * getLatitude()) + (getLongitude() * getLongitude());
                double aCalc = (locationA.getLatitude() - getLatitude()) * (locationA.getLatitude() - getLatitude())
                        + (locationA.getLongitude() - getLongitude()) * (locationA.getLongitude() - getLongitude());
                double bCalc = (locationB.getLatitude() - getLatitude()) * (locationB.getLatitude() - getLatitude())
                        + (locationB.getLongitude() - getLongitude()) * (locationB.getLongitude() - getLongitude());

                if (aCalc == bCalc) {
                    return 0;
                }

                if (aCalc < bCalc) {
                    return -1;
                }

                return 1;
            }
        });

        Enemy closestEnemy = enemies.get(0);
        return closestEnemy;
    }

}
