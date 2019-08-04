package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.enemy.ArmoredGroundEnemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.GroundEnemy;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.NestConfig;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.PathStep;
import org.noses.games.homedefense.tower.Tower;

import java.util.List;

public class SplitEnemyNest extends EnemyNest {

    List<Tower> towers;
    boolean servedOne;

    public SplitEnemyNest(MapScreen parent, NestConfig nestConfig, List<Tower> towers, Point nestLocation) {
        super(parent, nestConfig, nestLocation);
        this.towers = towers;
        servedOne = false;
    }

    @Override
    public double delayBetweenWaves() {
        return 1;
    }

    @Override
    public void clockTick(double delta) {
        super.clockTick(delta);
        boolean nestStillAlive = false;
        for (int g = enemyGroups.size() - 1; g >= 0; g--) {
            EnemyGroup enemyGroup = enemyGroups.get(g);
            if (!enemyGroup.isEmpty()) {
                nestStillAlive = true;
            }
            if (enemyGroup.isEmpty()) {
                enemyGroups.remove(enemyGroup);
            }
        }

        if (!nestStillAlive) {
            kill();
        }
    }

    @Override
    public EnemyGroup getNewEnemyGroup(NestConfig nestConfig) {
        if (servedOne) {
            return null;
        }

        servedOne = true;
        EnemyGroup enemyGroup = new EnemyGroup(0);

        Node node = parent.getNodeForLocation(new Point(getLatitude(), getLongitude()));
        //Thread.dumpStack();
        for (Tower tower : towers) {
            Djikstra djikstra = new Djikstra(parent.getIntersectionsAsHashmap());

            PathStep pathStep = djikstra.getBestPath(node, parent.getNodeForLocation(tower.getLocation()));

            if (pathStep == null) {
                continue;
            }

            GroundEnemy armoredEnemy = new ArmoredGroundEnemy(parent, nestConfig.getEnemyConfig(), pathStep);
            enemyGroup.addEnemy(armoredEnemy);

            GroundEnemy enemy = new GroundEnemy(parent, nestConfig.getEnemyConfig(), pathStep);
            enemyGroup.addEnemy(enemy);
        }
        parent.addClockTickHandler(enemyGroup);

        return enemyGroup;

    }
}
