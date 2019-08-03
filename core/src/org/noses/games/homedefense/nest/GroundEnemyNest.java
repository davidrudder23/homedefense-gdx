package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.GroundEnemy;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.NestConfig;

public class GroundEnemyNest extends EnemyNest {
    GroundEnemy.GroundEnemyBuilder builder;

    public GroundEnemyNest(MapScreen parent, NestConfig nestConfig, Point location) {
        super(parent, nestConfig, location);
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

        GroundEnemyNest enemyNest;

        public GroundEnemyNestFactory(MapScreen parent) {
            this.parent = parent;
        }

        @Override
        public String getClassName() {
            return "ground";
        }

        @Override
        public GroundEnemyNest build(NestConfig nestConfig, Point location) {
            enemyNest = new GroundEnemyNest(parent, nestConfig, location);
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
