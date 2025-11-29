package com.aureobank.theme;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class VirtualKeypad {
    private final TextField field = new TextField();
    private final VBox view;

    public VirtualKeypad() {
        field.setPromptText("0.00");
        field.getStyleClass().add("keypad-field");
        field.setEditable(false);
        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8); grid.setPadding(new Insets(8));
        String[] keys = {"1","2","3","4","5","6","7","8","9",".","0","<"};
        int k=0; for (int r=0;r<4;r++) for (int c=0;c<3;c++) {
            String key = keys[k++];
            Button b = new Button(key); b.getStyleClass().add("key"); b.setPrefSize(60,60);
            b.setOnAction(e -> {
                if ("<".equals(key)) { String t = field.getText(); if (!t.isEmpty()) field.setText(t.substring(0,t.length()-1)); }
                else if (".".equals(key)) { if (!field.getText().contains(".")) field.setText(field.getText()+key); }
                else field.setText(field.getText()+key);
            });
            grid.add(b, c, r);
        }
        view = new VBox(8, field, grid);
    }

    public Node getView() { return view; }
    public TextField getField() { return field; }
}
