package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.SplittingGroundEnemy;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.EnemyConfig;
import org.noses.games.homedefense.level.NestConfig;

public class SplittingEnemyNest extends EnemyNest {
    SplittingGroundEnemy.SplittingGroundEnemyBuilder builder;

    public SplittingEnemyNest(MapScreen parent, NestConfig nestConfig, Point location) {
        super(parent, nestConfig, location);
    }

    @Override
    public double delayBetweenWaves() {
        return 50;
    }

    @Override
    public EnemyGroup getNewEnemyGroup(NestConfig nestConfig) {
        if (builder == null) {
            Node bestNode = getNode();
            if (bestNode != null) {
                builder = new SplittingGroundEnemy.SplittingGroundEnemyBuilder(parent, nestConfig, bestNode);
            }
        }

        if (builder == null) {
            return null;
        }

        EnemyGroup enemyGroup = EnemyGroup.builder()
                .delay(nestConfig.getDelayBetweenEnemies())
                .numEnemies(nestConfig.getNumEnemiesPerWave())
                .enemyBuilder(builder)
                .build();
        parent.addClockTickHandler(enemyGroup);
        return enemyGroup;
    }

    public static class SplittingEnemyNestFactory implements NestFactory {
        MapScreen parent;

        SplittingEnemyNest enemyNest;

        boolean started;

        public SplittingEnemyNestFactory(MapScreen parent) {
            this.parent = parent;

            started = false;
        }

        @Override
        public String getClassName() {
            return "splitting";
        }

        @Override
        public SplittingEnemyNest build(NestConfig nestConfig, Point location) {
            enemyNest = new SplittingEnemyNest(parent,
                    nestConfig,
                    location);
            started = true;
            return enemyNest;
        }

        @Override
        public boolean isKilled() {
            if (!started) {
                return false;
            }
            return enemyNest.isKilled();
        }
    }

}
