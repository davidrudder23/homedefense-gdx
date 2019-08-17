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

public class BattleScreen extends MapScreen {
    HomeDefenseGame parent;

    @Getter
    List<EnemyGroup> enemyGroups;

    @Getter
    Home home;

    @Getter
    @Setter
    private int money;

    BitmapFont font;

    @Getter
    List<EnemyNest> enemyNests;

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

    public BattleScreen(HomeDefenseGame parent, Point location) {
        super(parent, location);
        this.parent = parent;

        enemyGroups = new ArrayList<>();

        enemyNests = new ArrayList<>();

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

        speedButton = new SpeedButton(this, getScreenWidth() - 40, getScreenHeight() - 40);
        addClickHandler(speedButton);

        startNewLevel();

        Gdx.input.setInputProcessor(this);

        towerChoiceMenu = new LeftSideTowerMenu(this);
        addClickHandler(towerChoiceMenu);

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

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            speedUp();
        }

        return false;
    }

    public void speedUp() {
        speedMultiplier += 1;

        if ((speedMultiplier > 5) || (speedMultiplier < 1)) {
            speedMultiplier = 1;
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

    public void dropNest(EnemyNest enemyNest) {
        addClockTickHandler(enemyNest);
        enemyNests.add(enemyNest);
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

    public List<Enemy> getEnemies() {
        List<Enemy> enemies = new ArrayList<>();

        for (EnemyNest enemyNest : enemyNests) {
            for (EnemyGroup enemyGroup : enemyNest.getEnemyGroups()) {
                enemies.addAll(enemyGroup.getEnemies());
            }
        }

        return enemies;
    }

    public void render(Batch batch) {
        super.render(batch);
        batch.begin();

        levelEngine.render(batch);

        // render the nestConfigs
        for (EnemyNest enemyNest : enemyNests) {
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


}
