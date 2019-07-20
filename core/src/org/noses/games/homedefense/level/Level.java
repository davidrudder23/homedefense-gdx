package org.noses.games.homedefense.level;

import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.nest.*;

import java.util.ArrayList;
import java.util.List;

public class Level implements ClockTickHandler {

    private List<NestFactory> nestFactories;
    double delayBetweenNests;
    MapScreen parent;

    boolean isKilled;

    double time;

    int nestFactoryNumber;

    public Level(MapScreen parent, double delayBetweenNests) {
        this.parent = parent;
        this.delayBetweenNests = delayBetweenNests;
        this.time = delayBetweenNests;

        reset();
    }

    public void reset() {
        System.out.println("Level resetting");
        isKilled = false;
        this.time = delayBetweenNests;

        nestFactoryNumber = 0;
        nestFactories = new ArrayList<>();
        nestFactories.add(new SplittingEnemyNest.SplittingEnemyNestFactory(parent, 1));
        nestFactories.add(new GroundEnemyNest.GroundEnemyNestFactory(parent, 1));
        nestFactories.add(new ArmoredEnemyNest.ArmoredEnemyNestFactory(parent, 1));

        parent.addClockTickHandler(this);
    }

    @Override
    public void clockTick(double delta) {
        if (isKilled()) {
            kill();
        }

        if (nestFactoryNumber >= nestFactories.size()) {
            return;
        }

        time += delta;

        if (time > delayBetweenNests) {
            System.out.println("Laying new NestLayingNest with factory(" + nestFactoryNumber + ")=" + nestFactories.get(nestFactoryNumber));
            time = 0;

            NestLayingNest nestLayingNest = new NestLayingNest(parent, nestFactories.get(nestFactoryNumber));
            nestFactoryNumber++;
            parent.dropNest(nestLayingNest);
            parent.addClockTickHandler(nestLayingNest);
        }
    }

    @Override
    public void kill() {
        if (isKilled) {
            return;
        }

        isKilled = true;
        parent.clockTick(0);
        parent.startNewLevel();
    }

    @Override
    public boolean isKilled() {
        for (NestFactory nestFactory : nestFactories) {
            if (!nestFactory.isKilled()) {
                //System.out.println("Level not killed because " + nestFactory);
                return false;
            }
        }

        kill();
        return true;
    }
}
