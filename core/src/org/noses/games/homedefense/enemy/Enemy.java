package org.noses.games.homedefense.enemy;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import org.noses.games.homedefense.HomeDefenseGame;
import org.noses.games.homedefense.game.*;
import org.noses.games.homedefense.geometry.Point;
import org.noses.games.homedefense.geometry.Rectangle;
import org.noses.games.homedefense.tower.Tower;

public abstract class Enemy extends Actor implements ClockTickHandler, PhysicalObject {

    @Getter
    private int health;

    private boolean killed = false;

    Sound hitSound;

    double upgradePercentage;

    protected Enemy(BattleScreen parent, String spriteFilename, Sound hitSound, int tileWidth, int tileHeight, double scale, int startingHealth) {
        super(parent);

        addState("attack", true, spriteFilename, tileWidth, tileHeight, scale, true);
        addState("hit", false, "enemy/hit.png", 512, 512, scale, false);
        addState("die", false, "enemy/die.png", 576, 561, scale, false);
        setCurrentState("attack");

        this.health = startingHealth;
        this.hitSound = hitSound;
        this.upgradePercentage = 0;
    }

    public void hit(int damage) {
        if (dying || killed) {
            return;
        }
        runNext(() -> {
            setCurrentState("attack");
        });
        setCurrentState("hit");
        health -= damage;
        if (health<=0) {
            kill();
        }

        hitSound.play();
    }

    public double getUpgradePercentage() {
        return upgradePercentage;
    }

    public void upgrade(double percentageToBeAdded) {
        this.upgradePercentage += percentageToBeAdded;
    }

    public void kill() {
        runNext(() -> {
            System.out.println("Dying done, killed=true");
            killed = true;
            dying = false;
        });
        dying = true;
        setCurrentState("die");
        parent.addMoney(getValue());
    }

    public boolean isKilled() {
        return killed;
    }

    public abstract int getDamage();

    public abstract int getValue();

    public abstract Point getLocation();

    public abstract boolean canBeHitBy(Tower tower);

    public abstract boolean canBeHitByHome();

    public Sprite getSprite() {
        Animation animation = getAnimation();
        Sprite sprite = new Sprite(animation.getFrameTextureRegion());

        sprite.setScale((float) parent.getSpriteScale(sprite, animation.getScale()));

        sprite.setCenterY(parent.convertLatToY(getLocation().getLatitude()));
        sprite.setCenterX(parent.convertLongToX(getLocation().getLongitude()));

        return sprite;
    }

    public Rectangle getBoundingBox() {
        Animation animation = getAnimation();
        double halfWidth = HomeDefenseGame.ONE_PIXEL_IN_LATLON * animation.getTileWidth()* parent.getScaledPixels(animation.getTileWidth(), animation.getScale()) / 2;
        double halfHeight = HomeDefenseGame.ONE_PIXEL_IN_LATLON * animation.getTileHeight() * parent.getScaledPixels(animation.getTileWidth(), animation.getScale()) / 2;

        Rectangle boundingBox = new Rectangle(getLocation().getLatitude()-halfWidth,
                getLocation().getLongitude()-halfHeight,
                getLocation().getLatitude()+halfWidth,
                getLocation().getLongitude()+halfHeight);
        return boundingBox;
    }
}