package org.noses.games.homedefense.home;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    final int maxOnScreen = 5;

    double angle;

    double timeSinceLastFired = 0;

    List<Bullet> bullets;

    Sound shotSound;

    Sound hitSound;

    @Getter
    @Setter
    int health;

    @Getter
    int x;

    @Getter
    int y;

    Timer.Task timer;


    public Home(HomeDefenseGame parent, int x, int y) {
        super(parent, "home.png", 64, 64);
        this.x = x;
        this.y = y;
        angle = 0;

        bullets = new ArrayList<>();

        timeSinceLastFired = timeBetweenShots;

        shotSound = parent.loadSound("normal_shot.mp3");

        hitSound = parent.loadSound("home_hit.mp3");

        health = 100;

        timer = Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       clockTick(0.1f);
                                   }
                               },
                0f, 1 / 10.0f);
    }

    public void hit(int damage) {
        health -= damage;
        hitSound.play();
    }

    public boolean isDead() {
        return health<=0;
    }

    public void clockTick(float delay) {
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

                double homeCalc = (getX() * getX()) + (getY() * getY());
                double aCalc = (locationA.getX() * locationA.getX()) + (locationA.getY() * locationA.getY()) - homeCalc;
                double bCalc = (locationB.getX() * locationB.getX()) + (locationB.getY() * locationB.getY()) - homeCalc;

                if (aCalc == bCalc) {
                    return 0;
                }

                if (aCalc < bCalc) {
                    return -1;
                }

                return 1;
            }
        });

        Enemy enemy = sortedEnemies.get(0);

        double angle = Math.atan2(getY() - enemy.getLocation().getY(),
                getX() - enemy.getLocation().getX());
        angle = 360 - (180 - (angle * (180 / Math.PI)));

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

        NormalBullet normalBullet = new NormalBullet(parent, shotSound, getX(), getY(), angle, 50);
        normalBullet.shoot();
        bullets.add(normalBullet);
    }

    public void render(Batch batch) {

        Sprite homeSprite = getSprite();
        homeSprite.setCenterX(getX());
        homeSprite.setCenterY(getY());

        homeSprite.draw(batch);

        for (Bullet bullet : bullets) {
            if (bullet.isDead()) {
                continue;
            }
            TextureRegion textureRegion = bullet.getFrameTextureRegion();
            Sprite bulletSprite = new Sprite(textureRegion);
            bulletSprite.setX(bullet.getX());
            bulletSprite.setY(bullet.getY());
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
