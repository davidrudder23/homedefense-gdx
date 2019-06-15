package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;

public class NestLayer extends Enemy {
    Point targetNestLocation;

    public NestLayer(MapScreen parent, Point targetNestLocation) {
        super(parent, "line1.png", parent.loadSound("normal_hit.mp3"), 32,32, 0.02, 200);

        this.targetNestLocation = targetNestLocation;
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
    public void clockTick(double delta) {

    }

    @Override
    public Point getLocation() {
        return null;
    }

    @Override
    public double getLatitude() {
        return 0;
    }

    @Override
    public double getLongitude() {
        return 0;
    }
}
