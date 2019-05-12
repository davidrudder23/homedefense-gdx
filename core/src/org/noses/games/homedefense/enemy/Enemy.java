package org.noses.games.homedefense.enemy;

import lombok.Data;
import org.noses.games.homedefense.HomeDefenseGame;

import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;

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

    public Rectangle getBoundingBox() {
        int halfWidth = tileWidth/2;
        int halfHeight = tileHeight/2;

        Rectangle boundingBox = new Rectangle(getLocation().getX()-halfWidth,
                getLocation().getY()-halfHeight,
                getLocation().getX()+halfWidth,
                getLocation().getY()+halfHeight);
        //System.out.println ("boundingBox="+boundingBox+" height="+tileHeight);
        return boundingBox;
    }
}