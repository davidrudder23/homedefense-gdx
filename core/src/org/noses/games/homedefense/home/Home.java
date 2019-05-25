package org.noses.games.homedefense.home;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.bullet.Bullet;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.enemy.Animation;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Home extends Animation {

    final double timeBetweenShots = 0.8;

    final int maxOnScreen = 15;

    double angle;

    double timeSinceLastFired = 0;

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

    Timer.Task timer;


    public Home(HomeDefenseGame parent, double latitude, double longitude) {
        super(parent, "home.png", 64, 64);
        this.latitude = latitude;
        this.longitude = longitude;
        angle = 0;

        bullets = new ArrayList<>();

        timeSinceLastFired = timeBetweenShots;

        shotSound = parent.loadSound("normal_shot.mp3");

        hitSound = parent.loadSound("home_hit.mp3");

        health = 100;

        timer = Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       clockTick(0.1);
                                   }
                               },
                0f, 0.1f);
    }

    public void hit(int damage) {
        health -= damage;
        hitSound.play();
    }

    public boolean isDead() {
        return health<=0;
    }

    public void clockTick(double delay) {
        angle = angle + 10;

        timeSinceLastFired += delay;

        if (timeSinceLastFired > timeBetweenShots) {
            shoot();
            timeSinceLastFired = 0;
        }

        angle = aim();

    }

    private double aim() {
        // find enemy
        List<Enemy> sortedEnemies = parent.getEnemies();
        if ((sortedEnemies == null) || (sortedEnemies.size() == 0)) {
            return 0;
        }


        Collections.sort(sortedEnemies, new Comparator<Enemy>() {
            @Override
            public int compare(Enemy a, Enemy b) {
                Point locationA = a.getLocation();
                Point locationB = b.getLocation();

                double homeCalc = (getLatitude() * getLatitude()) + (getLongitude() * getLongitude());
                double aCalc = (locationA.getLatitude() - getLatitude()) * (locationA.getLatitude() - getLatitude())
                        + (locationA.getLongitude() - getLongitude()) * (locationA.getLongitude() - getLongitude());
                double bCalc = (locationB.getLatitude() - getLatitude()) * (locationB.getLatitude() - getLatitude())
                        + (locationB.getLongitude() - getLongitude()) * (locationB.getLongitude() - getLongitude());

                if (aCalc == bCalc) {
                    return 0;
                }

                if (aCalc < bCalc) {
                    return -1;
                }

                return 1;
            }
        });

        Enemy closestEnemy = sortedEnemies.get(0);

        double enemyLong = closestEnemy.getLocation().getLongitude();
        double enemyLat = closestEnemy.getLocation().getLatitude();

        double angle = Math.atan2(getLongitude() - enemyLong,
                getLatitude() - enemyLat);
        angle = 360 - (180 - (angle * (180 / Math.PI)));

        //System.out.println ("Aiming "+angle+" deg at "+new Point(enemyLat, enemyLong)+" for enemy at "+closestEnemy.getBoundingBox());
        return angle;
    }

    private void shoot() {
        int numBulletsOnScreen = 0;
        for (Bullet bullet : bullets) {
            if (!bullet.isDead()) {
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
    }

    public void render(Batch batch) {

        Sprite homeSprite = getSprite();
        homeSprite.setCenterX(parent.convertLongToX(longitude));
        homeSprite.setCenterY(parent.convertLatToY(latitude));

        homeSprite.draw(batch);

        for (Bullet bullet : bullets) {
            if (bullet.isDead()) {
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
