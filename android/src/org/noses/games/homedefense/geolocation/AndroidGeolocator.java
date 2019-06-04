package org.noses.games.homedefense.geolocation;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import org.noses.games.homedefense.geometry.Point;

public class AndroidGeolocator extends Activity implements Geolocator {
    Point geoLocation;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        try {
            Location location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            geoLocation = new Point(location.getLatitude(), location.getLongitude());
        } catch (SecurityException exc) {
        }

    }

    @Override
    public Point getGeolocation() {

        return geoLocation;
    }
}
