package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.NormalBulletShooter;
import org.noses.games.homedefense.geometry.Point;

public class RifleTower extends Tower {

    public RifleTower(MapScreen parent, double longitude, double latitude) {
        super(parent, "rifle", longitude, latitude,0.03,
                new NormalBulletShooter(parent, 0.8, new Point(latitude, longitude), 4));
    }

    public static class RifleTowerFactor extends TowerFactory {
        @Override
        public Tower createTower(MapScreen parent, double longitude, double latitude) {
            return new RifleTower(parent, longitude, latitude);
        }

        public int getCost() { return 25; }
    }

    @Override
    public double minDistanceFromOtherTower() {
        return HomeDefenseGame.LATLON_MOVED_IN_1s_1mph*500;
    }

    @Override
    public int getCost() {
        return 25;
    }

    @Override
    public int getStartingHealth() {
        return 30;
    }


}
