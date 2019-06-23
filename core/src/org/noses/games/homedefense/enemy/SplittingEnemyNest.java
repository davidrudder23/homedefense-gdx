package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.game.MapScreen;

public class SplittingEnemyNest extends EnemyNest {
    SplittingGroundEnemy.SplittingGroundEnemyBuilder builder;

    public SplittingEnemyNest(MapScreen parent, double delayBeforeStart, double longitude, double latitude) {
        super (parent, "mind_probe", delayBeforeStart, longitude, latitude);
    }

    @Override
    public double delayBetweenWaves() {
        return 15;
    }

    @Override
    public EnemyGroup getNewEnemyGroup() {
        if (builder == null) {
            Node bestNode = getNode();
            if (bestNode != null) {
                System.out.println("Making builder with parent="+parent);
                builder = new SplittingGroundEnemy.SplittingGroundEnemyBuilder(parent, bestNode);
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
