package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.game.Animation;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.PhysicalObject;

import java.util.ArrayList;
import java.util.List;

public abstract class EnemyNest extends Animation implements PhysicalObject, ClockTickHandler {

    double longitude;
    double latitude;

    boolean killed;

    Node bestNode;

    @Getter
    List<EnemyGroup> enemyGroups;

    double timeSinceLastWave;

    public EnemyNest(MapScreen parent, String nestName, double delayBeforeStart, double longitude, double latitude) {
        super(parent, "nest/" + nestName + ".png", 199, 199, 0.03, true);
        this.longitude = longitude;
        this.latitude = latitude;
        killed = false;

        timeSinceLastWave = delayBetweenWaves() - delayBeforeStart;

        enemyGroups = new ArrayList<>();
    }

    public abstract double delayBetweenWaves();

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public abstract EnemyGroup getNewEnemyGroup();

    @Override
    public void clockTick(double delta) {
        timeSinceLastWave += delta;
        if (timeSinceLastWave > delayBetweenWaves()) {
            timeSinceLastWave = 0;

            EnemyGroup enemyGroup = getNewEnemyGroup();

            if (enemyGroup != null) {
                enemyGroups.add(enemyGroup);
            }
        }

        synchronized (enemyGroups) {
            for (int i = enemyGroups.size() - 1; i >= 0; i--) {
                EnemyGroup enemyGroup = enemyGroups.get(i);
                if (enemyGroup.isEmpty()) {
                    enemyGroup.kill();
                    enemyGroups.remove(enemyGroup);
                }
            }
        }
    }

    @Override
    public void kill() {
        killed = true;
    }

    @Override
    public boolean isKilled() {
        return killed;
    }

    public void render(Batch batch) {
        Sprite sprite = new Sprite(getFrameTextureRegion());

        sprite.setCenterX(parent.convertLongToX(getLongitude()));
        sprite.setCenterY(parent.convertLatToY(getLatitude()));
        sprite.setScale(64 / sprite.getWidth());
        sprite.draw(batch);
    }

    public Node getNode() {
        if (bestNode != null) {
            return bestNode;
        }

        double distanceToBest = 99999999;
        for (Way way : parent.getMap().getWays()) {
            for (Node node : way.getNodes()) {
                double distanceToThis = node.distanceFrom(getLongitude(), getLatitude());
                if (distanceToThis < distanceToBest) {
                    bestNode = node;
                    distanceToBest = distanceToThis;
                }
            }
        }

        System.out.println("bestNode=" + bestNode);

        if (bestNode == null) {
            return null;
        }
        return bestNode;
    }
}
