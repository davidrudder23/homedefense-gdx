package org.noses.games.homedefense.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.Configuration;
import org.noses.games.homedefense.geolocation.IPAddressGeolocator;

import java.io.File;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        Configuration gameConfig = new Configuration();

        try {
            File configFile = new File(System.getProperty("user.home") +
                    File.separator +
                    ".HomeDefense" +
                    File.separator +
                    "desktop.config");

            ObjectMapper objectMapper = new ObjectMapper();
            gameConfig = objectMapper.readValue(configFile, Configuration.class);
        } catch (Exception anyExc) {
            anyExc.printStackTrace();
        }

        config.width = gameConfig.getWidth();
        config.height = gameConfig.getHeight();

        // TODO Get api key from props
        IPAddressGeolocator geolocator = new IPAddressGeolocator("00a4da2c55a1d6b04c9dc8abe8a9474d");
        HomeDefenseGame game = new HomeDefenseGame(geolocator, gameConfig);

        new LwjglApplication(game, config);
    }
}
