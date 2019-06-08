package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.audio.Sound;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.Animation;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.game.PhysicalObject;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;

public abstract class Enemy extends Animation implements ClockTickHandler, PhysicalObject {

    @Getter
    private int health;

    private boolean killed = false;

    Sound hitSound;

    protected Enemy(MapScreen parent, String spriteFilename, Sound hitSound, int tileWidth, int tileHeight, double scale, int startingHealth) {
        super(parent, spriteFilename, tileWidth, tileHeight, scale, true);
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
        this.killed = true;
        parent.addMoney(getValue());
    }

    public boolean isKilled() {
        return killed;
    }

    public abstract int getDamage();

    public abstract int getValue();

    public abstract void clockTick(double delta);

    public abstract Point getLocation();

    public Rectangle getBoundingBox() {
        double halfWidth = HomeDefenseGame.ONE_PIXEL_IN_LATLON * tileWidth / 2;
        double halfHeight = HomeDefenseGame.ONE_PIXEL_IN_LATLON * tileHeight / 2;

        Rectangle boundingBox = new Rectangle(getLocation().getLatitude()-halfWidth,
                getLocation().getLongitude()-halfHeight,
                getLocation().getLatitude()+halfWidth,
                getLocation().getLongitude()+halfHeight);
        //System.out.println ("boundingBox="+boundingBox+" height="+tileHeight);
        return boundingBox;
    }
}