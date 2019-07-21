package org.noses.games.homedefense.geometry;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Rectangle {
    Point upperLeft;
    Point lowerRight;

    public Rectangle(double left, double top, double right, double bottom) {
        upperLeft = new Point(left, top);
        lowerRight = new Point(right, bottom);
    }

    public boolean doBoundsOverlap(Rectangle rectangle) {
        return (isInBoundingBox(rectangle.upperLeft) || isInBoundingBox((rectangle.lowerRight))) ||
                (rectangle.isInBoundingBox(upperLeft) || (rectangle.isInBoundingBox(lowerRight)) );
    }

    public boolean isInBoundingBox(Point point) {
        return (point.getLatitude() >= upperLeft.getLatitude()) &&
                (point.getLongitude() >= upperLeft.getLongitude()) &&
                (point.getLatitude() <= lowerRight.getLatitude()) &&
                (point.getLongitude() <= lowerRight.getLongitude());
    }

    public double getDiagonalSize() {
        double size = (getUpperLeft().getLatitude() - getLowerRight().getLatitude())*(getUpperLeft().getLatitude() - getLowerRight().getLatitude());
        size += (getUpperLeft().getLongitude() - getLowerRight().getLongitude()) * (getUpperLeft().getLongitude() - getLowerRight().getLongitude());
        size = Math.sqrt(size);
        return size;
    }

    @Override
    public String toString() {
        String string = upperLeft.toString()+" through "+lowerRight.toString();
        return string;
    }
}
