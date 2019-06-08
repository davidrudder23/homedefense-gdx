package org.noses.games.homedefense.geolocation;

import org.noses.games.homedefense.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public abstract class Geolocator {

    List<GeolocationListener> listeners;

    public abstract Point getGeolocation();

    public abstract boolean hasLiveGeolocation();

    public void updateLocation() {
        getListeners().stream().forEach(l-> {
            l.updateLocation(getGeolocation());
        });
    }

    private List<GeolocationListener> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        return listeners;
    }

    public void addListener(GeolocationListener listener) {
        getListeners().add(listener);
    }
}
