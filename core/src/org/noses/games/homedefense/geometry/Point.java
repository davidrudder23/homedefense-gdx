package org.noses.games.homedefense.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Point {
    int x;
    int y;

    @Override
    public String toString() {
        return "Point ("+x+"x"+y+")";
    }
}
