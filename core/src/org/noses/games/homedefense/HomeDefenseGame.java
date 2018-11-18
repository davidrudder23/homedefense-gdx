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
import org.noses.games.homedefense.pathfinding.Intersection;

import java.awt.*;
import java.io.IOException;
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

        try {
            MapClient mapClient = new MapClient();
            Account account = mapClient.register("drig1",
                    "drig1@noses.org",
                    "test1",
                    39.7392f,
                    -104.9874f);

            map = mapClient.getMap(account, 640, 480);
            //System.out.println(map);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            ;
        }

        for (int i = 0; i < 5; i++) {

            Way way = map.getWays().get((int) (Math.random() * map.getWays().size()));
            GroundEnemy mage = new GroundEnemy(this, way, "mage.png", 64, 64);
            enemies.add(mage);
        }
/*        for (Way way : map.getWays()) {
            GroundEnemy mage = new GroundEnemy(this, way, "mage.png", 64, 64);
            enemies.add(mage);
        }*/

        for (Way way: map.getWays()) {
            Point firstPoint = way.firstPoint();
            Point lastPoint = way.lastPoint();
            for (Node node: way.getNodes()) {
                double lengthX = lastPoint.getX() - firstPoint.getX();
                double lengthY = lastPoint.getY() - firstPoint.getY();
                double totalLength = Math.sqrt((lengthX*lengthX)+(lengthY*lengthY));

                double deltaX = node.getX() - firstPoint.getX();
                double deltaY = node.getY() - firstPoint.getY();
                double totalDelta = Math.sqrt((deltaX*deltaX)+(deltaY*deltaY));

                node.setProgress(totalDelta/totalLength);
            }
        }

        intersections = Intersection.buildIntersectionsFromMap(map);

        for (Intersection intersection: intersections.values()) {
            System.out.println ("------------------");
            for (Way way: intersection.getWayList()) {
                System.out.println(way.getName());
            }
        }

        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               clockTick(1 / 10.0f);
                           }
                       }
                , 0f, 1 / 10.0f);
    }

    public Intersection getIntersectionForNode(Node node) {
        return intersections.get(node.getLat()+"_"+node.getLon());
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
                if (enemy.getWay().equals(way)) {
                    sr.setColor(way.getColor());
                }
            }

            sr.begin(ShapeRenderer.ShapeType.Line);
            Node prevNode = null;
            for (Node node : way.getNodes()) {
                if (prevNode != null) {
                    sr.line(prevNode.getX(), 480 - prevNode.getY(),
                            node.getX(), 480 - node.getY());
                }
                prevNode = node;
            }
            sr.end();
        }

        batch.begin();
        for (GroundEnemy enemy : enemies) {
            Point location = enemy.getLocation();
            int x = location.x - 32;
            int y = 480 - (location.y + 32);
            batch.draw(enemy.getAnimation()[0][enemy.getFrame()], x, y);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
