package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import com.kellerkompanie.kekosync.client.settings.Settings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Schwaggot
 */
public class LauncherOptionsController implements Initializable {

    @FXML
    private VBox paramVBox;
    @FXML
    private TextArea parameterTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HashMap<String, ArmAParameter> params = Settings.getInstance().getLaunchParams();

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

        updateTextArea();
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
        HashMap<String, ArmAParameter> params = Settings.getInstance().getLaunchParams();

        parameterTextArea.clear();

        StringBuilder sb = new StringBuilder();
        for (ArmAParameter armAParameter : params.values()) {
            if (armAParameter.isEnabled()) {
                sb.append(armAParameter.getArgument());
                sb.append("\n");
            }
        }

        parameterTextArea.setText(sb.toString());
    }
}
