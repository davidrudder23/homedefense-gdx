package org.noses.games.homedefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.Screen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.home.Home;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.tower.Tower;
import org.noses.games.homedefense.ui.MouseHandler;
import org.noses.games.homedefense.ui.PieMenu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class HomeDefenseGame extends ApplicationAdapter implements InputProcessor {
    // TODO: calculate this based on current lat/long, not just hardcoded to Denver

    //
    public static double ONE_PIXEL_IN_LATLON = 0;

    // This is how far you move in 1 second, going 1 mph, in terms of latitude and longitude, in Denver
    public static double LATLON_MOVED_IN_1s_1mph = 0.000004901f;

    SpriteBatch batch;

    @Getter
    Map map = null;

    @Getter
    List<EnemyGroup> enemyGroups;

    HashMap<String, Intersection> intersections;

    private Timer.Task keyPressTimer;

    @Getter
    Home home;

    @Getter
    @Setter
    private int money;

    private List<MouseHandler> mouseHandlers;
    private List<MouseHandler> mouseHandlersToBeAdded;

    private List<ClockTickHandler> clockTickHandlers;
    private List<ClockTickHandler> clockTickHandlersToBeAdded;

    @Getter
    private List<Tower> towers;

    @Getter
    int speedMultiplier;

    Timer.Task timer;

    @Getter
    PieMenu towerChoiceMenu;
    Screen currentScreen;

    @Override
    public void create() {

        enemyGroups = new ArrayList<>();

        mouseHandlers = new ArrayList<>();
        mouseHandlersToBeAdded = new ArrayList<>();

        clockTickHandlers = new ArrayList<>();
        clockTickHandlersToBeAdded = new ArrayList<>();

        towers = new ArrayList<>();

        speedMultiplier = 1;

        money = 0;

        batch = new SpriteBatch();

        //currentScreen = new MainScreen(this);
        currentScreen = new MapScreen(this);

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

        Gdx.input.setInputProcessor(this);

        towerChoiceMenu = new PieMenu(this);
        addClickHandler(towerChoiceMenu);

        timer = Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       clockTick(1 / 10.0f);
                                   }
                               }
                , 0f, 1 / (10.0f * speedMultiplier));

    }

    public void addClockTickHandler(ClockTickHandler clockTickHandler) {
        synchronized (clockTickHandlersToBeAdded) {
            clockTickHandlersToBeAdded.add(clockTickHandler);
        }
    }

    public void addClickHandler(MouseHandler mouseHandler) {
        synchronized (mouseHandlers) {
            mouseHandlersToBeAdded.add(mouseHandler);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            System.exit(0);

        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            speedMultiplier += 1;

            if ((speedMultiplier > 5) || (speedMultiplier < 1)) {
                speedMultiplier = 1;
            }

            timer.cancel();
            timer = Timer.schedule(new Timer.Task() {
                                       @Override
                                       public void run() {
                                           clockTick(1 / 10.0f);
                                       }
                                   }
                    , 0f, 1 / (10.0f * speedMultiplier));
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    public void addTower(Tower tower) {
        towers.add(tower);
        addClockTickHandler(tower);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        synchronized (mouseHandlers) {
            for (MouseHandler mouseHandler : mouseHandlersToBeAdded) {
                mouseHandlers.add(mouseHandler);
            }
            mouseHandlersToBeAdded.clear();
        }

        int x = Gdx.input.getX();
        int y = Gdx.input.getY();

        if (Gdx.input.isButtonPressed(0)) {

            for (MouseHandler mouseHandler : mouseHandlers) {
                mouseHandler.onClick(x, y);
            }
        } else if (Gdx.input.isButtonPressed(1)) {
            for (MouseHandler mouseHandler : mouseHandlers) {
                mouseHandler.onRightClick(x, y);
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        synchronized (mouseHandlers) {
            for (MouseHandler mouseHandler : mouseHandlersToBeAdded) {
                mouseHandlers.add(mouseHandler);
            }
            mouseHandlersToBeAdded.clear();
        }

        for (MouseHandler mouseHandler : mouseHandlers) {
            mouseHandler.onClickUp();
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (MouseHandler mouseHandler : mouseHandlers) {
            mouseHandler.onMouseDragged(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
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

            map = mapClient.getMap(account, denverLatitude+0.0075, denverLatitude-0.0075, denverLongitude+0.015, denverLongitude-0.015);
            //map = mapClient.getMap(account, denverLatitude+0.03, denverLatitude-0.013, denverLongitude+0.06, denverLongitude-0.06);
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

    public void subtractMoney(int money) {
        this.money -= money;
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

        currentScreen.render(batch);
    }

    public double convertXToLong(int x) {
        float longPerPixel = (map.getEast() - map.getWest()) / (float) Gdx.graphics.getWidth();
        return map.getWest() + (x * longPerPixel);
    }

    public double convertYToLat(int y) {
        float latPerPixel = (map.getNorth() - map.getSouth()) / Gdx.graphics.getHeight();
        return map.getSouth() + (y * latPerPixel);
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
