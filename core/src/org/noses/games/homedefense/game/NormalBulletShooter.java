package org.noses.games.homedefense.game;

import org.noses.games.homedefense.bullet.Bullet;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.geometry.Point;

public class NormalBulletShooter extends Shooter {

    int damage;

    public NormalBulletShooter(MapScreen parent, double timeBetweenShots, Point location, int damage) {
        super(parent, timeBetweenShots, "normal_shot.mp3", location);
        this.damage = damage;
    }

    @Override
    public Bullet createBullet() {

        NormalBullet normalBullet = new NormalBullet(parent, shotSound, getLatitude(), getLongitude(), angle, damage);

        return normalBullet;
    }
}
