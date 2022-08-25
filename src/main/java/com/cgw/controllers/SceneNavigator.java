package com.cgw.controllers;

import com.cgw.CGWApp;
import  javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

/**
 * Used to organise and set which Scene is displayed within the Main View.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class SceneNavigator {

    // FXML file names for loading scenes.
	public static final String MAIN             = "mainView.fxml";
	public static final String startSCREEN      = "startScreen.fxml";
	public static final String generatingSCREEN = "generatingScreen.fxml";
    public static final String wikiSCREEN       = "wikiScreen.fxml";

	private static MainViewController mainViewController;
    private static CGWApp cgwApp;

    /**
     * Sets the Main View Controller in order to access the Center and set new scenes.
     * @param mainViewController The Main View Controller passed into this when started.
     */
    public static void setMainViewController(MainViewController mainViewController) {
        SceneNavigator.mainViewController = mainViewController;
    }

    /**
     * Sets a reference to the Application in order to call set the newly generated world
     * after it has been generated and set to the Wiki Controller.
     * @param app The Application that is currently running.
     */
    public static void setCGWApp(CGWApp app) {
        SceneNavigator.cgwApp = app;
    }

    /**
     * A string of the FXML file name to be loaded into the Main View Stage.
     * @param fxml The FXML file name to be loaded.
     */
    public static void loadScene(String fxml) {
        try {
            mainViewController.setCenter(
                    FXMLLoader.load(Objects.requireNonNull(SceneNavigator.class.getResource("/FXML/" + fxml))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calls the Application to get the World from the World Generator after it has been generated.
     */
    public static void setUpWorld() {
        cgwApp.setUpWorld();
    }
}
