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

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeDefenseGame extends ApplicationAdapter {
    SpriteBatch batch;
    MapDTO mapDTO = null;

    List<GroundEnemy> enemies;

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
            AccountDTO accountDTO = mapClient.register("drig1",
                    "drig1@noses.org",
                    "test1",
                    39.7392f,
                    -104.9874f);

            mapDTO = mapClient.getMap(accountDTO, 640, 480);
            //System.out.println(mapDTO);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            ;
        }

        for (int i = 0; i < 5; i++) {

            WayDTO wayDTO = mapDTO.getWays().get((int)(Math.random()*mapDTO.getWays().size()));
            GroundEnemy mage = new GroundEnemy(this, wayDTO, "mage.png", 64, 64);
            enemies.add(mage);
        }
/*        for (WayDTO wayDTO : mapDTO.getWays()) {
            GroundEnemy mage = new GroundEnemy(this, wayDTO, "mage.png", 64, 64);
            enemies.add(mage);
        }*/

        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               clockTick(1/10.0f);
                           }
                       }
                ,0f,1/10.0f);
    }

    public void clockTick(float delta) {
        for (GroundEnemy groundEnemy: enemies) {
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

        for (WayDTO wayDTO : mapDTO.getWays()) {
            Gdx.gl.glLineWidth(wayDTO.getMaxSpeed() - 24);

            sr.setColor(Color.WHITE);
            for (GroundEnemy enemy: enemies) {
                if (enemy.getWayDTO().equals(wayDTO)) {
                    sr.setColor(wayDTO.getColor());
                }
            }

            sr.begin(ShapeRenderer.ShapeType.Line);
            NodeDTO prevNode = null;
            for (NodeDTO nodeDTO : wayDTO.getNodes()) {
                if (prevNode != null) {
                    sr.line(prevNode.getX(), 480 - prevNode.getY(),
                            nodeDTO.getX(), 480 - nodeDTO.getY());
                }
                prevNode = nodeDTO;
            }
            sr.end();
        }

        batch.begin();
        for (GroundEnemy enemy : enemies) {
            Point location = enemy.getLocation();
            int x = location.x - 32;
            int y = 480 - (location.y+32);
            batch.draw(enemy.getAnimation()[0][enemy.getFrame()], x, y);
        }

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
