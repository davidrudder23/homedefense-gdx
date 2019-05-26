package org.noses.games.homedefense.tower;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.bullet.Bullet;
import org.noses.games.homedefense.bullet.NormalBullet;
import org.noses.games.homedefense.game.*;
import org.noses.games.homedefense.geometry.Point;

@Data
public abstract class Tower implements ClockTickHandler, PhysicalObject {
    double bulletSpeed;
    double delayBetweenShots;

    private Point location;

    HomeDefenseGame parent;
    String towerName;
    Animation animation;

    double timeSinceLastFired = 0;

    final int maxOnScreen = 15;

    Aimer aimer;
    Shooter shooter;

    public Tower(HomeDefenseGame parent, String towerName, double longitude, double latitude, Shooter shooter) {
        this.towerName = towerName;
        this.parent = parent;

        animation = new Animation(parent, "tower/" + towerName + ".png", 199, 199, true);
        parent.addClockTickHandler(animation);

        location = new Point(latitude, longitude);

        aimer = new Aimer(parent, latitude, longitude);
        this.shooter = shooter;
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public void clockTick(double delta) {
        shooter.setAngle(aimer.aim(aimer.findClosestEnemy(parent.getEnemies())));

        shooter.clockTick(delta);
    }

    public void render(Batch batch) {
        Sprite sprite = new Sprite(getFrameTextureRegion());
        sprite.setScale(50/sprite.getWidth());
        sprite.setCenterX(parent.convertLongToX(getLongitude()));
        sprite.setCenterY(parent.convertLatToY(getLatitude()));
        sprite.draw(batch);

        shooter.render(batch);
    }

    public abstract double minDistanceFromOtherTower();

    @Override
    public void kill() {

    }

    @Override
    public boolean isKilled() {
        return false;
    }

    public TextureRegion getFrameTextureRegion() {
        return animation.getFrameTextureRegion();
    }

}
