package com.calculator.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class CalculatorScreen implements Screen {
    private final CalculatorApplication calculatorApplication;
    private Calculator calculator = new Calculator();
    private Stage stage;
    private Skin skin;
    private Image background;
    private ArrayList<TextButton> buttons = new ArrayList<TextButton>();
    private CalculatorArray calculatorArray = new CalculatorArray();
    private String currentInput = "";
    private boolean hasOperation = false;
    public CalculatorScreen(CalculatorApplication calculatorApplication) {
        this. calculatorApplication = calculatorApplication;
    }
    @Override
    public void show() {
        stage = new Stage(new FitViewport(340, 510));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("UI/uiskin.json"));

        TextButton.TextButtonStyle customStyle1 = new TextButton.TextButtonStyle();
        customStyle1.font = skin.getFont("font");
        customStyle1.up = new TextureRegionDrawable(
            new TextureRegion(new Texture("Buttons/buttonStayed.png"))
        );
        customStyle1.down = new TextureRegionDrawable(
            new TextureRegion(new Texture("Buttons/buttonPressed.png"))
        );

        TextButton.TextButtonStyle customStyle2 = new TextButton.TextButtonStyle();
        customStyle2.font = skin.getFont("font");
        customStyle2.up = new TextureRegionDrawable(
            new TextureRegion(new Texture("Buttons/buttonPressed.png"))
        );
        customStyle2.down = new TextureRegionDrawable(
            new TextureRegion(new Texture("Buttons/buttonStayed.png"))
        );

        TextButton.TextButtonStyle equalStyle = new TextButton.TextButtonStyle();
        equalStyle.font = skin.getFont("font");
        equalStyle.up = new TextureRegionDrawable(
            new TextureRegion(new Texture("Buttons/buttonEqual.png"))
        );
        equalStyle.down = new TextureRegionDrawable(
            new TextureRegion(new Texture("Buttons/buttonStayed.png"))
        );

        buttons.add(new TextButton(calculatorArray.atIndex[0], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[1], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[2], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[3], equalStyle));
        buttons.add(new TextButton(calculatorArray.atIndex[4], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[5], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[6], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[7], customStyle2));
        buttons.add(new TextButton(calculatorArray.atIndex[8], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[9], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[10], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[11], customStyle2));
        buttons.add(new TextButton(calculatorArray.atIndex[12], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[13], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[14], customStyle1));
        buttons.add(new TextButton(calculatorArray.atIndex[15], customStyle2));
        buttons.add(new TextButton(calculatorArray.atIndex[16], customStyle2));
        buttons.add(new TextButton(calculatorArray.atIndex[17], customStyle2));
        buttons.add(new TextButton(calculatorArray.atIndex[18], customStyle2));
        buttons.add(new TextButton(calculatorArray.atIndex[19], customStyle2));

        background = new Image(new Texture("UI/background.png"));
        background.setFillParent(true);
        stage.addActor(background);

        Label display = new Label("", skin);
        display.setFontScale(3f);
        display.setAlignment(Align.right);
        display.setPosition(315, (float)(315 + 50));
        stage.addActor(display);

        Table table = new Table();
        table.setFillParent(true);
        table.setPosition(0, 0);

        for (int i = 16; i >= -1; i++) {
            table.add(buttons.get(i)).width(85).height(62.5f).pad(0);
            if (i == 3) {break;}
            if ((i + 1) % 4 == 0) {
                table.row();
                i -= 8;
            }
        }

        table.bottom();

        stage.addActor(table);
        buttons.get(0).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if (!hasOperation) {
                    display.setText(calculator.minusValueOperation(currentInput));
                    currentInput = String.valueOf(display.getText());
                }
                else {
                    currentInput = calculator.operation(currentInput);
                    updateDisplay(display);
                    hasOperation = false;
                }
            }
        });
        //Completed
        buttons.get(1).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[1];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(2).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentInput += calculatorArray.atIndex[2];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(3).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (hasOperation) {
                    currentInput = calculator.operation(currentInput);
                    updateDisplay(display);
                    hasOperation = false;
                }
            }
        });
        //Completed
        buttons.get(4).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[4];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(5).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[5];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(6).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[6];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(7).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!hasOperation) {
                    currentInput += calculatorArray.atIndex[7];
                    updateDisplay(display);
                    hasOperation = true;
                }
                else {
                    currentInput = calculator.operation(currentInput);
                    currentInput += calculatorArray.atIndex[7];
                    updateDisplay(display);
                }
            }
        });
        //Completed
        buttons.get(8).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[8];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(9).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[9];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(10).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[10];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(11).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!hasOperation) {
                    currentInput += calculatorArray.atIndex[11];
                    updateDisplay(display);
                    hasOperation = true;
                }
                else {
                    currentInput = calculator.operation(currentInput);
                    currentInput += calculatorArray.atIndex[11];
                    updateDisplay(display);
                }
            }
        });
        //Completed
        buttons.get(12).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[12];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(13).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[13];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(14).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (currentInput.equals("0")) currentInput = "";
                currentInput += calculatorArray.atIndex[14];
                updateDisplay(display);
            }
        });
        //Completed
        buttons.get(15).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!hasOperation) {
                    currentInput += calculatorArray.atIndex[15];
                    updateDisplay(display);
                    hasOperation = true;
                }
                else {
                    currentInput = calculator.operation(currentInput);
                    currentInput += calculatorArray.atIndex[15];
                    updateDisplay(display);
                }
            }
        });
        //Completed
        buttons.get(16).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!hasOperation) {
                    currentInput += "^";
                    updateDisplay(display);
                    hasOperation = true;
                }
                else {
                    currentInput = calculator.operation(currentInput);
                    currentInput += calculatorArray.atIndex[16];
                    updateDisplay(display);
                }
            }
        });
        //Completed
        buttons.get(17).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!hasOperation) {
                    currentInput = calculator.absValueOperation(currentInput);
                    updateDisplay(display);
                }
                else {
                    currentInput = calculator.operation(currentInput);
                    updateDisplay(display);
                    hasOperation = false;
                }
            }
        });
        //Completed
        buttons.get(18).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!hasOperation) {
                    currentInput = calculator.factorialOperation(currentInput);
                    updateDisplay(display);
                }
                else {
                    currentInput = calculator.operation(currentInput);
                    updateDisplay(display);
                    hasOperation = false;
                }
            }
        });
        //Completed
        buttons.get(19).addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!hasOperation) {
                    currentInput += calculatorArray.atIndex[19];
                    updateDisplay(display);
                    hasOperation = true;
                }
                else {
                    currentInput = calculator.operation(currentInput);
                    currentInput += calculatorArray.atIndex[19];
                    updateDisplay(display);
                }
            }
        });
        //Completed
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }
    @Override
    public void resize(int width, int height) {stage.getViewport().update(width, height, true);}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
    private void updateDisplay(Label display) {
        display.setText(currentInput);
        if (currentInput.length() >= 10 && currentInput.length() < 14) display.setFontScale(2.5f);
        if (currentInput.length() >= 14 && currentInput.length() < 18) display.setFontScale(2.2f);
        if (currentInput.length() >= 18 && currentInput.length() < 22) display.setFontScale(1.9f);
        if (currentInput.length() >= 22 && currentInput.length() < 26) display.setFontScale(1.6f);
    }
}
//button height 85, width 62.5
