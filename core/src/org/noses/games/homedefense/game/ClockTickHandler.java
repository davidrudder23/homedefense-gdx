package org.noses.games.homedefense.game;

public interface ClockTickHandler {

    void clockTick(double delta);

    void kill();

    boolean isKilled();
}
