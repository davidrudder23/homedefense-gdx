package org.noses.games.homedefense.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.Configuration;
import org.noses.games.homedefense.geolocation.IPAddressGeolocator;

import java.io.File;
import java.util.Properties;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        Configuration desktopConfig = new Configuration();

        try {
            File configFile = new File(System.getProperty("user.home") +
                    File.separator +
                    ".HomeDefense" +
                    File.separator +
                    "desktop.config");

            ObjectMapper objectMapper = new ObjectMapper();
            desktopConfig = objectMapper.readValue(configFile, Configuration.class);
        } catch (Exception anyExc) {
            anyExc.printStackTrace();
        }

        config.width = desktopConfig.getWidth();
        config.height = desktopConfig.getHeight();
        System.out.println("Base URL=" + desktopConfig.getBaseURL());

        // TODO Get api key from props
        new LwjglApplication(new HomeDefenseGame(new IPAddressGeolocator("00a4da2c55a1d6b04c9dc8abe8a9474d"), desktopConfig), config);
    }
}
