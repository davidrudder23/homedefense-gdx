package org.noses.games.homedefense.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.noses.games.homedefense.game.MapScreen;
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
    public void renderMenu(Batch batch) {
        super.renderMenu(batch);

        int radiusInPixels = 10;
        float longPerPixel = (parent.getMap().getEast() - parent.getMap().getWest()) / (float) Gdx.graphics.getWidth();

        for (LeftSideTowerMenuItem menuItem: menuItems.values()) {
            if (menuItem.mouseWithin) {
                Tower tower = menuItem.getTower(0,0);
                int radius = (int)(tower.minDistanceFromOtherTower()/longPerPixel);
                radiusInPixels = radius;
            }
        }

        batch.end();

        ShapeRenderer sr = new ShapeRenderer();
        sr.setColor(new Color(1, 1, 0, 0.5f));
        sr.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.circle(clickX, parent.getScreenHeight() - clickY, radiusInPixels);
        sr.end();


        for (Tower tower : parent.getTowers()) {
            int x = parent.convertLongToX(tower.getLocation().getLongitude());
            int y = parent.convertLatToY(tower.getLocation().getLatitude());

            int radius = (int)(tower.minDistanceFromOtherTower()/longPerPixel);


            sr = new ShapeRenderer();
            sr.setColor(new Color(1, 1, 0, 0.5f));
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.circle(x, parent.getScreenHeight() - y, radius);
            sr.end();
        }

        batch.begin();


    }

    @Override
    public boolean onClickUp(int x, int y) {
        
        if (hidden) {
            parent.hideMenus();

            clickX = x;
            clickY = y;

            // update close-to-others
            for (LeftSideTowerMenuItem menuItem: menuItems.values()) {
                menuItem.setCloseToOthers(menuItem.closeToOtherTowers(x, y));
            }
        } else {
            mouseMoved(x,y);
            for (LeftSideTowerMenuItem menuItem : menuItems.values()) {
                if (menuItem.isMouseWithin()) {
                    Tower tower = menuItem.getTower(parent.convertXToLong(clickX), parent.convertYToLat(parent.getScreenHeight() - clickY));
                    parent.addTower(tower);
                }
            }
        }

        hidden = !hidden;

        return true;
    }


}
