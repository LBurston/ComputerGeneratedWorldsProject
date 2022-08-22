package com.cgw;

import com.cgw.controllers.MainViewController;
import com.cgw.controllers.SceneNavigator;
import com.cgw.controllers.WorldWikiController;
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

public class CGWApp extends Application {

    World currentWorld;
    MainViewController mainViewController;
    WorldWikiController worldWikiController;

    Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setTitle("Computer Generated Worlds");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/globe32.png"))));

        primaryStage.setScene(createScene(loadMainView()));
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    private Pane loadMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainView = loader.load(getClass().getResourceAsStream("/FXML/" + SceneNavigator.MAIN));

        mainViewController = loader.getController();
        mainViewController.setHostServices(getHostServices());

        SceneNavigator.setMainViewController(mainViewController);
        SceneNavigator.loadScene(SceneNavigator.startSCREEN);
        SceneNavigator.setCGWApp(this);

        return mainView;
    }

    public void setWorldWikiController(WorldWikiController controller) {
        worldWikiController = controller;
    }

    public void setUpWorld() {
        currentWorld = WorldGenerator.getWorldGenerator().getWorld();
        stage.setTitle("Computer Generated Worlds: " + currentWorld.getName());
    }


    private Scene createScene(Pane mainView) {
        return new Scene(mainView);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
