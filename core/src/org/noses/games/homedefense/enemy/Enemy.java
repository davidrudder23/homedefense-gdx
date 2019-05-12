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
        Rectangle boundingBox = new Rectangle(getLocation(),
                new Point(getLocation().getX()+tileWidth, getLocation().getY()+tileHeight));
        return boundingBox;
    }
}