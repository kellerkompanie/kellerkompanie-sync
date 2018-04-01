package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class SearchDirectoriesController implements Initializable {

    @FXML
    private ListView listView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateListView();
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Add Search Directory");
        Stage stage = (Stage) listView.getScene().getWindow();
        File file = directoryChooser.showDialog(stage);
        Path path = Paths.get(file.getPath());
        Settings.addSearchDirectory(path);

        updateListView();
    }

    private void updateListView() {
        listView.getItems().clear();

        Set<Path> searchDirectories = Settings.getSearchDirectories();
        for(Path searchDir : searchDirectories) {
            listView.getItems().add(searchDir.toString());
        }
    }

    @FXML
    private void handleRemoveAction(ActionEvent event) {
        String path = (String) listView.getSelectionModel().getSelectedItem();
        Settings.removeSearchDirectory(Paths.get(path));
        updateListView();
    }
}
