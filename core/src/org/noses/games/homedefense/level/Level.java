package org.noses.games.homedefense.level;

import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.nest.ArmoredEnemyNest;
import org.noses.games.homedefense.nest.GroundEnemyNest;
import org.noses.games.homedefense.nest.NestFactory;
import org.noses.games.homedefense.nest.SplittingEnemyNest;

import java.util.ArrayList;
import java.util.List;

public class Level implements ClockTickHandler {

    private List<NestFactory> nestFactories;
    double delayBetweenNests;
    MapScreen parent;

    boolean isKilled;

    double time;

    public Level(MapScreen parent, double delayBetweenNests) {
        this.parent = parent;
        this.delayBetweenNests = delayBetweenNests;
        nestFactories = new ArrayList<>();

        nestFactories.add(new SplittingEnemyNest.SplittingEnemyNestFactory(parent));
        nestFactories.add(new GroundEnemyNest.GroundEnemyNestFactory(parent));
        nestFactories.add(new ArmoredEnemyNest.ArmoredEnemyNestFactory(parent));
    }

    @Override
    public void clockTick(double delta) {
        time += delta;

        if (time > delayBetweenNests) {
            time = 0;
        }
    }

    @Override
    public void kill() {
        isKilled = true;
    }

    @Override
    public boolean isKilled() {
        return isKilled;
    }
}
