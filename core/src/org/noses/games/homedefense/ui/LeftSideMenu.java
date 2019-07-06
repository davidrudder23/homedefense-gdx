package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.game.MapScreen;

import java.util.HashMap;

public abstract class LeftSideMenu implements MouseHandler {

    protected MapScreen parent;

    protected HashMap<String, LeftSideTowerMenuItem> menuItems; // normal, glow, grey, greyglow

    @Getter
    @Setter
    protected boolean hidden;

    protected int currentX;
    protected int currentY;

    public LeftSideMenu(MapScreen parent) {
        this.parent = parent;

        hidden = true;
    }

    protected int getX(int menuNum) {
        return 50;
    }

    protected int getY(int menuNum) {
        int sizeOfMenu = parent.getScreenHeight() / 10;
        int sizeOfPAdding = parent.getScreenHeight() / 50;
        int offset = 100;
        return offset + (sizeOfMenu + sizeOfPAdding) * menuNum;
    }

    public void renderMenu(Batch batch) {

        for (LeftSideTowerMenuItem menuItem : menuItems.values()) {
            Sprite sprite = menuItem.getSprite(menuItem.getX(), menuItem.getY());
            sprite.setScale((float) ((parent.getScreenWidth() * 0.08) / sprite.getWidth()));
            sprite.draw(batch);
        }
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

        for (MenuItem menuItem : menuItems.values()) {
            menuItem.setMouseWithin(menuItem.mouseWithin(0, 0, x, y));
        }
        return true;
    }

    @Override
    public boolean onMouseDragged(int x, int y) {
        return true;
    }

}
