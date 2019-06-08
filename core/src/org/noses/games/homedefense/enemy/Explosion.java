package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.game.Animation;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;

public class Explosion extends Animation implements ClockTickHandler {

    protected Explosion(MapScreen parent, String spriteFilename, int tileWidth, int tileHeight) {
        super(parent, "explosion.png", tileWidth, tileHeight, 0.02, true);
    }

    public void clockTick(double delta) {

    }

    @Override
    public boolean isKilled() {
        return false;
    }
}
