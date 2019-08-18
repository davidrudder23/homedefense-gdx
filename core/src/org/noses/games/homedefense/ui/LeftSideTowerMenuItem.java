package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.tower.Tower;
import org.noses.games.homedefense.tower.TowerFactory;

import java.util.List;

public class LeftSideTowerMenuItem extends MenuItem {

    TowerFactory towerFactory;

    @Getter
    @Setter
    boolean mouseWithin;

    @Getter
    @Setter
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

        return (moveX >= x) && (moveX <= (x + width)) &&
                (adjustedY >= y) && (adjustedY <= (y + height));
    }

    public Tower getTower(double longitude,double latitude) {
        return towerFactory.createTower(parent, longitude, latitude);
    }

    public boolean closeToOtherTowers(int clickX, int clickY) {
        List<Tower> towers = parent.getTowers();

        double longitude = parent.convertXToLong(clickX);
        double latitude = parent.convertYToLat(parent.getScreenHeight()-clickY);

        for (Tower tower: towers) {
            double minDistanceOtherSquared = tower.minDistanceFromOtherTower()*tower.minDistanceFromOtherTower();
            double minDistanceThisSquared = getTower(longitude, latitude).minDistanceFromOtherTower()*getTower(longitude, latitude).minDistanceFromOtherTower();
            double actualDistanceSquared = ((tower.getLongitude()-longitude)*(tower.getLongitude()-longitude)) +
                    ((tower.getLatitude()-latitude)*(tower.getLatitude()-latitude));

            if (actualDistanceSquared < minDistanceOtherSquared) {
                return true;
            }
            if (actualDistanceSquared < minDistanceThisSquared) {
                return true;
            }
        }

        return false;
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
