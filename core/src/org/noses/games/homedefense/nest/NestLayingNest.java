package org.noses.games.homedefense.nest;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.NestLayingEnemy;
import org.noses.games.homedefense.enemy.NestLayingEnemyGroup;
import org.noses.games.homedefense.nest.EnemyNest;
import org.noses.games.homedefense.nest.NestFactory;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.home.Home;
import org.noses.games.homedefense.pathfinding.Intersection;

import java.util.ArrayList;
import java.util.List;

// Not actually a "nest". but something that generates nest layers
public class NestLayingNest extends EnemyNest implements ClockTickHandler {
    MapScreen parent;

    boolean makingNest;

    List<EnemyGroup> enemyGroups;

    int nestNumber;

    List<NestFactory> nestFactories;

    public NestLayingNest(MapScreen parent, List<NestFactory> nestFactories) {
        super(parent, "splitting", 0, 0, 0);
        this.parent = parent;
        this.nestFactories = nestFactories;

        makingNest = false;
        org.noses.games.homedefense.enemy.NestLayingEnemyGroup enemyGroup = new NestLayingEnemyGroup();
        enemyGroups = new ArrayList<>();
        enemyGroups.add(enemyGroup);
        //parent.addClockTickHandler(enemyGroups.get(0));
        nestNumber = 0;

    }

    @Override
    public double delayBetweenWaves() {
        return 0;
    }

    @Override
    public EnemyGroup getNewEnemyGroup() {
        return null;
    }

    @Override
    public void render(Batch batch) {
        // don't actually render
    }

    @Override
    public List<EnemyGroup> getEnemyGroups() {
        return enemyGroups;
    }

    @Override
    public void clockTick(double delta) {
        if (nestNumber > nestFactories.size()) {
            return;
        }

        if (makingNest) {
            if (enemyGroups.size() <= 0) {
                return;
            }

            if (enemyGroups.get(0).getEnemies().size() <= 0) {
                return;
            }

            if (enemyGroups.get(0).getEnemies().get(0).isKilled()) {
                enemyGroups.get(0).getEnemies().clear();
                makingNest = false;
            } else {
                return;
            }
        }

        makingNest = true;
        System.out.println("Parent has "+parent.getEnemyNests().size()+" enemy nests");
        if (parent.getEnemyNests().size() < 4) {
            System.out.println("Making a new nest layer");
            double latitude = 0;
            double longitude = 0;

            List<Intersection> intersections = parent.getIntersections();
            Home home = parent.getHome();

            // If it's too close, don't add the nest
            Point homePoint = new Point(home.getLatitude(), home.getLongitude());

            boolean foundIntersection = false;

            Intersection intersection = null;

            while (!foundIntersection) {
                intersection = intersections.get((int) (Math.random() * intersections.size()));
                System.out.println("Testing intersection "+intersection);

                if (!parent.isGoodLocationForNest(intersection.getNode())) {
                    continue;
                }

                foundIntersection = true;
            }

            //System.out.println("Targetting nest at "+intersection);
            org.noses.games.homedefense.enemy.NestLayingEnemy nestLayingEnemy = new NestLayingEnemy(parent, new Point(intersection.getLatitude(), intersection.getLongitude()), nestFactories.get(nestNumber));
            parent.addClockTickHandler(nestLayingEnemy);
            enemyGroups.get(0).addEnemy(nestLayingEnemy);
            nestNumber++;
        }
    }

    @Override
    public void kill() {

    }

    @Override
    public boolean isKilled() {
        return false;
    }

    @Override
    public String toString() {
        return "Nest laying nest";
    }
}
