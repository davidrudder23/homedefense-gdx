package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;
import org.noses.games.homedefense.level.EnemyConfig;
import org.noses.games.homedefense.level.NestConfig;
import org.noses.games.homedefense.nest.EnemyNest;
import org.noses.games.homedefense.nest.NestFactory;
import org.noses.games.homedefense.tower.Tower;

public class NestLayingEnemy extends Enemy {
    Point targetNestLocation;
    Point currentLocation;
    double progressAlong;

    boolean killed;

    NestFactory nestFactory;
    NestConfig nestConfig;

    public NestLayingEnemy(MapScreen parent, Point targetNestLocation, NestConfig nestConfig, NestFactory nestFactory) {
        super(parent, "enemy/nest_laying.png", parent.loadSound("normal_hit.mp3"), 900, 900, 0.06, 200);

        this.nestFactory = nestFactory;
        this.nestConfig = nestConfig;
        this.targetNestLocation = targetNestLocation;

        progressAlong = 0;

        killed = false;

        // find the start location
        double startLat;
        double startLon;

        int topOrBottom = (int) (Math.random() * 2);
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

            if (distanceFromEast > distanceFromWest) {
                currentLocation = new Point(parent.getHome().getLatitude(), parent.getMap().getEast());
            } else {
                currentLocation = new Point(parent.getHome().getLatitude(), parent.getMap().getWest());
            }
        }
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(0,0,0,0);
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
    public boolean isKilled() {
        return killed;
    }

    @Override
    public void clockTick(double delta) {
        super.clockTick(delta);
        progressAlong += HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph * delta * 20000;

        if (isCloseToTarget()) {
            EnemyNest enemyNest = null;

            enemyNest = nestFactory.build(nestConfig, targetNestLocation);

            parent.dropNest(enemyNest);
            killed = true;
        }
    }

    @Override
    public Sprite getSprite() {
        Sprite sprite = super.getSprite();

        if (currentLocation.getLongitude() > targetNestLocation.getLongitude()) {
            sprite.flip(true, false);
        }
        return sprite;
    }


    @Override
    public boolean canBeHitBy(Tower tower) {
        return false;
    }

    @Override
    public boolean canBeHitByHome() {
        return false;
    }

    @Override
    public Point getLocation() {

        Point firstPoint = currentLocation;
        Point lastPoint = targetNestLocation;

        double currentLatitude = firstPoint.getLatitude() + (progressAlong * (lastPoint.getLatitude() - firstPoint.getLatitude()));
        double currentLongitude = firstPoint.getLongitude() + (progressAlong * (lastPoint.getLongitude() - firstPoint.getLongitude()));

        return new Point((float) currentLatitude, (float) currentLongitude);
    }

    private boolean isCloseToTarget() {
        Point location = getLocation();

        return location.getDistanceFrom(targetNestLocation) <= (HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph * 10);
    }

    @Override
    public double getLatitude() {
        return currentLocation.getLatitude();
    }

    @Override
    public double getLongitude() {
        return currentLocation.getLongitude();
    }
}
