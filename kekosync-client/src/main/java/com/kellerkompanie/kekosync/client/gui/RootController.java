package com.kellerkompanie.kekosync.client.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class RootController extends Application {
    @FXML
    private Tab launcherOptionsTab;

    @FXML
    private Tab settingTab;

    public static void openWindow(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("KekoSync");

        Parent root = loadFXML("root");

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private Parent loadFXML(String name) {
        URL url = getClass().getResource("/layout/" + name + ".fxml");
        Parent parent = null;
        try {
            parent = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parent;
    }
}
