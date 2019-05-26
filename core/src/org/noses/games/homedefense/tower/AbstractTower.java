package org.noses.games.homedefense.tower;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Data;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.Animation;
import org.noses.games.homedefense.geometry.Point;

@Data
public abstract class AbstractTower implements Tower {
    double bulletSpeed;
    double delayBetweenShots;

    private Point location;

    HomeDefenseGame parent;
    String towerName;
    Animation animation;

    public AbstractTower(HomeDefenseGame parent, String towerName, double longitude, double latitude) {
        this.towerName = towerName;
        this.parent = parent;

        animation = new Animation(parent, "tower/" + towerName + ".png", 199, 199, true);
        parent.addClockTickHandler(animation);

        location = new Point(latitude, longitude);
    }

    @Override
    public double getLatitude() {
        return location.getLatitude();
    }

    @Override
    public double getLongitude() {
        return location.getLongitude();
    }

    @Override
    public void clockTick(double delta) {
    }

    @Override
    public void kill() {

    }

    @Override
    public boolean isKilled() {
        return false;
    }

    @Override
    public TextureRegion getFrameTextureRegion() {
        return animation.getFrameTextureRegion();
    }

}
