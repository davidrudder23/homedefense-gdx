package org.noses.games.homedefense.level;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noses.games.homedefense.nest.NestFactory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NestConfig {
    String className;
    int numWaves;
    int numEnemiesPerWave;
    double delayBeforeStart;
    double delayBetweenWaves;
    @Builder.Default()
    boolean used = false;

    NestFactory nestFactory;

    EnemyConfig enemyConfig;
}
