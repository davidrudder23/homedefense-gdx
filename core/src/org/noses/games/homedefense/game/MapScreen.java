package org.noses.games.homedefense.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.client.Node;
import org.noses.games.homedefense.client.Way;
import org.noses.games.homedefense.enemy.Enemy;
import org.noses.games.homedefense.enemy.EnemyGroup;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.tower.Tower;

import java.util.List;

public class MapScreen extends Screen {
    HomeDefenseGame parent;

    public MapScreen(HomeDefenseGame parent) {
        this.parent = parent;
    }

    public void render(Batch batch) {
        List<EnemyGroup> enemyGroups = parent.getEnemyGroups();
        ShapeRenderer sr = new ShapeRenderer();
        sr.setColor(Color.WHITE);
        //sr.setProjectionMatrix(camera.combined);

        for (Way way : parent.getMap().getWays()) {
            Gdx.gl.glLineWidth(way.getMaxSpeed() - 24);

            sr.setColor(Color.WHITE);
            sr.setColor(way.getColor());

            sr.begin(ShapeRenderer.ShapeType.Line);
            Node prevNode = null;
            for (Node node : way.getNodes()) {
                if (prevNode != null) {
                    //System.out.println("Writing line starting at "+prevNode.getLat()+"x"+prevNode.getLon()+" - "+
                    //              convertLatToY(prevNode.getLat())+"x"+convertLongToX(prevNode.getLon()));
                    sr.line(parent.convertLongToX(prevNode.getLon()), parent.convertLatToY(prevNode.getLat()),
                            parent.convertLongToX(node.getLon()), parent.convertLatToY(node.getLat()));
                }
                prevNode = node;
            }
            sr.end();
        }

        batch.begin();

        // render the enemies

        for (EnemyGroup enemyGroup : enemyGroups) {
            List<Enemy> enemies = enemyGroup.getEnemies();
            for (Enemy enemy : enemies) {
                Point location = enemy.getLocation();

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                //batch.draw(enemy.getFrameTextureRegion(), x, y);

                Sprite sprite = new Sprite(enemy.getFrameTextureRegion());

                sprite.setCenterY(parent.convertLatToY(latitude));
                sprite.setCenterX(parent.convertLongToX(longitude));
                sprite.draw(batch);

            }
        }

        parent.getHome().render(batch);

        // render the score and other text
        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.draw(batch, "Health: " + parent.getHome().getHealth(), 10, Gdx.graphics.getHeight() - 30);

        font.draw(batch, "Money: " + parent.getMoney(), 10, Gdx.graphics.getHeight() - (35 + font.getCapHeight()));

        font.draw(batch, "Speed: " + parent.getSpeedMultiplier() + "x", 10, Gdx.graphics.getHeight() - (40 + (font.getCapHeight() * 2)));

        // render the towers
        for (Tower tower: parent.getTowers()) {
            tower.render(batch);
        }


        // render the pie menu

        if (!parent.getTowerChoiceMenu().isHidden()) {
            parent.getTowerChoiceMenu().renderMenu(batch);
        }


        batch.end();
    }

}
