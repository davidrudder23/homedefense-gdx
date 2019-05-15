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

        if (((Node) other).getLat() != getLat()) {
            return false;
        }

        return true;
    }

    public int distanceFrom(Node other) {
        float lengthLat = other.getLat() - getLat();
        float lengthLon = other.getLon() - getLon();
        int totalLength = (int) (Math.sqrt((lengthLat * lengthLat) + (lengthLon * lengthLon)));

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
