package org.noses.games.homedefense.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import lombok.Getter;

@Data
public final class Animation implements ClockTickHandler {
    protected int frameNumber = 0;
    protected TextureRegion[][] animation;

    protected int tileWidth;
    protected int tileHeight;

    boolean killed;
    boolean loop;

    @Getter
    double scale;

    Actor actor;

    public Animation(Actor actor, String spriteFilename, int tileWidth, int tileHeight, double scale, boolean loop) {

        killed = false;
        this.loop = loop;
        this.scale = scale;

        Texture avatarAnimationSheet = new Texture(spriteFilename);
        if (tileHeight <= 0) {
            tileHeight = avatarAnimationSheet.getHeight();
        }

        if (tileWidth <= 0) {
            tileWidth = avatarAnimationSheet.getWidth();
        }

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        animation = TextureRegion.split(avatarAnimationSheet, tileWidth, tileHeight);

        this.actor = actor;

    }

    public void kill() {
        killed = true;
        actor.advanceToNextState();
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
                frameNumber = 0;
                kill();
            }
        }

    }

    public TextureRegion getFrameTextureRegion() {
        return animation[0][frameNumber];
    }

}
