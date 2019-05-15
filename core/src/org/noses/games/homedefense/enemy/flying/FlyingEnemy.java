package org.noses.games.homedefense.enemy.flying;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyBuilder;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;

public class FlyingEnemy extends Enemy {
    private final int DAMAGE = 20;

    private final float baseSpeed = 1 / 21f;

    int x;
    int y;

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

        x = startingPoint.getX();
        y = startingPoint.getY();
    }

    @Override
    public int getDamage() {
        return 3;
    }

    @Override
    public void clockTick(float delta) {
        x++;
        y++;

        if ((x > 640) || (y > 480)) {
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
