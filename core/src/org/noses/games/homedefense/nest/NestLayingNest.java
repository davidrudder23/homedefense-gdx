package org.noses.games.homedefense.nest;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.enemy.NestLayingEnemy;
import org.noses.games.homedefense.enemy.NestLayingEnemyGroup;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.home.Home;
import org.noses.games.homedefense.level.NestConfig;
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

    public NestLayingNest(MapScreen parent, NestConfig nestConfig, NestFactory nestFactory) {
        super(parent, nestConfig,
                new Point(0, 0)
        );
        this.parent = parent;
        this.nestFactory = nestFactory;

        makingNest = false;
        NestLayingEnemyGroup enemyGroup = new NestLayingEnemyGroup();
        enemyGroups = new ArrayList<>();
        enemyGroups.add(enemyGroup);
        //parent.addClockTickHandler(enemyGroups.get(0));
        killed = false;

    }

    @Override
    public double delayBetweenWaves() {
        return 0;
    }

    @Override
    public EnemyGroup getNewEnemyGroup(NestConfig nestConfig) {
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

    public Intersection getIntersection() {
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

            if (intersection == null) {
                continue;
            }

            if (!parent.isGoodLocationForNest(intersection.getNode())) {
                continue;
            }

            foundIntersection = true;
        }

        return intersection;
    }

    public void makeNest(Intersection intersection) {
        long start = System.currentTimeMillis();
        NestLayingEnemy nestLayingEnemy = new NestLayingEnemy(parent, new Point(intersection.getLatitude(), intersection.getLongitude()), nestConfig, nestFactory);
        long end = System.currentTimeMillis();
        System.out.println("Make nest 1 took "+(end-start)+" millis");

        parent.addClockTickHandler(nestLayingEnemy);
        end = System.currentTimeMillis();
        System.out.println("Make nest 2 took "+(end-start)+" millis");

        enemyGroups.get(0).addEnemy(nestLayingEnemy);
        end = System.currentTimeMillis();

        System.out.println("Make nest took "+(end-start)+" millis");
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
