package org.noses.games.homedefense.game;

import com.badlogic.gdx.audio.Sound;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public abstract class Actor implements ClockTickHandler {
    protected MapScreen parent;

    Map<String, ActorState> states;

    ActorState defaultState;

    ActorState currentState;

    public Actor(MapScreen parent) {
        this.parent = parent;

        states = new HashMap<>();
    }

    public void addState(ActorState state, boolean isDefault ) {
        states.put(state.getName(), state);

        if (isDefault || (defaultState == null)) {
            defaultState = state;
        }
    }

    public ActorState getState(String identifier) {
        ActorState state = states.get(identifier);
        if (state == null) {
            return defaultState;
        }

        return  state;
    }

    public void setCurrentState(String identifier) {
        currentState = getState(identifier);
    }

    public void addState(String stateName, boolean isDefault, String spriteFilename, int tileWidth, int tileHeight, double scale, boolean loop) {
        Animation animation = new Animation(spriteFilename, tileWidth, tileHeight, scale, loop);
        ActorState attackState = new ActorState();
        attackState.setName(stateName);
        attackState.setAnimation(animation);
        addState(attackState, isDefault);

    }

    public ActorState getState() {
        if (currentState == null) {
            return defaultState;
        }

        return currentState;
    }

    public Animation getAnimation() {
        return getState().getAnimation();
    }

    @Override
    public void clockTick(double delta) {
        getState().getAnimation().clockTick(delta);
    }

    @Override
    public void kill() {
        getState().getAnimation().kill();
    }

    @Override
    public boolean isKilled() {
        return getState().getAnimation().isKilled();
    }

    @Getter
    @Setter
    public static class ActorState {
        String name;
        Animation animation;
    }

}

