package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.util.HashMap;

public class ArmoredGroundEnemy extends GroundEnemy {

    public ArmoredGroundEnemy(MapScreen parent, PathStep pathStep) {
        super(parent, pathStep, "line96.png", 2, 32, 32, 20);

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