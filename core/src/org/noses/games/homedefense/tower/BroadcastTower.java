package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.NormalBulletShooter;
import org.noses.games.homedefense.geometry.Point;

public class BroadcastTower extends Tower {

    public BroadcastTower(MapScreen parent, double longitude, double latitude) {
        super(parent, "broadcast", longitude, latitude,DEFAULT_SCALE,
                new NormalBulletShooter(parent, 0.8, new Point(latitude, longitude), 4));
    }

    public static class BroadcastTowerFactory extends TowerFactory {
        @Override
        public Tower createTower(MapScreen parent, double longitude, double latitude) {
            return new BroadcastTower(parent, longitude, latitude);
        }

        public int getCost() { return 100; }

    }

    @Override
    public double minDistanceFromOtherTower() {
        return HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph *1000;
    }

    @Override
    public int getCost() {
        return 100;
    }

    @Override
    public int getStartingHealth() {
        return 30;
    }

}
