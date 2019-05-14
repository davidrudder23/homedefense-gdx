package org.noses.games.homedefense.enemy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnemyGroupBuilder {
    HashMap<String, Intersection> intersections;
    int numEnemies;
    int delay;
    EnemyBuilder enemyBuilder;

    public EnemyGroupBuilder intersections(HashMap<String, Intersection> intersections) {
        this.intersections = intersections;
        return this;
    }

    public EnemyGroupBuilder numEnemies(int numEnemies) {
        this.numEnemies = numEnemies;
        return this;
    }

    public EnemyGroupBuilder delay(int delay) {
        this.delay = delay;
        return this;
    }

    public EnemyGroupBuilder enemyBuilder(EnemyBuilder enemyBuilder) {
        this.enemyBuilder = enemyBuilder;
        return this;
    }

    public EnemyGroup build() {
        EnemyGroup enemyGroup = new EnemyGroup(numEnemies, delay);

        for (int i = 0; i < numEnemies; i++) {

            Enemy enemy = enemyBuilder.build();
            enemyGroup.addEnemy(enemy);
        }

        return enemyGroup;
    }
}
