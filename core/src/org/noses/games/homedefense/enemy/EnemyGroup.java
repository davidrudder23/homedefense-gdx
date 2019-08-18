package org.noses.games.homedefense.enemy;

import lombok.Getter;
import org.noses.games.homedefense.game.ClockTickHandler;

import java.util.ArrayList;
import java.util.List;

public class EnemyGroup implements ClockTickHandler {

    @Getter
    double delayBetweenEnemies;

    @Getter
    List<Enemy> enemies;

    private List<Enemy> upcomingEnemies;

    private double count;

    public EnemyGroup(double delayBetweenEnemies) {
        enemies = new ArrayList<>();
        upcomingEnemies = new ArrayList<>();

        this.delayBetweenEnemies = delayBetweenEnemies;
    }

    public void addEnemy(Enemy enemy) {
        upcomingEnemies.add(enemy);
    }

    public boolean isEmpty() {
        return upcomingEnemies.isEmpty() && enemies.isEmpty();
    }

    public void clockTick(double delta) {
        count += delta;
        if (count > delayBetweenEnemies) {
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

    @Override
    public void kill() {
        // do nothing
    }

    @Override
    public boolean isKilled() {
        return isEmpty();
    }

    public static EnemyGroupBuilder builder() {
        return new EnemyGroupBuilder();
    }

}

