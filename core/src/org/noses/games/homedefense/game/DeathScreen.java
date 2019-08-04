package org.noses.games.homedefense.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.noses.games.homedefense.HomeDefenseGame;

public class DeathScreen extends Screen {
    HomeDefenseGame parent;

    Stage stage;

    Image backgroundImage;

    Button endButton;


    public DeathScreen(HomeDefenseGame parent) {
        this.parent = parent;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Texture backgroundTexture = new Texture("died_screen.png");

        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setX(0);
        backgroundImage.setY(0);
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);

        Skin skin = new Skin(Gdx.files.internal("skin/comic/skin/comic-ui.json"));

        endButton = new TextButton("Quit", skin);

        endButton.setX((float)(stage.getWidth()*.8));
        endButton.setY((float)(stage.getHeight()*.1));
        endButton.setScale((float)(parent.getScreenWidth()*.5)/ endButton.getWidth());
        stage.addActor(endButton);

        endButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startButtonClicked();
            }
        });
    }

    public void startButtonClicked() {
        parent.endGame();
    }


    @Override
    public void render(Batch batch) {
        stage.draw();
    }
}
