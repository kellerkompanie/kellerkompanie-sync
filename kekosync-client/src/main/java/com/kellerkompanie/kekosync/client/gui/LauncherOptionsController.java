package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LauncherOptionsController implements Initializable {

    @FXML
    private VBox parameterVBox;

    @FXML
    private TextArea parameterTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createLauncherOptions();
    }

    private void createLauncherOptions() {
        List<ArmAParameter> parameterList = ArmAParameter.getDefaultParameters();

        for (ArmAParameter param : parameterList) {
            HBox row = new HBox();
            CheckBox checkBox = new CheckBox();
            checkBox.setText(param.getDescription());
            checkBox.setSelected(param.isEnabled());
            row.getChildren().add(checkBox);

            if (param.getType() == ArmAParameter.ParameterType.COMBO) {
                ObservableList<String> options =
                        FXCollections.observableArrayList(
                                param.getValues()
                        );
                ComboBox<String> comboBox = new ComboBox<String>(options);
                comboBox.getSelectionModel().select(param.getValue());
                row.getChildren().add(comboBox);
            }

            parameterVBox.getChildren().add(row);
        }
    }

    @FXML
    private void handleChooseExecutableAction(ActionEvent event) {
        System.out.println("You clicked me!");
    }
}
