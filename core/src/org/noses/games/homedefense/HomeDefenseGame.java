package org.noses.games.homedefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Getter;
import org.noses.games.homedefense.game.*;
import org.noses.games.homedefense.geolocation.Geolocator;
import org.noses.games.homedefense.geometry.Point;

public class HomeDefenseGame extends ApplicationAdapter {
    // TODO: calculate this based on current lat/long, not just hardcoded to Denver

    //
    public static double ONE_PIXEL_IN_LATLON = 0;

    // This is how far you move in 1 second, going 1 mph, in terms of latitude and longitude, in Denver
    public static double LATLON_MOVED_IN_1s_1mph = 0.000004901f;

    SpriteBatch batch;

    Screen currentScreen;

    Geolocator geolocator;

    @Getter
    Configuration configuration;

    public HomeDefenseGame(Geolocator geolocator, Configuration configuration) {
        this.geolocator = geolocator;
        this.configuration = configuration;
    }

    public Point getGeolocation() {
        return geolocator.getGeolocation();
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        //currentScreen = new MapScreen(this);
        currentScreen = new MainScreen(this);
    }

    public void startGame(Point location) {
        currentScreen = new MapScreen(this, location);
    }

    public void die() {
        currentScreen = new DeathScreen(this);
    }

    public void endGame() {
        Gdx.app.exit();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.25f, 0.95f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentScreen.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        /*int originalWidth = Gdx.graphics.getWidth();
        int originalHeight = Gdx.graphics.getHeight();

        double xRatio = width / originalWidth;
        double yRatio = height / originalHeight;

        for (Way way : map.getWays()) {
            for (Node node : way.getNodes()) {
                node.setX((int) ((double) node.getLatitude() * xRatio));
                node.setY((int) ((double) node.getLongitude() * yRatio));
            }
        }*/
    }

    public int getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getScreenHeight() {
        return Gdx.graphics.getHeight();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
