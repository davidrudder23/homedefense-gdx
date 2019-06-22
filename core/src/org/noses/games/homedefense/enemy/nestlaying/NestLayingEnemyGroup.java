package org.noses.games.homedefense.enemy.nestlaying;

import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyGroup;

import java.util.ArrayList;
import java.util.List;

public class NestLayingEnemyGroup extends EnemyGroup {

    List<Enemy> enemies;

    public NestLayingEnemyGroup() {
        super(0, 100);

        enemies = new ArrayList<>();
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public List<Enemy> getEnemies() {
        return enemies;
    }

    @Override
    public boolean isEmpty() {
        return enemies.isEmpty();
    }

    @Override
    public void clockTick(double delta) {
    }

    @Override
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    @Override
    public void kill() {
    }

    @Override
    public boolean isKilled() {
        return false;
    }
}
