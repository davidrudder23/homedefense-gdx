package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.NormalBulletShooter;
import org.noses.games.homedefense.geometry.Point;

public class BomberTower extends Tower {

    public BomberTower(HomeDefenseGame parent, double longitude, double latitude) {
        super(parent, "bomber", longitude, latitude,
                new NormalBulletShooter(parent, 0.8, new Point(latitude, longitude)));
    }

    public static class BomberTowerFactory extends TowerFactory {
        @Override
        public Tower createTower(HomeDefenseGame parent, double longitude, double latitude) {
            return new BomberTower(parent, longitude, latitude);
        }
    }

}