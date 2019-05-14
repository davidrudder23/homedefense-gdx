package org.noses.games.homedefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import org.noses.games.homedefense.client.*;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;
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

        home = new Home(this, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        batch = new SpriteBatch();

        initializeMap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        intersections = Intersection.buildIntersectionsFromMap(map);

        HashMap<String, Intersection> startingIntersections = new HashMap<>();

        for (String intersectionName : intersections.keySet()) {
            Node node = intersections.get(intersectionName).getNode();

            if ((node.getX() < 0) ||
                    (node.getY() < 0) ||
                    (node.getX() > Gdx.graphics.getWidth()) ||
                    (node.getY() > Gdx.graphics.getHeight())
            ) {
                startingIntersections.put(intersectionName, intersections.get(intersectionName));
            }
        }

        createEnemies(intersections, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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

            float denverLatitude = 39.7392f;
            float denverLongitude = -104.9874f;

            float austinLatitude = 30.2746f;
            float austinLongitude = -97.7404f;

            MapClient mapClient = new MapClient();
            Account account = mapClient.register("drig1",
                    "drig1@noses.org",
                    "test1",
                    //denverLatitude,
                    //denverLongitude);
                    austinLatitude,
                    austinLongitude);

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

    public void createEnemies(HashMap<String, Intersection> startingIntersections, int width, int height) {

        EnemyGroup enemyGroup = EnemyGroup.builder()
                .intersections(startingIntersections)
                .game(this)
                .delay(10)
                .height(height)
                .width(width)
                .numEnemies(10)
                .build();
        enemyGroups.add(enemyGroup);
        EnemyGroup enemyGroup2 = EnemyGroup.builder()
                .intersections(startingIntersections)
                .game(this)
                .delay(20)
                .height(height)
                .width(width)
                .numEnemies(5)
                .build();
        //enemyGroups.add(enemyGroup2);
    }

    public Intersection getIntersectionForNode(Node node) {
        return intersections.get(node.getLat() + "_" + node.getLon());
    }

    public void clockTick(float delta) {
        for (EnemyGroup enemyGroup : enemyGroups) {
            enemyGroup.clockTick(delta);
        }

    }

    public void hitHome(int damage) {
        System.out.println("Home hit for "+damage+" health="+home.getHealth());
        home.hit(damage);
        if (home.isDead()) {
            Gdx.app.exit();
        }
    }

    public List<Enemy> getEnemies() {
        List<Enemy> enemies = new ArrayList<>();

        for (EnemyGroup enemyGroup : enemyGroups) {
            enemies.addAll(enemyGroup.getEnemies());
        }

        return enemies;
    }

    public void setupSound() {
        Sound backgroundLoop = loadSound("background.mp3");
        backgroundLoop.loop(0.5f);
    }

    public Sound loadSound(String fileName) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(fileName));

        return sound;
    }

    public int getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getScreenHeight() {
        return Gdx.graphics.getHeight();
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

                int x = location.getX();
                int y = location.getY();

                //batch.draw(enemy.getFrameTextureRegion(), x, y);

                Sprite sprite = new Sprite(enemy.getFrameTextureRegion());
                sprite.setCenterX(x);
                sprite.setCenterY(y);
                sprite.draw(batch);

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
