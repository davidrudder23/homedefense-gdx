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
}
