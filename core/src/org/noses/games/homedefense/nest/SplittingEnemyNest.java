package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.SplittingGroundEnemy;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;

public class SplittingEnemyNest extends EnemyNest {
    SplittingGroundEnemy.SplittingGroundEnemyBuilder builder;

    public SplittingEnemyNest(MapScreen parent, double delayBeforeStart, int numWaves, double longitude, double latitude) {
        super (parent, "splitting", delayBeforeStart, numWaves, longitude, latitude);
    }

    @Override
    public double delayBetweenWaves() {
        return 50;
    }

    @Override
    public EnemyGroup getNewEnemyGroup() {
        if (builder == null) {
            Node bestNode = getNode();
            if (bestNode != null) {
                builder = new SplittingGroundEnemy.SplittingGroundEnemyBuilder(parent, bestNode);
            }
        }

        if (builder == null) {
            return null;
        }

        EnemyGroup enemyGroup = EnemyGroup.builder()
                .delay(30)
                .numEnemies(3)
                .enemyBuilder(builder)
                .build();
        parent.addClockTickHandler(enemyGroup);
        return enemyGroup;
    }

    public static class SplittingEnemyNestFactory implements NestFactory {
        MapScreen parent;
        int numWaves;

        SplittingEnemyNest enemyNest;

        boolean started;

        public SplittingEnemyNestFactory(MapScreen parent, int numWaves) {
            this.parent = parent;
            this.numWaves = numWaves;

            started = false;
        }

        @Override
        public SplittingEnemyNest build(double delayBeforeStart, Point location) {
            enemyNest = new SplittingEnemyNest(parent, delayBeforeStart, numWaves, location.getLongitude(), location.getLatitude());
            started = true;
            return enemyNest;
        }

        @Override
        public boolean isKilled() {
            if (!started) {
                //System.out.println("Splitting enemy nest is not started");
                return false;
            }
            return enemyNest.isKilled();
        }
    }


}
