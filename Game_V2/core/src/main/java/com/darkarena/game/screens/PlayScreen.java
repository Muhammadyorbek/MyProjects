package com.darkarena.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.darkarena.game.GameApp;

public class PlayScreen implements Screen {

    private final GameApp game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public PlayScreen(GameApp game) {
        this.game = game;
    }

    @Override
    public void show() {
//        stage = new Stage(new FitViewport(1280, 720));
//        Gdx.input.setInputProcessor(stage);
//
//        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
//
//        Label label = new Label("THIS IS PLAY SCREEN", skin);
//        label.setPosition(500, 360);
//
//        stage.addActor(label);


        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Label label = new Label("THIS IS PLAY SCREEN", skin);
        label.setPosition(500, 360);

        stage.addActor(label);


        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        background = new Texture("play_background.png");
    }

    @Override
    public void render(float delta) {
//        Gdx.gl.glClearColor(0, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        stage.act(delta);
//        stage.draw();


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();


        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(background, 0, 0, 1280, 720);
        batch.end();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
//        stage.dispose();
//        skin.dispose();


        stage.dispose();
        skin.dispose();


        batch.dispose();
        background.dispose();
    }
}
