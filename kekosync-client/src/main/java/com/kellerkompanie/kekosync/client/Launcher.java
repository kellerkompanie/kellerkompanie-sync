package com.kellerkompanie.kekosync.client;

import com.kellerkompanie.kekosync.client.gui.LauncherController;
import com.kellerkompanie.kekosync.client.settings.Settings;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @author Schwaggot
 */
public class Launcher extends Application {

    private Stage stage;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        double width = Settings.getInstance().getWindowWidth();
        double height = Settings.getInstance().getWindowHeight();
        double x = Settings.getInstance().getWindowX();
        double y = Settings.getInstance().getWindowY();
        boolean maximized = Settings.getInstance().isWindowMaximized();

        stage.setMaximized(maximized);

        stage.setX(x);
        stage.setY(y);

        Parent root = FXMLLoader.load(getClass().getResource("/layout/Launcher.fxml"));

        stage.setTitle("KekoSync");
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/drawable/kk-signet-small-color.png")));
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        //stage.setResizable(false);

        scene = new Scene(root, width, height);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/custom.css").toExternalForm());

        scene.widthProperty().addListener(this::onWindowSizeChanged);
        scene.heightProperty().addListener(this::onWindowSizeChanged);

        stage.setScene(scene);
        stage.show();

        stage.xProperty().addListener(this::onWindowPositionChanged);
        stage.yProperty().addListener(this::onWindowPositionChanged);
        stage.maximizedProperty().addListener(this::onWindowMaximizedChanged);
    }

    private void onWindowPositionChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double x = stage.getX();
        double y = stage.getY();
        Settings.getInstance().updateWindowPosition(x, y);
    }

    private void onWindowSizeChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        double width = scene.getWidth();
        double height = scene.getHeight();
        Settings.getInstance().updateWindowSize(width, height);
    }

    private void onWindowMaximizedChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        Settings.getInstance().updateWindowMaximized(newValue);
    }

    @Override
    public void stop() {
        Settings.getInstance().saveSettings();
        LauncherController.getInstance().shutdown();
    }
}
