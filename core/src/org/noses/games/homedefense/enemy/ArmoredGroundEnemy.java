package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.level.EnemyConfig;
import org.noses.games.homedefense.pathfinding.PathStep;

public class ArmoredGroundEnemy extends GroundEnemy {

    public ArmoredGroundEnemy(MapScreen parent, EnemyConfig enemyConfig, PathStep pathStep) {
        super(parent, enemyConfig, pathStep, "enemy/armored.png", 96, 96, 20);

    }

    public static class ArmoredGroundEnemyBuilder extends GroundEnemyBuilder {
        public ArmoredGroundEnemyBuilder(MapScreen game, EnemyConfig enemyConfig, Node startingNode) {
            super(game, enemyConfig, startingNode);
        }

        @Override
        public Enemy build() {
            ArmoredGroundEnemy enemy = new ArmoredGroundEnemy(parent, enemyConfig, pathStep);
            enemy.setPath(pathStep);

            return enemy;
        }
    }
}