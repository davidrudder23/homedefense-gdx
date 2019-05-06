package org.noses.games.homedefense.player;

import lombok.Data;
import org.noses.games.homedefense.enemy.Enemy;

import java.awt.*;

@Data
public class Bullet extends Shot {

    int x;
    int y;

    double angle;

    double speed;

    public boolean checkIfHit(Enemy enemy) {
        Point enemyLocation = enemy.getLocation();

        int halfWidth = enemy.getWidth() / 2;
        int halfHeight = enemy.getHeight() / 2;

        if ((x >= (enemyLocation.getX() - halfWidth)) ||
                (x <= (enemyLocation.getX() + halfWidth))) {
            return true;
        }

        if ((y >= (enemyLocation.getY() - halfHeight)) ||
                (y <= (enemyLocation.getY() + halfHeight))) {
            return true;
        }

        return false;
    }

}
