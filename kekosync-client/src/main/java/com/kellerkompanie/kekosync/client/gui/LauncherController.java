package com.kellerkompanie.kekosync.client.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public final class LauncherController implements Initializable {

    private static LauncherController instance;
    @FXML
    private StackPane content;
    @FXML
    private ScrollPane newsRoot;
    @FXML
    private ScrollPane modsRoot;
    @FXML
    private ScrollPane settingsRoot;
    @FXML
    private Button buttonNews;
    @FXML
    private Button buttonMods;
    @FXML
    private Button buttonSettings;
    @FXML
    private Button buttonPlay;
    @FXML
    private Text progressText;
    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button buttonTeamspeak;
    @FXML
    private Button buttonWiki;
    @FXML
    private Button buttonForum;
    @FXML
    private Button buttonServer;

    public static LauncherController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        System.out.println("LauncherController initialize()");

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


        buttonTeamspeak.setGraphic(getImageView("/drawable/headset.png"));
        buttonWiki.setGraphic(getImageView("/drawable/wiki.png"));
        buttonForum.setGraphic(getImageView("/drawable/forum.png"));
        buttonServer.setGraphic(getImageView("/drawable/server.png"));

        addForwardAction(buttonTeamspeak, "ts3server://ts.kellerkompanie.com?port=9987");
        addForwardAction(buttonForum, "https://kellerkompanie.com/forum");
        addForwardAction(buttonWiki, "https://wiki.kellerkompanie.com");
        addForwardAction(buttonServer, "http://server.kellerkompanie.com");
    }

    private ImageView getImageView(String path) {
        Image imageServer = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(imageServer);
        imageView.setFitHeight(28);
        imageView.setFitWidth(28);
        return imageView;
    }

    private void addForwardAction(Button button, String url) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
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

    public void setProgressText(String str) {
        progressText.setText(str);
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }

    private enum ContentMode {
        NEWS,
        MODS,
        SETTINGS
    }
}
