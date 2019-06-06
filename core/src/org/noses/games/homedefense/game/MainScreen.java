package org.noses.games.homedefense.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Destination;
import org.noses.games.homedefense.client.MapClient;
import org.noses.games.homedefense.geometry.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainScreen extends Screen {
    HomeDefenseGame parent;

    Stage stage;

    Image backgroundImage;

    Button startButton;

    List<Destination> destinations;

    SelectBox<String> destinationSelect;

    Label destinationLabel;

    public MainScreen(HomeDefenseGame parent) {
        this.parent = parent;

        MapClient mapClient = new MapClient(parent.getConfiguration().getBaseURL());

        try {
            destinations = mapClient.getDestinations();
        } catch (IOException ioExc) {
            destinations = new ArrayList<>();
        }

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Texture backgroundTexture = new Texture("background.png");

        backgroundImage = new Image (backgroundTexture);
        backgroundImage.setX(0);
        backgroundImage.setY(0);
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);

        Skin skin = new Skin(Gdx.files.internal("skin/comic/skin/comic-ui.json"));

        startButton = new TextButton("Start", skin);

        startButton.setX((float)(stage.getWidth()*.6));
        startButton.setY((float)(stage.getHeight()*.1));
        startButton.setScale((float)(parent.getScreenWidth()*.5)/startButton.getWidth());
        stage.addActor(startButton);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startButtonClicked();
            }
        });

        destinationSelect = new SelectBox<String>(skin);

        Array<String> destinationNameArray = new Array<>();
        Point currentLocation = parent.getGeolocation();
        if (currentLocation != null) {
            destinations.add(new Destination("C", currentLocation.getLatitude(), currentLocation.getLongitude(), "Current Location", "Current Location"));
        }

        System.out.println("PPCY="+Gdx.graphics.getPpcY());

        for (Destination destination: destinations) {
            System.out.println(destination.getName()+" "+destination.getLat()+","+destination.getLon());
            destinationNameArray.add(destination.getName());
        }
        destinationSelect.setItems(destinationNameArray);

        if (currentLocation != null) {
            destinationSelect.setSelected("Current Location");
        } else {
            destinationSelect.setSelectedIndex(0);
        }

        destinationSelect.setX((float)(stage.getWidth()*.3));
        destinationSelect.setY((float)(stage.getHeight()*.1));
        destinationSelect.setWidth((float)(stage.getWidth() * .3));
        stage.addActor(destinationSelect);

        destinationLabel = new Label("Choose where to defend", skin);
        destinationLabel.setFontScale(1.8f);
        destinationLabel.setX((float)(stage.getWidth()*.3));
        destinationLabel.setY((float)(stage.getHeight()*.1)+destinationSelect.getHeight());
        destinationLabel.setWidth(destinationSelect.getWidth());
        destinationLabel.setHeight(destinationSelect.getHeight());
        System.out.println(destinationLabel.getX()+","+destinationLabel.getY()+" +"+destinationLabel.getWidth()+"+"+destinationLabel.getHeight());
        stage.addActor(destinationLabel);
    }

    public void startButtonClicked() {
        Point location = null;

        for (Destination destination: destinations) {
            if (destination.getName().equals(destinationSelect.getSelected())) {
                location = new Point(destination.getLat(), destination.getLon());
            }
        }

        if (location == null) {
            location = new Point(9.7392,-104.9874);
        }
        parent.startGame(location);
    }


    @Override
    public void render(Batch batch) {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    public double getPpcX() {
        return parent.getPpcX();
    }

    public double getPpcY() {
        return parent.getPpcY();
    }

}
