package com.darkarena.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.darkarena.game.GameApp;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;



public class MenuScreen implements Screen {
    private static final float WORLD_WIDTH = 1280;
    private static final float WORLD_HEIGHT = 720;
    private final GameApp game;
//    private SpriteBatch batch;
//    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private TextButton playButton;
    private TextButton settingsButton;
    private TextButton exitButton;

    public MenuScreen(GameApp game) {
        this.game = game;
    }

    @Override
    public void show() {

//        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
//        camera = new OrthographicCamera();
        background = new Texture("background.png");

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        TextButton.TextButtonStyle customStyle = new TextButton.TextButtonStyle();

        customStyle.up = new TextureRegionDrawable(
            new TextureRegion(new Texture("ui/button_dark.png"))
        );

        customStyle.down = new TextureRegionDrawable(
            new TextureRegion(new Texture("ui/button_pressed.png"))
        );

        customStyle.font = skin.getFont("font");

        playButton = new TextButton("PLAY", customStyle);
        settingsButton = new TextButton("SETTINGS", customStyle);
        exitButton = new TextButton("EXIT", customStyle);

        playButton.getLabel().setColor(0.95f, 0.84f, 0.64f, 1f);
        settingsButton.getLabel().setColor(0.95f, 0.84f, 0.64f, 1f);
        exitButton.getLabel().setColor(0.95f, 0.84f, 0.64f, 1f);
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        Image bg = new Image(new Texture("background.png"));
        bg.setFillParent(true);
        stage.addActor(bg);

        Table table = new Table();
        table.setFillParent(true);
        table.padRight(700).padTop(360);

        table.add(playButton).width(300).height(60).pad(10);
        table.row();
        table.add(settingsButton).width(300).height(60).pad(10);
        table.row();
        table.add(exitButton).width(300).height(60).pad(10);

        stage.addActor(table);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }


//    @Override
//public void render(float delta) {
//    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//    camera.update();
//    batch.setProjectionMatrix(camera.combined);
//    if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) &&
//        Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
//
//        if (Gdx.graphics.isFullscreen()) {
//            Gdx.graphics.setWindowedMode(1280, 720);
//        } else {
//            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
//        }
//    }
//
//    batch.begin();
//    batch.draw(background, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
//    batch.end();
//    stage.getBatch().begin();
//    stage.getBatch().end();
//
//    stage.act(delta);
//    stage.draw();
//}
@Override
public void render(float delta) {

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) &&
        Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(1280, 720);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
    }

    stage.act(delta);
    stage.draw();
}




    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
        skin.dispose();
    }
}
