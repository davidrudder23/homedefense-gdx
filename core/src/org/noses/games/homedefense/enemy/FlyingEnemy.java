package org.noses.games.homedefense.enemy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.pathfinding.Intersection;

import java.util.HashMap;

public class FlyingEnemy extends Enemy {
    private final int DAMAGE = 20;

    private final float baseSpeed = 1 / 2f;

    int x;
    int y;

    @Getter
    private int width;

    @Getter
    private int height;

    public FlyingEnemy(HomeDefenseGame parent) {
        super(parent, "line32.png", parent.loadSound("normal_hit.mp3"), 32, 32, 2);

        x = 0;
        y = 0;
    }

    @Override
    public int getDamage() {
        return 3;
    }

    @Override
    public void clockTick(float delta) {
        x++;
        y++;

        if ((x>640) || (y>480)) {
            kill();
        }
    }

    @Override
    public Point getLocation() {
        return new Point(x,y);
    }

    @Override
    public int getWidth() {
        return 32;
    }

    @Override
    public int getHeight() {
        return 32;
    }


    @AllArgsConstructor
    public static class FlyingEnemyBuilder implements EnemyBuilder {
        HomeDefenseGame game;

        @Override
        public Enemy build() {
            return new FlyingEnemy(game);
        }
    }
}
