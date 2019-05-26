package org.noses.games.homedefense.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Data;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.tower.Tower;
import org.noses.games.homedefense.tower.TowerFactory;

import java.util.List;

@Data
public class PieMenuItem {

    int x;
    int y;
    int width;
    int height;

    String towerName;
    Sprite[] towerSprites;

    TowerFactory towerFactory;
    HomeDefenseGame parent;

    public PieMenuItem(HomeDefenseGame parent, int x, int y, int width, int height, String towerName, TowerFactory towerFactory) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.towerName = towerName;
        this.towerFactory = towerFactory;

        Texture textureNormal = new Texture("tower/" + towerName + ".png");
        Texture textureGlow = new Texture("tower/" + towerName + "_glow.png");
        Texture textureGrey = new Texture("tower/" + towerName + "_grey.png");
        Texture textureGreyGlow = new Texture("tower/" + towerName + "_grey_glow.png");

        towerSprites = new Sprite[4];
        towerSprites[0] = new Sprite(textureNormal);
        towerSprites[1] = new Sprite(textureGlow);
        towerSprites[2] = new Sprite(textureGrey);
        towerSprites[3] = new Sprite(textureGreyGlow);
    }

    public Tower getTower(double longitude,double latitude) {
        return towerFactory.createTower(parent, longitude, latitude);
    }

    public Sprite getSprite(int clickX, int clickY, int dragX, int dragY) {
        Sprite sprite = null;
        if (mouseWithin(clickX, clickY, dragX, dragY)) {
            if (!isAllowedToBuild(clickX, clickY)) {
                sprite = getGreyGlowSprite();
            } else{
                sprite = getGlowSprite();
            }
        } else {
            if (!isAllowedToBuild(clickX, clickY)) {
                sprite = getGreySprite();
            } else{
                sprite = getNormalSprite();
            }
        }

        sprite.setCenterX(clickX+x+(width/2));
        sprite.setCenterY(Gdx.graphics.getHeight()-clickY+y+(height/2));
        sprite.setScale(width/sprite.getWidth());

        //System.out.println("Showing pieMenuItem "+getTowerName()+" at "+(clickX+x+(width/2))+"x"+(clickY+y+(height/2))+" scale="+sprite.getScaleX());

        return sprite;
    }

    public boolean isAllowedToBuild(int clickX, int clickY) {
        return canAffordIt() && !closeToOtherTowers(clickX, clickY);
    }

    public boolean canAffordIt() {
        return parent.getMoney()>=towerFactory.createTower(parent, 0, 0).getCost();
    }

    private boolean closeToOtherTowers(int clickX, int clickY) {
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
                ((Gdx.graphics.getHeight()-dragY) >= ((Gdx.graphics.getHeight()-clickY)+y)) &&
                ((Gdx.graphics.getHeight()-dragY) <=((Gdx.graphics.getHeight()-clickY)+y+height))) {
            return true;
        }
        return false;
    }

    private Sprite getNormalSprite() {
        return towerSprites[0];
    }

    private Sprite getGlowSprite() {
        return towerSprites[1];
    }

    private Sprite getGreySprite() {
        return towerSprites[2];
    }

    private Sprite getGreyGlowSprite() {
        return towerSprites[3];
    }

}
