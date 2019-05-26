package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.HomeDefenseGame;

public abstract class TowerFactory {

    public abstract Tower createTower(HomeDefenseGame parent, double longitude, double latitude);
}
