package org.noses.games.homedefense.enemy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.WayDTO;

import java.awt.*;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class GroundEnemy extends Enemy {
    private final float baseSpeed = 1 / 25f;
    private WayDTO wayDTO;
    private float progressAlong = 0;
    private float direction;

    public GroundEnemy(HomeDefenseGame parent, WayDTO wayDTO, String spriteFilename, int tileWidth, int tileHeight) {
        super(parent, spriteFilename, tileWidth, tileHeight);
        this.wayDTO = wayDTO;
        progressAlong = 0;
        direction = 1;

    }

    public Point getLocation() {
        if (wayDTO == null) {
            return null;
        }

        Point firstPoint = wayDTO.firstPoint();
        Point lastPoint = wayDTO.lastPoint();

        double currentX = ((lastPoint.getX() - firstPoint.getX()) * progressAlong) + firstPoint.getX();
        double currentY = ((lastPoint.getY() - firstPoint.getY()) * progressAlong) + firstPoint.getY();

        return new Point((int) currentX, (int) currentY);
    }

    public void clockTick(float delta) {
        float speed = wayDTO.getMaxSpeed();
        progressAlong += direction * baseSpeed * delta * speed;
        if (progressAlong < 0) {
            progressAlong = 0;
            direction = 1;
        } else if (progressAlong > 1) {
            progressAlong = 1;
            direction = -1;
        }
    }
}
