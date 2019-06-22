package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.NormalBulletShooter;
import org.noses.games.homedefense.geometry.Point;

public class FactoryTower extends Tower {

    public FactoryTower(MapScreen parent, double longitude, double latitude) {
        super(parent, "factory", longitude, latitude, 0.03,
                new NormalBulletShooter(parent, 0.8, new Point(latitude, longitude), 12));
    }

    public static class FactoryTowerFactory extends TowerFactory {
        @Override
        public Tower createTower(MapScreen parent, double longitude, double latitude) {
            return new FactoryTower(parent, longitude, latitude);
        }
        public int getCost() { return 100; }

    }

    @Override
    public double minDistanceFromOtherTower() {
        return HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph *1500;
    }

    @Override
    public int getCost() {
        return 100;
    }

    @Override
    public int getStartingHealth() {
        return 50;
    }
}
