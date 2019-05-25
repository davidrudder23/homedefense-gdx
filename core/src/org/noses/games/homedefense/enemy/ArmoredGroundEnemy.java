package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.util.HashMap;

public class ArmoredGroundEnemy extends GroundEnemy {

    public ArmoredGroundEnemy(HomeDefenseGame parent, Way way) {
        super(parent, way, "line96.png", 2, 32, 32, 20);

    }

    public static class ArmoredGroundEnemyBuilder extends GroundEnemyBuilder {
        public ArmoredGroundEnemyBuilder(HomeDefenseGame game, HashMap<String, Intersection> intersections) {
            super(game, intersections);
        }

        @Override
        public Enemy build() {
            ArmoredGroundEnemy enemy = new ArmoredGroundEnemy(game, way);
            enemy.setPath(pathStep);

            return enemy;
        }
    }
}