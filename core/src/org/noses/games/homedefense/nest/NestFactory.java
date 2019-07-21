package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.geometry.Point;

public interface NestFactory {

    EnemyNest build(double delayBeforeStart, Point location);

    boolean isKilled();
}
