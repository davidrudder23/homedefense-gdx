package org.noses.games.homedefense.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.noses.games.homedefense.HomeDefenseGame;

public class MainScreen extends Screen {
    HomeDefenseGame parent;

    Sprite backgroundImage;

    public MainScreen(HomeDefenseGame parent) {
        this.parent = parent;

        Texture backgroundTexture = new Texture("background.png");

        backgroundImage = new Sprite (backgroundTexture);
        backgroundImage.setX(0);
        backgroundImage.setY(0);
        backgroundImage.setScale(parent.getScreenWidth()/backgroundTexture.getWidth(), parent.getScreenHeight()/backgroundTexture.getHeight());

    }

    @Override
    public void render(Batch batch) {
        backgroundImage.draw(batch);

    }
}
