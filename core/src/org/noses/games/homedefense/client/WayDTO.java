package org.noses.games.homedefense.client;

import com.badlogic.gdx.graphics.Color;
import lombok.Data;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class WayDTO {
    String name;
    int lanes;
    int maxSpeed;
    String highway;

    Color color;

    List<NodeDTO> nodes;

    public WayDTO() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(NodeDTO node) {
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
        NodeDTO startNode = nodes.get(0);
        return new Point(startNode.getX(), startNode.getY());
    }

    public Point lastPoint() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        NodeDTO endNode = nodes.get(nodes.size() - 1);
        return new Point(endNode.getX(), endNode.getY());
    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof WayDTO)) {
            return false;
        }

        WayDTO otherWayDto = (WayDTO)other;

        if (!otherWayDto.getHighway().equals(getHighway())) {
            return false;
        }

        if (otherWayDto.getLanes() != getLanes()) {
            return false;
        }

        if (otherWayDto.getMaxSpeed() != getMaxSpeed()) {
            return false;
        }

        if (otherWayDto.getNodes().size() != getNodes().size()) {
            return false;
        }

        if (otherWayDto.firstPoint().x != firstPoint().x) {
            return false;
        }

        if (otherWayDto.firstPoint().y != firstPoint().y) {
            return false;
        }

        if (otherWayDto.lastPoint().x != lastPoint().x) {
            return false;
        }

        if (otherWayDto.lastPoint().y != lastPoint().y) {
            return false;
        }

        return true;

    }

    public NodeDTO firstNode() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        NodeDTO startNode = nodes.get(0);
        return startNode;
    }

    public NodeDTO lastNode() {
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        NodeDTO endNode = nodes.get(nodes.size() - 1);
        return endNode;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}