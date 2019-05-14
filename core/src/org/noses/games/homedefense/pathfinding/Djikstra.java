package org.noses.games.homedefense.pathfinding;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;

import java.util.*;

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

    public void getBestPath(Node from, int toX, int toY) {
        Intersection fromIntersection = getIntersectionForNode(allIntersections, from);

        getBestPath(fromIntersection, toX, toY);
    }

    public PathStep getBestPath(Intersection from, int toX, int toY) {
        for (Intersection intersection: allIntersections.values()) {
            intersection.setPathStep(null);
        }

        // Sort by distance to toX and toY, so we can find the closest intersections
        List<Intersection> sortedIntersections = new ArrayList<>();
        sortedIntersections.addAll(allIntersections.values());

        Collections.sort(sortedIntersections, new Comparator<Intersection>() {
            @Override
            public int compare(Intersection a, Intersection b) {
                int distanceA = (a.getNode().getX()-toX) * (a.getNode().getX()-toX)+
                        (a.getNode().getY()-toY) * (a.getNode().getY()-toY);
                int distanceB = (b.getNode().getX()-toX) * (b.getNode().getX()-toX)+
                        (b.getNode().getY()-toY) * (b.getNode().getY()-toY);
                return distanceA-distanceB;
            }
        });

        Node finishNode = sortedIntersections.get(0).getNode();
        System.out.println("Closest is at "+finishNode);


        long startTime = System.currentTimeMillis();

        PathStep pathStep = new PathStep();
        pathStep.setIntersection(from);

        if (from.closeTo(finishNode.getX(), finishNode.getY())) {
            return pathStep; // TODO SUCCESS!
        }
        from.pathStep = pathStep;
        unvisitedIntersections.add(from);
        seen.put(from.getLatitude()+"_"+from.getLongitude(), Boolean.TRUE);

        while (unvisitedIntersections.size() > 0) {
            from = unvisitedIntersections.get(0);
            if (from.closeTo(finishNode.getX(), finishNode.getY())) {
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

            /*System.out.println("Unvisited intersections has " + unvisitedIntersections.size());
            System.out.println("Visited intersections has " + visitedIntersections.size());
            System.out.println("Current PathStep=" + pathStep);*/
        }

        if (destination == null) {
            return null;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Djikstra took " + (endTime - startTime) + " millis");
        return destination.getPathStep();
    }

    public Intersection getIntersectionForNode(HashMap<String, Intersection> intersections, Node node) {
        String key = node.getLat()+"_"+node.getLon();
        return intersections.get(key);
    }
}
