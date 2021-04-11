package org.noses.games.homedefense.enemy;

import org.noses.games.homedefense.pathfinding.Intersection;

import java.util.HashMap;

public class EnemyGroupBuilder {
    int numEnemies;
    double delay;
    EnemyBuilder enemyBuilder;

    public EnemyGroupBuilder numEnemies(int numEnemies) {
        this.numEnemies = numEnemies;
        return this;
    }

    public EnemyGroupBuilder delay(double delay) {
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
