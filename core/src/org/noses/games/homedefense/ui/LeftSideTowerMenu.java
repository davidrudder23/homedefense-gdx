package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.tower.*;

import java.util.HashMap;

public class LeftSideTowerMenu extends LeftSideMenu {
    private int clickX;
    private int clickY;

    Sprite menuBackground;

    HashMap<String, LeftSideTowerMenuItem> menuItems; // normal, glow, grey, greyglow

    public LeftSideTowerMenu(MapScreen parent) {
        super(parent);

        menuItems = new HashMap<>();

        int spiteWidth = 64;
        int spiteHeight = 64;

        LeftSideTowerMenuItem menuItem = new LeftSideTowerMenuItem(parent, getX(0), getY(0), spiteWidth, spiteHeight, "tower/rifle", new RifleTower.RifleTowerFactor());
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideTowerMenuItem(parent, getX(1), getY(1), spiteWidth, spiteHeight, "tower/laser", new LaserTower.LaserTowerFactory());
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideTowerMenuItem(parent, getX(2), getY(2), spiteWidth, spiteHeight, "tower/bomber", new BomberTower.BomberTowerFactory());
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideTowerMenuItem(parent, getX(3), getY(3), spiteWidth, spiteHeight, "tower/factory", new FactoryTower.FactoryTowerFactory());
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideTowerMenuItem(parent, getX(4), getY(4), spiteWidth, spiteHeight, "tower/broadcast", new BroadcastTower.BroadcastTowerFactory());
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
    public int getZ() {
        return 1;
    }

    @Override
    public boolean onClick(int x, int y) {
        System.out.println("Left tower menu Onclick="+new Point(x,y));
        if (hidden) {
            parent.hideMenus();

            clickX = x;
            clickY = y;
        } else {
            mouseMoved(x,y);
            for (LeftSideTowerMenuItem menuItem : menuItems.values()) {
                if (menuItem.isMouseWithin()) {
                    Tower tower = menuItem.getTower(parent.convertXToLong(clickX), parent.convertYToLat(parent.getScreenHeight() - clickY));
                    parent.addClickHandler(tower);
                    parent.addTower(tower);
                }
            }
        }

        hidden = !hidden;

        return true;
    }


}
