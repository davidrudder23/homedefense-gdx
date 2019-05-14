package org.noses.games.homedefense.pathfinding;

import lombok.Data;
import lombok.ToString;
import org.noses.games.homedefense.client.Map;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@ToString
public class Intersection {

    double longitude;
    double latitude;

    List<Way> wayList;

    Node node;

    PathStep pathStep;

    public static HashMap<String, Intersection> buildIntersectionsFromMap(Map map) {
        HashMap<String, Intersection> intersections = new HashMap<>();

        for (Way way : map.getWays()) {
            for (Node node: way.getNodes()) {
                String key = node.getLat()+"_"+node.getLon();
                //System.out.println("key="+key);

                Intersection intersection = intersections.get(key);
                if (intersection == null) {
                    intersection = new Intersection();
                    intersection.setWayList(new ArrayList<>());
                    intersection.setLatitude(node.getLat());
                    intersection.setLongitude(node.getLon());
                }

                if (!intersection.getWayList().contains(way)) {
                    intersection.getWayList().add(way);
                }

                intersections.put(key, intersection);
            }
        }


        return intersections;
    }

    public Node getNode() {
        if (node == null) {
            for (Way way: getWayList()) {
                for (Node wayNode: way.getNodes()) {
                    if ((wayNode.getLat() == getLatitude()) &&
                            (wayNode.getLon() == getLongitude())) {
                        node = wayNode;
                        break;
                    }
                }
            }
        }
        return node;
    }

    public Node getNode(Way way) {
        Node node = null;
        for (Node wayNode: way.getNodes()) {
            if ((wayNode.getLat() == getLatitude()) &&
                    (wayNode.getLon() == getLongitude())) {
                node = wayNode;
                break;
            }
        }
        return node;
    }

    public double getWeightFromIntersection(Intersection other, Way connectingWay) {
        Node node = getNode();
        double distance = node.distanceFrom(other.getNode());
        double inversedSpeed =  1/connectingWay.getMaxSpeed();
        double weight = distance * inversedSpeed;

        return  weight;
    }

    public boolean closeTo(int x, int y) {
        Node node = getNode();
        //System.out.println ("Comparing closeTo "+x+" vs "+node.getX()+", "+y+" vs "+node.getY());
        if (Math.abs(x - node.getX()) > 5) { // todo - this fails to get a good point for home.  we should choose the closest, not just find one within an arbitrary distance
            return false;
        }
        if (Math.abs(y - node.getY()) > 5) {
            return false;
        }
        return true;
    }

    public boolean equals(Object other) {
        if (!(other instanceof  Intersection)) {
            return false;
        }

        Intersection otherIntersection = (Intersection)other;
        return ((otherIntersection.getLatitude() == getLatitude()) &&
                (otherIntersection.getLongitude() == getLongitude()));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
