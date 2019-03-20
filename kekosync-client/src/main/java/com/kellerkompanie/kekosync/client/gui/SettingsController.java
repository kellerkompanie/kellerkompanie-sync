package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    private VBox settingsRoot;

    @FXML
    private TextField executableLocationTextField;
    @FXML
    private TextField customTextField;

    private static SettingsController instance;

    static SettingsController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        //updateExecutableTextField();
        //updateListView();
        loadSettings();

        customTextField.textProperty().addListener((obs, oldText, newText) -> {
            String key = customTextField.getId();
            String value = customTextField.getText();
            Settings.getInstance().updateLaunchParam(key, value);
            updateTextArea();
        });
    }

    private static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }

    private void loadSettings() {
        Map<String, ArmAParameter> params = Settings.getInstance().getLaunchParams();

        List<Node> children = getAllNodes(paramVBox);
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

        String customValue = params.get("CUSTOM").getValue();
        customTextField.setText(customValue);

        updateTextArea();
    }

    @FXML
    private void handleExecutableLocationAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Settings.getInstance().getExecutableLocation()).getParentFile());

        Stage stage = (Stage) settingsRoot.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Settings.getInstance().setExecutableLocation(file.getPath());
            updateExecutableTextField();
        }
    }

    private void updateExecutableTextField() {
        executableLocationTextField.setText(Settings.getInstance().getExecutableLocation());
    }

    private void updateListView() {
        listView.getItems().clear();

        Set<Path> searchDirectories = Settings.getInstance().getSearchDirectories();
        for(Path searchDir : searchDirectories) {
            listView.getItems().add(searchDir.toString());
        }
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
