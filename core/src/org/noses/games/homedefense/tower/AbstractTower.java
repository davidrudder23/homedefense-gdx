package org.noses.games.homedefense.tower;

import lombok.Data;
import org.noses.games.homedefense.geometry.Point;

@Data
public abstract class AbstractTower implements Tower {
    double bulletSpeed;
    double delayBetweenShots;

    private Point location;

    @Override
    public double getLatitude() {
        return location.getLatitude();
    }

    @Override
    public double getLongitude() {
        return location.getLongitude();
    }

    @Override
    public void clockTick(double delta) {

    }

    @Override
    public void kill() {

    }

    @Override
    public boolean isKilled() {
        return false;
    }
}
