package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.settings.Settings;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class RootController extends Application {
    @FXML
    private Tab launcherOptionsTab;

    @FXML
    private Tab settingTab;

    private Scene scene;
    private Stage stage;

    public static void openWindow(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle("KekoSync");

        double width = Settings.getInstance().getWindowWidth();
        double height = Settings.getInstance().getWindowHeight();
        double x = Settings.getInstance().getWindowX();
        double y = Settings.getInstance().getWindowY();
        boolean maximized = Settings.getInstance().isWindowMaximized();

        primaryStage.setMaximized(maximized);

        primaryStage.setX(x);
        primaryStage.setY(y);

        Parent root = loadFXML("root");
        scene = new Scene(root, width, height);

        scene.widthProperty().addListener(this::onWindowSizeChanged);
        scene.heightProperty().addListener(this::onWindowSizeChanged);

        primaryStage.setScene(scene);

        primaryStage.show();

        stage.xProperty().addListener(this::onWindowPositionChanged);
        stage.yProperty().addListener(this::onWindowPositionChanged);
        stage.maximizedProperty().addListener(this::onWindowMaximizedChanged);
    }

    private static final int SAVE_THRESHOLD = 5000;
    private long timestmapLastWindowMovement = -1;
    private long timestampLastWindowResize = -1;

    private void onWindowPositionChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double x = stage.getX();
        double y = stage.getY();

        long currentTimestamp = System.currentTimeMillis();
        if(timestmapLastWindowMovement != -1 && (currentTimestamp - timestmapLastWindowMovement) > SAVE_THRESHOLD) {
            Settings.getInstance().updateWindowPosition(x, y);
        }
    }

    private void onWindowSizeChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double width = scene.getWidth();
        double height = scene.getHeight();

        long currentTimestamp = System.currentTimeMillis();
        if(timestampLastWindowResize != -1 && (currentTimestamp - timestampLastWindowResize) > SAVE_THRESHOLD) {
            Settings.getInstance().updateWindowSize(width, height);
        }
    }

    private void onWindowMaximizedChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        Settings.getInstance().updateWindowMaximized(newValue);
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
