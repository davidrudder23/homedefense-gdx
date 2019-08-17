package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.GroundEnemy;
import org.noses.games.homedefense.game.BattleScreen;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.NestConfig;

public class GroundEnemyNest extends EnemyNest {
    GroundEnemy.GroundEnemyBuilder builder;
    BattleScreen parent;

    public GroundEnemyNest(BattleScreen parent, NestConfig nestConfig, Point location) {
        super(parent, nestConfig, location);
        this.parent = parent;
    }

    @Override
    public double delayBetweenWaves() {
        return 10;
    }

    @Override
    public EnemyGroup getNewEnemyGroup(NestConfig nestConfig) {
        if (builder == null) {
            Node bestNode = getNode();
            if (bestNode != null) {
                builder = new GroundEnemy.GroundEnemyBuilder(parent, nestConfig.getEnemyConfig(), bestNode);
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

    public static class GroundEnemyNestFactory implements NestFactory {
        BattleScreen parent;

        GroundEnemyNest enemyNest;

        public GroundEnemyNestFactory(BattleScreen parent) {
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
