package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.settings.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private VBox settingsTabRoot;

    @FXML
    private TextField executableLocationTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executableLocationTextField.setText(Settings.getInstance().getExecutableLocation());
    }

    @FXML
    private void handleExecutableLocationAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Settings.getInstance().getExecutableLocation()).getParentFile());

        Stage stage = (Stage) settingsTabRoot.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
    }
}
