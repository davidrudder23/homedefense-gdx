package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.ClockTickHandler;

public class Explosion extends Animation implements ClockTickHandler {

    protected Explosion(HomeDefenseGame parent, String spriteFilename, int tileWidth, int tileHeight) {
        super(parent, "explosion.png", tileWidth, tileHeight);
    }

    public void clockTick(float delta) {

    }

}
