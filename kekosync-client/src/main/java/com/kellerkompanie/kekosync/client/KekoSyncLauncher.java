package com.kellerkompanie.kekosync.client;

import com.kellerkompanie.kekosync.client.arma.ArmALauncher;
import com.kellerkompanie.kekosync.client.arma.ArmAParameter;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Schwaggot
 */
public class KekoSyncLauncher extends Application {

    private TabPane tabPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("KekoSync");

        Button buttonStartGame = new Button();
        buttonStartGame.setText("Start game");
        buttonStartGame.setOnAction(this::startGame);

        tabPane = new TabPane();


        /* Launcher Options Tab */
        createLauncherOptionsTab();

        /* Settings Tab */
        createSettingsTab();


        //StackPane root = new StackPane();
        //root.getChildren().add(buttonStartGame);
        primaryStage.setScene(new Scene(tabPane, 800, 600));
        primaryStage.show();
    }

    private Tab addTab(String name) {
        Tab tab = new Tab();
        tab.setText(name);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
        return tab;
    }

    private void createLauncherOptionsTab() {
        Tab launcherOptionsTab = addTab("Launcher Options");


        GridPane gridpane = new GridPane();

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        VBox vbox = new VBox(textArea);
        GridPane.setConstraints(vbox, 1, 0);

        VBox paramVBox = new VBox();
        GridPane.setConstraints(paramVBox, 0, 0);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        gridpane.getColumnConstraints().addAll(column1, column2);

        // don't forget to add children to gridpane
        gridpane.getChildren().addAll(vbox, paramVBox);

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

            paramVBox.getChildren().add(row);
        }

        launcherOptionsTab.setContent(gridpane);
    }

    private void createSettingsTab() {
        Tab settingsTab = addTab("Settings");


    }

    private void startGame(ActionEvent event) {
        ArmALauncher armALauncher = new ArmALauncher(Settings.ARMA3_EXECUTABLE_LOCATION);
        try {
            armALauncher.startArmA(new LinkedList<ArmAParameter>());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
