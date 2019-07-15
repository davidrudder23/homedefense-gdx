package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.SplittingGroundEnemy;
import org.noses.games.homedefense.game.MapScreen;

public class SplittingEnemyNest extends EnemyNest {
    SplittingGroundEnemy.SplittingGroundEnemyBuilder builder;

    public SplittingEnemyNest(MapScreen parent, double delayBeforeStart, double longitude, double latitude) {
        super (parent, "splitting", delayBeforeStart, longitude, latitude);
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

        public SplittingEnemyNestFactory(MapScreen parent) {
            this.parent = parent;
        }

        @Override
        public SplittingEnemyNest build(double delayBeforeStart, double longitude, double latitude) {
            return new SplittingEnemyNest(parent, delayBeforeStart, longitude, latitude);
        }
    }


}
