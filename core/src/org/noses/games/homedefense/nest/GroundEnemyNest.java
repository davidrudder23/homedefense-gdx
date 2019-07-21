package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.GroundEnemy;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;

public class GroundEnemyNest extends EnemyNest {
    GroundEnemy.GroundEnemyBuilder builder;

    public GroundEnemyNest(MapScreen parent, double delayBeforeStart, int numWaves, double longitude, double latitude) {
        super(parent, "ground", delayBeforeStart, numWaves, longitude, latitude);
    }

    @Override
    public double delayBetweenWaves() {
        return 10;
    }

    @Override
    public EnemyGroup getNewEnemyGroup() {
        if (builder == null) {
            Node bestNode = getNode();
            if (bestNode != null) {
                builder = new GroundEnemy.GroundEnemyBuilder(parent, bestNode);
            }
        }

        if (builder == null) {
            return null;
        }

        EnemyGroup enemyGroup = EnemyGroup.builder()
                .delay(10)
                .numEnemies(10)
                .enemyBuilder(builder)
                .build();
        parent.addClockTickHandler(enemyGroup);
        return enemyGroup;
    }

    public static class GroundEnemyNestFactory implements NestFactory {
        MapScreen parent;
        int numWaves;

        GroundEnemyNest enemyNest;

        public GroundEnemyNestFactory(MapScreen parent, Integer numWaves) {
            this.parent = parent;
            this.numWaves = numWaves;
        }

        @Override
        public GroundEnemyNest build(double delayBeforeStart, Point location) {
            enemyNest = new GroundEnemyNest(parent, delayBeforeStart, numWaves, location.getLongitude(), location.getLatitude());
            return enemyNest;
        }

        @Override
        public boolean isKilled() {
            if (enemyNest == null) {
                return false;
            }
            return enemyNest.isKilled();
        }
    }

}
