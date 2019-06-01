package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.NormalBulletShooter;
import org.noses.games.homedefense.geometry.Point;

public class LaserTower extends Tower {

    public LaserTower(MapScreen parent, double longitude, double latitude) {
        super(parent, "laser", longitude, latitude,
                new NormalBulletShooter(parent, 0.8, new Point(latitude, longitude), 10));
    }

    public static class LaserTowerFactory extends TowerFactory {
        @Override
        public Tower createTower(MapScreen parent, double longitude, double latitude) {
            return new LaserTower(parent, longitude, latitude);
        }
    }

    @Override
    public double minDistanceFromOtherTower() {
        return HomeDefenseGame.LATLON_MOVED_IN_1s_1mph*900;
    }


    @Override
    public int getCost() {
        return 75;
    }

}
