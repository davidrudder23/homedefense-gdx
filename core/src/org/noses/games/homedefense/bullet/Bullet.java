package org.noses.games.homedefense.bullet;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Map;
import org.noses.games.homedefense.enemy.Animation;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Rectangle;

import java.util.ArrayList;
import java.util.List;

public abstract class Bullet extends Animation {

    protected double originalLatitude;
    protected double originalLongitude;

    protected double currentLatitude;
    protected double currentLongitude;

    protected double angle;
    protected double speed;

    protected double distanceTraveled;

    Timer.Task timer;

    Sound shotSound;

    @Getter
    boolean dead;

    public Bullet(HomeDefenseGame parent, String spriteFilename, Sound shotSound, int tileWidth, int tileHeight) {
        super(parent, spriteFilename, tileWidth, tileHeight);

        this.shotSound = shotSound;

        dead = false;

        timer = Timer.schedule(new Timer.Task() {
                                   @Override
                                   public void run() {
                                       clockTick(0.1f);
                                   }
                               },
                0f, 1 / 10.0f);
    }

    public void clockTick(float delta) {
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

    public abstract int getRadius();

    public void move(double delta) {

        if (dead) {
            return;
        }

        distanceTraveled += delta * speed;

        double rad = angle * Math.PI / 180;
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

    private void kill() {
        dead = true;
        timer.cancel();
    }

    public List<Enemy> whichEnemiesHit() {
        List<Enemy> enemiesHit = new ArrayList<>();

        double halfRadius = getRadius()/2;

        Rectangle boundingBox = new Rectangle(currentLatitude -halfRadius, currentLongitude -halfRadius, currentLatitude + halfRadius, currentLongitude + halfRadius);
        //System.out.println ("Bullet's bounding box is "+boundingBox);

        for (Enemy enemy : parent.getEnemies()) {
            //System.out.println ("Bullet "+boundingBox+" vs Enemy "+enemy.getBoundingBox()+"="+enemy.getBoundingBox().doBoundsOverlap(boundingBox));
            if (enemy.getBoundingBox().doBoundsOverlap(boundingBox)) {
                System.out.println("Bullet hit " + enemy.getLocation()+" with "+enemy.getHealth()+" health.  Hitting with "+getDamage());
                enemy.hit(getDamage());
                this.kill();
            }
        }

        return enemiesHit;
    }
}
