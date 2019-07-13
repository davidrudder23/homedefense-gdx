package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Data;
import org.noses.games.homedefense.game.MapScreen;

@Data
public abstract class MenuItem {

    int x;
    int y;
    int width;
    int height;

    String spriteName;
    Sprite[] itemSprites;

    MapScreen parent;

    boolean mouseWithin;

    public MenuItem(MapScreen parent, int x, int y, int width, int height, int level, String spriteName) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.spriteName = spriteName;
        this.mouseWithin = false;

        Texture textureNormal = new Texture(spriteName + "_lvl_"+level+".png");
        Texture textureGlow = new Texture(spriteName + "_glow_lvl_"+level+".png");
        Texture textureGrey = new Texture(spriteName + "_grey_lvl_"+level+".png");
        Texture textureGreyGlow = new Texture(spriteName + "_glow_grey_lvl_"+level+".png");

        itemSprites = new Sprite[4];
        itemSprites[0] = new Sprite(textureNormal);
        itemSprites[1] = new Sprite(textureGlow);
        itemSprites[2] = new Sprite(textureGrey);
        itemSprites[3] = new Sprite(textureGreyGlow);
    }

    public Sprite getSprite(int clickX, int clickY) {
        Sprite sprite = null;
        if (mouseWithin) {
            if (!isAllowedToSelect(clickX, clickY)) {
                sprite = getGreyGlowSprite();
            } else{
                sprite = getGlowSprite();
            }
        } else {
            if (!isAllowedToSelect(clickX, clickY)) {
                sprite = getGreySprite();
            } else{
                sprite = getNormalSprite();
            }
        }

        sprite.setCenterX(clickX+x+(width/2));
        sprite.setCenterY(parent.getScreenHeight()-clickY+y+(height/2));
        sprite.setScale(width/sprite.getWidth());

        //System.out.println("Showing pieMenuItem "+getSpriteName()+" at "+(clickX+x+(width/2))+"x"+(clickY+y+(height/2))+" scale="+sprite.getPpcX());

        return sprite;
    }

    public abstract boolean isAllowedToSelect(int clickX, int clickY);

    public boolean mouseWithin(int clickX, int clickY, int dragX, int dragY) {

        if ((dragX >= (clickX+x)) && (dragX<= (clickX+x+width)) &&
                ((parent.getScreenHeight()-dragY) >= ((parent.getScreenHeight()-clickY)+y)) &&
                ((parent.getScreenHeight()-dragY) <=((parent.getScreenHeight()-clickY)+y+height))) {
            return true;
        }
        return false;
    }

    protected Sprite getNormalSprite() {
        return itemSprites[0];
    }

    protected Sprite getGlowSprite() {
        return itemSprites[1];
    }

    protected Sprite getGreySprite() {
        return itemSprites[2];
    }

    protected Sprite getGreyGlowSprite() {
        return itemSprites[3];
    }

}
