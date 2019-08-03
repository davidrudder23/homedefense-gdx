package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.enemy.ArmoredGroundEnemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.NestConfig;

public class ArmoredEnemyNest extends EnemyNest {

    public ArmoredEnemyNest(MapScreen parent, NestConfig nestConfig, Point location) {
        super(parent, nestConfig, location);
    }

    @Override
    public EnemyGroup getNewEnemyGroup(NestConfig nestConfig) {

        Node bestNode = null;
        double distanceToBest = 99999999;
        for (Way way: parent.getMap().getWays()) {
            for (Node node: way.getNodes()) {
                double distanceToThis = node.distanceFrom(getLongitude(), getLatitude());
                if (distanceToThis < distanceToBest) {
                    bestNode = node;
                    distanceToBest = distanceToThis;
                }
            }
        }

        if (bestNode == null) {
            return null;
        }

        EnemyGroup enemyGroup = EnemyGroup.builder()
                .delay(nestConfig.getDelayBetweenEnemies())
                .numEnemies(nestConfig.getNumEnemiesPerWave())
                .enemyBuilder(new ArmoredGroundEnemy.ArmoredGroundEnemyBuilder(parent, nestConfig.getEnemyConfig(), bestNode))
                .build();
        parent.addClockTickHandler(enemyGroup);
        return enemyGroup;
    }

    public static class ArmoredEnemyNestFactory implements NestFactory {
        MapScreen parent;
        ArmoredEnemyNest enemyNest;

        public ArmoredEnemyNestFactory(MapScreen parent) {
            this.parent = parent;
        }

        @Override
        public String getClassName() {
            return "armored";
        }

        @Override
        public ArmoredEnemyNest build(NestConfig nestConfig, Point location) {
            enemyNest = new ArmoredEnemyNest(parent,nestConfig, location);
            return  enemyNest;
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
