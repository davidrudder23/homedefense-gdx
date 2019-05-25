package org.noses.games.homedefense.game;

public interface ClockTickHandler {

    public void clockTick(double delta);

    public void kill();

    public boolean isKilled();
}
