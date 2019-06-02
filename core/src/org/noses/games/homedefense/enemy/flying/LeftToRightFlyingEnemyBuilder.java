package org.noses.games.homedefense.enemy.flying;

import lombok.AllArgsConstructor;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyBuilder;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;

@AllArgsConstructor
public class LeftToRightFlyingEnemyBuilder implements EnemyBuilder {
    MapScreen parent;

    @Override
    public Enemy build() {
        return new FlyingEnemy(parent, new Point(parent.getMap().getWest(), parent.getMap().getNorth()),
                new Point(parent.getMap().getWest(), parent.getMap().getSouth()));
    }

}
