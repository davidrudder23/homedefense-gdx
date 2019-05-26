package org.noses.games.homedefense.game;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.bullet.BombBullet;
import org.noses.games.homedefense.bullet.Bullet;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.geometry.Point;

public class BombBulletShooter extends Shooter {

    public BombBulletShooter(HomeDefenseGame parent, double timeBetweenShots, Point location) {

        super(parent, timeBetweenShots, "normal_shot.mp3", location);
    }

    @Override
    public Bullet createBullet() {

        BombBullet bombBullet = new BombBullet(parent, shotSound, getLatitude(), getLongitude(), angle);

        return bombBullet;
    }
}
