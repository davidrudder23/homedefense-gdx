package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.PathStep;
import org.noses.games.homedefense.tower.Tower;

import java.util.ArrayList;
import java.util.List;

public class SplitEnemyNest extends EnemyNest {

    List<Tower> towers;
    Djikstra djikstra;

    public SplitEnemyNest(MapScreen parent, List<Tower> towers, Point nestLocation) {
        super(parent, "cell_tower", 0, nestLocation.getLongitude(), nestLocation.getLatitude());
        this.towers = towers;
        djikstra = new Djikstra(parent.getIntersectionsAsHashmap());
    }

    @Override
    public double delayBetweenWaves() {
        return 1;
    }

    @Override
    public EnemyGroup getNewEnemyGroup() {
        EnemyGroup enemyGroup = new EnemyGroup(0);

        Node node = parent.getNodeForLocation(new Point(getLatitude(), getLongitude()));
        for (Tower tower: towers) {
            PathStep pathStep = djikstra.getBestPath(node, tower.getLatitude(), tower.getLongitude());

            if (pathStep == null) {
                System.out.println("Couldn't find pathstep for tower at "+tower.getLocation());
                continue;
            }

            GroundEnemy enemy = new GroundEnemy(parent, pathStep);
            enemyGroup.addEnemy(enemy);
        }
        parent.addClockTickHandler(enemyGroup);

        return enemyGroup;

    }
}
