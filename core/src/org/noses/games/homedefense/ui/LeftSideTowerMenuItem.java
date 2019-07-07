package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.tower.Tower;
import org.noses.games.homedefense.tower.TowerFactory;
import org.noses.games.homedefense.tower.TowerUpgrader;

public class LeftSideTowerMenuItem extends MenuItem {

    TowerFactory towerFactory;

    @Getter
    @Setter
    boolean mouseWithin;
    boolean closeToOthers;

    public LeftSideTowerMenuItem(MapScreen parent, int x, int y, int width, int height, String towerName, TowerFactory towerFactory) {
        super(parent, x, y, width, height, 1, towerName);
        this.towerFactory = towerFactory;
        this.closeToOthers = false;
    }

    @Override
    public boolean isAllowedToSelect(int clickX, int clickY) {
        return canAffordIt() && !closeToOthers;
    }

    public boolean canAffordIt() {
        return parent.getMoney() >= towerFactory.getCost();
    }

    @Override
    public boolean mouseWithin(int ignoreX, int ignoreY, int moveX, int moveY) {

        int adjustedY = parent.getScreenHeight() - moveY;

        if ((moveX >= x) && (moveX <= (x + width)) &&
                (adjustedY >= y) && (adjustedY <= (y + height))) {
            return true;
        }
        return false;
    }

    public Tower getTower(double longitude,double latitude) {
        parent.subtractMoney(towerFactory.getCost());
        return towerFactory.createTower(parent, longitude, latitude);
    }

    @Override
    public Sprite getSprite(int clickX, int clickY) {
        Sprite sprite = null;

        if (mouseWithin) {
            if (!isAllowedToSelect(clickX, clickY)) {
                sprite = getGreyGlowSprite();
            } else {
                sprite = getGlowSprite();
            }
        } else {
            if (!isAllowedToSelect(clickX, clickY)) {
                sprite = getGreySprite();
            } else {
                sprite = getNormalSprite();
            }
        }

        sprite.setCenterX(x+(width/2));
        sprite.setCenterY(y+(height/2));
        sprite.setScale(width / sprite.getWidth(), height/sprite.getHeight());

        return sprite;
    }
}
