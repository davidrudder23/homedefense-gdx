package org.noses.games.homedefense.enemy.flying;

import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Point;

public class FlyingEnemy extends Enemy {
    private final int DAMAGE = 20;

    private final float baseSpeed = 1 / 21f;

    double latitude;
    double longitude;

    Point startingPoint;
    Point endingPoint;

    public FlyingEnemy(HomeDefenseGame parent, Point startingPoint, Point endingPoint) {
        super(parent, "line32.png", parent.loadSound("normal_hit.mp3"), 32, 32, 2);

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
    public void clockTick(double delta) {
        latitude+=HomeDefenseGame.ONE_PIXEL_IN_LATLON;
        longitude+=HomeDefenseGame.ONE_PIXEL_IN_LATLON;

        if ((latitude > parent.getMap().getEast()) || (longitude > parent.getMap().getNorth())) {
            System.out.println("Flying enemy outside bounds");
            kill();
        }
    }

    @Override
    public Point getLocation() {
        return new Point(latitude, longitude);
    }

    @Override
    public double getWidth() {
        return 32;
    }

    @Override
    public double getHeight() {
        return 32;
    }
}
