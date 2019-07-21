package org.noses.games.homedefense.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.bullet.Bullet;
import org.noses.games.homedefense.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public abstract class Shooter implements PhysicalObject, ClockTickHandler {

    List<Bullet> bullets;

    MapScreen parent;

    double timeBetweenShots;

    double timeSinceLastFired = 0;

    final int maxOnScreen = 15;

    @Getter
    @Setter
    double angle;

    Sound shotSound;

    Point location;

    boolean killed;

    boolean paused;

    public Shooter(MapScreen parent, double timeBetweenShots, String shotSoundFilename, Point location) {
        this.parent = parent;
        this.timeBetweenShots = timeBetweenShots;
        this.location = location;

        killed = false;
        paused = true;

        shotSound = parent.loadSound(shotSoundFilename);
        bullets = new ArrayList<>();
    }

    @Override
    public double getLatitude() {
        return location.getLatitude();
    }

    @Override
    public double getLongitude() {
        return location.getLongitude();
    }

    @Override
    public void clockTick(double delta) {
        timeSinceLastFired += delta;

        if (timeSinceLastFired > timeBetweenShots) {
            shoot();
            timeSinceLastFired = 0;
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

    public abstract Bullet createBullet();

    public void shoot() {
        if (paused) {
            return;
        }

        if (parent.getEnemies().size() == 0) {
            return;
        }

        int numBulletsOnScreen = 0;
        for (Bullet bullet : bullets) {
            if (!bullet.isKilled()) {
                numBulletsOnScreen++;
            }
        }
        if (numBulletsOnScreen >= maxOnScreen) {
            return;
        }

        if (parent.getEnemies().size() <= 0) {
            return;
        }

        Bullet bullet = createBullet();

        bullet.shoot();
        bullets.add(bullet);

        parent.addClockTickHandler(bullet);
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        paused = false;
    }

    public void render(Batch batch) {

        for (Bullet bullet : bullets) {
            if (bullet.isKilled()) {
                continue;
            }
            TextureRegion textureRegion = bullet.getFrameTextureRegion();
            Sprite bulletSprite = new Sprite(textureRegion);

            bulletSprite.setX(parent.convertLongToX(bullet.getLongitude()));
            bulletSprite.setY(parent.convertLatToY(bullet.getLatitude()));
            bulletSprite.draw(batch);
        }

    }
}
