package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.tower.Tower;
import org.noses.games.homedefense.tower.TowerUpgrader;

public class LeftSideUpgradeMenuItem extends MenuItem {

    TowerUpgrader towerUpgrader;

    @Getter
    @Setter
    boolean mouseWithin;
    boolean closeToOthers;

    public LeftSideUpgradeMenuItem(MapScreen parent, int x, int y, int width, int height, int level, String towerName, TowerUpgrader towerUpgrader) {
        super(parent, x, y, width, height, level, towerName);
        this.towerUpgrader = towerUpgrader;
        this.closeToOthers = false;
    }

    public void upgradeTower(Tower tower) {
        parent.subtractMoney(towerUpgrader.getCost());
        towerUpgrader.upgradeTower(tower);
    }

    @Override
    public boolean isAllowedToSelect(int clickX, int clickY) {
        return canAffordIt() && !closeToOthers;
    }

    public boolean canAffordIt() {
        return parent.getMoney() >= towerUpgrader.getCost();
    }

    @Override
    public boolean mouseWithin(int ignoreX, int ignoreY, int moveX, int moveY) {

        int adjustedY = parent.getScreenHeight() - moveY;

        return (moveX >= x) && (moveX <= (x + width)) &&
                (adjustedY >= y) && (adjustedY <= (y + height));
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
