package org.noses.games.homedefense.bullet;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Animation;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Rectangle;

import java.util.ArrayList;
import java.util.List;

public abstract class Bullet extends Animation {

    protected int originalX;
    protected int originalY;

    protected int currentX;
    protected int currentY;

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

    public int getX() {
        return currentX;
    }

    public int getY() {
        return currentY;
    }

    public void shoot() {
        shotSound.play();
    }

    public abstract int getRadius();

    public void move(float delta) {

        if (dead) {
            return;
        }

        distanceTraveled += delta * speed;

        double rad = angle * Math.PI / 180;
        currentX = (int) (originalX + (Math.cos(rad) * distanceTraveled));
        currentY = (int) (originalY + (Math.sin(rad) * distanceTraveled));

        if ((currentX < 0) ||
                (currentY < 0) ||
                (currentX > parent.getScreenWidth()) ||
                (currentY > parent.getScreenHeight())) {
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

        int halfRadius = getRadius()/2;

        Rectangle boundingBox = new Rectangle(currentX-halfRadius, currentY-halfRadius, currentX + halfRadius, currentY + halfRadius);
        //System.out.println ("Bullet's bounding box is "+boundingBox);

        for (Enemy enemy : parent.getEnemies()) {
            //System.out.println ("Bullet "+boundingBox+" vs Enemy "+enemy.getBoundingBox()+"="+enemy.getBoundingBox().doBoundsOverlap(boundingBox));
            if (enemy.getBoundingBox().doBoundsOverlap(boundingBox)) {
                System.out.println("Bullet hit " + enemy.getLocation());
                enemy.kill();
                this.kill();
            }
        }

        return enemiesHit;
    }
}
