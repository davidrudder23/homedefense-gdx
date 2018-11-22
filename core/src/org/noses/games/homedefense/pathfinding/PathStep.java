package org.noses.games.homedefense.pathfinding;

import lombok.Data;
import org.noses.games.homedefense.client.Way;

@Data
public class PathStep {

    PathStep previousPath;
    Way connectingWay;
    Intersection intersection;
    double weight = Double.MAX_VALUE;

    public String toString() {
        StringBuffer string = new StringBuffer();

        if (connectingWay != null) {
            string.append("connectingWay=" + connectingWay.getName()+", ");
        } else {
            string.append("connectingWay=null, ");
        }

        if (intersection != null) {
            string.append("intersection="+intersection.getNode().getX()+"x"+intersection.getNode().getY()+", ");
        } else {
            string.append("intersection=null, ");
        }
        string.append("weight="+weight);
        return string.toString();
    }
}
