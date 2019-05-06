package org.noses.games.homedefense.enemy;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.pathfinding.Djikstra;
import org.noses.games.homedefense.pathfinding.Intersection;
import org.noses.games.homedefense.pathfinding.PathStep;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EnemyGroup implements ClockTickHandler {

    @Getter
    int width;
    @Getter
    int height;

    @Getter
    int delay;

    @Getter
    List<Enemy> enemies;

    private List<Enemy> upcomingEnemies;

    private int count;

    int numEnemies;


    EnemyGroup(int numEnemies, int width, int height, int delay) {
        enemies = new ArrayList<>();
        upcomingEnemies = new ArrayList<>();

        this.numEnemies = numEnemies;
        this.width = width;
        this.height = height;
        this.delay = delay;
    }

    void addEnemy(Enemy enemy) {
        upcomingEnemies.add(enemy);
    }

    public boolean isEmpty() {
        return upcomingEnemies.isEmpty() && enemies.isEmpty();
    }

    public void clockTick(float delta) {
        count++;
        if (count > delay) {
            count = 0;
            if (upcomingEnemies.size()>0) {
                Enemy upcomingEnemy = upcomingEnemies.get(0);
                upcomingEnemies.remove(0);
                enemies.add(upcomingEnemy);
            }
        }

        for (Enemy enemy: enemies) {
            enemy.clockTick(delta);
        }

        for (int i = enemies.size()-1; i>=0; i--) {
            if (enemies.get(i).isKilled()) {
                enemies.remove(i);
            }
        }
    }

    public static EnemyGroupBuilder builder() {
        return new EnemyGroupBuilder();
    }

}

