package org.noses.games.homedefense.ui;

public interface MouseHandler {

    public int getZ();

    public boolean onClick(int x, int y);

    public boolean onRightClick(int x, int y);

    public boolean onClickUp(int x, int y);

    public boolean onMouseDragged(int x, int y);

    public boolean mouseMoved(int x, int y);

    // Commenting this out, because I think it's better for mobile
    // if I don't use mouse-moved
    //public void onMouseMoved();
}
