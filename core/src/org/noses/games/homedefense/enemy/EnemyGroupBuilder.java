package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.pathfinding.Intersection;

import java.util.HashMap;

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
        EnemyGroup enemyGroup = new EnemyGroup(delay);

        for (int i = 0; i < numEnemies; i++) {

            Enemy enemy = enemyBuilder.build();
            enemyGroup.addEnemy(enemy);
        }

        return enemyGroup;
    }
}
