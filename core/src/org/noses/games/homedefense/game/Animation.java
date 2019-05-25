package org.noses.games.homedefense.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;
import lombok.Data;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.ClockTickHandler;

@Data
public abstract class Animation implements ClockTickHandler {
    protected HomeDefenseGame parent;
    protected int frameNumber = 0;
    protected TextureRegion[][] animation;
    Timer.Task timer;

    protected int tileWidth;
    protected int tileHeight;

    protected Animation(HomeDefenseGame parent, String spriteFilename, int tileWidth, int tileHeight) {
        this.parent = parent;

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        Texture avatarAnimationSheet = new Texture(spriteFilename);
        animation = TextureRegion.split(avatarAnimationSheet, tileWidth, tileHeight);

        parent.addClockTickHandler(this);
    }

    public void kill() {

    }

    @Override
    public void clockTick(double delta) {

    }

    protected void advanceAnimation(float delay) {
        frameNumber++;
        if (frameNumber >= animation[0].length) {
            frameNumber = 0;
        }

    }

    public TextureRegion getFrameTextureRegion() {
        return animation[0][frameNumber];
    }

}
