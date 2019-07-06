package org.noses.games.homedefense.tower;

public interface TowerUpgrader {

    public abstract void upgradeTower(Tower tower);

    public int getCost();

}
