package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.NormalBulletShooter;
import org.noses.games.homedefense.geometry.Point;

public class BroadcastTower extends Tower {

    public BroadcastTower(HomeDefenseGame parent, double longitude, double latitude) {
        super(parent, "broadcast", longitude, latitude,
                new NormalBulletShooter(parent, 0.8, new Point(latitude, longitude)));
    }

    public static class BroadcastTowerFactory extends TowerFactory {
        @Override
        public Tower createTower(HomeDefenseGame parent, double longitude, double latitude) {
            return new BroadcastTower(parent, longitude, latitude);
        }
    }

    @Override
    public double minDistanceFromOtherTower() {
        return HomeDefenseGame.LATLON_MOVED_IN_1s_1mph*1000;
    }

}
