package org.noses.games.homedefense.enemy.nestlaying;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.ArmoredEnemyNest;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyNest;
import org.noses.games.homedefense.enemy.WeakEnemyNest;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.tower.Tower;

public class NestLayingEnemy extends Enemy {
    Point targetNestLocation;
    Point currentLocation;
    double progressAlong;

    boolean killed;

    int enemyType;

    public NestLayingEnemy(MapScreen parent, Point targetNestLocation, int enemyType) {
        super(parent, "line64.png", parent.loadSound("normal_hit.mp3"), 32, 32, 0.02, 200);

        this.targetNestLocation = targetNestLocation;

        progressAlong = 0;

        killed = false;

        // find the start location
        double startLat;
        double startLon;

        int topOrBottom = (int) (Math.random() * 2);
        if (topOrBottom == 0) {
            double distanceFromTop = Math.abs(targetNestLocation.getLatitude() - parent.getMap().getNorth());
            double distanceFromBottom = Math.abs(targetNestLocation.getLatitude() - parent.getMap().getSouth());
            if (distanceFromTop > distanceFromBottom) {
                currentLocation = new Point(parent.getMap().getNorth(), parent.getHome().getLongitude());
            } else {
                currentLocation = new Point(parent.getMap().getSouth(), parent.getHome().getLongitude());
            }
        } else {
            double distanceFromEast = Math.abs(targetNestLocation.getLongitude() - parent.getMap().getEast());
            double distanceFromWest = Math.abs(targetNestLocation.getLongitude() - parent.getMap().getWest());

            if (distanceFromEast > distanceFromWest) {
                currentLocation = new Point(parent.getHome().getLatitude(), parent.getMap().getEast());
            } else {
                currentLocation = new Point(parent.getHome().getLatitude(), parent.getMap().getWest());
            }
        }
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public int getValue() {
        return 100;
    }

    @Override
    public boolean isKilled() {
        return killed;
    }

    @Override
    public void clockTick(double delta) {
        progressAlong += HomeDefenseGame.LATLON_MOVED_IN_1s_1mph * delta * 10000;

        if (isCloseToTarget()) {
            System.out.println("Dropping a nest");
            EnemyNest enemyNest = null;

            if (enemyType == NestLayingNest.ENEMY_TYPE_ARMORED) {
                enemyNest = new ArmoredEnemyNest(parent, 1, targetNestLocation.getLongitude(), targetNestLocation.getLatitude());
            } else if (enemyType == NestLayingNest.ENEMY_TYPE_GROUND) {
                enemyNest = new WeakEnemyNest(parent, 1, targetNestLocation.getLongitude(), targetNestLocation.getLatitude());
            }

            enemyType++;

            parent.dropNest(enemyNest);
            killed = true;
        }
    }

    @Override
    public boolean canBeHitBy(Tower tower) {
        return false;
    }

    @Override
    public boolean canBeHitByHome() {
        return false;
    }

    @Override
    public Point getLocation() {

        Point firstPoint = currentLocation;
        Point lastPoint = targetNestLocation;

        double currentLatitude = firstPoint.getLatitude() + (progressAlong * (lastPoint.getLatitude() - firstPoint.getLatitude()));
        double currentLongitude = firstPoint.getLongitude() + (progressAlong * (lastPoint.getLongitude() - firstPoint.getLongitude()));

        return new Point((float) currentLatitude, (float) currentLongitude);
    }

    private boolean isCloseToTarget() {
        Point location = getLocation();

        if (location.getDistanceFrom(targetNestLocation) <= (HomeDefenseGame.LATLON_MOVED_IN_1s_1mph*10)) {
            return true;
        }
        return false;
    }

    @Override
    public double getLatitude() {
        return currentLocation.getLatitude();
    }

    @Override
    public double getLongitude() {
        return currentLocation.getLongitude();
    }
}
