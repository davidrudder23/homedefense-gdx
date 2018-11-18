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

    public static HashMap<String, Intersection> buildIntersectionsFromMap(Map map) {
        HashMap<String, Intersection> intersections = new HashMap<>();

        for (Way way : map.getWays()) {
            for (Node node: way.getNodes()) {
                String key = node.getLat()+"_"+node.getLon();
                System.out.println("key="+key);

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
}
