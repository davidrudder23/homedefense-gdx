package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.pathfinding.PathStep;

public class ArmoredGroundEnemy extends GroundEnemy {

    public ArmoredGroundEnemy(MapScreen parent, PathStep pathStep) {
        super(parent, pathStep, "enemy/armored.png", 3, 96, 96, 20);

    }

    public static class ArmoredGroundEnemyBuilder extends GroundEnemyBuilder {
        public ArmoredGroundEnemyBuilder(MapScreen game, Node startingNode) {
            super(game, startingNode);
        }

        @Override
        public Enemy build() {
            ArmoredGroundEnemy enemy = new ArmoredGroundEnemy(game, pathStep);
            enemy.setPath(pathStep);

            return enemy;
        }
    }
}