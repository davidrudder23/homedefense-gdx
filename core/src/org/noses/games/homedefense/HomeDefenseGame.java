package org.noses.games.homedefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import org.noses.games.homedefense.client.*;
import org.noses.games.homedefense.enemy.GroundEnemy;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.awt.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeDefenseGame extends ApplicationAdapter {
    SpriteBatch batch;
    Map map = null;

    List<GroundEnemy> enemies;

    HashMap<String, Intersection> intersections;

    @Override
    public void create() {
        enemies = new ArrayList<>();

	    /*GroundEnemyTest groundEnemyTest = new GroundEnemyTest();
	    groundEnemyTest.testMovingMultiplePointsXDoubleY();
	    groundEnemyTest.testMovingMultiplePointsXEqualY();
	    groundEnemyTest.testMovingTwoPointsXDoubleY();
*/

        batch = new SpriteBatch();

        initializeMap(640, 480);

        intersections = Intersection.buildIntersectionsFromMap(map);

        Djikstra djikstra = new Djikstra(intersections);
        Intersection djikstraIntersection = djikstra.getIntersectionForNode(intersections,
                enemies.get(0).getWay().firstNode());

        PathStep pathStep = djikstra.getBestPath(
                djikstraIntersection,
                320, 240);
        if (pathStep == null) {
            System.out.println ("PathStep was null");
        } else {
            System.out.println ("Starting from "+enemies.get(0).getWay().firstPoint());
            System.out.println ("Starting intersection "+djikstraIntersection.getNode());
            do {
                System.out.println("Found step " + pathStep);
                pathStep.getConnectingWay().setColor(Color.RED);
                pathStep = pathStep.getPreviousPath();
            } while (pathStep.getPreviousPath() != null);
            System.out.println ("Final step "+pathStep);
        }

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

        SecureRandom random = new SecureRandom();
        random.setSeed(System.currentTimeMillis());

        for (int i = 0; i < 5; i++) {
            Way way = map.getWays().get(random.nextInt(map.getWays().size()));
            GroundEnemy mage = new GroundEnemy(this, way, "mage.png", 64, 64);
            enemies.add(mage);

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
                    int delta = way.firstNode().distanceFrom(node);
                    node.setProgress(delta / length);
                }
            }
        }
    }

    public Intersection getIntersectionForNode(Node node) {
        return intersections.get(node.getLat() + "_" + node.getLon());
    }

    public void clockTick(float delta) {
        for (GroundEnemy groundEnemy : enemies) {
            groundEnemy.clockTick(delta);
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
            for (GroundEnemy enemy : enemies) {
                //if (enemy.getWay().equals(way)) {
                    sr.setColor(way.getColor());
                //}
            }

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
        for (GroundEnemy enemy : enemies) {
            Point location = enemy.getLocation();
            int x = location.x - 32;
            int y = Gdx.graphics.getHeight() - (location.y + 32);
            batch.draw(enemy.getAnimation()[0][enemy.getFrame()], x, y);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        int originalWidth = Gdx.graphics.getWidth();
        int originalHeight = Gdx.graphics.getHeight();

        double xRatio = width/originalWidth;
        double yRatio = height/originalHeight;

        System.out.println("xRatio="+xRatio);
        System.out.println("yRatio="+yRatio);
        for (Way way: map.getWays()) {
            for (Node node: way.getNodes()) {
                node.setX((int)((double)node.getX()*xRatio));
                node.setY((int)((double)node.getY()*yRatio));
            }
        }
    }


    @Override
    public void dispose() {
        batch.dispose();
    }
}
