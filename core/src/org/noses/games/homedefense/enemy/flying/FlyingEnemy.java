package org.noses.games.homedefense.enemy.flying;

import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Point;

public class FlyingEnemy extends Enemy {
    private final int DAMAGE = 20;

    private final float baseSpeed = 1 / 21f;

    float x;
    float y;

    @Getter
    private int width;

    @Getter
    private int height;

    Point startingPoint;
    Point endingPoint;

    public FlyingEnemy(HomeDefenseGame parent, Point startingPoint, Point endingPoint) {
        super(parent, "line32.png", parent.loadSound("normal_hit.mp3"), 32, 32, 2);

        this.startingPoint = startingPoint;
        this.endingPoint = endingPoint;

        x = startingPoint.getLatitude();
        y = startingPoint.getLongitude();
    }

    @Override
    public int getDamage() {
        return 3;
    }

    @Override
    public void clockTick(float delta) {
        x++;
        y++;

        if ((x > parent.getMap().getEast()) || (y > parent.getMap().getNorth())) {
            kill();
        }
    }

    @Override
    public Point getLocation() {
        return new Point(x, y);
    }

    @Override
    public int getWidth() {
        return 32;
    }

    @Override
    public int getHeight() {
        return 32;
    }
}
