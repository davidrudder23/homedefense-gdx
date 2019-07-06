package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Data;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.tower.Tower;
import org.noses.games.homedefense.tower.TowerFactory;

import java.util.List;

@Data
public class PieMenuItem extends MenuItem {

    TowerFactory towerFactory;

    boolean mouseWithin;
    boolean closeToOthers;

    public PieMenuItem(MapScreen parent, int x, int y, int width, int height, String towerName, TowerFactory towerFactory) {
        super (parent, x, y, width, height, "tower/"+towerName);
        this.towerFactory = towerFactory;
        this.closeToOthers = false;
    }

    public Tower getTower(double longitude,double latitude) {
        return towerFactory.createTower(parent, longitude, latitude);
    }

    @Override
    public boolean isAllowedToSelect(int clickX, int clickY) {
        return canAffordIt() && !closeToOthers;
    }

    public boolean canAffordIt() {
        return parent.getMoney()>=towerFactory.getCost();
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

    public boolean mouseWithin(int clickX, int clickY, int dragX, int dragY) {

        if ((dragX >= (clickX+x)) && (dragX<= (clickX+x+width)) &&
                ((parent.getScreenHeight()-dragY) >= ((parent.getScreenHeight()-clickY)+y)) &&
                ((parent.getScreenHeight()-dragY) <=((parent.getScreenHeight()-clickY)+y+height))) {
            return true;
        }
        return false;
    }
}
