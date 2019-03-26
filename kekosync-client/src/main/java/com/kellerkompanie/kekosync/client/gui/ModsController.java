package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.gui.task.UpdateCurrentModsetTask;
import com.kellerkompanie.kekosync.client.gui.task.UpdateModsetsTask;
import com.kellerkompanie.kekosync.client.gui.task.UpdateServerInfoTask;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.UUID;

@Slf4j
public class ModsController implements Initializable {

    // FileLocationHelper.getModLocalRootpath(mod, Settings.getInstance().getSearchDirectories());

    private static final UUID RUNNING_MODSET_UUID = new UUID(0, 0);
    private static ModsController instance;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox content;
    private HashMap<UUID, LocalModGroup> localModGroups = new HashMap<>();
    private HashMap<UUID, ModpackController> uuidToModpackControllerMap = new HashMap<>();
    private Node currentModsetControl;

    public static ModsController getInstance() {
        return instance;
    }

    private static ModpackController getModpackController(Node node) {
        Object controller;
        do {
            controller = node.getUserData();
            node = node.getParent();
        } while (controller == null && node != null);
        return (ModpackController) controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initalize()");
        instance = this;

        /*scrollPane.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!oldValue && newValue)
                update();
        });*/
    }


    public void update() {
        log.info("update");

        // download server info
        UpdateServerInfoTask updateNewsTask = new UpdateServerInfoTask();
        LauncherController.getInstance().queueProgressTask(updateNewsTask);

        // download server running modset
        UpdateCurrentModsetTask updateCurrentModsetTask = new UpdateCurrentModsetTask();
        LauncherController.getInstance().queueProgressTask(updateCurrentModsetTask);

        // update modsets
        UpdateModsetsTask updateModsetsTask = new UpdateModsetsTask();
        LauncherController.getInstance().queueProgressTask(updateModsetsTask);

        // now that all tasks are schedule start processing
        LauncherController.getInstance().processQueue();
    }

    public void updateRunningModset(LocalModGroup runningLocalModGroup) {
        try {
            content.getChildren().remove(currentModsetControl);
            currentModsetControl = createCurrentRunningModsetControl(runningLocalModGroup);
            ModpackController modpackController = getModpackController(currentModsetControl);
            uuidToModpackControllerMap.put(RUNNING_MODSET_UUID, modpackController);
            content.getChildren().add(currentModsetControl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sortModsetDisplay();
    }

    private Node createCurrentRunningModsetControl(LocalModGroup localModGroup) throws IOException {
        Parent modpackRoot = FXMLLoader.load(getClass().getResource("/layout/Modpack.fxml"));
        ModpackController modpackController = getModpackController(modpackRoot);
        modpackController.setLocalModGroup(localModGroup);
        modpackController.setLocationButtonVisible(false);
        return modpackRoot;
    }

    public void addLocalModGroup(LocalModGroup localModGroup) {
        localModGroups.put(localModGroup.getUuid(), localModGroup);
    }

    public void updateModgroupDisplays() {
        for (LocalModGroup localModGroup : localModGroups.values()) {
            if (uuidToModpackControllerMap.containsKey(localModGroup.getUuid())) {
                // control already exists, just update
                ModpackController modpackController = uuidToModpackControllerMap.get(localModGroup.getUuid());
                modpackController.setDescription(localModGroup.getSyncStatus().toString());
            } else {
                try {
                    // create new control
                    Node modpackControl = createModgroupControl(localModGroup);
                    content.getChildren().add(modpackControl);
                    ModpackController modpackController = getModpackController(modpackControl);
                    uuidToModpackControllerMap.put(localModGroup.getUuid(), modpackController);


                } catch (IOException e) {
                    log.error("{}", e);
                }
            }
        }

        sortModsetDisplay();
    }

    private void sortModsetDisplay() {
        LinkedList<Node> sortedNodeList = new LinkedList<>(content.getChildren());
        sortedNodeList.sort((o1, o2) -> {
            ModpackController modpackController1 = getModpackController(o1);
            ModpackController modpackController2 = getModpackController(o2);
            LocalModGroup localModGroup1 = modpackController1.getLocalModGroup();
            LocalModGroup localModGroup2 = modpackController2.getLocalModGroup();
            return Integer.compare(localModGroup1.getPriority(), localModGroup2.getPriority());
        });
        content.getChildren().clear();
        content.getChildren().addAll(sortedNodeList);
    }

    private Node createModgroupControl(LocalModGroup localModGroup) throws IOException {
        Parent modpackRoot = FXMLLoader.load(getClass().getResource("/layout/Modpack.fxml"));
        ModpackController modpackController = getModpackController(modpackRoot);
        modpackController.setLocalModGroup(localModGroup);
        return modpackRoot;
    }
}
