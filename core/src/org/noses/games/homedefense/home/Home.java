package org.noses.games.homedefense.home;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.bullet.Bullet;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.game.Animation;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.game.PhysicalObject;
import org.noses.games.homedefense.game.Aimer;
import org.noses.games.homedefense.geometry.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Home extends Animation implements PhysicalObject {

    final double timeBetweenShots = 0.8;

    double timeSinceLastFired = 0;

    final int maxOnScreen = 15;

    double angle;

    List<Bullet> bullets;

    Sound shotSound;

    Sound hitSound;

    @Getter
    @Setter
    int health;

    @Getter
    double latitude;

    @Getter
    double longitude;

    Aimer aimer;

    public Home(HomeDefenseGame parent, double latitude, double longitude) {
        super(parent, "home.png", 64, 64, true);
        this.latitude = latitude;
        this.longitude = longitude;
        angle = 0;

        bullets = new ArrayList<>();

        timeSinceLastFired = timeBetweenShots;

        shotSound = parent.loadSound("normal_shot.mp3");

        hitSound = parent.loadSound("home_hit.mp3");

        health = 100;
        aimer = new Aimer(parent, latitude, longitude);

        parent.addClockTickHandler(this);
    }

    public void hit(int damage) {
        health -= damage;
        hitSound.play();
    }

    @Override
    public boolean isKilled() {
        return health <= 0;
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

        NormalBullet normalBullet = new NormalBullet(parent, shotSound, getLatitude(), getLongitude(), angle);
        normalBullet.shoot();
        bullets.add(normalBullet);

        //System.out.println("Adding bullet to clock tick handlers");
        parent.addClockTickHandler(normalBullet);
    }

    public void render(Batch batch) {

        Sprite homeSprite = getSprite();
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
