package com.cgw;

import com.cgw.controllers.MainViewController;
import com.cgw.controllers.SceneNavigator;
import com.cgw.generators.WorldGenerator;
import com.cgw.features.World;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * The Main Application for the Program setting up the first Window and scene.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class CGWApp extends Application {

    World currentWorld;
    MainViewController mainViewController;

    Stage stage;

    /**
     * The Overridden method from Application to start it and set the JavaFX Stage.
     * @param primaryStage The Main Window of the Application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            primaryStage.setTitle("Computer Generated Worlds");
            primaryStage.getIcons().add(new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/images/globe32.png"))));

            primaryStage.setScene(new Scene(loadMainView()));
            primaryStage.setResizable(false);

            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Calls the Scene Navigator to load the Main View with the Start Screen View in the Center of the Main View.
     * @return The Pane loaded from the fxml file to be placed within the Main View Center.
     * @throws IOException Thrown if fxml file not found.
     */
    private Pane loadMainView() throws IOException {
        // Creates a FXMLLoader and loads the fxml of the Main View from the Scene Navigator
        FXMLLoader loader = new FXMLLoader();
        Pane mainView = loader.load(getClass().getResourceAsStream("/FXML/" + SceneNavigator.MAIN));

        // Sets the Host Services in the Main View to pass to the About Window for opening external links.
        mainViewController = loader.getController();
        mainViewController.setHostServices(getHostServices());

        // Loads the Start Screen View to place into the Main View. This is done to keep the
        // Application Main View with Menubar constant, but switch the center View.
        SceneNavigator.setMainViewController(mainViewController);
        SceneNavigator.loadScene(SceneNavigator.startSCREEN);
        SceneNavigator.setCGWApp(this);

        return mainView;
    }

    /**
     * Called after the World Generator has generated a new World. Sets the currentWorld field
     * and adds it to the Window title.
     */
    public void setUpWorld() {
        currentWorld = WorldGenerator.getWorldGenerator().getWorld();
        stage.setTitle("Computer Generated Worlds: " + currentWorld.getName());
    }

    /**
     * The main method to be called to Launch the Application.
     * @param args Arguments passed.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
