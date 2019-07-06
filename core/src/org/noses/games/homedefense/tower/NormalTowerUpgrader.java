package org.noses.games.homedefense.tower;

public class NormalTowerUpgrader implements TowerUpgrader {
    int cost;

    public NormalTowerUpgrader(int cost) {
        this.cost = cost;
    }
    @Override
    public void upgradeTower(Tower tower) {
        tower.upgradeTower();
    }

    @Override
    public int getCost() {
        return 50;
    }
}
