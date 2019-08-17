package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.game.BattleScreen;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.EnemyConfig;
import org.noses.games.homedefense.level.NestConfig;
import org.noses.games.homedefense.nest.SplitEnemyNest;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.util.Optional;

public class SplittingGroundEnemy extends GroundEnemy {
    NestConfig nestConfig;

    public SplittingGroundEnemy(BattleScreen parent, NestConfig nestConfig, PathStep pathStep) {
        super(parent, nestConfig.getEnemyConfig(), pathStep, "enemy/splitting.png", 96, 96, 20);
        this.nestConfig = nestConfig;
    }

    @Override
    public void kill() {
        super.kill();

        Optional<Node> optionalNewNode = getWay().getNodes().stream().sorted((a, b)->
                (int)(new Point(a.getLat(), a.getLon()).getDistanceFrom(getLocation())-
                        new Point(b.getLat(), b.getLon()).getDistanceFrom(getLocation()))).findFirst();

        if (!optionalNewNode.isPresent()) {
            return;
        }

        SplitEnemyNest splitEnemyNest = new SplitEnemyNest(parent, nestConfig, parent.getTowers(), getLocation());
        parent.addClockTickHandler(splitEnemyNest);

        parent.getEnemyNests().add(splitEnemyNest);
    }

    public static class SplittingGroundEnemyBuilder extends GroundEnemyBuilder {
        NestConfig nestConfig;
        public SplittingGroundEnemyBuilder(BattleScreen parent, NestConfig nestConfig, Node startingNode) {
            super(parent, nestConfig.getEnemyConfig(), startingNode);
            this.nestConfig = nestConfig;
        }

        @Override
        public Enemy build() {
            SplittingGroundEnemy enemy = new SplittingGroundEnemy(parent, nestConfig, pathStep);

            return enemy;
        }
    }
}
