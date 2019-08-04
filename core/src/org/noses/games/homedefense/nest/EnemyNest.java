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
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.EnemyConfig;
import org.noses.games.homedefense.level.NestConfig;

import java.util.ArrayList;
import java.util.List;

public abstract class EnemyNest extends Animation implements PhysicalObject, ClockTickHandler {

    @Getter
    NestConfig nestConfig;
    Point location;

    boolean killed;

    Node bestNode;

    @Getter
    List<EnemyGroup> enemyGroups;

    double timeSinceLastWave;

    int waveNum;

    public EnemyNest(MapScreen parent, NestConfig nestConfig, Point location) {
        super(parent, "nest/" + nestConfig.getClassName().toLowerCase()+ ".png", 199, 199, 0.08, true);
        this.nestConfig = nestConfig;
        this.location = location;
        killed = false;

        waveNum = 0;

        timeSinceLastWave = delayBetweenWaves();

        enemyGroups = new ArrayList<>();
    }

    public double delayBetweenWaves() {
        return nestConfig.getDelayBetweenWaves();
    }

    @Override
    public double getLatitude() {
        return location.getLatitude();
    }

    @Override
    public double getLongitude() {
        return location.getLongitude();
    }

    public abstract EnemyGroup getNewEnemyGroup(NestConfig nestConfig);

    @Override
    public void clockTick(double delta) {

        if (waveNum >= nestConfig.getNumWaves()) {
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

                            EnemyGroup enemyGroup = getNewEnemyGroup(nestConfig);

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
        if (waveNum < nestConfig.getNumWaves()) {
            return false;
        }

        for (EnemyGroup enemyGroup: enemyGroups) {
            if (!enemyGroup.isKilled()) {
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

        if (bestNode == null) {
            return null;
        }
        return bestNode;
    }
}
