package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.arma.ArmALauncher;
import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class RootController extends Application implements Initializable {
    @FXML
    private Tab modsTab;

    @FXML
    private Tab settingsTab;

    @FXML
    private TabPane tabPane;

    @FXML
    private ComboBox serverComboBox;

    private Scene scene;
    private Stage stage;

    public static void openWindow(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        primaryStage.setTitle("KekoSync");
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/drawable/kk-signet-small-color.png")));

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

        ModsController.getInstance().update();
        SettingsController.getInstance().update();
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

    @FXML
    private void handleStartGameAction(ActionEvent event) {
        ArmALauncher.getInstance().startArmA();
    }

    private static final int MODS_TAB = 0;
    private static final int SETTINGS_TAB = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArmAParameter param = Settings.getInstance().getLaunchParams().get(ArmAParameter.SERVER);
        if (param != null) {
            if (param.isEnabled())
                serverComboBox.getSelectionModel().select("server.kellerkompanie.com");
        }

        serverComboBox.setOnAction((e) -> {
            String selected = (String) serverComboBox.getSelectionModel().getSelectedItem();
            Settings.getInstance().updateLaunchParam(ArmAParameter.SERVER, !selected.isEmpty());
            Settings.getInstance().updateLaunchParam(ArmAParameter.PORT, !selected.isEmpty());
            Settings.getInstance().updateLaunchParam(ArmAParameter.PASSWORD, !selected.isEmpty());
        });

        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                int tabIndex = newValue.intValue();
                switch(tabIndex) {
                    case MODS_TAB:
                        // do not update since ModController is only updated if something changed
                        // ModsController.getInstance().update();
                        break;
                    case SETTINGS_TAB:
                        SettingsController.getInstance().update();
                        break;
                }
            }
        });
    }
}
