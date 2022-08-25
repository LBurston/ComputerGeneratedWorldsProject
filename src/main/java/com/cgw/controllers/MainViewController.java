package com.cgw.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * A Controller class for the Main Window of the App. This Window remains active for the duration of
 * the Application, swapping out JavaFX Node in the center of the BorderPane with the new Window.
 * Also contains functionality for the MenuBar to access the About and Help windows.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class MainViewController {
    // The center of the BorderPane, which is an AnchorPane and where new Scenes are placed.
    @FXML public AnchorPane center;

    // Host Services to be sent to the About window for opening external links.
    private HostServices hostServices;

    /**
     * Sets a JavaFX Node into the center of the Main View
     * @param node JavaFX Node which forms the root of each Scene.
     */
    public void setCenter(Node node) {
        center.getChildren().setAll(node);
    }

    /**
     * Loads a new About Window containing information about the Application.
     * @throws IOException Thrown if fxml file not found.
     */
    @FXML
    private void openAbout() throws IOException {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/about.fxml"));
        aboutStage.setScene(new Scene(loader.load()));
        AboutController aboutController = loader.getController();
        aboutController.setHostServices(hostServices);          // Sets the Host Services in the Controller.

        aboutStage.setResizable(false);
        aboutStage.initStyle(StageStyle.UTILITY);               // Makes Window bar only show Title and Close button.
        aboutStage.initModality(Modality.APPLICATION_MODAL);    // Makes Main Window unresponsive until this is closed.
        aboutStage.show();
    }

    /**
     * Loads a new Help Window containing information about the Application.
     * @throws IOException Thrown if fxml file not found.
     */
    @FXML
    private void openHelp() throws IOException {
        Stage helpStage = new Stage();
        helpStage.setTitle("Help");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/help.fxml"));
        helpStage.setScene(new Scene(loader.load()));

        helpStage.setResizable(false);
        helpStage.initStyle(StageStyle.UTILITY);                // Makes Window bar only show Title and Close button.
        helpStage.initModality(Modality.APPLICATION_MODAL);     // Makes Main Window unresponsive until this is closed.
        helpStage.show();
    }

    /**
     * Sets the Host Services from the Main Application to pass onto the About Window.
     * @param hostServices Host Services of the Application.
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

}
