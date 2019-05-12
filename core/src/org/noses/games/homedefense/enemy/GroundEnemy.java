package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.graphics.Texture;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class GroundEnemy extends Enemy {
    private final float baseSpeed = 1 / 5f;
    private Way way;
    private double progressAlong = 0;
    private double direction;
    private List<PathStep> pathSteps;
    private int currentPathStep;
    private int width;
    private int height;

    public GroundEnemy(HomeDefenseGame parent, Way way) {
        super(parent, "mage.png", 64, 64);
        this.way = way;
        progressAlong = 0;
        direction = 1;
    }

    public void setPath(PathStep finalPathStep) {
        pathSteps = new ArrayList<>();

        PathStep pathStep = finalPathStep;
        while (pathStep != null) {
            pathSteps.add(0, pathStep);
            pathStep = pathStep.getPreviousPath();
        }
        pathStep = pathSteps.get(0);

        if (pathSteps.size() < 2) {
            return;
        }

        // Figure out what direction we're going
        way = pathSteps.get(1).getConnectingWay();
        Intersection intersection = pathStep.getIntersection();
        progressAlong = intersection.getNode(way).getProgress();

        /*System.out.println("Reconfigured finalPath: ");
        for (PathStep debugPathStep : pathSteps) {
            System.out.println("  " + debugPathStep);
        }

        System.out.println("Starting on " + way.getName() + " " + progressAlong);
        */

        currentPathStep = 1;
        putOnPathStep(pathSteps.get(1));
    }

    public void putOnPathStep(PathStep newPathStep) {
        if (newPathStep == null) {
            return;
        }

        if (newPathStep.getStartingNode() == null) {
            return;
        }

        way = newPathStep.getConnectingWay();

        progressAlong = newPathStep.getStartingNode().getProgress();
        direction = ((newPathStep.getEndingNode().getProgress() - newPathStep.getStartingNode().getProgress()) > 0) ? 1 : -1;
        if (currentPathStep >= 2) {
            /*System.out.println("Moving from " + pathSteps.get(currentPathStep - 1).getConnectingWay().getName() +
                    pathSteps.get(currentPathStep - 1).getEndingNode().getX()+"x"+
                    pathSteps.get(currentPathStep - 1).getEndingNode().getY()+" "+
                    " to " + pathSteps.get(currentPathStep).getConnectingWay().getName() +
                    pathSteps.get(currentPathStep).getStartingNode().getX()+"x"+
                    pathSteps.get(currentPathStep).getStartingNode().getY());
                    */
            /*System.out.println("Moving from " + getWay().getName() + " " +
                    getLocation().getX() + "x" +
                    getLocation().getY() + " " +
                    " to " + newPathStep.getConnectingWay().getName() + " " +
                    newPathStep.getStartingNode().getX() + "x" +
                    newPathStep.getStartingNode().getY() + " but actually " +
                    getLocation().getX() + "x" +
                    getLocation().getY() +  " direction="+direction+" progress along="+progressAlong);*/
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

        //currentX -= tileWidth/2;
        //currentY += tileHeight/2;

        return new Point((int) currentX, (int) currentY);
    }

    public void clockTick(float delta) {
        crossesIntersection(delta);

        float speed = way.getMaxSpeed()/4;

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

    private void crossesIntersection(float delta) {
        if (pathSteps.size() <= currentPathStep) {
            return;
        }

        if (pathSteps.size() < 2) {
            System.out.println(getWay().getName() + " has only " + pathSteps.size() + " paths");
            return;
        }

        float speed = way.getMaxSpeed();
        //double newProgress = progressAlong + (direction * baseSpeed * delta * speed);
        double newProgress = progressAlong + (direction * baseSpeed * delta * speed * (1.0f / Math.sqrt(way.getDistance())));

        Node node = pathSteps.get(currentPathStep).getEndingNode();
        while ((node == null) && (currentPathStep < (pathSteps.size() - 1))) {
            System.out.println("NODE IS NULL");
            currentPathStep++;
            node = pathSteps.get(currentPathStep).getEndingNode();
        }

        if (node == null) {
            System.out.println(getWay().getName() + " could not find the next path");
            return;
        }

        boolean needsNewPath = false;
        if (direction > 0) {
            if (newProgress >= node.getProgress()) {
                needsNewPath = true;
            }
        } else {
            if (newProgress <= node.getProgress()) {
                needsNewPath = true;
            }
        }
        if (needsNewPath) {
            currentPathStep++;
            if (currentPathStep >= pathSteps.size()) {
                // TODO
                System.out.println("\n\n\n");
                System.out.println("BOOM!!!");
                System.out.println("\n\n\n");
                Texture avatarAnimationSheet = new Texture("explosion.png");
                frameNumber = 0;

                //animation = TextureRegion.split(avatarAnimationSheet, 64,64);

                parent.killEnemy(this);
            } else {
                putOnPathStep(pathSteps.get(currentPathStep));
            }
        }
    }
}
