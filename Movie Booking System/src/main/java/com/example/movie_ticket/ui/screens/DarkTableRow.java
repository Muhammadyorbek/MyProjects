package com.example.movie_ticket.ui.screens;

import javafx.scene.control.TableRow;

public class DarkTableRow<T> extends TableRow<T> {
    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setStyle(null);
            return;
        }
        if (getIndex() % 2 == 0) {
            setStyle("-fx-background-color: " + UI.getDark());
        } else {
            setStyle("-fx-background-color: " + UI.getDarkColumn());
        }
    }
}