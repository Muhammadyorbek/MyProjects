package com.darkarena.game;

import com.badlogic.gdx.Game;
import com.darkarena.game.screens.MenuScreen;

public class GameApp extends Game {

    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
