package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.NormalBulletShooter;
import org.noses.games.homedefense.geometry.Point;

public class LaserTower extends Tower {

    public LaserTower(HomeDefenseGame parent, double longitude, double latitude) {
        super(parent, "laser", longitude, latitude,
                new NormalBulletShooter(parent, 0.8, new Point(latitude, longitude)));
    }

    public static class LaserTowerFactory extends TowerFactory {
        @Override
        public Tower createTower(HomeDefenseGame parent, double longitude, double latitude) {
            return new LaserTower(parent, longitude, latitude);
        }
    }

}
