package org.noses.games.homedefense.tower;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import org.noses.games.homedefense.game.*;
import org.noses.games.homedefense.geometry.Point;

@Data
public abstract class Tower implements ClockTickHandler, PhysicalObject {
    double bulletSpeed;
    double delayBetweenShots;

    private Point location;

    MapScreen parent;
    String towerName;
    Animation animation;

    double timeSinceLastFired = 0;

    final int maxOnScreen = 15;

    Aimer aimer;
    Shooter shooter;

    boolean killed;

    int health;

    public Tower(MapScreen parent, String towerName, double longitude, double latitude, double scale, Shooter shooter) {
        this.towerName = towerName;
        this.parent = parent;

        killed = false;

        animation = new Animation(parent, "tower/" + towerName + ".png", 199, 199, scale, true);
        parent.addClockTickHandler(animation);

        location = new Point(latitude, longitude);

        aimer = new Aimer(parent, latitude, longitude);
        this.shooter = shooter;

        health = getStartingHealth();
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
        Sprite sprite = new Sprite(getFrameTextureRegion());
        sprite.setScale(50/sprite.getWidth());
        sprite.setCenterX(parent.convertLongToX(getLongitude()));
        sprite.setCenterY(parent.convertLatToY(getLatitude()));
        sprite.draw(batch);

        shooter.render(batch);
    }

    public abstract double minDistanceFromOtherTower();

    public abstract int getCost();

    public abstract int getStartingHealth();

    public void damage(int points) {
        health -= points;

        System.out.println(this+" was damaged with "+points+" now at "+health);

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

    public TextureRegion getFrameTextureRegion() {
        return animation.getFrameTextureRegion();
    }

}
