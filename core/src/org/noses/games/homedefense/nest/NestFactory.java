package org.noses.games.homedefense.nest;

public interface NestFactory {

    public EnemyNest build(double delayBeforeStart, double longitude, double latitude);
}
