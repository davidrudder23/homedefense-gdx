package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.graphics.Color;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;

public class NestLayingEnemy extends Enemy {
    Point targetNestLocation;
    Point currentLocation;
    double progressAlong;

    public NestLayingEnemy(MapScreen parent, Point targetNestLocation) {
        super(parent, "line128.png", parent.loadSound("normal_hit.mp3"), 32,32, 0.02, 200);

        this.targetNestLocation = targetNestLocation;

        progressAlong = 0;

        // find the start location
        double startLat;
        double startLon;

        int topOrBottom = (int)(Math.random()*2);
        if (topOrBottom == 0) {
            double distanceFromTop = Math.abs(targetNestLocation.getLatitude() - parent.getMap().getNorth());
            double distanceFromBottom = Math.abs(targetNestLocation.getLatitude() - parent.getMap().getSouth());
            if (distanceFromTop > distanceFromBottom) {
                currentLocation = new Point(parent.getMap().getNorth(), parent.getHome().getLongitude());
            } else {
                currentLocation = new Point(parent.getMap().getSouth(), parent.getHome().getLongitude());
            }
        } else {
            double distanceFromEast = Math.abs(targetNestLocation.getLongitude() - parent.getMap().getEast());
            double distanceFromWest = Math.abs(targetNestLocation.getLongitude() - parent.getMap().getWest());

            if (distanceFromEast> distanceFromWest) {
                currentLocation = new Point(parent.getHome().getLatitude(), parent.getMap().getEast());
            } else {
                currentLocation = new Point(parent.getHome().getLatitude(), parent.getMap().getWest());
            }
        }
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public int getValue() {
        return 100;
    }

    @Override
    public void clockTick(double delta) {
        progressAlong += HomeDefenseGame.LATLON_MOVED_IN_1s_1mph*delta;
        System.out.println("nest layer progress="+progressAlong);
    }

    @Override
    public Point getLocation() {

        Point firstPoint = currentLocation;
        Point lastPoint = targetNestLocation;

        double currentLatitude = firstPoint.getLatitude() + (progressAlong*(lastPoint.getLatitude() - firstPoint.getLatitude()));
        double currentLongitude = firstPoint.getLongitude() + (progressAlong*(lastPoint.getLongitude() - firstPoint.getLongitude()));

        return new Point((float) currentLatitude, (float) currentLongitude);    }

    @Override
    public double getLatitude() {
        return currentLocation.getLatitude();
    }

    @Override
    public double getLongitude() {
        return currentLocation.getLongitude();
    }
}
