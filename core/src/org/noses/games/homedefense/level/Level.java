package org.noses.games.homedefense.level;

import lombok.Data;
import org.noses.games.homedefense.nest.NestFactory;

import java.util.List;

@Data
public class Level {

    String name;
    List<Nest> nests;

}

@Data
class Nest {
    String className;
    int numWaves;
    int numEnemiesPerWave;
    double delay;
    boolean used = false;

    NestFactory nestFactory;
}
