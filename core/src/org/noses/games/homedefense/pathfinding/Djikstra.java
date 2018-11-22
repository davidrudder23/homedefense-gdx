package org.noses.games.homedefense.pathfinding;

import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Djikstra {
    private List<Intersection> visitedIntersections;
    private List<Intersection> unvisitedIntersections;
    private HashMap<String, Intersection> allIntersections;

    private Intersection destination;

    public Djikstra(HashMap<String, Intersection> intersections) {
        allIntersections = intersections;

        unvisitedIntersections = new ArrayList<>();
        visitedIntersections = new ArrayList<>();
    }

    public void getBestPath(Node from, int toX, int toY) {
        Intersection fromIntersection = getIntersectionForNode(allIntersections, from);

        getBestPath(fromIntersection, toX, toY);
    }

    public PathStep getBestPath(Intersection from, int toX, int toY) {

        long startTime = System.currentTimeMillis();

        PathStep pathStep = new PathStep();
        pathStep.setIntersection(from);

        if (from.closeTo(toX, toY)) {
            return pathStep; // TODO SUCCESS!
        }
        from.pathStep = pathStep;
        unvisitedIntersections.add(from);

        while (unvisitedIntersections.size() > 0) {
            from = unvisitedIntersections.get(0);
            if (from.closeTo(toX, toY)) {
                if (destination == null) {
                    destination = from;
                }
                if (destination.getPathStep().getWeight() > from.getPathStep().getWeight()) {
                    destination.setPathStep(from.getPathStep());
                }
            }

            unvisitedIntersections.remove(0);
            visitedIntersections.add(from);

            for (Way way : from.getWayList()) {
                for (Node node : way.getNodes()) {
                    Intersection intersection = getIntersectionForNode(allIntersections, node);
                    if (intersection == null) {
                        continue;
                    }

                    if (visitedIntersections.contains(intersection)) {
                        //System.out.println ("Visited contains "+intersection);
                        continue;
                    }

                    if (unvisitedIntersections.contains(intersection)) {
                        //System.out.println ("Unvisited contains "+intersection);
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

                        intersection.setPathStep(pathStep);
                    }

                    unvisitedIntersections.add(intersection);
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
