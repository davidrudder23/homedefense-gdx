package org.noses.games.homedefense.game;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.bullet.Bullet;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.geometry.Point;

public class NormalBulletShooter extends Shooter {

    public NormalBulletShooter(HomeDefenseGame parent, double timeBetweenShots, Point location) {

        super(parent, timeBetweenShots, "normal_shot.mp3", location);
    }

    @Override
    public Bullet createBullet() {

        NormalBullet normalBullet = new NormalBullet(parent, shotSound, getLatitude(), getLongitude(), angle);

        return normalBullet;
    }
}
