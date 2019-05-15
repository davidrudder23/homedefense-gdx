package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.audio.Sound;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;

import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;

public abstract class Enemy extends Animation implements ClockTickHandler {

    @Getter
    private int health;

    @Getter
    private boolean killed = false;

    Sound hitSound;

    protected Enemy(HomeDefenseGame parent, String spriteFilename, Sound hitSound, int tileWidth, int tileHeight, int startingHealth) {
        super(parent, spriteFilename, tileWidth, tileHeight);
        this.health = startingHealth;
        this.hitSound = hitSound;
    }

    public void hit(int damage) {
        health -= damage;
        if (health<=0) {
            kill();
        }

        hitSound.play();
    }

    public void kill() {
        timer.cancel();
        this.killed = true;
    }

    public abstract int getDamage();

    public abstract void clockTick(float delta);

    public abstract Point getLocation();

    public abstract int getWidth();

    public abstract int getHeight();

    public Rectangle getBoundingBox() {
        int halfWidth = tileWidth/2;
        int halfHeight = tileHeight/2;

        Rectangle boundingBox = new Rectangle(getLocation().getLatitude()-halfWidth,
                getLocation().getLongitude()-halfHeight,
                getLocation().getLatitude()+halfWidth,
                getLocation().getLongitude()+halfHeight);
        //System.out.println ("boundingBox="+boundingBox+" height="+tileHeight);
        return boundingBox;
    }
}