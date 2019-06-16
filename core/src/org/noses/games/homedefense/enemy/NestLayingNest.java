package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.graphics.g2d.Batch;
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

    public NestLayingNest(MapScreen parent) {
        super(parent,"hataak", 0, 0,  0);
        this.parent = parent;
        makingNest = false;
        EnemyGroup enemyGroup = new EnemyGroup(0, 100);
        enemyGroups = new ArrayList<>();
        enemyGroups.add(enemyGroup);
        parent.addClockTickHandler(enemyGroups.get(0));

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
        System.out.println("Nest laying nest clock tick, making="+makingNest);
        if (makingNest) {
            return;
        }
        makingNest = true;
        if (parent.getEnemyNests().size() < 3) {
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

                Point nestPoint = new Point(intersection.getLatitude(), intersection.getLongitude());
                if (nestPoint.getDistanceFrom(homePoint) < 0.005) {
                    continue;
                }

                foundIntersection = true;
            }

            System.out.println("Targetting nest at "+intersection);
            NestLayingEnemy nestLayingEnemy = new NestLayingEnemy(parent, new Point(intersection.getLatitude(), intersection.getLongitude()));
            parent.addClockTickHandler(nestLayingEnemy);
            enemyGroups.get(0).addEnemy(nestLayingEnemy);
            System.out.println("now "+enemyGroups.get(0).getEnemies().size());
        }
    }

    @Override
    public void kill() {

    }

    @Override
    public boolean isKilled() {
        return false;
    }
}
