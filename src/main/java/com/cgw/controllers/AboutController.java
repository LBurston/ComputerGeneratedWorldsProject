package com.cgw.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AboutController {

    @FXML private Hyperlink emailLink;
    @FXML private Hyperlink githubLink;
    @FXML private Button licenseButton;

    private HostServices hostServices;

    @FXML
    private void openLicense() throws IOException {
        Desktop.getDesktop().open(new File("src/main/java/COPYING.txt"));
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
