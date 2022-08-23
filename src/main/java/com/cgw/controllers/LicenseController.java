package com.cgw.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class LicenseController implements Initializable {

    @FXML private VBox textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            InputStream is = getClass().getResourceAsStream("/COPYING.txt");
            assert is != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                Label label = new Label(currentLine);
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 15));
                textArea.getChildren().add(label);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
