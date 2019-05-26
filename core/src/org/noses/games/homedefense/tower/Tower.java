package org.noses.games.homedefense.tower;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.PhysicalObject;

public interface Tower extends ClockTickHandler, PhysicalObject {

    public TextureRegion getFrameTextureRegion();
}

