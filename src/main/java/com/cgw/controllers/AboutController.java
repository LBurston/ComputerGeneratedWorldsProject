package com.cgw.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class AboutController {

    @FXML private Hyperlink emailLink;
    @FXML private Hyperlink githubLink;
    @FXML private Button licenseButton;

    private HostServices hostServices;

    @FXML
    private void openLicense() throws IOException {
        Stage licenseStage = new Stage();
        licenseStage.setTitle("License");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/license.fxml"));
        licenseStage.setScene(new Scene(loader.load()));
        LicenseController licenseController = loader.getController();

        licenseStage.setResizable(false);
        licenseStage.initStyle(StageStyle.UTILITY);
        licenseStage.initModality(Modality.APPLICATION_MODAL);
        licenseStage.show();
    }

    @FXML
    private void openEmail() {
        hostServices.showDocument("mailto:" + emailLink.getText());
    }

    @FXML
    private void openGitHub() {
        hostServices.showDocument("https://github.com/LBurston/ComputerGeneratedWorldsProject");
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

}
