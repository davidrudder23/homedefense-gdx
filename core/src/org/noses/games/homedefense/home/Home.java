package org.noses.games.homedefense.home;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;
import org.noses.games.homedefense.bullet.Bullet;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.game.Aimer;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.PhysicalObject;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.tower.Tower;

import java.util.ArrayList;
import java.util.List;

public class Home extends Enemy implements PhysicalObject {

    final double timeBetweenShots = 0.8;

    double timeSinceLastFired = 0;

    final int maxOnScreen = 15;

    double angle;

    List<Bullet> bullets;

    Sound shotSound;

    Sound hitSound;

    @Getter
    double latitude;

    @Getter
    double longitude;

    Aimer aimer;

    public Home(MapScreen parent, double latitude, double longitude) {
        super(parent, "home.png", parent.loadSound("home_hit.mp3"), 64, 64, .08, 100);
        this.latitude = latitude;
        this.longitude = longitude;
        angle = 0;

        bullets = new ArrayList<>();

        timeSinceLastFired = timeBetweenShots;

        shotSound = parent.loadSound("normal_shot.mp3");

        hitSound = parent.loadSound("home_hit.mp3");

        aimer = new Aimer(parent, latitude, longitude);

        parent.addClockTickHandler(this);
    }

    @Override
    public int getDamage() {
        return 300;
    }

    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public Point getLocation() {
        return new Point(latitude, longitude);
    }

    @Override
    public boolean canBeHitBy(Tower tower) {
        return false;
    }

    @Override
    public boolean canBeHitByHome() {
        return false;
    }

    public void clockTick(double delay) {
        timeSinceLastFired += delay;

        if (timeSinceLastFired > timeBetweenShots) {
            shoot();
            timeSinceLastFired = 0;
        }

        angle = aim();

    }

    private double aim() {
        Enemy closestEnemy = aimer.findClosestEnemy(parent.getEnemies());

        if (closestEnemy == null) {
            return 0;
        }

        return aimer.aim(closestEnemy);
    }

    private void shoot() {
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

        NormalBullet normalBullet = new NormalBullet(parent, shotSound, getLatitude(), getLongitude(), angle, getDamage());
        normalBullet.shoot();
        bullets.add(normalBullet);

        //System.out.println("Adding bullet to clock tick handlers");
        parent.addClockTickHandler(normalBullet);
    }

    public void render(Batch batch) {

        Sprite homeSprite = getSprite();
        homeSprite.setScale((float)((parent.getScreenWidth()/homeSprite.getWidth())*getScale()));
        homeSprite.setCenterX(parent.convertLongToX(longitude));
        homeSprite.setCenterY(parent.convertLatToY(latitude));

        homeSprite.draw(batch);

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

    public Sprite getSprite() {
        Texture frame = animation[0][frameNumber].getTexture();

        Sprite sprite = new Sprite(frame);

        sprite.setRotation((float) angle);

        return sprite;
    }

}
