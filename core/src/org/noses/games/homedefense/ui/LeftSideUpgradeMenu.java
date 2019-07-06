package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.tower.RifleTower;

import java.util.HashMap;

public class LeftSideUpgradeMenu implements MouseHandler {
    private int clickX;
    private int clickY;

    private int currentX;
    private int currentY;

    HashMap<String, LeftSideUpgradeMenuItem> menuItems; // normal, glow, grey, greyglow

    MapScreen parent;


    @Getter
    boolean hidden;

    Sprite menuBackground;


    public LeftSideUpgradeMenu(MapScreen parent) {
        this.parent = parent;

        Texture backgroundTexture = new Texture("menuBackground.png");

        menuBackground = new Sprite(backgroundTexture);

        menuItems = new HashMap<>();

        int spiteWidth = 64;
        int spiteHeight = 64;

        LeftSideUpgradeMenuItem menuItem = new LeftSideUpgradeMenuItem(parent, getX(0), getY(0), spiteWidth, spiteHeight, "tower/rifle", new RifleTower.RifleTowerUpgrader());
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideUpgradeMenuItem(parent, getX(1), getY(1), spiteWidth, spiteHeight, "tower/laser", new RifleTower.RifleTowerUpgrader());
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideUpgradeMenuItem(parent, getX(2), getY(2), spiteWidth, spiteHeight, "tower/bomber", new RifleTower.RifleTowerUpgrader());
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideUpgradeMenuItem(parent, getX(3), getY(3), spiteWidth, spiteHeight, "tower/factory", new RifleTower.RifleTowerUpgrader());
        menuItems.put(menuItem.getSpriteName(), menuItem);

        menuItem = new LeftSideUpgradeMenuItem(parent, getX(4), getY(4), spiteWidth, spiteHeight, "tower/broadcast", new RifleTower.RifleTowerUpgrader());
        menuItems.put(menuItem.getSpriteName(), menuItem);
    }

    private int getX(int menuNum) {
        return 50;
    }

    private int getY(int menuNum) {
        int sizeOfMenu = parent.getScreenHeight()/10;
        int sizeOfPAdding = parent.getScreenHeight()/50;
        int offset = 100;
        return offset + (sizeOfMenu+sizeOfPAdding)*menuNum;
    }

    @Override
    public boolean onClick(int x, int y) {
        clickX = x;
        clickY = y;

        hidden = !hidden;

        for (LeftSideUpgradeMenuItem menuItem: menuItems.values()) {
            if (menuItem.isMouseWithin()) {
                //menuItem.upgradeTower(sfvc);
            }
        }

        return true;
    }

    @Override
    public boolean onRightClick(int x, int y) {
        hidden = false;

        return true;
    }

    @Override
    public boolean onClickUp() {
        return true;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        currentX = x;
        currentY = y;

        for (MenuItem menuItem: menuItems.values()) {
            menuItem.setMouseWithin(menuItem.mouseWithin(0,0, x, y));
        }
        return true;
    }

    public void renderMenu(Batch batch) {

        for (LeftSideUpgradeMenuItem menuItem : menuItems.values()) {
            Sprite sprite = menuItem.getSprite(menuItem.getX(), menuItem.getY());
            sprite.setScale((float)((parent.getScreenWidth()*0.08)/sprite.getWidth()));
            sprite.draw(batch);
        }
    }

    @Override
    public boolean onMouseDragged(int x, int y) {
        return true;
    }
}
