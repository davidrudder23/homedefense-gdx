package org.noses.games.homedefense.geometry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
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
