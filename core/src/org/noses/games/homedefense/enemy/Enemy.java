package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import org.noses.games.homedefense.HomeDefenseGame;

import com.badlogic.gdx.utils.Timer;
import org.noses.games.homedefense.game.ClockTickHandler;

import java.awt.*;

@Data
public abstract class Enemy extends Animation implements ClockTickHandler {

    private int health;

    private boolean killed = false;

    protected Enemy(HomeDefenseGame parent, String spriteFilename, int tileWidth, int tileHeight) {
        super(parent, spriteFilename, tileWidth, tileHeight);
    }

    public void kill() {
        timer.cancel();
        this.killed = true;
    }

    public abstract void clockTick(float delta);

    public abstract Point getLocation();

    public abstract int getWidth();

    public abstract int getHeight();
}