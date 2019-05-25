package org.noses.games.homedefense.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;

public class PieMenu implements MouseHandler {

    @Getter
    boolean hidden;

    private int x;
    private int y;

    public PieMenu() {

    }

    @Override
    public void onClick(int x, int y) {
        hidden = false;
        System.out.println("Pie Menu clicked");
    }

    @Override
    public void onRightClick(int x, int y) {

    }

    @Override
    public void onClickUp() {
        hidden = true;
        System.out.println("Pie Menu un-clicked");
    }

    public Sprite renderMenu(Batch batch) {
        Sprite sprite = new Sprite();

        return sprite;
    }

    @Override
    public void onMouseDragged(int x, int y) {
        
    }
}
