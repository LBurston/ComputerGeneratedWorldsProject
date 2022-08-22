package com.cgw.controllers;

import com.cgw.generators.WorldGenerator;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StartScreenController {

    @FXML
    public AnchorPane startScreen;

    public void switchToGeneratingScreen() {
        SceneNavigator.loadScene(SceneNavigator.generatingSCREEN);
    }
}
