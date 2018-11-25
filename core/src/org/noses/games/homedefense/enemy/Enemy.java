package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import org.noses.games.homedefense.HomeDefenseGame;

import com.badlogic.gdx.utils.Timer;

@Data
public abstract class Enemy {

    private int health;

    TextureRegion[][] animation;
    HomeDefenseGame parent;
    int frame = 0;

    Timer.Task timer;

    protected Enemy(HomeDefenseGame parent, String spriteFilename, int tileWidth, int tileHeight) {
        this.parent = parent;

        Texture avatarAnimationSheet = new Texture(spriteFilename);
        animation = TextureRegion.split(avatarAnimationSheet, tileWidth, tileHeight);

        timer = Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               frame++;
                               if (frame >= animation[0].length)
                                   frame = 0;
                           }
                       }
                , 0f, 1 / 10.0f);
    }

    public void kill() {
        timer.cancel();;
    }
}
