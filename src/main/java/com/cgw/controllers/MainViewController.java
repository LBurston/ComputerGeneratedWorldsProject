package com.cgw.controllers;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainViewController {

    @FXML private MenuItem aboutMenuItem;
    @FXML private MenuItem helpMenuItem;
    @FXML public AnchorPane center;

    private HostServices hostServices;

    public void setCenter(Node node) {
        center.getChildren().setAll(node);
    }

    @FXML
    private void openAbout() throws IOException {
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/about.fxml"));
        aboutStage.setScene(new Scene(loader.load()));
        AboutController aboutController = loader.getController();
        aboutController.setHostServices(hostServices);

        aboutStage.setResizable(false);
        aboutStage.initStyle(StageStyle.UTILITY);
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.show();
    }

    @FXML
    private void openHelp() throws IOException {
        Stage helpStage = new Stage();
        helpStage.setTitle("Help");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/help.fxml"));
        helpStage.setScene(new Scene(loader.load()));

        helpStage.setResizable(false);
        helpStage.initStyle(StageStyle.UTILITY);
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.show();
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

}
