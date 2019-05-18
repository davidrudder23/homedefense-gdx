package org.noses.games.homedefense.bullet;

import com.badlogic.gdx.audio.Sound;
import org.noses.games.homedefense.HomeDefenseGame;

public class NormalBullet extends Bullet {

    public NormalBullet(HomeDefenseGame parent, Sound shotSound, double latitude, double longitude, double angle, double speed) {
        super(parent, "bullet_small.png", shotSound, 16, 16);
        currentLatitude = latitude;
        currentLongitude = longitude;
        originalLatitude = latitude;
        originalLongitude = longitude;

        this.angle = angle;
        this.speed = speed;
    }

    public int getRadius() {
        return 5;
    }

    @Override
    public int getDamage() {
        return 1;
    }
}
