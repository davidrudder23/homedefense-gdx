package org.noses.games.homedefense.pathfinding;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Djikstra {
    private List<Intersection> unvisitedIntersections;
    private HashMap<String, Intersection>  allIntersections;
    private HashMap<String, Boolean> seen;

    private Intersection destination;

    public Djikstra(HashMap<String, Intersection> intersections) {
        allIntersections = intersections;

        unvisitedIntersections = new ArrayList<>();

        seen = new HashMap<>();
    }

    public PathStep getBestPath(Node from, Node finishNode) {
        Intersection fromIntersection = getIntersectionForNode(allIntersections, from);

        return getBestPath(fromIntersection, finishNode);
    }

    public PathStep getBestPath(Intersection from, Node finishNode) {
        for (Intersection intersection: allIntersections.values()) {
            intersection.setPathStep(null);
        }

        long startTime = System.currentTimeMillis();

        PathStep pathStep = new PathStep();
        pathStep.setIntersection(from);

        if (from.getNode().equals(finishNode)) {
            return pathStep; // TODO SUCCESS!
        }
        from.pathStep = pathStep;
        unvisitedIntersections.add(from);
        seen.put(from.getLatitude()+"_"+from.getLongitude(), Boolean.TRUE);

        while (unvisitedIntersections.size() > 0) {
            from = unvisitedIntersections.get(0);
            if (from.getNode().equals(finishNode)) {
                if (destination == null) {
                    destination = from;
                }
                if (destination.getPathStep().getWeight() > from.getPathStep().getWeight()) {
                    destination.setPathStep(from.getPathStep());
                }
            }

            unvisitedIntersections.remove(0);
            seen.put(from.getLatitude()+"_"+from.getLongitude(), Boolean.TRUE);

            for (Way way : from.getWayList()) {
                for (Node node : way.getNodes()) {
                    Intersection intersection = getIntersectionForNode(allIntersections, node);
                    if (intersection == null) {
                        continue;
                    }

                    if (seen.get(intersection.getLatitude()+"_"+intersection.getLongitude()) != null) {
                        continue;
                    }

                    double weight = intersection.getWeightFromIntersection(from, way);
                    if (from.getPathStep() != null) {
                        weight += from.getPathStep().getWeight();
                    }
                    if ((intersection.getPathStep() == null) ||
                            (intersection.getPathStep().getWeight() > weight)) {
                        pathStep = new PathStep();
                        pathStep.setConnectingWay(way);
                        pathStep.setIntersection(intersection);
                        pathStep.setPreviousPath(from.getPathStep());
                        pathStep.setWeight(weight);
                        pathStep.setStartingNode(from.getNode(way));
                        pathStep.setEndingNode(node);

                        intersection.setPathStep(pathStep);
                    }

                    unvisitedIntersections.add(intersection);
                    seen.put(intersection.getLatitude()+"_"+intersection.getLongitude(), Boolean.TRUE);
                }
            }
        }

        if ((destination == null) || (destination.getPathStep() == null)){
            System.out.println("Can not find a path from "+from.getNode()+" to "+finishNode);
            return null;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Djikstra took " + (endTime - startTime) + " millis and found a pathstep "+(pathStep!=null));
        return destination.getPathStep();
    }

    public Intersection getIntersectionForNode(HashMap<String, Intersection> intersections, Node node) {
        String key = node.getLat()+"_"+node.getLon();
        return intersections.get(key);
    }
}
