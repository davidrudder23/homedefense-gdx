package org.noses.games.homedefense.enemy.flying;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.tower.Tower;

public class FlyingEnemy extends Enemy {
    private final int DAMAGE = 20;

    private final float baseSpeed = 1 / 21f;

    double latitude;
    double longitude;

    Point startingPoint;
    Point endingPoint;

    public FlyingEnemy(MapScreen parent, Point startingPoint, Point endingPoint) {
        super(parent, "line64.png", parent.loadSound("normal_hit.mp3"), 32, 32, 0.02, 2);

        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;

        latitude = startingPoint.getLatitude();
        longitude = startingPoint.getLongitude();
    }

    @Override
    public int getDamage() {
        return 3;
    }

    @Override
    public int getValue() {
        return 15;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public void clockTick(double delta) {
        latitude+=HomeDefenseGame.ONE_PIXEL_IN_LATLON;
        longitude+=HomeDefenseGame.ONE_PIXEL_IN_LATLON;

        if ((latitude > parent.getMap().getEast()) || (longitude > parent.getMap().getNorth())) {
            System.out.println("Flying enemy outside bounds");
            kill();
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
        return new Point(latitude, longitude);
    }

}
