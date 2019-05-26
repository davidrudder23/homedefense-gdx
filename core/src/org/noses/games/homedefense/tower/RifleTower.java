package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.NormalBulletShooter;
import org.noses.games.homedefense.geometry.Point;

public class RifleTower extends Tower {

    public RifleTower(HomeDefenseGame parent, double longitude, double latitude) {
        super(parent, "rifle", longitude, latitude,
                new NormalBulletShooter(parent, 0.8, new Point(latitude, longitude)));
    }

}
