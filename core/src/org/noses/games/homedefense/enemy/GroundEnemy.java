package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.graphics.Color;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.EnemyConfig;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;
import org.noses.games.homedefense.tower.Tower;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class GroundEnemy extends Enemy {

    private final int DAMAGE = 20;

    private double progressAlong = 0;
    private double direction;
    private List<PathStep> pathSteps;
    private int currentPathStep;
    private Way way;
    private EnemyConfig enemyConfig;

    public GroundEnemy(MapScreen parent, EnemyConfig enemyConfig, PathStep pathStep) {
        this(parent, enemyConfig, pathStep, "enemy/ground.png", 96, 96, 10);
    }

    protected GroundEnemy(MapScreen parent, EnemyConfig enemyConfig, PathStep pathStep, String spriteFilename, int tileWidth, int tileHeight, int startingHealth) {
        super(parent, spriteFilename, parent.loadSound("normal_hit.mp3"), tileWidth, tileHeight, 0.06, enemyConfig.getHealth());

        this.enemyConfig = enemyConfig;

        progressAlong = 0;
        direction = 1;

        setPath(pathStep);
    }

    @Override
    public boolean canBeHitBy(Tower tower) {
        return true;
    }

    @Override
    public boolean canBeHitByHome() {
        return true;
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
    }

    @Override
    public int getValue() {
        return 10;
    }

    @Override
    public double getLatitude() {
        return getLocation().getLatitude();
    }

    @Override
    public double getLongitude() {
        return getLocation().getLongitude();
    }

    public Point getLocation() {
        if (way == null) {
            return null;
        }

        way.setColor(Color.RED);

        Point firstPoint = way.firstPoint();
        Point lastPoint = way.lastPoint();

        double currentLatitude = firstPoint.getLatitude() + (progressAlong * (lastPoint.getLatitude() - firstPoint.getLatitude())) / way.getDistance();
        double currentLongitude = firstPoint.getLongitude() + (progressAlong * (lastPoint.getLongitude() - firstPoint.getLongitude())) / way.getDistance();

        return new Point((float) currentLatitude, (float) currentLongitude);
    }

    public void clockTick(double delta) {
        super.clockTick(delta);
        crossesIntersection(delta);
        checkForCollision();

        double speed = way.getMaxSpeed() * enemyConfig.getSpeedMultiplier();

        double newProgress = direction * HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph * delta * speed;// * (1.0f / Math.sqrt(way.getDistance()));

        if (way.getDistance() != 0) {
            progressAlong += newProgress;
        }
        if (progressAlong < 0) {
            progressAlong = 0;
            direction = 1;
        } else if (progressAlong > way.getDistance()) {
            progressAlong = way.getDistance();
            direction = -1;
        }
    }

    protected void checkForCollision() {
        Point location = getLocation();
        if (location == null) {
            return;
        }

        for (Tower tower : parent.getTowers()) {
            if (tower == null) {
                continue;
            }
            if (location.getDistanceFrom(tower.getLocation()) < HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph * 100) {
                tower.damage(getDamage());
                kill();
            }
        }

        if (location.getDistanceFrom(parent.getHome().getLocation()) < HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph * 100) {
            parent.hitHome(getDamage());
            kill();
        }

        for (int i = parent.getTowers().size() - 1; i >= 0; i--) {
            if (parent.getTowers().get(i).isKilled()) {
                parent.getTowers().remove(i);
            }
        }
    }

    public int getDamage() {
        return DAMAGE;
    }

    private void crossesIntersection(double delta) {
        if (pathSteps.size() <= currentPathStep) {
            return;
        }

        if (getWay() == null) {
            return;
        }

        if (pathSteps.size() < 2) {
            return;
        }

        float speed = way.getMaxSpeed();
        double newProgress = progressAlong + (direction * HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph * delta * speed * (1.0f / Math.sqrt(way.getDistance())));

        Node node = pathSteps.get(currentPathStep).getEndingNode();
        while ((node == null) && (currentPathStep < (pathSteps.size() - 1))) {
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

            if (currentPathStep < pathSteps.size()) {
                putOnPathStep(pathSteps.get(currentPathStep));
            }
        }
    }

    public static class GroundEnemyBuilder implements EnemyBuilder {
        MapScreen parent;

        PathStep pathStep;
        EnemyConfig enemyConfig;

        public GroundEnemyBuilder(MapScreen parent, EnemyConfig enemyConfig, Node startingNode) {
            this(parent, enemyConfig, startingNode, parent.getHome().getLocation());
        }

        public GroundEnemyBuilder(MapScreen parent, EnemyConfig enemyConfig, Node startingNode, Point target) {
            this (parent, enemyConfig, startingNode, parent.getNodeForLocation(target));
        }

        public GroundEnemyBuilder(MapScreen parent, EnemyConfig enemyConfig, Node startingNode, Node target) {

            this.parent = parent;
            this.enemyConfig = enemyConfig;

            HashMap<String, Intersection> intersections = Intersection.buildIntersectionsFromMap(parent.getMap());

            SecureRandom random = new SecureRandom();
            random.setSeed(System.currentTimeMillis());

            pathStep = null;

            while (pathStep == null) {
                Djikstra djikstra = new Djikstra(intersections);
                Intersection intersection = djikstra.getIntersectionForNode(intersections, startingNode);

                pathStep = djikstra.getBestPath(intersection, target);
            }
        }

        @Override
        public Enemy build() {
            GroundEnemy enemy = new GroundEnemy(parent, enemyConfig, pathStep);
            return enemy;
        }
    }
}
