package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.tower.NormalTowerUpgrader;
import org.noses.games.homedefense.tower.RifleTower;
import org.noses.games.homedefense.tower.Tower;

import java.util.HashMap;

public class LeftSideUpgradeMenu extends LeftSideMenu {
    private int clickX;
    private int clickY;

    private int currentX;
    private int currentY;

    HashMap<String, LeftSideUpgradeMenuItem> menuItems; // normal, glow, grey, greyglow

    private Tower towerBeingUpgraded;

    public LeftSideUpgradeMenu(MapScreen parent, Tower tower) {
        super(parent);

        this.towerBeingUpgraded = tower;

        int spiteWidth = 64;
        int spiteHeight = 64;

        LeftSideUpgradeMenuItem menuItem = new LeftSideUpgradeMenuItem(parent, getX(0), getY(0), spiteWidth, spiteHeight, "tower/rifle", new NormalTowerUpgrader(25));
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideUpgradeMenuItem(parent, getX(1), getY(1), spiteWidth, spiteHeight, "tower/laser", new NormalTowerUpgrader(50));
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideUpgradeMenuItem(parent, getX(2), getY(2), spiteWidth, spiteHeight, "tower/bomber", new NormalTowerUpgrader(75));
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideUpgradeMenuItem(parent, getX(3), getY(3), spiteWidth, spiteHeight, "tower/factory", new NormalTowerUpgrader(100));
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideUpgradeMenuItem(parent, getX(4), getY(4), spiteWidth, spiteHeight, "tower/broadcast", new NormalTowerUpgrader(250));
        menuItems.put(menuItem.getSpriteName(), menuItem);
    }

    @Override
    public boolean onClick(int x, int y) {
        clickX = x;
        clickY = y;

        hidden = !hidden;

        for (LeftSideUpgradeMenuItem menuItem: menuItems.values()) {
            if (menuItem.isMouseWithin()) {
                menuItem.upgradeTower(towerBeingUpgraded);
            }
        }

        return true;
    }

}
