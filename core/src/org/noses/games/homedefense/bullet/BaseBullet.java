package org.noses.games.homedefense.bullet;

import lombok.AllArgsConstructor;
import lombok.Builder;

public abstract class BaseBullet {

    int x;
    int y;

    float directionHorizontal;
    float directionVertical;
    float pixelsPerClockTick;

    public void move(float delta) {
        float pixelsThisMove = pixelsPerClockTick*delta;
    }
}
