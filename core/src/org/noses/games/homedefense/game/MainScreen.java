package org.noses.games.homedefense.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.noses.games.homedefense.HomeDefenseGame;

public class MainScreen extends Screen {
    HomeDefenseGame parent;

    Stage stage;

    Image backgroundImage;

    Button startButton;

    public MainScreen(HomeDefenseGame parent) {
        this.parent = parent;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Texture backgroundTexture = new Texture("background.png");

        backgroundImage = new Image (backgroundTexture);
        backgroundImage.setX(0);
        backgroundImage.setY(0);
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);

        Skin skin = new Skin();

        BitmapFont font = new BitmapFont();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;

        startButton = new TextButton("Start", textButtonStyle);

        startButton.setX((float)(stage.getWidth()*.8));
        startButton.setY((float)(stage.getHeight()*.1));
        startButton.setScale((float)(parent.getScreenWidth()*.5)/startButton.getWidth());
        stage.addActor(startButton);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startButtonClicked();
            }
        });
    }

    public void startButtonClicked() {
        parent.startGame();
    }


    @Override
    public void render(Batch batch) {
        stage.draw();
    }
}
