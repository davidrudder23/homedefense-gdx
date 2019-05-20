package org.noses.games.homedefense.enemy;

import lombok.Getter;
import org.noses.games.homedefense.game.ClockTickHandler;

import java.util.ArrayList;
import java.util.List;

public class EnemyGroup implements ClockTickHandler {

    @Getter
    int delay;

    @Getter
    List<Enemy> enemies;

    private List<Enemy> upcomingEnemies;

    private int count;

    int numEnemies;

    EnemyGroup(int numEnemies, int delay) {
        enemies = new ArrayList<>();
        upcomingEnemies = new ArrayList<>();

        this.numEnemies = numEnemies;
        this.delay = delay;
    }

    void addEnemy(Enemy enemy) {
        upcomingEnemies.add(enemy);
    }

    public boolean isEmpty() {
        return upcomingEnemies.isEmpty() && enemies.isEmpty();
    }

    public void clockTick(double delta) {
        count++;
        if (count > delay) {
            count = 0;
            if (upcomingEnemies.size() > 0) {
                Enemy upcomingEnemy = upcomingEnemies.get(0);
                upcomingEnemies.remove(0);
                enemies.add(upcomingEnemy);
            }
        }

        for (Enemy enemy : enemies) {
            enemy.clockTick(delta);
        }

        for (int i = enemies.size() - 1; i >= 0; i--) {
            if (enemies.get(i).isKilled()) {
                enemies.remove(i);
            }
        }
    }

    public static EnemyGroupBuilder builder() {
        return new EnemyGroupBuilder();
    }

}

