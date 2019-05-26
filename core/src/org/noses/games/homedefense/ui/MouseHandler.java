package org.noses.games.homedefense.ui;

public interface MouseHandler {

    public void onClick(int x, int y);

    public void onRightClick(int x, int y);

    public void onClickUp();

    public void onMouseDragged(int x, int y);

    // Commenting this out, because I think it's better for mobile
    // if I don't use mouse-moved
    //public void onMouseMoved();
}
