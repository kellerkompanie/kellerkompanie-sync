package com.kellerkompanie.kekosync.client.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public final class LauncherController implements Initializable {

    @FXML
    private StackPane content;
    @FXML
    private VBox newsRoot;
    @FXML
    private VBox modsRoot;
    @FXML
    private VBox settingsRoot;


    @FXML
    private Button buttonNews;
    @FXML
    private Button buttonMods;
    @FXML
    private Button buttonSettings;
    @FXML
    private Button buttonPlay;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("LauncherController initialize()");

        newsRoot = (VBox) content.lookup("#newsRoot");
        modsRoot = (VBox) content.lookup("#modsRoot");
        settingsRoot = (VBox) content.lookup("#settingsRoot");

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (event.getSource() == buttonNews)
                    switchContent(ContentMode.NEWS);
                else if (event.getSource() == buttonMods)
                    switchContent(ContentMode.MODS);
                else if (event.getSource() == buttonSettings)
                    switchContent(ContentMode.SETTINGS);
            }
        };

        buttonNews.setOnAction(eventHandler);
        buttonMods.setOnAction(eventHandler);
        buttonSettings.setOnAction(eventHandler);
    }

    private void switchContent(ContentMode contentMode) {
        switch (contentMode) {
            case NEWS:
                newsRoot.setVisible(true);
                modsRoot.setVisible(false);
                settingsRoot.setVisible(false);
                break;
            case MODS:
                newsRoot.setVisible(false);
                modsRoot.setVisible(true);
                settingsRoot.setVisible(false);
                break;
            case SETTINGS:
                newsRoot.setVisible(false);
                modsRoot.setVisible(false);
                settingsRoot.setVisible(true);
                break;
        }
    }

    private enum ContentMode {
        NEWS,
        MODS,
        SETTINGS
    }
}
