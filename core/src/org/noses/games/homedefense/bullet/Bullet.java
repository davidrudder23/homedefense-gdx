package org.noses.games.homedefense.bullet;

import com.badlogic.gdx.audio.Sound;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Map;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.game.Actor;
import org.noses.games.homedefense.game.Animation;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;

import java.util.ArrayList;
import java.util.List;

public abstract class Bullet extends Actor {

    protected double originalLatitude;
    protected double originalLongitude;

    protected double currentLatitude;
    protected double currentLongitude;

    protected double angle;
    double rad;
    protected double speed;

    protected double distanceTraveled;

    Sound shotSound;

    boolean dead;

    public Bullet(MapScreen parent, String spriteFilename, Sound shotSound, int tileWidth, int tileHeight, double scale, double angle) {
        super(parent);

        addState("attack", true, spriteFilename, tileWidth, tileHeight, scale, true);

        this.shotSound = shotSound;
        this.angle = angle;
        rad = angle * Math.PI / 180;

        dead = false;
    }

    public void clockTick(double delta) {
        move(delta);
    }

    public double getLatitude() {
        return currentLatitude;
    }

    public double getLongitude() {
        return currentLongitude;
    }

    public abstract int getDamage();

    public void shoot() {
        shotSound.play();
    }

    public abstract double getRadius();

    public void move(double delta) {
        if (dead) {
            return;
        }

        distanceTraveled += delta * speed * HomeDefenseGame.LATLON_MOVED_IN_1ms_1mph;

        currentLatitude = originalLatitude + (Math.cos(rad) * distanceTraveled);
        currentLongitude = originalLongitude + (Math.sin(rad) * distanceTraveled);

        Map map = parent.getMap();
        if ((currentLatitude < map.getSouth()) ||
                (currentLongitude < map.getWest()) ||
                (currentLatitude > map.getNorth()) ||
                (currentLongitude > map.getEast())) {
            kill();
        }

        List<Enemy> hitEnemies = whichEnemiesHit();
    }

    @Override
    public void kill() {
        dead = true;
    }

    @Override
    public boolean isKilled() {
        return dead;
    }

    public List<Enemy> whichEnemiesHit() {
        List<Enemy> enemiesHit = new ArrayList<>();

        double halfRadius = getRadius()/2 * HomeDefenseGame.ONE_PIXEL_IN_LATLON;

        Rectangle boundingBox = new Rectangle(currentLatitude -halfRadius, currentLongitude -halfRadius, currentLatitude + halfRadius, currentLongitude + halfRadius);

        for (Enemy enemy : parent.getEnemies()) {
            if (enemy.isKilled()) {
                continue;
            }
            
            if (enemy.getBoundingBox().doBoundsOverlap(boundingBox)) {
                enemy.hit(getDamage());
                this.kill();
            }
        }

        return enemiesHit;
    }

    @Override
    public String toString() {
        return "Bullet at "+new Point(getLatitude(), getLongitude());
    }
}
