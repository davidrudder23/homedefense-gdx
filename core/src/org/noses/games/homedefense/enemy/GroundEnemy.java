package org.noses.games.homedefense.enemy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class GroundEnemy extends Enemy {
    private final float baseSpeed = 1 / 25f;
    private Way way;
    private double progressAlong = 0;
    private double direction;
    private List<PathStep> pathSteps;

    public GroundEnemy(HomeDefenseGame parent, Way way, String spriteFilename, int tileWidth, int tileHeight) {
        super(parent, spriteFilename, tileWidth, tileHeight);
        this.way = way;
        progressAlong = 0;
        direction = 1;
    }

    public void setPathStep(PathStep pathStep) {
        pathSteps = new ArrayList<>();
        while (pathStep != null) {
            pathSteps.add(0, pathStep);
            pathStep = pathStep.getPreviousPath();
        }
        pathStep = pathSteps.get(0);

        if (pathSteps.size()<2) {
            return;
        }

        // Figure out what direction we're going
        way = pathSteps.get(1).getConnectingWay();
        Intersection intersection = pathStep.getIntersection();
        progressAlong = intersection.getNode(way).getProgress();

        double progressTowards = pathSteps.get(1).getIntersection().getNode(way).getProgress();
        if (progressTowards>progressAlong) {
            direction = 1;
        } else {
            direction = -1;
        }
    }

    public Point getLocation() {
        if (way == null) {
            return null;
        }

        Point firstPoint = way.firstPoint();
        Point lastPoint = way.lastPoint();

        double currentX = ((lastPoint.getX() - firstPoint.getX()) * progressAlong) + firstPoint.getX();
        double currentY = ((lastPoint.getY() - firstPoint.getY()) * progressAlong) + firstPoint.getY();

        return new Point((int) currentX, (int) currentY);
    }

    public void clockTick(float delta) {
        Intersection crossedIntersection = crossesIntersection(delta);
        if (crossedIntersection != null) {
            int newWayNum = (int) (Math.random() * crossedIntersection.getWayList().size());
            Way newWay = crossedIntersection.getWayList().get(newWayNum);
            if (!newWay.equals(way)) {
                Node crossedNode = null;
                for (Node node : newWay.getNodes()) {
                    if ((node.getLat() == crossedIntersection.getLatitude()) && (node.getLon() == crossedIntersection.getLongitude())) {
                        crossedNode = node;
                    }
                }
                System.out.println(way.getName() + " crossed intersection with " + crossedIntersection.getWayList().size() + " nodes and moved to " + newWay.getName());
                way = newWay;
                progressAlong = crossedNode.getProgress();
                direction = 1;
            }
        }

        float speed = way.getMaxSpeed();

        if (way.getDistance() != 0) {
            double newProgress = direction * baseSpeed * delta * speed * (1.0f / Math.sqrt(way.getDistance()));
            /*System.out.printf ("total=%f ", newProgress);
            System.out.println("  "
                    +" way distance="+way.getDistance()
                    +" first="+way.firstPoint()
                    +" last="+way.lastPoint()
                    +" baseSpeed="+baseSpeed
                    +" delta="+delta
                    +" speed="+speed
                    +" inv distance="+(1f/(double)way.getDistance())
                    +" progressAlong="+(progressAlong+newProgress));
                    */
            progressAlong += newProgress;
        }
        if (progressAlong < 0) {
            progressAlong = 0;
            direction = 1;
        } else if (progressAlong > 1) {
            progressAlong = 1;
            direction = -1;
        }
    }

    private Intersection crossesIntersection(float delta) {
        float speed = way.getMaxSpeed();
        double newProgress = progressAlong + (direction * baseSpeed * delta * speed);

        for (Node node : way.getNodes()) {
            if ((progressAlong < node.getProgress()) && (newProgress > node.getProgress())) {
                return parent.getIntersectionForNode(node);
            }
        }
        return null;
    }
}
