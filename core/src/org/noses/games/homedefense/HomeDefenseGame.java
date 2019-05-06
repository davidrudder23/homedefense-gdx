package org.noses.games.homedefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import lombok.experimental.var;
import org.noses.games.homedefense.client.*;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.EnemyGroupBuilder;
import org.noses.games.homedefense.enemy.GroundEnemy;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;
import org.noses.games.homedefense.player.Shot;

import java.awt.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeDefenseGame extends ApplicationAdapter {
    SpriteBatch batch;

    @Getter
    Map map = null;

    List<EnemyGroup> enemyGroups;
    List<Shot> shots;

    HashMap<String, Intersection> intersections;

    @Override
    public void create() {
        enemyGroups = new ArrayList<>();
        shots = new ArrayList<>();

	    /*GroundEnemyTest groundEnemyTest = new GroundEnemyTest();
	    groundEnemyTest.testMovingMultiplePointsXDoubleY();
	    groundEnemyTest.testMovingMultiplePointsXEqualY();
	    groundEnemyTest.testMovingTwoPointsXDoubleY();
*/

        batch = new SpriteBatch();

        initializeMap(640, 480);

        intersections = Intersection.buildIntersectionsFromMap(map);

        createEnemies(640, 480);

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

/*        for (Way way : map.getWays()) {
            GroundEnemy mage = new GroundEnemy(this, way, "mage.png", 64, 64);
            enemies.add(mage);
        }*/

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

        /*System.out.println ("Ways");
        for (Way way: map.getWays()) {
            System.out.println("  "+way.getName());
        }*/

        /*for (int i = 0; i < 1; i++) {
            createEnemy(width, height, random);
        }*/
        EnemyGroup enemyGroup = EnemyGroup.builder()
                .intersections(intersections)
                .game(this)
                .delay(10)
                .height(height)
                .width(width)
                .numEnemies(10)
                .build();
        enemyGroups.add(enemyGroup);
    }

    /*
    private void createEnemy(int width, int height, SecureRandom random) {
        if (random == null) {
            random = new SecureRandom();
            random.setSeed(System.currentTimeMillis());
        }

        Way way = map.getWays().get(random.nextInt(map.getWays().size()));
        //Way way = map.getWays().get(i*3);
        System.out.println ("In create enemy, starting on way "+way.getName());
        GroundEnemy mage = new GroundEnemy(this, way, "mage.png", 64, 64);

        Djikstra djikstra = new Djikstra(intersections);
        Intersection intersection = djikstra.getIntersectionForNode(intersections, way.firstNode());
        System.out.println("Intersection=("+intersection.getLatitude()+"x"+intersection.getLongitude());
        PathStep pathStep = djikstra.getBestPath(intersection, width/2, height/2);
        System.out.println ("Enemy's path - "+pathStep.getPath());

        mage.setPath(pathStep);

        enemies.add(mage);
    }*/

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
            List<Enemy> enemies = enemyGroup.getEnemies();

            if (enemyGroup.isEmpty()) {
                System.out.println("Empty group!");
                enemyGroup = EnemyGroup.builder()
                        .intersections(intersections)
                        .game(this)
                        .delay(10)
                        .width(enemyGroup.getWidth())
                        .height(enemyGroup.getHeight())
                        .build();
            }
        }
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

        batch.begin();

        for (EnemyGroup enemyGroup : enemyGroups) {
            List<Enemy> enemies = enemyGroup.getEnemies();
            for (Enemy enemy : enemies) {
                Point location = enemy.getLocation();
                int x = location.x - 32;
                int y = Gdx.graphics.getHeight() - (location.y + 32);
                batch.draw(enemy.getAnimation()[0][enemy.getFrame()], x, y);
            }
        }

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
