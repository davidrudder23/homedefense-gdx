package org.noses.games.homedefense.enemy;

import lombok.Setter;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.util.Optional;

public class SplittingGroundEnemy extends GroundEnemy {

    public SplittingGroundEnemy(MapScreen parent, PathStep pathStep) {
        super(parent, pathStep, "line128.png", 10, 32, 32, 20);
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

        SplitEnemyNest splitEnemyNest = new SplitEnemyNest(parent, parent.getTowers(), getLocation());

        parent.getEnemyNests().add(splitEnemyNest);
    }

    public static class SplittingGroundEnemyBuilder extends GroundEnemyBuilder {
        public SplittingGroundEnemyBuilder(MapScreen parent, Node startingNode) {
            super(parent, startingNode);
        }

        @Override
        public Enemy build() {
            SplittingGroundEnemy enemy = new SplittingGroundEnemy(game, pathStep);

            return enemy;
        }
    }
}