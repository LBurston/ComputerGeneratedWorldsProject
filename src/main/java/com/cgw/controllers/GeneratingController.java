package com.cgw.controllers;

import com.cgw.generators.WorldGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GeneratingController implements Initializable {

    @FXML
    public AnchorPane generatingScreen;

    private void generateWorld() throws InterruptedException {
        Thread generationThread = new Thread(() -> {
            WorldGenerator.getWorldGenerator().generateWorld();
            System.out.println("Finished");
            Platform.runLater(() -> {
                SceneNavigator.loadScene(SceneNavigator.wikiScreen);
                SceneNavigator.setUpWorld();
            });
        });
        generationThread.start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            generateWorld();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
