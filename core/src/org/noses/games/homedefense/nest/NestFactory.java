package org.noses.games.homedefense.nest;

import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.level.NestConfig;

public interface NestFactory {

    public String getClassName();

    EnemyNest build(NestConfig nestConfig, Point location);

    boolean isKilled();
}
