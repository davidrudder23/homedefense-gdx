package org.noses.games.homedefense.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.*;
import org.noses.games.homedefense.enemy.*;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.hero.Hero;
import org.noses.games.homedefense.home.Home;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.tower.Tower;
import org.noses.games.homedefense.ui.MouseHandler;
import org.noses.games.homedefense.ui.PieMenu;
import org.noses.games.homedefense.ui.SpeedButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapScreen extends Screen implements InputProcessor {
    HomeDefenseGame parent;

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

    BitmapFont font;

    @Getter
    private List<Tower> towers;

    List<EnemyNest> enemyNests;

    @Getter
    int speedMultiplier;

    SpeedButton speedButton;

    Timer.Task timer;

    @Getter
    PieMenu towerChoiceMenu;

    @Getter
    Hero hero;

    public MapScreen(HomeDefenseGame parent, Point location) {
        this.parent = parent;

        enemyGroups = new ArrayList<>();

        mouseHandlers = new ArrayList<>();
        mouseHandlersToBeAdded = new ArrayList<>();

        clockTickHandlers = new ArrayList<>();
        clockTickHandlersToBeAdded = new ArrayList<>();

        enemyNests = new ArrayList<>();

        towers = new ArrayList<>();

        speedMultiplier = 1;

        money = 0;

        font = new BitmapFont();
        font.setColor(Color.WHITE);

        initializeMap(location);

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

        speedButton = new SpeedButton(this, getScreenWidth() - 40, getScreenHeight() - 40);
        addClickHandler(speedButton);

        createNests();

        setupSound();

        setupHero();

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

    public void setupHero() {

        if (parent.hasLiveGeolocation()) {
            hero = new Hero(this);
            addClockTickHandler(hero);
            parent.addGeolocationListener(hero);
        }
    }


    public int getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getScreenHeight() {
        return Gdx.graphics.getHeight();
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
            parent.die();

        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            speedUp();
        }

        return false;
    }

    public void speedUp() {
        speedMultiplier += 1;

        if ((speedMultiplier > 3) || (speedMultiplier < 1)) {
            speedMultiplier = 1;
        }

        System.out.println("Speed multiplier=" + speedMultiplier);

        /*timer.cancel();
        timer = Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       clockTick(1 / 10.0f);
                                   }
                               }
                , 0f, 1 / (10.0f * speedMultiplier));

         */

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
                if (!mouseHandler.onClick(x, y)) {
                    break;
                }
            }
        } else if (Gdx.input.isButtonPressed(1)) {
            for (int i = mouseHandlers.size()-1; i>=0; i++) {
                MouseHandler mouseHandler = mouseHandlers.get(i);
                if (!mouseHandler.onRightClick(x, y)) {
                    break;
                }
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

    public void initializeMap(Point location) {
        try {
            float denverLatitude = 39.7392f;
            float denverLongitude = -104.9874f;

            float austinLatitude = 30.2746f;
            float austinLongitude = -97.7404f;

            MapClient mapClient = new MapClient(parent.getConfiguration().getBaseURL());

            Account account = mapClient.register("drig1",
                    "drig1@noses.org",
                    "test1",
                    denverLatitude,
                    denverLongitude);
            //austinLatitude,
            //austinLongitude);

            map = mapClient.getMap(account, location.getLatitude() + 0.0075,
                    location.getLatitude() - 0.0075,
                    location.getLongitude() + 0.015,
                    location.getLongitude() - 0.015);
            //map = mapClient.getMap(account, denverLatitude + 0.0075, denverLatitude - 0.0075, denverLongitude + 0.015, denverLongitude - 0.015);
            //map = mapClient.getMap(account, denverLatitude+0.03, denverLatitude-0.013, denverLongitude+0.06, denverLongitude-0.06);
            //System.out.println(map);
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
            ;
        }

        for (Way way : map.getWays()) {
            for (Node node : way.getNodes()) {

                double length = way.firstNode().distanceFrom(way.lastNode());

                if (length == 0) {
                    node.setProgress(0);
                } else {
                    double delta = way.firstNode().distanceFrom(node);
                    node.setProgress(delta);
                }
            }
        }
    }

    public void createNests() {
        double delayBeforeStart = 0;
        Djikstra djikstra = new Djikstra(intersections);

        for (Nest nest : map.getNests()) {

            // If it's too close, don't add the nest
            Point nestPoint = new Point(nest.getLat(), nest.getLon());
            Point homePoint = new Point(home.getLatitude(), home.getLongitude());
            if (nestPoint.getDistanceFrom(homePoint) < 0.005) {
                continue;
            }

            EnemyNest enemyNest = null;
            if (nest.getType().equalsIgnoreCase("standard")) {
                enemyNest = new WeakEnemyNest(this, delayBeforeStart, nest.getLon(), nest.getLat());
                delayBeforeStart += 3;
            } else if (nest.getType().equalsIgnoreCase("armored")) {
                enemyNest = new ArmoredEnemyNest(this, delayBeforeStart, nest.getLon(), nest.getLat());
                delayBeforeStart += 3;
            }

            if (enemyNest == null) {
                continue;
            }

            if (djikstra.getBestPath(enemyNest.getNode(), homePoint.getLongitude(), homePoint.getLongitude()) == null) {
                continue;
            }

            addClockTickHandler(enemyNest);
            enemyNests.add(enemyNest);
        }

        /*EnemyGroup enemyGroup = EnemyGroup.builder()
                .intersections(startingIntersections)
                .delay(20)
                .numEnemies(10)
                .enemyBuilder(new ArmoredGroundEnemy.ArmoredGroundEnemyBuilder(this, intersections))
                .build();
        enemyGroups.add(enemyGroup);
        addClockTickHandler(enemyGroup);*/
    }

    public Intersection getIntersectionForNode(Node node) {
        return intersections.get(node.getLat() + "_" + node.getLon());
    }

    public void clockTick(float delta) {
        System.out.println(delta+" vs "+(speedMultiplier/10));
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
                clockTickHandler.clockTick((float)(1+(speedMultiplier/2))/10);
            }
        }

    }

    public void hitHome(int damage) {
        System.out.println("Home hit for " + damage + " health=" + home.getHealth());
        home.hit(damage);
        if (home.isKilled()) {
            die();
        }
    }

    private void die() {
        timer.cancel();
        parent.die();
    }

    public void addMoney(int money) {
        this.money += money;
    }

    public void subtractMoney(int money) {
        this.money -= money;
    }

    public List<Enemy> getEnemies() {
        List<Enemy> enemies = new ArrayList<>();

        for (EnemyNest enemyNest : enemyNests) {
            for (EnemyGroup enemyGroup : enemyNest.getEnemyGroups()) {
                enemies.addAll(enemyGroup.getEnemies());
            }
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

    public void render(Batch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setColor(Color.WHITE);

        for (Way way : getMap().getWays()) {
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

        // render the nests
        for (EnemyNest enemyNest : enemyNests) {
            Sprite sprite = new Sprite(enemyNest.getFrameTextureRegion());

            sprite.setCenterX(convertLongToX(enemyNest.getLongitude()));
            sprite.setCenterY(convertLatToY(enemyNest.getLatitude()));
            sprite.setScale(64 / sprite.getWidth());
            sprite.draw(batch);

        }

        // render the enemies

        for (EnemyNest enemyNest : enemyNests) {
            for (EnemyGroup enemyGroup : enemyNest.getEnemyGroups()) {
                List<Enemy> enemies = enemyGroup.getEnemies();
                for (Enemy enemy : enemies) {
                    Point location = enemy.getLocation();

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    //batch.draw(enemy.getFrameTextureRegion(), x, y);

                    Sprite sprite = new Sprite(enemy.getFrameTextureRegion());

                    sprite.setScale((float)((parent.getScreenWidth()*enemy.getScale())/sprite.getWidth()));

                    sprite.setCenterY(convertLatToY(latitude));
                    sprite.setCenterX(convertLongToX(longitude));
                    sprite.draw(batch);

                }
            }
        }

        getHome().render(batch);

        // render the score and other text
        font.draw(batch, "Health: " + getHome().getHealth(), 10, Gdx.graphics.getHeight() - 30);

        font.draw(batch, "Money: " + getMoney(), 10, Gdx.graphics.getHeight() - (35 + font.getCapHeight()));

        font.draw(batch, "Speed: " + getSpeedMultiplier() + "x", 10, Gdx.graphics.getHeight() - (40 + (font.getCapHeight() * 2)));

        speedButton.render(batch);

        // render the towers
        for (Tower tower : getTowers()) {
            tower.render(batch);
        }

        // render the pie menu

        if (!towerChoiceMenu.isHidden()) {
            towerChoiceMenu.renderMenu(batch);
        }

        if (parent.hasLiveGeolocation()) {
            Sprite heroSprite = new Sprite(hero.getFrameTextureRegion());
            heroSprite.setScale((float) ((parent.getScreenWidth() * hero.getScale()) / heroSprite.getWidth()));
            heroSprite.setCenterX(convertLongToX(hero.getLongitude()));
            heroSprite.setCenterY(convertLatToY(hero.getLatitude()));
            heroSprite.draw(batch);
        }

        batch.end();
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

    public double getPpcX() {
        return parent.getPpcX();
    }

    public double getPpcY() {
        return parent.getPpcY();
    }


}
