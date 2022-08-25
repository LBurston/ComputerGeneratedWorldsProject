package com.cgw.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * A Controller Class for the Start Screen, showing the App Name, Logo, and Button to Generate.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class StartScreenController {

    @FXML
    public AnchorPane startScreen;

    /**
     * Called when the Button to Generate is pressed, switching scenes on the Main View.
     */
    public void switchToGeneratingScreen() {
        SceneNavigator.loadScene(SceneNavigator.generatingSCREEN);
    }
}
