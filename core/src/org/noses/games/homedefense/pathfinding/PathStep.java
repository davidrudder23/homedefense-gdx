package org.noses.games.homedefense.pathfinding;

import lombok.Data;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;

@Data
public class PathStep {

    PathStep previousPath;
    Way connectingWay;
    Intersection intersection;
    double weight = Double.MAX_VALUE;

    Node startingNode;
    Node endingNode;

    public String toString() {
        StringBuffer string = new StringBuffer();

        if (connectingWay != null) {
            string.append("connectingWay=" + connectingWay.getName() + ", ");
        } else {
            string.append("connectingWay=null, ");
        }

        if (intersection != null) {
            string.append("intersection=" + intersection.getNode().getX() + "x" + intersection.getNode().getY() + ", ");
        }
        string.append("from " + getStartingNode() + " to " + getEndingNode() + ", ");
        string.append("weight=" + weight);
        return string.toString();
    }

    public String getPath() {
        StringBuffer pathString = new StringBuffer();
        pathString.append(toString());
        PathStep thisPathStep = getPreviousPath();
        while (thisPathStep != null) {
            pathString.append("\n");
            pathString.append(thisPathStep.toString());
            thisPathStep = thisPathStep.getPreviousPath();
        }
        return pathString.toString();
    }
}
