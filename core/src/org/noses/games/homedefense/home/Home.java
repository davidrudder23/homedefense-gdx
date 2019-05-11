package org.noses.games.homedefense.home;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.enemy.Animation;
import org.noses.games.homedefense.enemy.Enemy;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Home extends Animation {

    final double timeBetweenShots = 5;

    double angle;

    float timeSinceLastFired = 0;

    @Getter
    int x;

    @Getter
    int y;

    public Home(HomeDefenseGame parent, int x, int y) {
        super(parent, "home.png", 64, 64);
        this.x = x;
        this.y = y;
        angle = 0;
    }

    public void clockTick(float delay) {
        super.clockTick(delay);
        angle = angle + 10;

        timeSinceLastFired += delay;

        if (timeSinceLastFired > timeSinceLastFired) {
            shoot();
        }

        angle = getAim();

    }

    private double getAim() {
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

        System.out.println("Aiming at " + sortedEnemies.get(0).getLocation());

        Enemy enemy = sortedEnemies.get(0);

        double angle = Math.atan2(getX() - enemy.getLocation().getX(),
                getY() - enemy.getLocation().getY());
        angle = angle * (180/Math.PI);
        return angle;
    }

    private void shoot() {
        //NormalBullet normalBullet = NormalBullet.builder().
    }

    public Sprite getSprite() {
        Texture frame = animation[0][frameNumber].getTexture();

        Sprite sprite = new Sprite(frame);

        System.out.println("Rotating " + angle);
        sprite.setRotation((float)angle);

        return sprite;
    }

}
