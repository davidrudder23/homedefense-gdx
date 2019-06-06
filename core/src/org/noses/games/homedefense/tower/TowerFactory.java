package org.noses.games.homedefense.tower;

import org.noses.games.homedefense.game.MapScreen;

public abstract class TowerFactory {

    public abstract Tower createTower(MapScreen parent, double longitude, double latitude);

    public abstract int getCost();

}
