package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.noses.games.homedefense.game.MapScreen;

public class SpeedButton implements MouseHandler{

    int x;
    int y;

    protected TextureRegion[][] icons;

    MapScreen parent;

    public SpeedButton(MapScreen parent, int x, int y) {
        this.parent = parent;
        this.x = x;
        this.y = y;

        Texture avatarAnimationSheet = new Texture("speed.png");
        icons = TextureRegion.split(avatarAnimationSheet, 32, 32);
    }

    public TextureRegion getIcon(int speed) {
        return icons[0][speed - 1];
    }

    @Override
    public void onClick(int x, int y) {

        if (isClicked(x, parent.getScreenHeight()-y)) {
            parent.speedUp();
        }

    }

    @Override
    public void onRightClick(int x, int y) {

    }

    @Override
    public void onClickUp() {

    }

    @Override
    public void onMouseDragged(int x, int y) {

    }

    public boolean isClicked(int x, int y) {
        return (x >= this.x) && (x <= this.x + 32) &&
                (y >= this.y) && (y <= this.y + 32);
    }

    public void render(Batch batch) {
        batch.draw(getIcon(parent.getSpeedMultiplier()), x, y);
    }
}
