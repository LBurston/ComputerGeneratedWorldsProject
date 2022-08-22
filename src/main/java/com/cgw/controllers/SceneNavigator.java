package com.cgw.controllers;

import com.cgw.CGWApp;
import  javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

public class SceneNavigator {

	public static final String MAIN             = "mainView.fxml";
	public static final String startSCREEN      = "startScreen.fxml";
	public static final String generatingSCREEN = "generatingScreen.fxml";
    public static final String wikiScreen       = "wikiScreen.fxml";

	private static MainViewController mainViewController;
    private static CGWApp cgwApp;

    public static void setMainViewController(MainViewController mainViewController) {
        SceneNavigator.mainViewController = mainViewController;
    }

    public static void setCGWApp(CGWApp app) {
        SceneNavigator.cgwApp = app;
    }

    public static void loadScene(String fxml) {
        try {
            mainViewController.setCenter(FXMLLoader.load(Objects.requireNonNull(SceneNavigator.class.getResource("/FXML/" + fxml))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setUpWorld() {
        cgwApp.setUpWorld();
    }

    public static void passWikiController(WorldWikiController controller) {
        cgwApp.setWorldWikiController(controller);
    }
}
