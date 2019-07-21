package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.enemy.ArmoredGroundEnemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;

public class ArmoredEnemyNest extends EnemyNest {

    public ArmoredEnemyNest(MapScreen parent, double delayBeforeStart, int numWaves, double longitude, double latitude) {
        super (parent, "armored", delayBeforeStart, numWaves, longitude, latitude);
    }

    @Override
    public double delayBetweenWaves() {
        return 10;
    }

    @Override
    public EnemyGroup getNewEnemyGroup() {

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
                .delay(10)
                .numEnemies(10)
                .enemyBuilder(new ArmoredGroundEnemy.ArmoredGroundEnemyBuilder(parent, bestNode))
                .build();
        parent.addClockTickHandler(enemyGroup);
        return enemyGroup;
    }

    public static class ArmoredEnemyNestFactory implements NestFactory {
        MapScreen parent;
        int numWaves;
        ArmoredEnemyNest enemyNest;

        public ArmoredEnemyNestFactory(MapScreen parent, Integer numWaves) {
            this.parent = parent;
            this.numWaves = numWaves;
        }

        @Override
        public ArmoredEnemyNest build(double delayBeforeStart, Point location) {
            enemyNest = new ArmoredEnemyNest(parent, delayBeforeStart, numWaves, location.getLongitude(), location.getLatitude());
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
