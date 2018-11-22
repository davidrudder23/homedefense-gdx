package org.noses.games.homedefense.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Node {
    int x;
    int y;

    float lat;
    float lon;

    long id;

    int order;

    double progress;

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Node)) {
            return false;
        }

        Node otherNode = (Node)other;

        if (otherNode.getLon() != getLon()) {
            return false;
        }

        if (((Node) other).getLat() != getLat()) {
            return false;
        }

        return true;
    }

    public int distanceFrom(Node other) {
        int lengthX = other.getX() - getX();
        int lengthY = other.getY() - getY();
        int totalLength = (int)(Math.sqrt((lengthX*lengthX)+(lengthY*lengthY)));

        return Math.abs(totalLength);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
