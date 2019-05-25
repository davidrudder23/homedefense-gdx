package org.noses.games.homedefense.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PieMenu implements MouseHandler {

    @Getter
    boolean hidden;

    private int clickX;
    private int clickY;

    private int dragX;
    private int dragY;

    List<Sprite[]> towerSprites; // normal, glow, grey, greyglow

    String[] towerNames = {
            "bomber",
            "broadcast",
            "factory",
            "laser",
            "rifle"
    };

    public PieMenu() {
        hidden = true;

        towerSprites = new ArrayList<>();

        for (int i = 0; i < towerNames.length; i++) {
            Texture textureNormal = new Texture("tower/"+towerNames[i]+".png");
            Texture textureGlow = new Texture("tower/"+towerNames[i]+"_glow.png");
            Texture textureGrey = new Texture("tower/"+towerNames[i]+"_grey.png");
            Texture textureGreyGlow = new Texture("tower/"+towerNames[i]+"_grey_glow.png");
            Sprite[] towerSpriteArray = new Sprite[4];
            towerSpriteArray[0] = new Sprite(textureNormal);
            towerSpriteArray[1] = new Sprite(textureGlow);
            towerSpriteArray[2] = new Sprite(textureGrey);
            towerSpriteArray[3] = new Sprite(textureGreyGlow);
            towerSprites.add(towerSpriteArray);
        }
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
        System.out.println("Pie Menu un-clicked");
    }

    public void renderMenu(Batch batch) {
        for (Sprite[] spriteArray: towerSprites) {
            int type = 0;
            spriteArray[type].setScale(0.5f);
            spriteArray[type].setCenterX(dragX);
            spriteArray[type].setCenterY(Gdx.graphics.getHeight() - dragY);
            spriteArray[type].draw(batch);
        }
    }

    @Override
    public void onMouseDragged(int x, int y) {
        dragX = x;
        dragY = y;
    }
}
