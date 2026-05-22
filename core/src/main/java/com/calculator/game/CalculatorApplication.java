package com.calculator.game;

import com.badlogic.gdx.Game;

//** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class CalculatorApplication extends Game {

    @Override
    public void create() {
        setScreen(new CalculatorScreen(this));
    }
}
