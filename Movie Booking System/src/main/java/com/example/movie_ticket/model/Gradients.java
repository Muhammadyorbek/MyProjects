package com.example.movie_ticket.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class Gradients {
    static Stop[] stops = new Stop[] {
            new Stop(0.0, Color.web("#4A4A4A")),
            new Stop(0.3, Color.web("#3A3A3A")),
            new Stop(0.6, Color.web("#2F2F2F")),
            new Stop(1.0, Color.web("#242424"))
    };
    public static LinearGradient gradient1 = new LinearGradient(
            0,
            Math.sin(Math.toRadians(45)),
            Math.cos(Math.toRadians(45)),
            0,
            true,
            CycleMethod.NO_CYCLE,
            stops
    );
}
