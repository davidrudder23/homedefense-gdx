package org.noses.games.homedefense.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.*;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.hero.Hero;
import org.noses.games.homedefense.home.Home;
import org.noses.games.homedefense.level.LevelEngine;
import org.noses.games.homedefense.nest.EnemyNest;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.tower.Tower;
import org.noses.games.homedefense.ui.LeftSideTowerMenu;
import org.noses.games.homedefense.ui.LeftSideUpgradeMenu;
import org.noses.games.homedefense.ui.MouseHandler;
import org.noses.games.homedefense.ui.SpeedButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Getter
    List<org.noses.games.homedefense.nest.EnemyNest> enemyNests;

    @Getter
    int speedMultiplier;

    SpeedButton speedButton;

    Timer.Task timer;

    @Getter
    LeftSideTowerMenu towerChoiceMenu;

    @Getter
    LeftSideUpgradeMenu upgradeMenu;

    LevelEngine levelEngine;

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

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/score.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 22;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        font.setColor(Color.BLACK);

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

        startNewLevel();

        setupHero();

        setupHero();

        Gdx.input.setInputProcessor(this);

        towerChoiceMenu = new LeftSideTowerMenu(this);
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

    public boolean isDebug() {
        return parent.isDebug();
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

    public void removeClickHandler(MouseHandler mouseHandler) {
        synchronized (mouseHandlers) {
            mouseHandlersToBeAdded.remove(mouseHandler);
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
        subtractMoney(tower.getCost());
        addClickHandler(tower);
        addClockTickHandler(tower);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        synchronized (mouseHandlers) {
            for (MouseHandler mouseHandler : mouseHandlersToBeAdded) {
                mouseHandlers.add(mouseHandler);
            }
            mouseHandlersToBeAdded.clear();

            mouseHandlers.sort(new Comparator<MouseHandler>() {
                @Override
                public int compare(MouseHandler a, MouseHandler b) {
                    return b.getZ() - a.getZ();
                }
            });
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
            for (int i = mouseHandlers.size() - 1; i >= 0; i++) {
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
            if (!mouseHandler.onClickUp(screenX, screenY)) {
                return false;
            }
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
        for (MouseHandler mouseHandler : mouseHandlers) {
            mouseHandler.mouseMoved(screenX, screenY);
        }
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
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
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

    public boolean isGoodLocationForNest(Node node) {
        Point homePoint = getHome().getLocation();
        Point nodePoint = new Point(node.getLat(), node.getLon());
        if (nodePoint.getDistanceFrom(homePoint) < 0.005) {
            return false;
        }

        Djikstra djikstra = new Djikstra(intersections);
        if (djikstra.getBestPath(node, getNodeForLocation(homePoint)) == null) {
            return false;
        }

        return isInsideMap(nodePoint);
    }

    public Node findGoodPlaceForNest() {
        // If it's too close, don't add the nest
        Point homePoint = new Point(home.getLatitude(), home.getLongitude());

        Intersection intersection = null;

        int count = 0;

        while (count < 1000) {
            count++;
            intersection = intersections.get((int) (Math.random() * intersections.size()));

            if (!isGoodLocationForNest(intersection.getNode())) {
                continue;
            }

            return intersection.getNode();
        }

        return null;
    }

    public void startNewLevel() {
        if (levelEngine == null) {
            levelEngine = new LevelEngine(this, 10);
            addClockTickHandler(levelEngine);
        }
        if (!levelEngine.reset()) {
            System.out.println("You won!!!");
            win();
        }
    }

    public void dropNest(org.noses.games.homedefense.nest.EnemyNest enemyNest) {
        addClockTickHandler(enemyNest);
        enemyNests.add(enemyNest);
    }

    public HashMap<String, Intersection> getIntersectionsAsHashmap() {
        return intersections;
    }

    public List<Intersection> getIntersections() {
        List<Intersection> retList = new ArrayList<>();
        retList.addAll(intersections.values());
        return retList;
    }

    public Intersection getIntersectionForNode(Node node) {
        return intersections.get(node.getLat() + "_" + node.getLon());
    }

    public Node getNodeForLocation(Point location) {
        double closestDistance = 99999;
        Node closestNode = null;
        for (Way way : map.getWays()) {
            for (Node node : way.getNodes()) {
                double distance = new Point(node.getLat(), node.getLon()).getDistanceFrom(location);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestNode = node;
                }
            }
        }

        return closestNode;
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
            if (!clockTickHandler.isKilled()) {
                clockTickHandler.clockTick((float) (1 + (speedMultiplier / 2)) / 10);
            }
        }

    }

    public void hitHome(int damage) {
        home.hit(damage);
        if (home.isKilled()) {
            die();
        }
    }

    private void die() {
        timer.cancel();
        parent.die();
    }

    private void win() {
        timer.cancel();
        parent.win();
    }

    public void addMoney(int money) {
        this.money += money;
    }

    public void subtractMoney(int money) {
        this.money -= money;
    }

    public List<Enemy> getEnemies() {
        List<Enemy> enemies = new ArrayList<>();

        for (org.noses.games.homedefense.nest.EnemyNest enemyNest : enemyNests) {
            for (EnemyGroup enemyGroup : enemyNest.getEnemyGroups()) {
                enemies.addAll(enemyGroup.getEnemies());
            }
        }

        return enemies;
    }

    public boolean isInsideMap(Point point) {

        return isPointWithinBounds(point, new Point(map.getNorth(), map.getWest()),
                new Point(map.getSouth(), map.getEast()));
    }

    public boolean isPointWithinBounds(Point point, Point upperLeft, Point lowerRight) {

        if (upperLeft.getLatitude() > lowerRight.getLatitude()) {
            if (point.getLatitude() > upperLeft.getLatitude()) {
                return false;
            }
            if (point.getLatitude() < lowerRight.getLatitude()) {
                return false;
            }
        } else {
            if (point.getLatitude() > lowerRight.getLatitude()) {
                return false;
            }
            if (point.getLatitude() < upperLeft.getLatitude()) {
                return false;
            }
        }

        if (upperLeft.getLongitude() > lowerRight.getLongitude()) {
            if (point.getLongitude() > upperLeft.getLongitude()) {
                return false;
            }
            if (point.getLongitude() < lowerRight.getLongitude()) {
                return false;
            }
        } else {
            if (point.getLongitude() > lowerRight.getLongitude()) {
                return false;
            }
            if (point.getLongitude() < upperLeft.getLongitude()) {
                return false;
            }
        }

        return true;
    }

    public Sound loadSound(String fileName) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(fileName));

        return sound;
    }

    public void render(Batch batch) {
        ShapeRenderer sr = new ShapeRenderer();
        sr.setColor(Color.WHITE);

        for (Way way : getMap().getWays()) {
            Gdx.gl.glLineWidth(way.getMaxSpeed() - 14);

            sr.setColor(Color.WHITE);
            //sr.setColor(way.getColor());

            sr.begin(ShapeRenderer.ShapeType.Line);
            Node prevNode = null;
            for (Node node : way.getNodes()) {
                if (prevNode != null) {
                    sr.line(convertLongToX(prevNode.getLon()), convertLatToY(prevNode.getLat()),
                            convertLongToX(node.getLon()), convertLatToY(node.getLat()));
                }
                prevNode = node;
            }
            sr.end();
        }

        batch.begin();

        levelEngine.render(batch);

        // render the nestConfigs
        for (org.noses.games.homedefense.nest.EnemyNest enemyNest : enemyNests) {
            if (!enemyNest.isKilled()) {
                enemyNest.render(batch);
            }
        }

        // render the towers
        for (Tower tower : getTowers()) {
            tower.render(batch);
        }

        // render the enemies

        for (EnemyNest enemyNest : enemyNests) {
            for (EnemyGroup enemyGroup : enemyNest.getEnemyGroups()) {
                List<Enemy> enemies = enemyGroup.getEnemies();
                for (Enemy enemy : enemies) {
                    if (enemy.isKilled()) {
                        continue;
                    }

                    enemy.getSprite().draw(batch);

                }
            }
        }

        getHome().render(batch);

        // render the score and other text
        font.draw(batch, "Health: " + getHome().getHealth(), 10, Gdx.graphics.getHeight() - (int) (Gdx.graphics.getHeight() * .1));

        font.draw(batch, "Money: " + getMoney(), 10, Gdx.graphics.getHeight() - (int) ((Gdx.graphics.getHeight() * .1) + (font.getCapHeight() * 2)));

        font.draw(batch, "Speed: " + getSpeedMultiplier() + "x", 10, Gdx.graphics.getHeight() - (int) ((Gdx.graphics.getHeight() * .1) + (font.getCapHeight() * 4)));

        speedButton.render(batch);

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

        if ((upgradeMenu != null) && (!upgradeMenu.isHidden())) {
            upgradeMenu.renderMenu(batch);
        }

        batch.end();
    }

    public double getSpriteScale(Sprite sprite, double scale) {
        return (parent.getScreenWidth() * scale) / sprite.getWidth();
    }

    public double getScaledPixels(int spriteWidth, double scale) {
        return (parent.getScreenWidth() * scale) / spriteWidth;
    }

    public void hideMenus() {
        towerChoiceMenu.setHidden(true);
        hideUpgradeMenu();
    }

    public void showUpgradeMenu(Tower tower) {

        upgradeMenu = new LeftSideUpgradeMenu(this, tower);
        addClickHandler(upgradeMenu);
        upgradeMenu.setHidden(false);
    }

    public void hideUpgradeMenu() {

        if (upgradeMenu == null) {
            return;
        }

        removeClickHandler(upgradeMenu);
        upgradeMenu = null;
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
