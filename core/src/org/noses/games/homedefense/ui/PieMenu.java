package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.tower.*;

import java.util.HashMap;

public class PieMenu implements MouseHandler {

    @Getter
    boolean hidden;

    private int clickX;
    private int clickY;

    private int dragX;
    private int dragY;

    HashMap<String, PieMenuItem> pieMenuItems; // normal, glow, grey, greyglow

    MapScreen parent;

    public PieMenu(MapScreen parent) {
        this.parent = parent;
        hidden = true;

        pieMenuItems = new HashMap<>();

        int spiteWidth = 64;
        int spiteHeight = 64;

        PieMenuItem pieMenuItem = new PieMenuItem(parent, getX(Math.PI), getY(Math.PI), spiteWidth, spiteHeight, "rifle", new RifleTower.RifleTowerFactor());
        pieMenuItems.put(pieMenuItem.getSpriteName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(parent, getX(Math.PI*.75), getY(Math.PI*.75), spiteWidth, spiteHeight, "laser", new LaserTower.LaserTowerFactory());
        pieMenuItems.put(pieMenuItem.getSpriteName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(parent, getX(Math.PI/2), getY(Math.PI/2), spiteWidth, spiteHeight, "bomber", new BomberTower.BomberTowerFactory());
        pieMenuItems.put(pieMenuItem.getSpriteName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(parent, getX(Math.PI*.25), getY(Math.PI*.25), spiteWidth, spiteHeight, "factory", new FactoryTower.FactoryTowerFactory());
        pieMenuItems.put(pieMenuItem.getSpriteName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(parent, getX(0), getY(0), spiteWidth, spiteHeight, "broadcast", new BroadcastTower.BroadcastTowerFactory());
        pieMenuItems.put(pieMenuItem.getSpriteName(), pieMenuItem);
    }

    private int getX(double radians) {
        return (int)(Math.cos(radians)*parent.getScreenWidth()*.08);
    }

    private int getY(double radians) {
        return (int)(Math.sin(radians)*parent.getScreenWidth()*.08);
    }

    @Override
    public int getZ() {
        return 10;
    }

    @Override
    public boolean onClick(int x, int y) {
        return true;
    }

    @Override
    public boolean onRightClick(int x, int y) {
        return true;
    }

    @Override
    public boolean onClickUp(int x, int y) {
        hidden = true;

        for (PieMenuItem pieMenuItem: pieMenuItems.values()) {
            pieMenuItem.setCloseToOthers(pieMenuItem.closeToOtherTowers(x, y));
        }

        for (PieMenuItem pieMenuItem : pieMenuItems.values()) {
            if (pieMenuItem.mouseWithin(clickX, clickY, dragX, dragY)) {

                if (pieMenuItem.isAllowedToSelect(clickX, clickY)) {
                    parent.addTower(pieMenuItem.getTower(parent.convertXToLong(clickX), parent.convertYToLat(parent.getScreenHeight() - clickY)));
                }
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    public void renderMenu(Batch batch) {
        for (PieMenuItem pieMenuItem : pieMenuItems.values()) {
            Sprite sprite = pieMenuItem.getSprite(clickX, clickY);
            sprite.setScale((float)((parent.getScreenWidth()*0.08)/sprite.getWidth()));
            sprite.draw(batch);
        }
    }

    @Override
    public boolean onMouseDragged(int x, int y) {
        dragX = x;
        dragY = y;

        for (PieMenuItem pieMenuItem : pieMenuItems.values()) {
            pieMenuItem.setMouseWithin(pieMenuItem.mouseWithin(clickX,clickY, x,y));
        }
        return true;
    }
}
