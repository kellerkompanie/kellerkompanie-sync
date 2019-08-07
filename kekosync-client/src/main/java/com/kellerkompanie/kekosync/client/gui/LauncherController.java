package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.arma.ArmALauncher;
import com.kellerkompanie.kekosync.client.gui.task.ProgressTask;
import com.kellerkompanie.kekosync.client.gui.task.ProgressTaskState;
import com.kellerkompanie.kekosync.client.settings.Settings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.*;

@Slf4j
public final class LauncherController implements Initializable {

    private static LauncherController instance;
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

    private PlayButtonMode playButtonMode = PlayButtonMode.PLAY;
    private ExecutorService progressTaskExecutorService = Executors.newFixedThreadPool(1);
    private BlockingQueue<ProgressTask> progressTaskQueue = new LinkedBlockingDeque<>();
    private Runnable progressTask = () -> {
        try {
            // take one task from the queue
            ProgressTask progressTask = progressTaskQueue.take();

            // execute pre execute code
            progressTask.onPreExecute();

            // execute that task
            Object result = progressTask.doInBackground();

            // execute post execute code
            progressTask.onPostExecute(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    public static LauncherController getInstance() {
        return instance;
    }

    public boolean queueProgressTask(ProgressTask progressTask) {
        synchronized (progressTaskQueue) {
            boolean wasAdded = progressTaskQueue.add(progressTask);
            if (wasAdded)
                progressTask.setState(ProgressTaskState.QUEUED);
            return wasAdded;
        }
    }

    public void processQueue() {
        for(int i = 0; i < progressTaskQueue.size(); i++)
            progressTaskExecutorService.submit(progressTask);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        log.info("LauncherController initialize()");

        EventHandler<ActionEvent> eventHandler = event -> {
            if (event.getSource() == buttonNews)
                switchContent(ContentMode.NEWS);
            else if (event.getSource() == buttonMods)
                switchContent(ContentMode.MODS);
            else if (event.getSource() == buttonSettings)
                switchContent(ContentMode.SETTINGS);
        };

        buttonNews.setOnAction(eventHandler);
        buttonMods.setOnAction(eventHandler);
        buttonSettings.setOnAction(eventHandler);
        buttonPlay.setOnAction(event -> {
            switch (playButtonMode) {
                case PLAY:
                    if (isSteamRunning()) {
                        ArmALauncher.getInstance().startArmA();
                        playButtonMode = PlayButtonMode.STARTED;
                        buttonPlay.setDisable(true);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("KekoSync Error");
                        alert.setHeaderText("ERROR: Steam not running");
                        alert.setContentText("Du Jockel hast vergessen Steam zu starten ¯\\_(ツ)_/¯");
                        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/drawable/kk-signet-small-color.png")));
                        alert.showAndWait();
                    }
                    break;
                case UPDATE:
                    break;
                case STARTED:
                    // game is already running, do nothing
                    break;
            }
        });

        buttonTeamspeak.setGraphic(getImageView("/drawable/headset.png"));
        buttonWiki.setGraphic(getImageView("/drawable/wiki.png"));
        buttonForum.setGraphic(getImageView("/drawable/forum.png"));
        buttonServer.setGraphic(getImageView("/drawable/server.png"));

        addForwardAction(buttonTeamspeak, "ts3server://ts.kellerkompanie.com?port=9987");
        addForwardAction(buttonForum, "https://forum.kellerkompanie.com");
        addForwardAction(buttonWiki, "https://wiki.kellerkompanie.com");
        addForwardAction(buttonServer, "http://server.kellerkompanie.com");

        progressTaskExecutorService.submit(progressTask);

        if (!isServerReachable()) {
            log.warn("Server is not reachable");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server Connection");
            alert.setHeaderText("Connection Error");
            alert.setContentText("Cannot connect to server, are you online? Is the server running?");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/drawable/kk-signet-small-color.png")));
            alert.showAndWait();

            return;
        }

        NewsController.getInstance().updateNews();
        ModsController.getInstance().update();
    }

    private boolean isServerReachable() {
        try {
            return InetAddress.getByName(Settings.SERVER_URL).isReachable(1000 * 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private ImageView getImageView(String path) {
        Image imageServer = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(imageServer);
        imageView.setFitHeight(28);
        imageView.setFitWidth(28);
        return imageView;
    }

    private void addForwardAction(Button button, String url) {
        button.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void switchContent(ContentMode contentMode) {
        switch (contentMode) {
            case NEWS:
                buttonNews.setStyle("-fx-text-fill:#ee4d2e; -fx-font-size:14px; -fx-background-radius: 0; -fx-border-style:solid; -fx-border-width: 2px; -fx-border-color: transparent transparent #ee4d2e transparent; -fx-paddong:0;");
                buttonMods.setStyle("-fx-text-fill:#888888; -fx-font-size:14px; -fx-background-radius: 0;");
                buttonSettings.setStyle("-fx-text-fill:#888888; -fx-font-size:14px; -fx-background-radius: 0;");

                newsRoot.setVisible(true);
                modsRoot.setVisible(false);
                settingsRoot.setVisible(false);
                break;
            case MODS:
                buttonNews.setStyle("-fx-text-fill:#888888; -fx-font-size:14px; -fx-background-radius: 0;");
                buttonMods.setStyle("-fx-text-fill:#ee4d2e; -fx-font-size:14px; -fx-background-radius: 0; -fx-border-style:solid; -fx-border-width: 2px; -fx-border-color: transparent transparent #ee4d2e transparent; -fx-paddong:0;");
                buttonSettings.setStyle("-fx-text-fill:#888888; -fx-font-size:14px; -fx-background-radius: 0;");

                newsRoot.setVisible(false);
                modsRoot.setVisible(true);
                settingsRoot.setVisible(false);
                break;
            case SETTINGS:
                buttonNews.setStyle("-fx-text-fill:#888888; -fx-font-size:14px; -fx-background-radius: 0;");
                buttonMods.setStyle("-fx-text-fill:#888888; -fx-font-size:14px; -fx-background-radius: 0;");
                buttonSettings.setStyle("-fx-text-fill:#ee4d2e; -fx-font-size:14px; -fx-background-radius: 0; -fx-border-style:solid; -fx-border-width: 2px; -fx-border-color: transparent transparent #ee4d2e transparent; -fx-paddong:0;");

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

    private boolean isSteamRunning() {
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                sb.append(line);
            }
            input.close();
            String pidInfo = sb.toString();
            return pidInfo.contains("Steam.exe");
        } catch (Exception e) {
            log.error("{}", e);
        }
        return false;
    }

    private enum ContentMode {
        NEWS,
        MODS,
        SETTINGS
    }

    private enum PlayButtonMode {
        PLAY,
        STARTED,
        UPDATE
    }

    public void shutdown() {
        progressTaskExecutorService.shutdown();
    }
}
