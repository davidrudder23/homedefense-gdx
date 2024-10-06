package org.noses.games.homedefense.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;

@Data
public final class Animation {
    protected int frameNumber = 0;
    protected TextureRegion[][] animation;

    protected int tileWidth;
    protected int tileHeight;

    boolean loop;

    @Getter
    double scale;

    Actor actor;

    static HashMap<String, Texture> spriteCache;

    public Animation(Actor actor, String spriteFilename, int tileWidth, int tileHeight, double scale, boolean loop) {

        if (spriteCache == null) {
            spriteCache = new HashMap<>();
        }

        this.loop = loop;
        this.scale = scale;

        Texture avatarAnimationSheet = spriteCache.get(spriteFilename);
        if (avatarAnimationSheet == null) {
            avatarAnimationSheet = new Texture(spriteFilename);
            spriteCache.put(spriteFilename, avatarAnimationSheet);
        }

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
        frameNumber = 0;
        actor.finishState();
    }

    public void clockTick(double delta) {
        advanceAnimation();
    }

    protected void advanceAnimation() {
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
