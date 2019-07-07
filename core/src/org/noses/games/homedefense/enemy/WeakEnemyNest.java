package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.game.MapScreen;

public class WeakEnemyNest extends EnemyNest {
    GroundEnemy.GroundEnemyBuilder builder;

    public WeakEnemyNest(MapScreen parent, double delayBeforeStart, double longitude, double latitude) {
        super (parent, "ground", delayBeforeStart, longitude, latitude);
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


}
