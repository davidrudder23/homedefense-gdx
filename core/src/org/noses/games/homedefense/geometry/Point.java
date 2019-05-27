package org.noses.games.homedefense.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Point {
    double latitude;
    double longitude;

    @Override
    public String toString() {
        return "Point ("+ latitude +"x"+ longitude +")";
    }

    public double getDistanceFrom(Point other) {
        double longitude = other.getLongitude() - getLongitude();
        longitude *= longitude;

        double latitude = other.getLatitude() - getLatitude();
        latitude *= latitude;

        return Math.sqrt(longitude+latitude);
    }
}
