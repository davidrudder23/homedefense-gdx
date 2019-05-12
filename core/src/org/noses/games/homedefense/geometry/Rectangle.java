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

    public boolean doBoundsOverlap(Rectangle rectangle) {
        return (isInBoundingBox(rectangle.upperLeft) || isInBoundingBox((rectangle.lowerRight))) ||
                (rectangle.isInBoundingBox(upperLeft) || (rectangle.isInBoundingBox(lowerRight)) );
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

    @Override
    public String toString() {
        String string = upperLeft.toString()+" through "+lowerRight.toString();
        return string;
    }
}
