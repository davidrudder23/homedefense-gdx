package org.noses.games.homedefense.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noses.games.homedefense.game.ClockTickHandler;
import org.noses.games.homedefense.game.MapScreen;
import org.noses.games.homedefense.nest.NestFactory;
import org.noses.games.homedefense.nest.NestLayingNest;

import java.lang.reflect.Constructor;

public class LevelEngine implements ClockTickHandler {

    double delayBetweenNests;
    MapScreen parent;

    boolean isKilled;

    double time;

    int nestFactoryNumber;

    int state;

    int levelNum;

    public static final int STATE_LEVEL_INTRO = 0;
    public static final int STATE_LEVEL_GAMEPLAY = 1;
    public static final int STATE_LEVEL_OUTRO = 2;

    double clockTick;

    FreeTypeFontGenerator generator;

    LevelConfig levelConfig;

    public LevelEngine(MapScreen parent, double delayBetweenNests) {
        this.parent = parent;
        this.delayBetweenNests = delayBetweenNests;
        this.time = delayBetweenNests;

        levelNum = 0;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/score.ttf"));

    }

    public boolean reset() {
        System.out.println("LevelEngine resetting");

        levelNum++;

        clockTick = 0;
        state = STATE_LEVEL_INTRO;

        isKilled = false;
        this.time = 0;
        nestFactoryNumber = 0;

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String debug = parent.isDebug() ? "debug/" : "";
            levelConfig = objectMapper.readValue(Gdx.files.internal("levels/" + debug + levelNum + ".json").reader(), LevelConfig.class);

            //nestFactories.add(new GroundEnemyNest.GroundEnemyNestFactory(parent, 1));
            //nestFactories.add(new ArmoredEnemyNest.ArmoredEnemyNestFactory(parent, 1));

            parent.addClockTickHandler(this);
        } catch (Exception anyExc) {
            anyExc.printStackTrace();
            return false;
        }
        return true;
    }

    private NestFactory getNestFactory(NestConfig nestConfig) {
        try {
            Class<?> cl = Class.forName("org.noses.games.homedefense.nest." + nestConfig.className + "EnemyNest" + "$" + nestConfig.className + "EnemyNestFactory");
            Constructor<?> cons = cl.getConstructor(MapScreen.class);
            return (NestFactory) cons.newInstance(parent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void clockTick(double delta) {
        if (isLevelDone()) {
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

        if (clockTick > 10) {
            clockTick = 0;
            state = STATE_LEVEL_GAMEPLAY;
            return;
        }

        clockTick += delta;
    }

    public void clockTickGamePlay(double delta) {

        if (levelConfig == null) {
            return;
        }

        if (levelConfig.getNestConfigs() == null) {
            return;
        }

        if (nestFactoryNumber >= levelConfig.getNestConfigs().size()) {
            return;
        }

        time += delta;

        for (NestConfig nestConfig : levelConfig.getNestConfigs()) {
            if (nestConfig.isUsed()) {
                continue;
            }

            if (nestConfig.getDelayBeforeStart() > time) {
                continue;
            }

            time = 0;

            NestFactory nestFactory = getNestFactory(nestConfig);
            nestConfig.setNestFactory(nestFactory);
            NestLayingNest nestLayingNest = new NestLayingNest(parent, nestFactory);
            nestConfig.setUsed(true);

            nestFactoryNumber++;
            parent.dropNest(nestLayingNest);
            parent.addClockTickHandler(nestLayingNest);
        }
    }

    public void clockTickOutro(double delta) {
        if (clockTick > 10) {
            isKilled = true;
            //parent.clockTick(0);
            parent.startNewLevel();
        }

        clockTick += delta;
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
        font.getData().scale((float) clockTick / 10);

        font.draw(batch, "Level " + levelNum, 100, 300);
    }

    public void renderGamePlay(Batch batch) {

    }

    public void renderOuttro(Batch batch) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 128;
        parameter.color = Color.BLACK;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        //font.getData().scale((float)clockTick/10);

        font.draw(batch, "Success", 100, 300);
    }

    @Override
    public void kill() {
        /*if (isKilled) {
            return;
        }*/

        state = STATE_LEVEL_OUTRO;

    }

    @Override
    public boolean isKilled() {
        return false;
    }

    public boolean isLevelDone() {
        if (levelConfig == null) {
            return false;
        }

        if (levelConfig.getNestConfigs() == null) {
            return false;
        }

        for (NestConfig nestConfig : levelConfig.getNestConfigs()) {
            if (!nestConfig.isUsed()) {
                return false;
            }
            NestFactory nestFactory = nestConfig.getNestFactory();
            if (nestFactory == null) {
                return false;
            }
            if (!nestFactory.isKilled()) {
                return false;
            }
        }

        kill();
        return true;
    }
}
