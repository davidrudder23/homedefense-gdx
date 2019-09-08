package org.noses.games.homedefense;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import lombok.Setter;
import org.noses.games.homedefense.game.*;
import org.noses.games.homedefense.geolocation.GeolocationListener;
import org.noses.games.homedefense.geolocation.Geolocator;
import org.noses.games.homedefense.geometry.Point;

public class HomeDefenseGame extends ApplicationAdapter {
    // TODO: calculate this based on current lat/long, not just hardcoded to Denver

    //
    public static double ONE_PIXEL_IN_LATLON = 0;

    // This is how far you move in 1 second, going 1 mph, in terms of latitude and longitude, in Denver
    public static double LATLON_MOVED_IN_1ms_1mph = 0.000004901f;

    SpriteBatch batch;

    Screen currentScreen;

    Geolocator geolocator;

    @Getter
    Configuration configuration;

    @Getter
    double ppcX;

    @Getter
    double ppcY;

    @Getter
    @Setter
    boolean isUsingCurrentAddress;

    public HomeDefenseGame(Geolocator geolocator, Configuration configuration) {
        this.geolocator = geolocator;
        this.configuration = configuration;

    }

    public Point getGeolocation() {
        return geolocator.getGeolocation();
    }

    public void addGeolocationListener(GeolocationListener listener) {
        geolocator.addListener(listener);
    }

    public boolean hasLiveGeolocation() {
        return isUsingCurrentAddress && geolocator.hasLiveGeolocation();
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        //currentScreen = new MapScreen(this);
        currentScreen = new MainScreen(this);
        this.ppcX = Gdx.graphics.getPpcX();
        this.ppcY = Gdx.graphics.getPpcY();
    }

    public void startGame(Point location) {
        currentScreen = new BattleScreen(this, location);
    }

    public void startExploration(Point location) {
        currentScreen = new ExplorationScreen(this, location);
        if (geolocator instanceof ClockTickHandler) {
            ((MapScreen)currentScreen).addClockTickHandler((ClockTickHandler) geolocator);
        }
    }

    public void die() {
        currentScreen = new DeathScreen(this);
    }

    public void win() {
        currentScreen = new WinScreen(this);
    }

    public void endGame() {
        Gdx.app.exit();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.803f, 0.690f, 0.323f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentScreen.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setProjectionMatrix(matrix);
    }

    public int getScreenWidth() {
        return Gdx.graphics.getWidth();
    }

    public int getScreenHeight() {
        return Gdx.graphics.getHeight();
    }

    public boolean isDebug() {
        return configuration.isDebug();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
