package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.noses.games.homedefense.game.BattleScreen;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;

public class SpeedButton implements MouseHandler {

    int x;
    int y;

    protected TextureRegion[][] icons;

    BattleScreen parent;

    public SpeedButton(BattleScreen parent, int x, int y) {
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
    public int getZ() {
        return 20;
    }

    @Override
    public boolean onClick(int x, int y) {
        return true;
    }

    @Override
    public boolean onRightClick(int x, int y) {
        return true;
    }

    @Override
    public boolean onClickUp(int x, int y) {
        if (isWithinBounds(x, parent.getScreenHeight() - y)) {
            parent.speedUp();
            return false;
        }
        return true;
    }

    @Override
    public boolean onMouseDragged(int x, int y) {
        return true;
    }

    private boolean isWithinBounds(int clickX, int clickY) {

        Sprite sprite = getSprite();
        int width = (int) (sprite.getWidth() * sprite.getScaleX());
        int height = (int) (sprite.getHeight() * sprite.getScaleY());

        int upperLeftX = x- (width / 2);
        int upperLeftY = y - (height / 2);

        int lowerRightX = upperLeftX + width;
        int lowerRightY = upperLeftY + height;

        return parent.isPointWithinBounds(new Point(clickX, clickY),
                new Point(upperLeftX, upperLeftY),
                new Point(lowerRightX, lowerRightY));
    }

    public double getScale() {
        return 0.03;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    public void render(Batch batch) {
        Sprite sprite = getSprite();
        sprite.draw(batch);
    }

    private Sprite getSprite() {
        Sprite sprite = new Sprite(getIcon(parent.getSpeedMultiplier()));
        sprite.setCenterX(x);
        sprite.setCenterY(y);
        sprite.setScale((float)((parent.getScreenWidth()*getScale())/sprite.getWidth()));
        return sprite;
    }
}
