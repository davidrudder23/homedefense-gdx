package org.noses.games.homedefense.enemy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.pathfinding.Intersection;

import java.awt.*;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class GroundEnemy extends Enemy {
    private final float baseSpeed = 1 / 25f;
    private Way way;
    private double progressAlong = 0;
    private double direction;

    public GroundEnemy(HomeDefenseGame parent, Way way, String spriteFilename, int tileWidth, int tileHeight) {
        super(parent, spriteFilename, tileWidth, tileHeight);
        this.way = way;
        progressAlong = 0;
        direction = 1;

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
            int newWayNum = (int)(Math.random() * crossedIntersection.getWayList().size());
            Way newWay = crossedIntersection.getWayList().get(newWayNum);
            if (!newWay.equals(way)) {
                Node crossedNode = null;
                for (Node node: newWay.getNodes()) {
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
        progressAlong += direction * baseSpeed * delta * speed;
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

        for (Node node: way.getNodes()) {
            if ((progressAlong < node.getProgress()) && (newProgress>node.getProgress())) {
                return parent.getIntersectionForNode(node);
            }
        }
        return null;
    }
}
