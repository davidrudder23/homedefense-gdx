package org.noses.games.homedefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.client.*;
import org.noses.games.homedefense.enemy.ArmoredGroundEnemy;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.GroundEnemy;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.home.Home;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.ui.ClickHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeDefenseGame extends ApplicationAdapter {
    // TODO: calculate this based on current lat/long, not just hardcoded to Denver

    //
    public static double ONE_PIXEL_IN_LATLON = 0;

    // This is how far you move in 1 second, going 1 mph, in terms of latitude and longitude, in Denver
    public static double LATLON_MOVED_IN_1s_1mph = 0.000004901f;

    SpriteBatch batch;

    @Getter
    Map map = null;

    List<EnemyGroup> enemyGroups;

    HashMap<String, Intersection> intersections;

    private Timer.Task keyPressTimer;

    @Getter
    Home home;

    @Getter
    @Setter
    private int money;

    private List<ClickHandler> clickHandlers;
    private List<ClickHandler> clickHandlersToBeAdded;

    private List<ClockTickHandler> clockTickHandlers;
    private List<ClockTickHandler> clockTickHandlersToBeAdded;

    int speedMultiplier;

    Timer.Task timer;

    @Override
    public void create() {

        enemyGroups = new ArrayList<>();

        clickHandlers = new ArrayList<>();
        clickHandlersToBeAdded = new ArrayList<>();

        clockTickHandlers = new ArrayList<>();
        clockTickHandlersToBeAdded = new ArrayList<>();

        speedMultiplier = 1;

        money = 0;

        batch = new SpriteBatch();

        initializeMap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        HomeDefenseGame.ONE_PIXEL_IN_LATLON = (map.getEast() - map.getWest()) / Gdx.graphics.getWidth();

        home = new Home(this,
                ((map.getNorth() - map.getSouth()) / 2) + map.getSouth(),
                ((map.getEast() - map.getWest()) / 2) + map.getWest());

        intersections = Intersection.buildIntersectionsFromMap(map);

        HashMap<String, Intersection> startingIntersections = new HashMap<>();

        for (String intersectionName : intersections.keySet()) {
            Node node = intersections.get(intersectionName).getNode();

            if ((node.getLat() < getMap().getSouth()) ||
                    (node.getLon() < getMap().getWest()) ||
                    (node.getLat() > getMap().getNorth()) ||
                    (node.getLon() > getMap().getWest())
            ) {
                startingIntersections.put(intersectionName, intersections.get(intersectionName));
            }
        }

        createEnemies(intersections);

        setupSound();

        keyPressLoop();

        mouseClickLoop();

        timer = Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               clockTick(1 / 10.0f);
                           }
                       }
                , 0f, 1 / (10.0f*speedMultiplier));

    }

    public void addClockTickHandler(ClockTickHandler clockTickHandler) {
        synchronized (clockTickHandlersToBeAdded) {
            clockTickHandlersToBeAdded.add(clockTickHandler);
            System.out.println("Clock tick handlers size=" + clockTickHandlers.size());
        }
    }

    public void addClickHandler(ClickHandler clickHandler) {
        clickHandlersToBeAdded.add(clickHandler);
    }

    private void keyPressLoop() {
        keyPressTimer = Timer.schedule(new Timer.Task() {

            @Override
            public void run() {

                if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                    System.exit(0);

                }

                if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                    speedMultiplier += 1;

                    if ((speedMultiplier>5) || (speedMultiplier<1)) {
                        speedMultiplier = 1;
                    }

                    timer.cancel();
                    timer = Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           clockTick(1 / 10.0f);
                                       }
                                   }
                            , 0f, 1 / (10.0f*speedMultiplier));
                }

            }
        }, 1, 0.1f);

    }

    private void mouseClickLoop() {
        keyPressTimer = Timer.schedule(new Timer.Task() {

            @Override
            public void run() {

                if (Gdx.input.isButtonPressed(0)) {
                    int x = Gdx.input.getX();
                    int y = Gdx.input.getY();

                    System.out.println("Mouse clicked");

                    for (ClickHandler clickHandler : clickHandlers) {
                        clickHandler.onClick(x, y);
                    }
                }
                if (Gdx.input.isButtonPressed(1)) {
                    int x = Gdx.input.getX();
                    int y = Gdx.input.getY();

                    System.out.println("Right Mouse clicked");

                    for (ClickHandler clickHandler : clickHandlers) {
                        clickHandler.onRightClick(x, y);
                    }
                }
            }
        }, 1, 0.1f);

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
                    denverLatitude,
                    denverLongitude);
            //austinLatitude,
            //austinLongitude);

            map = mapClient.getMap(account, width, height);
            //System.out.println(map);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            ;
        }

        for (Way way : map.getWays()) {
            for (Node node : way.getNodes()) {

                float length = way.firstNode().distanceFrom(way.lastNode());

                if (length == 0) {
                    node.setProgress(0);
                } else {
                    float delta = way.firstNode().distanceFrom(node);
                    node.setProgress(delta);
                }
            }
        }
    }

    public void createEnemies(HashMap<String, Intersection> startingIntersections) {

        EnemyGroup enemyGroup = EnemyGroup.builder()
                .intersections(startingIntersections)
                .delay(10)
                .numEnemies(10)
                .enemyBuilder(new GroundEnemy.GroundEnemyBuilder(this, intersections))
                .build();
        enemyGroups.add(enemyGroup);
        addClockTickHandler(enemyGroup);

        enemyGroup = EnemyGroup.builder()
                .intersections(startingIntersections)
                .delay(20)
                .numEnemies(10)
                .enemyBuilder(new ArmoredGroundEnemy.ArmoredGroundEnemyBuilder(this, intersections))
                .build();
        enemyGroups.add(enemyGroup);
        addClockTickHandler(enemyGroup);

        /*
        EnemyGroup enemyGroup2 = EnemyGroup.builder()
                .intersections(startingIntersections)
                .delay(10)
                .numEnemies(10)
                .enemyBuilder(new LeftToRightFlyingEnemyBuilder(this))
                .build();

        enemyGroups.add(enemyGroup2);
         */
    }

    public Intersection getIntersectionForNode(Node node) {
        return intersections.get(node.getLat() + "_" + node.getLon());
    }

    public void clockTick(float delta) {
        synchronized (clockTickHandlersToBeAdded) {
            for (ClockTickHandler clockTickHandler : clockTickHandlersToBeAdded) {
                clockTickHandlers.add(clockTickHandler);
            }
            clockTickHandlersToBeAdded.clear();
        }

        if (clockTickHandlers.size() > 0) {
            for (int i = clockTickHandlers.size() - 1; i >= 0; i--) {
                ClockTickHandler clockTickHandler = clockTickHandlers.get(i);
                if (clockTickHandler.isKilled()) {
                    clockTickHandlers.remove(i);
                }
            }
        }

        for (ClockTickHandler clockTickHandler : clockTickHandlers) {
            //System.out.println ("Clock ticking "+clockTickHandler+" iskilled="+clockTickHandler.isKilled());
            if (!clockTickHandler.isKilled()) {
                clockTickHandler.clockTick(delta);
            }
        }

    }

    public void hitHome(int damage) {
        System.out.println("Home hit for " + damage + " health=" + home.getHealth());
        home.hit(damage);
        if (home.isKilled()) {
            Gdx.app.exit();
        }
    }

    public void addMoney(int money) {
        this.money += money;
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
                    //System.out.println("Writing line starting at "+prevNode.getLat()+"x"+prevNode.getLon()+" - "+
                    //              convertLatToY(prevNode.getLat())+"x"+convertLongToX(prevNode.getLon()));
                    sr.line(convertLongToX(prevNode.getLon()), convertLatToY(prevNode.getLat()),
                            convertLongToX(node.getLon()), convertLatToY(node.getLat()));
                }
                prevNode = node;
            }
            sr.end();
        }

        batch.begin();

        // render the enemies

        for (EnemyGroup enemyGroup : enemyGroups) {
            List<Enemy> enemies = enemyGroup.getEnemies();
            for (Enemy enemy : enemies) {
                Point location = enemy.getLocation();

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                //batch.draw(enemy.getFrameTextureRegion(), x, y);

                Sprite sprite = new Sprite(enemy.getFrameTextureRegion());

                sprite.setCenterY(convertLatToY(latitude));
                sprite.setCenterX(convertLongToX(longitude));
                sprite.draw(batch);

            }
        }

        home.render(batch);

        // render the score and other text
        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.draw(batch, "Health: " + home.getHealth(), 10, Gdx.graphics.getHeight() - 30);

        font.draw(batch, "Money: " + money, 10, Gdx.graphics.getHeight() - (30 + font.getCapHeight()));

        batch.end();

    }

    public int convertLongToX(double longitude) {
        float longPerPixel = (map.getEast() - map.getWest()) / (float) Gdx.graphics.getWidth();
        return (int) ((longitude - map.getWest()) / longPerPixel);
    }

    public int convertLatToY(double latitude) {
        float latPerPixel = (map.getNorth() - map.getSouth()) / Gdx.graphics.getHeight();

        return (int) ((latitude - map.getSouth()) / latPerPixel);
    }

    public String printPointInXY(Point point) {
        return (convertLongToX(point.getLongitude()) + "x" + convertLatToY(point.getLatitude()));
    }

    @Override
    public void resize(int width, int height) {
        /*int originalWidth = Gdx.graphics.getWidth();
        int originalHeight = Gdx.graphics.getHeight();

        double xRatio = width / originalWidth;
        double yRatio = height / originalHeight;

        for (Way way : map.getWays()) {
            for (Node node : way.getNodes()) {
                node.setX((int) ((double) node.getLatitude() * xRatio));
                node.setY((int) ((double) node.getLongitude() * yRatio));
            }
        }*/
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
