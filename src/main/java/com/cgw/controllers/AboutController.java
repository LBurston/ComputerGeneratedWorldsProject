package com.cgw.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * A Controller class for the 'About' window, displaying App info.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class AboutController {

    @FXML private Hyperlink emailLink;

    // Set from Application class to open links.
    private HostServices hostServices;

    /**
     * Loads a small new window to show the GNU GPL v3.0 License.
     * @throws IOException Thrown if fxml file not found.
     */
    @FXML
    private void openLicense() throws IOException {
        Stage licenseStage = new Stage();
        licenseStage.setTitle("License");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/license.fxml"));
        licenseStage.setScene(new Scene(loader.load()));

        licenseStage.setResizable(false);
        licenseStage.initStyle(StageStyle.UTILITY);
        licenseStage.initModality(Modality.APPLICATION_MODAL);
        licenseStage.show();
    }

    /**
     * Opens the user's default E-mail app with a new e-mail to the link.
     */
    @FXML
    private void openEmail() {
        hostServices.showDocument("mailto:" + emailLink.getText());
    }

    /**
     * Opens the user's default Internet Browser to this project's GitHub page.
     */
    @FXML
    private void openGitHub() {
        hostServices.showDocument("https://github.com/LBurston/ComputerGeneratedWorldsProject");
    }

    /**
     * Sets the Host Services for use with opening external links.
     * @param hostServices Host Services of the Application.
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

}
