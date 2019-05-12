package org.noses.games.homedefense.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Rectangle {
    Point upperLeft;
    Point lowerRight;

    public Rectangle(int left, int top, int right, int bottom) {
        upperLeft = new Point(left, top);
        lowerRight = new Point(right, bottom);
    }

    public boolean isInBoundingBox(Point point) {
        if ((point.getX() >= upperLeft.getX()) &&
                (point.getY() >= upperLeft.getY()) &&
                (point.getX() <= lowerRight.getX()) &&
                (point.getY() <= lowerRight.getY())) {
            return true;
        }
        return false;
    }
}
