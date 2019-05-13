package org.noses.games.homedefense.enemy;

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
    HomeDefenseGame game;
    int numEnemies;
    int delay;
    int width;
    int height;

    public EnemyGroupBuilder intersections(HashMap<String, Intersection> intersections) {
        this.intersections = intersections;
        return this;
    }

    public EnemyGroupBuilder game(HomeDefenseGame game) {
        this.game = game;
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

    public EnemyGroupBuilder width(int width) {
        this.width = width;
        return this;
    }

    public EnemyGroupBuilder height(int height) {
        this.height = height;
        return this;
    }

    public EnemyGroup build() {
        SecureRandom random = new SecureRandom();
        random.setSeed(System.currentTimeMillis());

        // Ensure enemies always start off-screen
        List<Way> startingWays = new ArrayList<>();

        for (Way way: game.getMap().getWays()) {
            Node node = way.firstNode();

            if ((node.getX() < 0) ||
                    (node.getY() < 0) ||
                    (node.getX() > game.getScreenWidth()) ||
                    (node.getY() > game.getScreenHeight())
            ) {
                startingWays.add(way);
            }

        }

        Way way = startingWays.get(random.nextInt(startingWays.size()));

        EnemyGroup enemyGroup = new EnemyGroup(numEnemies, width, height, delay);

        for (int i = 0; i < numEnemies; i++) {

            GroundEnemy enemy = new GroundEnemy(game, way);
            Djikstra djikstra = new Djikstra(intersections);
            Intersection intersection = djikstra.getIntersectionForNode(intersections, way.firstNode());
            System.out.println("Intersection=("+intersection.getLatitude()+"x"+intersection.getLongitude());
            PathStep pathStep = djikstra.getBestPath(intersection, width/2, height/2);
            System.out.println ("Enemy's path - "+pathStep.getPath());

            enemy.setPath(pathStep);
            enemyGroup.addEnemy(enemy);
        }

        return enemyGroup;
    }
}
