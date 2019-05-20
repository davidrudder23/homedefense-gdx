package org.noses.games.homedefense.enemy.flying;

import lombok.AllArgsConstructor;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyBuilder;
import org.noses.games.homedefense.geometry.Point;

@AllArgsConstructor
public class LeftToRightFlyingEnemyBuilder implements EnemyBuilder {
    HomeDefenseGame game;

    @Override
    public Enemy build() {
        return new FlyingEnemy(game, new Point(game.getMap().getWest(),game.getMap().getNorth()),
                new Point(game.getMap().getWest(),game.getMap().getSouth()));
    }

}
