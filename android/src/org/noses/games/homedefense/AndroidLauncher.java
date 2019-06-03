package org.noses.games.homedefense;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noses.games.homedefense.game.Configuration;
import org.noses.games.homedefense.geolocation.AndroidGeolocator;
import org.noses.games.homedefense.geolocation.IPAddressGeolocator;
import org.noses.games.homedefense.geolocation.NullGeolocator;

import java.io.File;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//        initialize(new HomeDefenseGame(new AndroidGeolocator(), gameConfig), config);
        initialize(new HomeDefenseGame(new IPAddressGeolocator(), gameConfig), config);
    }
}
