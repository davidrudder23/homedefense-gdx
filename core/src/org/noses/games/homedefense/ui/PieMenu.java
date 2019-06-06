package org.noses.games.homedefense.ui;

import com.badlogic.gdx.Gdx;
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

        PieMenuItem pieMenuItem = new PieMenuItem(parent, -60, 0, spiteWidth, spiteHeight, "rifle", new RifleTower.RifleTowerFactor());
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(parent, -40, 40, spiteWidth, spiteHeight, "laser", new LaserTower.LaserTowerFactory());
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(parent, 0, 60, spiteWidth, spiteHeight, "bomber", new BomberTower.BomberTowerFactory());
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(parent, 40, 40, spiteWidth, spiteHeight, "factory", new FactoryTower.FactoryTowerFactory());
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(parent, 60, 0, spiteWidth, spiteHeight, "broadcast", new BroadcastTower.BroadcastTowerFactory());
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);
    }

    @Override
    public boolean onClick(int x, int y) {
        hidden = false;
        clickX = x;
        clickY = y;

        dragX = x;
        dragY = y;

        for (PieMenuItem pieMenuItem: pieMenuItems.values()) {
            pieMenuItem.setCloseToOthers(pieMenuItem.closeToOtherTowers(x, y));
        }

        return true;
    }

    @Override
    public boolean onRightClick(int x, int y) {
        return true;
    }

    @Override
    public boolean onClickUp() {
        hidden = true;

        for (PieMenuItem pieMenuItem : pieMenuItems.values()) {
            if (pieMenuItem.mouseWithin(clickX, clickY, dragX, dragY)) {

                if (pieMenuItem.isAllowedToBuild(clickX, clickY)) {
                    parent.addTower(pieMenuItem.getTower(parent.convertXToLong(clickX), parent.convertYToLat(parent.getScreenHeight() - clickY)));
                }
            }
        }
        return true;
    }

    public void renderMenu(Batch batch) {
        for (PieMenuItem pieMenuItem : pieMenuItems.values()) {
            Sprite sprite = pieMenuItem.getSprite(clickX, clickY, dragX, dragY);
            sprite.setScale(Gdx.graphics.getPpcX()/sprite.getWidth(), Gdx.graphics.getPpcY()/sprite.getHeight());
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
