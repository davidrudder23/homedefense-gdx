package org.noses.games.homedefense.home;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.bullet.BaseBullet;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.enemy.Animation;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Home extends Animation {

    final double timeBetweenShots = 1;

    double angle;

    double timeSinceLastFired = 0;

    List<BaseBullet> bullets;

    @Getter
    int x;

    @Getter
    int y;

    public Home(HomeDefenseGame parent, int x, int y) {
        super(parent, "home.png", 64, 64);
        this.x = x;
        this.y = y;
        angle = 0;

        bullets = new ArrayList<>();

        timeSinceLastFired = timeBetweenShots;
    }

    public void clockTick(float delay) {
        super.clockTick(delay);
        angle = angle + 10;

        timeSinceLastFired += delay;

        if (timeSinceLastFired > timeBetweenShots) {
            shoot();
            timeSinceLastFired = 0;
        }

        angle = getAngle();

    }

    private double getAngle() {
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
        angle = 180 - (angle * (180/Math.PI));

        System.out.println("Aiming at " + enemy.getLocation()+" angle="+angle);

        return angle;
    }

    private void shoot() {
        System.out.println ("Shooting");
        NormalBullet normalBullet = new NormalBullet(parent, getX(), getY(), angle, 50);
        bullets.add(normalBullet);
    }

    public void render(Batch batch) {

        Sprite homeSprite = getSprite();
        homeSprite.setCenterX(getX());
        homeSprite.setCenterY(getY());

        homeSprite.draw(batch);

        //System.out.println ("Rendering "+bullets.size()+" bullets");
        for (BaseBullet bullet: bullets) {
            TextureRegion textureRegion = bullet.getFrameTextureRegion();
            Sprite bulletSprite = new Sprite(textureRegion);
            bulletSprite.setCenterX(bullet.getX());
            bulletSprite.setCenterY(bullet.getY());
            bulletSprite.draw(batch);
        }

    }

    public Sprite getSprite() {
        Texture frame = animation[0][frameNumber].getTexture();

        Sprite sprite = new Sprite(frame);

        //System.out.println("Rotating " + angle);
        sprite.setRotation((float)angle);

        return sprite;
    }

}
