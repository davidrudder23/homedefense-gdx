package org.noses.games.homedefense.bullet;

import com.badlogic.gdx.audio.Sound;
import org.noses.games.homedefense.game.MapScreen;

public class BombBullet extends Bullet {

    public BombBullet(MapScreen parent, Sound shotSound, double latitude, double longitude, double angle) {
        super(parent, "bullet_small.png", shotSound, 16, 16);
        currentLatitude = latitude;
        currentLongitude = longitude;
        originalLatitude = latitude;
        originalLongitude = longitude;

        this.angle = angle;
        this.speed = 300;
    }

    public double getRadius() {
        return tileWidth/2;
    }

    @Override
    public int getDamage() {
        return 10;
    }
}
