package org.noses.games.homedefense.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Point {
    float latitude;
    float longitude;

    @Override
    public String toString() {
        return "Point ("+ latitude +"x"+ longitude +")";
    }
}
