package org.noses.games.homedefense.bullet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.noses.games.homedefense.HomeDefenseGame;

public class NormalBullet extends BaseBullet {

    public NormalBullet(HomeDefenseGame parent, int x, int y, double angle, double speed) {
        super(parent, "bullet_small.png", 16, 16);
        currentX = x;
        currentY = y;
        originalX = x;
        originalY = y;

        this.angle = angle;
        this.speed = speed;
    }

    public int getRadius() {
        return 5;
    }
}
