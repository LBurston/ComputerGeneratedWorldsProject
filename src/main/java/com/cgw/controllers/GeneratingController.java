package com.cgw.controllers;

import com.cgw.exceptions.GenerationFailureException;
import com.cgw.generators.WorldGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A Controller class for the Generating Screen to be displayed while the program generates a new world.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class GeneratingController implements Initializable {

    @FXML
    public AnchorPane generatingScreen;

    /**
     * Starts a new Thread to call the World Generator to create a new World, then when finished,
     * calls the Scene Navigator to switch to that generated World's Wiki.
     * @throws InterruptedException For new Generation Thread.
     */
    private void generateWorld() throws InterruptedException {
        Thread generationThread = new Thread(() -> {
            try {
                WorldGenerator.getWorldGenerator().generateWorld();
            } catch (GenerationFailureException e) {
                e.printStackTrace();
            }
            System.out.println("Finished");

            // Once generation is fully finished and the Thread is done, calls the Application
            // to load the Wiki scene and place it in the Main View.
            Platform.runLater(() -> {
                SceneNavigator.loadScene(SceneNavigator.wikiSCREEN);
                SceneNavigator.setUpWorld();
            });
        });
        generationThread.start();
    }

    /**
     * Calls the method to generate a new World as soon as this Controller is initialised.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            generateWorld();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
