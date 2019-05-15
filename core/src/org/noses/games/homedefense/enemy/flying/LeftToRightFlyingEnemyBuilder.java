package org.noses.games.homedefense.enemy.flying;

import lombok.AllArgsConstructor;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyBuilder;
import org.noses.games.homedefense.enemy.flying.FlyingEnemy;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;

@AllArgsConstructor
public class LeftToRightFlyingEnemyBuilder implements EnemyBuilder {
    HomeDefenseGame game;

    Rectangle screenCoordinates;

    @Override
    public Enemy build() {
        return new FlyingEnemy(game, new Point(0,0), new Point(640,40));
    }

}
