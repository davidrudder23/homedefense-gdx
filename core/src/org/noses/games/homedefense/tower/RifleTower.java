package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;

public class RifleTower extends AbstractTower {

    public RifleTower(HomeDefenseGame parent, double longitude, double latitude) {
        super (parent, "rifle", longitude, latitude);
    }

    @Override
    public void clockTick(double delta) {

    }
}
