package org.noses.games.homedefense.player;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import org.noses.games.homedefense.enemy.Enemy;

@Data
public abstract class Shot {
    TextureRegion[][] animation;

    double power;

    public abstract boolean checkIfHit(Enemy enemy);
}
