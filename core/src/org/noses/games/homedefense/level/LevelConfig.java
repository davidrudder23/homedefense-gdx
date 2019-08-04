package org.noses.games.homedefense.level;

import lombok.Data;
import org.noses.games.homedefense.nest.NestFactory;

import java.util.List;

@Data
public class LevelConfig {

    String name;
    List<NestConfig> nestConfigs;

}

