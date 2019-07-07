package org.noses.games.homedefense.tower;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import org.noses.games.homedefense.game.*;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.ui.LeftSideTowerMenu;
import org.noses.games.homedefense.ui.MouseHandler;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Tower implements ClockTickHandler, PhysicalObject, MouseHandler {
    public static final double DEFAULT_SCALE=0.15;
    double bulletSpeed;
    double delayBetweenShots;

    private Point location;

    MapScreen parent;
    String towerName;
    List<Animation> animations;

    double timeSinceLastFired = 0;

    final int maxOnScreen = 15;

    Aimer aimer;
    Shooter shooter;

    boolean killed;

    int health;

    int level;

    double scale;

    public Tower(MapScreen parent, String towerName, double longitude, double latitude, double scale, Shooter shooter) {
        this.towerName = towerName;
        this.parent = parent;
        this.scale = scale;

        level = 0;

        killed = false;

        animations = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            Animation animation = new Animation(parent, "tower/" + towerName + "_lvl_" + i + ".png", 0, 0, scale, true);
            animations.add(animation);
            parent.addClockTickHandler(animation);
        }

        location = new Point(latitude, longitude);

        aimer = new Aimer(parent, latitude, longitude);
        this.shooter = shooter;

        health = getStartingHealth();
    }

    public void upgradeTower() {
        level++;

        if (level >= animations.size()) {
            level = animations.size() - 1;
        }

        System.out.println("Tower " + getTowerName() + " is upgraded to " + level);
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public void clockTick(double delta) {
        shooter.setAngle(aimer.aim(aimer.findClosestEnemy(parent.getEnemies())));

        shooter.clockTick(delta);
    }

    public void render(Batch batch) {
        Sprite sprite = getSprite();
        sprite.draw(batch);

        shooter.render(batch);
    }

    private Sprite getSprite() {
        Sprite sprite = new Sprite(getFrameTextureRegion());
        sprite.setScale((float)scale);
        sprite.setCenterX(parent.convertLongToX(getLongitude()));
        sprite.setCenterY(parent.convertLatToY(getLatitude()));

        return sprite;
    }

    public abstract double minDistanceFromOtherTower();

    public abstract int getCost();

    public abstract int getStartingHealth();

    public void damage(int points) {
        health -= points;

        System.out.println(this + " was damaged with " + points + " now at " + health);

        if (health <= 0) {
            kill();
        }
    }

    @Override
    public void kill() {
        killed = true;
    }

    @Override
    public boolean isKilled() {
        return killed;
    }

    private boolean isWithinBounds(int x, int y) {

        Sprite sprite = getSprite();
        int width = (int) (sprite.getWidth() * sprite.getScaleX());
        int height = (int) (sprite.getHeight() * sprite.getScaleY());

        int upperLeftX = parent.convertLongToX(getLongitude()) - (int) (width / 2);
        int upperLeftY = parent.convertLatToY(getLatitude()) - (int) (height / 2);

        int lowerRightX = upperLeftX + width;
        int lowerRightY = upperLeftY + height;

        if (parent.isPointWithinBounds(new Point(x, parent.getScreenHeight() - y),
                new Point(upperLeftX, upperLeftY),
                new Point(lowerRightX, lowerRightY))) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onClick(int x, int y) {

        if (!isWithinBounds(x, y)) {
            System.out.println(towerName + " not was clicked");
            return true;
        }

        if (level >= (animations.size()-1)) {
            return false;
        }

        System.out.println(towerName + " was clicked");
        parent.hideMenus();

        parent.showUpgradeMenu(this);

        return false;
    }

    @Override
    public int getZ() {
        return 5;
    }

    @Override
    public boolean onRightClick(int x, int y) {
        return false;
    }

    @Override
    public boolean onClickUp(int x, int y) {
        return onClick(x, y);
    }

    @Override
    public boolean onMouseDragged(int x, int y) {
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    public TextureRegion getFrameTextureRegion() {
        return animations.get(level).getFrameTextureRegion();
    }

}
