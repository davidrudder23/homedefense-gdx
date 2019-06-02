package org.noses.games.homedefense.ui;

public interface MouseHandler {

    public boolean onClick(int x, int y);

    public boolean onRightClick(int x, int y);

    public boolean onClickUp();

    public boolean onMouseDragged(int x, int y);

    // Commenting this out, because I think it's better for mobile
    // if I don't use mouse-moved
    //public void onMouseMoved();
}
