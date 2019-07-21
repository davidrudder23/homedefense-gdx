package org.noses.games.homedefense.nest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.enemy.EnemyGroup;
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

    int numWaves;
    int waveNum;

    public EnemyNest(MapScreen parent, String nestName, double delayBeforeStart, int numWaves, double longitude, double latitude) {
        super(parent, "nest/" + nestName + ".png", 199, 199, 0.08, true);
        this.longitude = longitude;
        this.latitude = latitude;
        killed = false;

        this.numWaves = numWaves;
        waveNum = 0;

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

        if (waveNum >= numWaves) {
            return;
        }

        timeSinceLastWave += delta;
        if (timeSinceLastWave > delayBetweenWaves()) {
            timeSinceLastWave = 0;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // do something important here, asynchronously to the rendering thread
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {

                            EnemyGroup enemyGroup = getNewEnemyGroup();

                            if (enemyGroup != null) {
                                enemyGroups.add(enemyGroup);
                            }
                        }
                    });
                }
            }).start();

            synchronized (enemyGroups) {
                for (int i = enemyGroups.size() - 1; i >= 0; i--) {
                    EnemyGroup enemyGroup = enemyGroups.get(i);
                    if (enemyGroup.isEmpty()) {
                        enemyGroup.kill();
                        enemyGroups.remove(enemyGroup);
                    }
                }
            }

            waveNum++;
        }
    }

    @Override
    public void kill() {
        killed = true;
    }

    @Override
    public boolean isKilled() {
        if (waveNum < numWaves) {
            //System.out.println("EnemyNest not killed because waveNum<numWaves, "+waveNum+"<"+numWaves);
            return false;
        }

        for (EnemyGroup enemyGroup: enemyGroups) {
            if (!enemyGroup.isKilled()) {
                //System.out.println("EnemyNest not killed because "+enemyGroup+" is not killed");
                return false;
            }
        }

        kill();
        return true;
    }

    public void render(Batch batch) {
        Sprite sprite = new Sprite(getFrameTextureRegion());

        sprite.setCenterX(parent.convertLongToX(getLongitude()));
        sprite.setCenterY(parent.convertLatToY(getLatitude()));
        sprite.setScale((float) parent.getSpriteScale(sprite, getScale()));
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
