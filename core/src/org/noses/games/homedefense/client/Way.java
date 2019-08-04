package org.noses.games.homedefense.client;

import com.badlogic.gdx.graphics.Color;
import lombok.Data;
import org.noses.games.homedefense.geometry.Point;

import java.util.ArrayList;
import java.util.List;

@Data
public class Way {
    String name;
    int lanes;
    int maxSpeed;
    String highway;

    Color color;

    List<Node> nodes;

    public Way() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public Color getColor() {
        if (color == null) {
            //setColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f));
            setColor(Color.WHITE);
        }

        return color;
    }

    public Point firstPoint() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        Node startNode = nodes.get(0);
        return new Point(startNode.getLat(), startNode.getLon());
    }

    public Point lastPoint() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        Node endNode = nodes.get(nodes.size() - 1);
        return new Point(endNode.getLat(), endNode.getLon());
    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof Way)) {
            return false;
        }

        Way otherWay = (Way)other;

        if (!otherWay.getHighway().equals(getHighway())) {
            return false;
        }

        if (otherWay.getLanes() != getLanes()) {
            return false;
        }

        if (otherWay.getMaxSpeed() != getMaxSpeed()) {
            return false;
        }

        if (otherWay.getNodes().size() != getNodes().size()) {
            return false;
        }

        if (otherWay.firstPoint().getLatitude() != firstPoint().getLatitude()) {
            return false;
        }

        if (otherWay.firstPoint().getLongitude() != firstPoint().getLongitude()) {
            return false;
        }

        if (otherWay.lastPoint().getLatitude() != lastPoint().getLatitude()) {
            return false;
        }

        return otherWay.lastPoint().getLongitude() == lastPoint().getLongitude();

    }

    public Node firstNode() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        Node startNode = nodes.get(0);
        return startNode;
    }

    public Node lastNode() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        Node endNode = nodes.get(nodes.size() - 1);
        return endNode;
    }

    public double getDistance() {
        return firstNode().distanceFrom(lastNode());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}