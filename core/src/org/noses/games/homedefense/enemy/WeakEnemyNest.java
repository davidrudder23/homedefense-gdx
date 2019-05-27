package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.game.MapScreen;

public class WeakEnemyNest extends EnemyNest {

    public WeakEnemyNest(MapScreen parent, double delayBeforeStart, double longitude, double latitude) {
        super (parent, "cell_tower", delayBeforeStart, longitude, latitude);
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

        System.out.println("bestNode="+bestNode);

        if (bestNode == null) {
            return null;
        }

        EnemyGroup enemyGroup = EnemyGroup.builder()
                .delay(10)
                .numEnemies(10)
                .enemyBuilder(new GroundEnemy.GroundEnemyBuilder(parent, bestNode))
                .build();
        parent.addClockTickHandler(enemyGroup);
        return enemyGroup;
    }
}
