package org.noses.games.homedefense.client;

import com.badlogic.gdx.graphics.Color;
import lombok.Data;

import java.awt.*;
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
        this.color = Color.WHITE;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public Color getColor() {
        if (color == null) {
            setColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f));
        }

        return color;
    }

    public Point firstPoint() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        Node startNode = nodes.get(0);
        return new Point(startNode.getX(), startNode.getY());
    }

    public Point lastPoint() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        Node endNode = nodes.get(nodes.size() - 1);
        return new Point(endNode.getX(), endNode.getY());
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

        if (otherWay.firstPoint().x != firstPoint().x) {
            return false;
        }

        if (otherWay.firstPoint().y != firstPoint().y) {
            return false;
        }

        if (otherWay.lastPoint().x != lastPoint().x) {
            return false;
        }

        if (otherWay.lastPoint().y != lastPoint().y) {
            return false;
        }

        return true;

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

    public int getDistance() {
        return firstNode().distanceFrom(lastNode());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}