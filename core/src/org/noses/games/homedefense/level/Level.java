package org.noses.games.homedefense.level;

import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.nest.GroundEnemyNest;
import org.noses.games.homedefense.nest.NestFactory;

import java.util.ArrayList;
import java.util.List;

public class Level {

    private List<NestFactory> nestFactories;

    public Level(MapScreen parent) {
        nestFactories = new ArrayList<>();

        nestFactories.add(new GroundEnemyNest.GroundEnemyNestFactory(parent));
    }

}
