package org.noses.games.homedefense.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;

@Data
public class Animation implements ClockTickHandler {
    protected MapScreen parent;
    protected int frameNumber = 0;
    protected TextureRegion[][] animation;

    protected int tileWidth;
    protected int tileHeight;

    boolean killed;
    boolean loop;

    public Animation(MapScreen parent, String spriteFilename, int tileWidth, int tileHeight, boolean loop) {
        this.parent = parent;

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        killed = false;
        this.loop = loop;

        Texture avatarAnimationSheet = new Texture(spriteFilename);
        animation = TextureRegion.split(avatarAnimationSheet, tileWidth, tileHeight);
    }

    public void kill() {
        killed = true;
    }

    @Override
    public void clockTick(double delta) {
        if (killed) {
            return;
        }

        advanceAnimation();
    }

    protected void advanceAnimation() {
        if (killed) {
            return;
        }

        frameNumber++;
        if (frameNumber >= animation[0].length) {
            if (loop) {
                frameNumber = 0;
            } else {
                killed = true;
            }
        }

    }

    public TextureRegion getFrameTextureRegion() {
        return animation[0][frameNumber];
    }

}
