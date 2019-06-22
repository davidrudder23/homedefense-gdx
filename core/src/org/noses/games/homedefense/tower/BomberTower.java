package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.BombBulletShooter;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;

public class BomberTower extends Tower {

    public BomberTower(MapScreen parent, double longitude, double latitude) {
        super(parent, "bomber", longitude, latitude, 0.03,
                new BombBulletShooter(parent, 0.8, new Point(latitude, longitude)));
    }

    public static class BomberTowerFactory extends TowerFactory {
        @Override
        public Tower createTower(MapScreen parent, double longitude, double latitude) {
            return new BomberTower(parent, longitude, latitude);
        }

        public int getCost() { return 50; }

    }

    @Override
    public double minDistanceFromOtherTower() {
        return HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph *500;
    }

    @Override
    public int getCost() {
        return 50;
    }

    @Override
    public int getStartingHealth() {
        return 60;
    }

}
