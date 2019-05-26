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

    HashMap<String, Sprite[]> towerSprites; // normal, glow, grey, greyglow

    String[] towerNames = {
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

        towerSprites = new HashMap<>();

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
            towerSprites.put(towerNames[i], towerSpriteArray);
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
        System.out.println("Pie Menu un-clicked at "+dragX+"x"+dragY);
        parent.addTower(new RifleTower(parent, parent.convertXToLong(dragX), parent.convertYToLat(parent.getScreenHeight()-dragY)));
    }

    public void renderMenu(Batch batch) {
        int height = Gdx.graphics.getHeight();

        // first pass - do this very manually
        // TODO - make this more flexible, once you figure out what's needed
        Sprite sprite = null;

        if (mouseWithin(clickX-66, clickY - 16, clickX - 32, clickY + 16)) {
            sprite = towerSprites.get("bomber")[1];
        } else {
            sprite = towerSprites.get("bomber")[0];
        }
        sprite.setScale(32.0f/sprite.getRegionWidth());
        sprite.setCenterX(clickX - 50);
        sprite.setCenterY(height - clickY);
        sprite.draw(batch);

        if (mouseWithin(clickX-45, clickY - 45, clickX - 13, clickY - 13)) {
            sprite = towerSprites.get("broadcast")[1];
        } else {
            sprite = towerSprites.get("broadcast")[0];
        }
        sprite.setScale(32.0f/sprite.getRegionWidth());
        sprite.setCenterX(clickX - 29);
        sprite.setCenterY(height - (clickY-29));
        sprite.draw(batch);

        if (mouseWithin(clickX-16, clickY - 66, clickX + 16, clickY - 34)) {
            sprite = towerSprites.get("rifle")[1];
        } else {
            sprite = towerSprites.get("rifle")[0];
        }
        sprite.setScale(32.0f/sprite.getRegionWidth());
        sprite.setCenterX(clickX);
        sprite.setCenterY(height - (clickY-50));
        sprite.draw(batch);

        if (mouseWithin(clickX + 13, clickY - 45, clickX + 45, clickY - 13)) {
            sprite = towerSprites.get("factory")[1];
        } else {
            sprite = towerSprites.get("factory")[0];
        }
        sprite.setScale(32.0f/sprite.getRegionWidth());
        sprite.setCenterX(clickX + 29);
        sprite.setCenterY(height - (clickY-29));
        sprite.draw(batch);

        if (mouseWithin(clickX + 34, clickY - 16, clickX + 66, clickY + 16)) {
            sprite = towerSprites.get("laser")[1];
        } else {
            sprite = towerSprites.get("laser")[0];
        }
        sprite.setScale(32.0f/sprite.getRegionWidth());
        sprite.setCenterX(clickX + 50);
        sprite.setCenterY(height - clickY);
        sprite.draw(batch);

    }

    private boolean mouseWithin(int x, int y, int xp, int yp) {
        if ((dragX >= x) && (dragX <= xp) &&
                (dragY >=y) && (dragY <=yp)) {
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
