package org.noses.games.homedefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import org.noses.games.homedefense.client.*;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.home.Home;
import org.noses.games.homedefense.player.Shot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeDefenseGame extends ApplicationAdapter {
    SpriteBatch batch;

    @Getter
    Map map = null;

    List<EnemyGroup> enemyGroups;

    HashMap<String, Intersection> intersections;

    Home home;

    @Override
    public void create() {
        enemyGroups = new ArrayList<>();

        home = new Home(this, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

        batch = new SpriteBatch();

        initializeMap(640, 480);

        intersections = Intersection.buildIntersectionsFromMap(map);

        createEnemies(640, 480);

        setupSound();

        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               clockTick(1 / 10.0f);
                           }
                       }
                , 0f, 1 / 10.0f);

    }

    public void initializeMap(int width, int height) {
        try {
            MapClient mapClient = new MapClient();
            Account account = mapClient.register("drig1",
                    "drig1@noses.org",
                    "test1",
                    39.7392f,
                    -104.9874f);

            map = mapClient.getMap(account, width, height);
            //System.out.println(map);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            ;
        }

        for (Way way : map.getWays()) {
            for (Node node : way.getNodes()) {

                int length = way.firstNode().distanceFrom(way.lastNode());

                if (length == 0) {
                    node.setProgress(0);
                } else {
                    float delta = way.firstNode().distanceFrom(node);
                    node.setProgress(delta / length);
                }
            }
        }
    }

    public void createEnemies(int width, int height) {

        EnemyGroup enemyGroup = EnemyGroup.builder()
                .intersections(intersections)
                .game(this)
                .delay(10)
                .height(height)
                .width(width)
                .numEnemies(10)
                .build();
        enemyGroups.add(enemyGroup);
        EnemyGroup enemyGroup2 = EnemyGroup.builder()
                .intersections(intersections)
                .game(this)
                .delay(20)
                .height(height)
                .width(width)
                .numEnemies(5)
                .build();
        enemyGroups.add(enemyGroup2);
    }

    public Intersection getIntersectionForNode(Node node) {
        return intersections.get(node.getLat() + "_" + node.getLon());
    }

    public void clockTick(float delta) {
        for (EnemyGroup enemyGroup : enemyGroups) {
            enemyGroup.clockTick(delta);
        }

    }

    public void killEnemy(Enemy enemy) {
        enemy.kill();
        for (EnemyGroup enemyGroup : enemyGroups) {

            if (enemyGroup.isEmpty()) {
                System.out.println("Empty group!");
            }
        }
    }

    public List<Enemy> getEnemies() {
        List<Enemy> enemies = new ArrayList<>();

        for (EnemyGroup enemyGroup: enemyGroups) {
            enemies.addAll(enemyGroup.getEnemies());
        }

        return enemies;
    }

    public void setupSound() {
        Sound backgroundLoop= Gdx.audio.newSound(Gdx.files.internal("background.mp3"));
        backgroundLoop.loop();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.25f, 0.95f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        ShapeRenderer sr = new ShapeRenderer();
        sr.setColor(Color.WHITE);
        //sr.setProjectionMatrix(camera.combined);

        for (Way way : map.getWays()) {
            Gdx.gl.glLineWidth(way.getMaxSpeed() - 24);

            sr.setColor(Color.WHITE);
            sr.setColor(way.getColor());

            sr.begin(ShapeRenderer.ShapeType.Line);
            Node prevNode = null;
            for (Node node : way.getNodes()) {
                if (prevNode != null) {
                    sr.line(prevNode.getX(), Gdx.graphics.getHeight() - prevNode.getY(),
                            node.getX(), Gdx.graphics.getHeight() - node.getY());
                }
                prevNode = node;
            }
            sr.end();
        }

        // render the enemies

        batch.begin();

        for (EnemyGroup enemyGroup : enemyGroups) {
            List<Enemy> enemies = enemyGroup.getEnemies();
            for (Enemy enemy : enemies) {
                Point location = enemy.getLocation();
                int x = location.getX() - 32;
                int y = Gdx.graphics.getHeight() - (location.getY() + 32);
                batch.draw(enemy.getFrameTextureRegion(), x, y);
            }
        }

        home.render(batch);

        batch.end();

    }

    @Override
    public void resize(int width, int height) {
        int originalWidth = Gdx.graphics.getWidth();
        int originalHeight = Gdx.graphics.getHeight();

        double xRatio = width / originalWidth;
        double yRatio = height / originalHeight;

        System.out.println("xRatio=" + xRatio);
        System.out.println("yRatio=" + yRatio);
        for (Way way : map.getWays()) {
            for (Node node : way.getNodes()) {
                node.setX((int) ((double) node.getX() * xRatio));
                node.setY((int) ((double) node.getY() * yRatio));
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
