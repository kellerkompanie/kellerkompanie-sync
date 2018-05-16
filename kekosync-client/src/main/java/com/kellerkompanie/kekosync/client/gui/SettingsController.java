package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SettingsController implements Initializable {

    @FXML
    private VBox paramVBox;
    @FXML
    private TextArea parameterTextArea;

    @FXML
    private ListView listView;

    @FXML
    private VBox settingsTabRoot;

    @FXML
    private TextField executableLocationTextField;

    private static SettingsController instance;

    static SettingsController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        //updateExecutableTextField();
        //updateListView();

        Map<String, ArmAParameter> params = Settings.getInstance().getLaunchParams();

        List<Node> children = paramVBox.getChildren();
        for (Node child : children) {
            ArmAParameter param = params.get(child.getId());
            if (param != null) {
                if (child instanceof CheckBox) {
                    CheckBox cb = (CheckBox) child;
                    cb.setSelected(param.isEnabled());
                } else if (child instanceof ComboBox) {
                    ComboBox cb = (ComboBox) child;
                    cb.getSelectionModel().select(param.getValue());
                }
            }
        }

        //updateTextArea();
    }



    @FXML
    private void handleExecutableLocationAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Settings.getInstance().getExecutableLocation()).getParentFile());

        Stage stage = (Stage) settingsTabRoot.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        Settings.getInstance().setExecutableLocation(file.getPath());

        updateExecutableTextField();
    }

    private void updateExecutableTextField() {
        executableLocationTextField.setText(Settings.getInstance().getExecutableLocation());
    }

    @FXML
    private void handleAddAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Add Search Directory");
        Stage stage = (Stage) listView.getScene().getWindow();
        File file = directoryChooser.showDialog(stage);

        if(file != null) {
            Path path = Paths.get(file.getPath());
            Settings.getInstance().addSearchDirectory(path);
            updateListView();
            ModsController.getInstance().update();
        }
    }

    private void updateListView() {
        listView.getItems().clear();

        Set<Path> searchDirectories = Settings.getInstance().getSearchDirectories();
        for(Path searchDir : searchDirectories) {
            listView.getItems().add(searchDir.toString());
        }
    }

    @FXML
    private void handleRemoveAction(ActionEvent event) {
        String pathStr = (String) listView.getSelectionModel().getSelectedItem();
        Path path = Paths.get(pathStr);
        Settings.getInstance().removeSearchDirectory(path);
        updateListView();
        ModsController.getInstance().update();
    }

    @FXML
    private void handleCheckBoxStateChanged(ActionEvent event) {
        CheckBox chk = (CheckBox) event.getSource();
        String key = chk.getId();
        boolean selected = chk.isSelected();
        Settings.getInstance().updateLaunchParam(key, selected);
        updateTextArea();
    }

    @FXML
    private void handleComboBoxStateChanged(ActionEvent event) {
        ComboBox cb = (ComboBox) event.getSource();
        String key = cb.getId();
        String value = cb.getSelectionModel().getSelectedItem().toString();
        Settings.getInstance().updateLaunchParam(key, value);
        updateTextArea();
    }

    private void updateTextArea() {
        Map<String, ArmAParameter> params = Settings.getInstance().getLaunchParams();

        parameterTextArea.clear();

        StringBuilder sb = new StringBuilder();
        for (ArmAParameter armAParameter : params.values()) {
            if (armAParameter.isEnabled()) {
                sb.append(armAParameter.getArgument());
                sb.append("\n");
            }
        }

        LinkedList<String> modsToStart = LauncherUtils.getModsToStart();
        for(String modPath : modsToStart) {
            sb.append("-mod=" + modPath);
            sb.append("\n");
        }

        parameterTextArea.setText(sb.toString());
    }

    void update() {
        updateExecutableTextField();
        updateListView();
        updateTextArea();
    }
}
