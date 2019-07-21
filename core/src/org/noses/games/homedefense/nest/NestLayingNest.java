package org.noses.games.homedefense.nest;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.NestLayingEnemy;
import org.noses.games.homedefense.enemy.NestLayingEnemyGroup;
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

    boolean killed;

    NestFactory nestFactory;

    public NestLayingNest(MapScreen parent, NestFactory nestFactory) {
        super(parent, "splitting", 0, 1, 0, 0);
        this.parent = parent;
        this.nestFactory = nestFactory;

        makingNest = false;
        org.noses.games.homedefense.enemy.NestLayingEnemyGroup enemyGroup = new NestLayingEnemyGroup();
        enemyGroups = new ArrayList<>();
        enemyGroups.add(enemyGroup);
        //parent.addClockTickHandler(enemyGroups.get(0));
        killed = false;

        makeNest();
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
    }

    public void makeNest() {
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
            System.out.println("Testing intersection " + intersection);

            if (!parent.isGoodLocationForNest(intersection.getNode())) {
                continue;
            }

            foundIntersection = true;
        }

        //System.out.println("Targetting nest at "+intersection);
        NestLayingEnemy nestLayingEnemy = new NestLayingEnemy(parent, new Point(intersection.getLatitude(), intersection.getLongitude()), nestFactory);
        parent.addClockTickHandler(nestLayingEnemy);
        enemyGroups.get(0).addEnemy(nestLayingEnemy);
    }

    @Override
    public void kill() {
        killed = true;
    }

    @Override
    public boolean isKilled() {
        return killed;
    }

    @Override
    public String toString() {
        return "Nest laying nest";
    }
}
