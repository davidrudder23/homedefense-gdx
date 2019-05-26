package org.noses.games.homedefense.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.tower.RifleTower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PieMenu implements MouseHandler {

    @Getter
    boolean hidden;

    private int clickX;
    private int clickY;

    private int dragX;
    private int dragY;

    HashMap<String, PieMenuItem> pieMenuItems; // normal, glow, grey, greyglow

    String[] towerNamesDELETE = {
            "bomber",
            "broadcast",
            "factory",
            "laser",
            "rifle"
    };

    HomeDefenseGame parent;

    public PieMenu(HomeDefenseGame parent) {
        this.parent = parent;
        hidden = true;

        pieMenuItems = new HashMap<>();

        int spiteWidth = 64;
        int spiteHeight = 64;

        PieMenuItem pieMenuItem = new PieMenuItem(-100, 0, spiteWidth, spiteHeight, "rifle");
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(-70, 70, spiteWidth, spiteHeight, "laser");
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(0, 100, spiteWidth, spiteHeight, "bomber");
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(70, 70, spiteWidth, spiteHeight, "factory");
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);

        pieMenuItem = new PieMenuItem(100, 0, spiteWidth, spiteHeight, "broadcast");
        pieMenuItems.put(pieMenuItem.getTowerName(), pieMenuItem);
    }

    @Override
    public void onClick(int x, int y) {
        hidden = false;
        clickX = x;
        clickY = y;

        dragX = x;
        dragY = y;

        System.out.println("Pie Menu clicked");
    }

    @Override
    public void onRightClick(int x, int y) {
        clickX = x;
        clickY = y;
    }

    @Override
    public void onClickUp() {
        hidden = true;
        System.out.println("Pie Menu un-clicked at " + dragX + "x" + dragY);

        for (PieMenuItem pieMenuItem: pieMenuItems.values()) {
            if (pieMenuItem.mouseWithin(clickX, clickY, dragX, dragY)) {
                System.out.println("Creating a "+pieMenuItem.getTowerName());
                parent.addTower(new RifleTower(parent, parent.convertXToLong(dragX), parent.convertYToLat(parent.getScreenHeight() - dragY)));
            }
        }
    }

    public void renderMenu(Batch batch) {
        for (PieMenuItem pieMenuItem: pieMenuItems.values()) {
            Sprite sprite = pieMenuItem.getSprite(clickX, clickY, dragX, dragY);
            sprite.draw(batch);
        }
    }

    private boolean mouseWithin(int x, int y, int xp, int yp) {
        if ((dragX >= x) && (dragX <= xp) &&
                (dragY >= y) && (dragY <= yp)) {
            return true;
        }
        return false;
    }

    @Override
    public void onMouseDragged(int x, int y) {
        dragX = x;
        dragY = y;
    }
}
