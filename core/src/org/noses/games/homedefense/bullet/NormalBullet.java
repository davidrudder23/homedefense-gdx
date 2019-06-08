package org.noses.games.homedefense.bullet;

import com.badlogic.gdx.audio.Sound;
import org.noses.games.homedefense.game.MapScreen;

public class NormalBullet extends Bullet {

    int damage;

    public NormalBullet(MapScreen parent, Sound shotSound, double latitude, double longitude, double angle, int damage) {
        super(parent, "bullet_small.png", shotSound, 16, 16, 0.02, angle);

        currentLatitude = latitude;
        currentLongitude = longitude;
        originalLatitude = latitude;
        originalLongitude = longitude;

        this.speed = 1000;

        this.damage = damage;
    }

    public double getRadius() {
        return tileWidth/2;
    }

    @Override
    public int getDamage() {
        return damage;
    }
}
