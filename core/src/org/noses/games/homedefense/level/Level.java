package org.noses.games.homedefense.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.nest.*;

import java.util.ArrayList;
import java.util.List;

public class Level implements ClockTickHandler {

    private List<NestFactory> nestFactories;
    double delayBetweenNests;
    MapScreen parent;

    boolean isKilled;

    double time;

    int nestFactoryNumber;

    int state;

    int levelNum;

    public static final int STATE_LEVEL_INTRO=0;
    public static final int STATE_LEVEL_GAMEPLAY=1;
    public static final int STATE_LEVEL_OUTRO=2;

    double clockTick;

    FreeTypeFontGenerator generator;

    public Level(MapScreen parent, double delayBetweenNests) {
        this.parent = parent;
        this.delayBetweenNests = delayBetweenNests;
        this.time = delayBetweenNests;

        levelNum = 0;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/score.ttf"));
    }

    public void reset() {
        System.out.println("Level resetting");

        levelNum++;

        clockTick = 0;
        state = STATE_LEVEL_INTRO;

        isKilled = false;
        this.time = delayBetweenNests;

        nestFactoryNumber = 0;
        nestFactories = new ArrayList<>();
        nestFactories.add(new SplittingEnemyNest.SplittingEnemyNestFactory(parent, 1));
        nestFactories.add(new GroundEnemyNest.GroundEnemyNestFactory(parent, 1));
        nestFactories.add(new ArmoredEnemyNest.ArmoredEnemyNestFactory(parent, 1));

        parent.addClockTickHandler(this);
    }

    @Override
    public void clockTick(double delta) {
        if (isKilled()) {
            kill();
        }

        if (state == STATE_LEVEL_INTRO) {
            clockTickIntro(delta);
        } else if (state == STATE_LEVEL_GAMEPLAY) {
            clockTickGamePlay(delta);
        } else if (state == STATE_LEVEL_OUTRO) {
            clockTickOutro(delta);
        }
    }

    public void clockTickIntro(double delta) {

        if (clockTick>10) {
            state = STATE_LEVEL_GAMEPLAY;
        }

        clockTick+= delta;
    }

    public void clockTickGamePlay(double delta) {

        if (nestFactoryNumber >= nestFactories.size()) {
            return;
        }

        time += delta;

        if (time > delayBetweenNests) {
            System.out.println("Laying new NestLayingNest with factory(" + nestFactoryNumber + ")=" + nestFactories.get(nestFactoryNumber));
            time = 0;

            NestLayingNest nestLayingNest = new NestLayingNest(parent, nestFactories.get(nestFactoryNumber));
            nestFactoryNumber++;
            parent.dropNest(nestLayingNest);
            parent.addClockTickHandler(nestLayingNest);
        }
    }

    public void clockTickOutro(double delta) {

    }

    public void render(Batch batch) {
        if (state == STATE_LEVEL_INTRO) {
            renderIntro(batch);
        } else if (state == STATE_LEVEL_GAMEPLAY) {
            renderGamePlay(batch);
        } else if (state == STATE_LEVEL_OUTRO) {
            renderOuttro(batch);
        }
    }

    public void renderIntro(Batch batch) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 128;
        parameter.color = Color.BLACK;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        font.getData().scale((float)clockTick/10);

        font.draw(batch, "Level "+levelNum, 100, 300);
    }

    public void renderGamePlay(Batch batch) {

    }

    public void renderOuttro(Batch batch) {
        isKilled = true;
        parent.clockTick(0);
        parent.startNewLevel();
    }



    @Override
    public void kill() {
        if (isKilled) {
            return;
        }

        state = STATE_LEVEL_OUTRO;

    }

    @Override
    public boolean isKilled() {
        for (NestFactory nestFactory : nestFactories) {
            if (!nestFactory.isKilled()) {
                //System.out.println("Level not killed because " + nestFactory);
                return false;
            }
        }

        kill();
        return true;
    }
}
