package org.noses.games.homedefense;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noses.games.homedefense.game.Configuration;
import org.noses.games.homedefense.geolocation.AndroidGeolocator;
import org.noses.games.homedefense.geometry.Point;

import java.io.File;

public class AndroidLauncher extends AndroidApplication {
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int INITIAL_REQUEST = 1337;

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

        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            System.out.println ("NO PERMS");
        }

        AndroidGeolocator androidGeolocator = new AndroidGeolocator();

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            Location location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            androidGeolocator.setLive(true);
            if (location != null) {
                androidGeolocator.setGeoLocation(new Point(location.getLatitude(), location.getLongitude()));
            }
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new mylocationlistener(androidGeolocator);
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
        } catch (SecurityException exc) {
            exc.printStackTrace();
        }

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(new HomeDefenseGame(androidGeolocator, gameConfig), config);
    }
}

class mylocationlistener implements LocationListener {
    AndroidGeolocator androidGeolocator;

    public mylocationlistener(AndroidGeolocator androidGeolocator) {
        this.androidGeolocator = androidGeolocator;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("LOCATION CHANGED", location.getLatitude() + "");
            Log.d("LOCATION CHANGED", location.getLongitude() + "");

            androidGeolocator.setGeoLocation(new Point(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}

