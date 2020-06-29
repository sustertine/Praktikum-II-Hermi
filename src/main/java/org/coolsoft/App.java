package org.coolsoft;

import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Logger log = LoggerFactory.getLogger(this.getClass());
        log.info("\n System info: \n - JRE version: [" + System.getProperty("java.version") + "]" +
                "\n - JavaFx version: ["
                + System.getProperty("javafx.version") + "]");

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/gui.fxml"));
        primaryStage.setTitle("Hermi-Generator");
        primaryStage.setScene(new Scene(root, 1045, 615));
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("file:icon.png"));
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }

}