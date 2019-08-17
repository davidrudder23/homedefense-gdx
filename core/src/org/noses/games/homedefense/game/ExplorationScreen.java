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

public class ExplorationScreen extends MapScreen {
    HomeDefenseGame parent;

    @Getter
    Map map = null;

    HashMap<String, Intersection> intersections;

    @Getter
    Home home;

    @Getter
    @Setter
    private int money;

    BitmapFont font;

    @Getter
    private List<Tower> towers;

    Timer.Task timer;

    @Getter
    LeftSideTowerMenu towerChoiceMenu;

    @Getter
    Hero hero;

    public ExplorationScreen(HomeDefenseGame parent, Point location) {
        super(parent, location);

        towers = new ArrayList<>();

        money = 0;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/score.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 22;
        font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        font.setColor(Color.BLACK);

        initializeMap(location);

        HomeDefenseGame.ONE_PIXEL_IN_LATLON = (map.getEast() - map.getWest()) / Gdx.graphics.getWidth();

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

        setupHero();

        Gdx.input.setInputProcessor(this);

        timer = Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       clockTick(1 / 10.0f);
                                   }
                               }
                , 0f, 1 / 10.0f);

    }

    public void setupHero() {

        if (parent.hasLiveGeolocation()) {
            hero = new Hero(this, parent.getGeolocation());
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

    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            parent.die();

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
        subtractMoney(tower.getCost());
        addClickHandler(tower);
        addClockTickHandler(tower);
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
        super.render(batch);
        batch.begin();

        // render the towers
        for (Tower tower : getTowers()) {
            tower.render(batch);
        }

        getHome().render(batch);

        font.draw(batch, "Money: " + getMoney(), 10, Gdx.graphics.getHeight() - (int) ((Gdx.graphics.getHeight() * .1) + (font.getCapHeight() * 2)));

        if (parent.hasLiveGeolocation()) {
            Sprite heroSprite = new Sprite(hero.getFrameTextureRegion());
            heroSprite.setScale((float) ((parent.getScreenWidth() * hero.getScale()) / heroSprite.getWidth()));
            heroSprite.setCenterX(convertLongToX(hero.getLongitude()));
            heroSprite.setCenterY(convertLatToY(hero.getLatitude()));
            heroSprite.draw(batch);
        }

        batch.end();
    }

    public List<Enemy> getEnemies() {
        return new ArrayList<>();
    }

    public void hideMenus() {
    }

    public void showUpgradeMenu(Tower tower) {

    }
}