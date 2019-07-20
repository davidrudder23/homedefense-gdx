package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.geometry.Point;

public interface NestFactory {

    public EnemyNest build(double delayBeforeStart, Point location);

    public boolean isKilled();
}
