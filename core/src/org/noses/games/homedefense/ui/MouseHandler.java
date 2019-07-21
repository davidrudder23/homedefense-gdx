package org.noses.games.homedefense.ui;

public interface MouseHandler {

    int getZ();

    boolean onClick(int x, int y);

    boolean onRightClick(int x, int y);

    boolean onClickUp(int x, int y);

    boolean onMouseDragged(int x, int y);

    boolean mouseMoved(int x, int y);

    // Commenting this out, because I think it's better for mobile
    // if I don't use mouse-moved
    //public void onMouseMoved();
}
