package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.settings.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class ModpackController implements Initializable {

    @FXML
    private CheckBox checkBox;
    @FXML
    private Button buttonLocation;
    @FXML
    private Button buttonUpdate;
    @FXML
    private Button buttonSettings;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @Getter
    private LocalModGroup localModGroup;

    @FXML
    private void handleCheckBoxStateChanged(ActionEvent event) {
        if (event.getSource() != checkBox)
            throw new IllegalStateException();
        boolean selected = checkBox.isSelected();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonLocation.setGraphic(getImageView("/drawable/open_location.png", 14));
        buttonUpdate.setGraphic(getImageView("/drawable/play.png", 28));
        buttonSettings.setGraphic(getImageView("/drawable/settings.png", 28));

        buttonSettings.setOnAction(event -> openChooseModGroupLocationDialog());
        buttonLocation.setOnAction(event -> openModsetLocation());
    }

    private ImageView getImageView(String path, int size) {
        Image imageServer = new Image(getClass().getResourceAsStream(path));
        ImageView imageView = new ImageView(imageServer);
        imageView.setFitHeight(size);
        imageView.setFitWidth(size);
        return imageView;
    }

    void setDescription(String description) {
        this.description.setText(description);
    }

    void setLocationButtonVisible(boolean visible) {
        buttonLocation.setVisible(visible);
    }

    void setLocalModGroup(LocalModGroup localModGroup) {
        this.localModGroup = localModGroup;
        this.title.setText(localModGroup.getName());
        setDescription(localModGroup.getSyncStatus().toString());

        if(localModGroup.getLocation() == null) {
            buttonLocation.setVisible(false);
            buttonUpdate.setText("Download");

            buttonUpdate.setOnAction(event -> {
                boolean folderChosen = openChooseModGroupLocationDialog();
                if(folderChosen) {
                    // TODO do schedule download
                }
            });
        }
    }

    private boolean openChooseModGroupLocationDialog() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Select folder for " + localModGroup.getName());

        if( localModGroup.getLocation() != null)
            fileChooser.setInitialDirectory(localModGroup.getLocation().toFile());

        Stage stage = (Stage) checkBox.getScene().getWindow();
        File file = fileChooser.showDialog(stage);
        if (file != null) {
            localModGroup.setLocation(file.toPath());
            Settings.getInstance().updateModsetLocation(localModGroup);
            buttonLocation.setVisible(true);
            return true;
        }

        return false;
    }

    private void openModsetLocation() {
        try {
            Runtime.getRuntime().exec("explorer.exe " + localModGroup.getLocation().toAbsolutePath().toString());
        } catch (IOException ex) {
            log.warn(ex.getMessage(), ex);
        }
    }
}
