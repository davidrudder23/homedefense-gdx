package org.noses.games.homedefense.client;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    int x;
    int y;

    @Getter
    float lat;

    @Getter
    float lon;

    long id;

    int order;

    @Getter
    @Setter
    double progress;

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Node)) {
            return false;
        }

        Node otherNode = (Node) other;

        if (otherNode.getLon() != getLon()) {
            return false;
        }

        return ((Node) other).getLat() == getLat();
    }

    public double distanceFrom(Node other) {
        double lengthLat = other.getLat() - getLat();
        double lengthLon = other.getLon() - getLon();
        double totalLength = (float) (Math.sqrt((lengthLat * lengthLat) + (lengthLon * lengthLon)));

        return Math.abs(totalLength);
    }

    public double distanceFrom(double longitude, double latitude) {
        double lengthLat = latitude- getLat();
        double lengthLon = longitude - getLon();
        double totalLength = Math.sqrt((lengthLat * lengthLat) + (lengthLon * lengthLon));

        return Math.abs(totalLength);
    }

    @Override
    public String toString() {
        return "latlong=(" + getLat() + "," + getLon() + ")";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
