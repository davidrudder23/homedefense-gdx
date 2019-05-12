package org.noses.games.homedefense.bullet;

import com.badlogic.gdx.utils.Timer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.enemy.Animation;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.geometry.Rectangle;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBullet extends Animation  {

    protected int originalX;
    protected int originalY;

    protected int currentX;
    protected int currentY;

    protected double angle;
    protected double speed;

    protected double distanceTraveled;

    Timer.Task timer;

    public BaseBullet(HomeDefenseGame parent, String spriteFilename, int tileWidth, int tileHeight) {
        super(parent, spriteFilename, tileWidth, tileHeight);

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

    public abstract int getRadius();

    public void move(float delta) {
        distanceTraveled += delta*speed;

        double rad = angle* Math.PI/180;
        currentX = (int)(originalX+(Math.cos(rad)*distanceTraveled));
        currentY = (int)(originalY+(Math.sin(rad)*distanceTraveled));

        List<Enemy> hitEnemies = whichEnemiesHit();
    }

    public List<Enemy> whichEnemiesHit() {
        List<Enemy> enemiesHit = new ArrayList<>();

        Rectangle boundingBox = new Rectangle(currentX, currentY, currentX+getRadius(), currentY+getRadius());

        for (Enemy enemy: parent.getEnemies()) {

        }

        return enemiesHit;
    }
}
