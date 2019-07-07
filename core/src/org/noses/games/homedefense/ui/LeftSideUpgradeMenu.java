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

        menuItems = new HashMap<>();

        this.towerBeingUpgraded = tower;

        int spiteWidth = 64;
        int spiteHeight = 64;

        LeftSideUpgradeMenuItem menuItem = new LeftSideUpgradeMenuItem(parent, getX(0), getY(0), spiteWidth, spiteHeight, tower.getLevel()+2, "tower/"+tower.getTowerName(), new NormalTowerUpgrader(25));
        menuItems.put(menuItem.getSpriteName(), menuItem);
    }

    @Override
    public HashMap<String, MenuItem> getMenuItems() {

        HashMap<String, MenuItem> menuItems = new HashMap<>();
        for (String name: this.menuItems.keySet()) {
            menuItems.put(name, this.menuItems.get(name));
        }
        return menuItems;
    }


    @Override
    public boolean onClick(int x, int y) {
        for (LeftSideUpgradeMenuItem menuItem: menuItems.values()) {
            mouseMoved(x,y);
            if (menuItem.isMouseWithin()) {
                parent.hideMenus();

                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onClickUp(int x, int y) {
        clickX = x;
        clickY = y;

        hidden = !hidden;

        for (LeftSideUpgradeMenuItem menuItem: menuItems.values()) {
            mouseMoved(x,y);
            if (menuItem.isMouseWithin()) {
                menuItem.upgradeTower(towerBeingUpgraded);
                parent.hideMenus();

                return false;
            }
        }

        return true;
    }

}
