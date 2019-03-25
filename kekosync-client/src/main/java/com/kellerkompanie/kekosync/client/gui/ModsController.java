package com.kellerkompanie.kekosync.client.gui;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kellerkompanie.kekosync.client.download.DownloadCallback;
import com.kellerkompanie.kekosync.client.download.DownloadManager;
import com.kellerkompanie.kekosync.client.download.DownloadTask;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.*;
import com.kellerkompanie.kekosync.core.helper.FileSyncHelper;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;
import com.kellerkompanie.kekosync.core.helper.ModStatusHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class ModsController implements Initializable {

    // FileLocationHelper.getModLocalRootpath(mod, Settings.getInstance().getSearchDirectories());

    private static final UUID RUNNING_MODSET_UUID = new UUID(0, 0);
    private static final File appdataPath = new File(System.getenv("APPDATA") + File.separator + "KekoSync" + File.separator + "cache");
    private static final File serverInfoFile = new File(appdataPath, "serverinfo.json");
    private static final File currentModsetFile = new File(appdataPath, "current_modset.json");
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox content;
    private HashMap<UUID, LocalModGroup> localModGroups = new HashMap<>();
    private HashMap<UUID, ModpackController> uuidToModpackControllerMap = new HashMap<>();
    private Node currentModsetControl;

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

        scrollPane.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!oldValue && newValue)
                update();
        });
    }

    private ServerInfo readServerInfoFromFile() throws IOException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(serverInfoFile));
        return gson.fromJson(reader, ServerInfo.class);
    }

    private RunningModset readCurrentModsetFromFile() throws IOException {
        Gson gson = new Gson();
        log.info("{}", currentModsetFile);
        JsonReader reader = new JsonReader(new FileReader(currentModsetFile));
        return gson.fromJson(reader, RunningModset.class);
    }

    private void update() {
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

        log.info("downloading news");

        DownloadTask downloadTask = new DownloadTask(LauncherUtils.getServerURL() + Filenames.FILENAME_SERVERINFO, serverInfoFile, new DownloadCallback() {
            @Override
            public void onDownloadStart(DownloadTask downloadTask) {
                Platform.runLater(() -> {
                    log.info("download of server info started");
                    LauncherController.getInstance().setProgressText("Downloading Server Info ...");
                });
            }

            @Override
            public void onDownloadProgress(DownloadTask downloadTask, double progress) {
                Platform.runLater(() -> {
                    log.info("download of server info progress {}", progress);
                    LauncherController.getInstance().setProgress(progress);
                });
            }

            @Override
            public void onDownloadFinished(DownloadTask downloadTask) {
                log.info("onDownloadFinished");

                try {
                    ServerInfo serverInfo = readServerInfoFromFile();
                    Settings.getInstance().setServerInfo(serverInfo);

                    Platform.runLater(() -> {
                        LauncherController.getInstance().setProgressText("Server Info up-to-date");
                        LauncherController.getInstance().setProgress(0);
                    });

                    updateCurrentlyRunningModset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        DownloadManager downloadManager = new DownloadManager();
        downloadManager.queueDownloadTask(downloadTask);
        downloadManager.processQueue();
    }

    private boolean isServerReachable() {
        try {
            return InetAddress.getByName(LauncherUtils.getServerURL()).isReachable(1000 * 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void updateCurrentlyRunningModset() {
        DownloadTask downloadTask = new DownloadTask(Settings.getInstance().getServerInfo().getInfoURL(), currentModsetFile, new DownloadCallback() {
            @Override
            public void onDownloadStart(DownloadTask downloadTask) {
                Platform.runLater(() -> {
                    log.info("download of current modset started");
                    LauncherController.getInstance().setProgressText("Downloading Current Modset ...");
                });
            }

            @Override
            public void onDownloadProgress(DownloadTask downloadTask, double progress) {
                Platform.runLater(() -> {
                    log.info("download of current modset progress {}", progress);
                    LauncherController.getInstance().setProgress(progress);
                });
            }

            @Override
            public void onDownloadFinished(DownloadTask downloadTask) {
                log.info("onDownloadFinished");

                try {
                    RunningModset runningModset = readCurrentModsetFromFile();

                    HashSet<Mod> currentMods = new HashSet<>(runningModset.getMods());
                    ModGroup currentlyRunningModGroup = new ModGroup("Current Server Modset", RUNNING_MODSET_UUID, currentMods);
                    LocalModGroup currentlyRunningLocalModGroup = new LocalModGroup(currentlyRunningModGroup);
                    currentlyRunningLocalModGroup.setPriority(0);

                    Platform.runLater(() -> {
                        try {
                            LauncherController.getInstance().setProgressText("Current Modset up-to-date");
                            LauncherController.getInstance().setProgress(0);
                            content.getChildren().remove(currentModsetControl);
                            currentModsetControl = createCurrentRunningModsetControl(currentlyRunningLocalModGroup);
                            ModpackController modpackController = getModpackController(currentModsetControl);
                            uuidToModpackControllerMap.put(RUNNING_MODSET_UUID, modpackController);
                            content.getChildren().add(currentModsetControl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        sortModsetDisplay();
                    });

                    updateModsets();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        DownloadManager downloadManager = new DownloadManager();
        downloadManager.queueDownloadTask(downloadTask);
        downloadManager.processQueue();
    }

    private Node createCurrentRunningModsetControl(LocalModGroup localModGroup) throws IOException {
        Parent modpackRoot = FXMLLoader.load(getClass().getResource("/layout/Modpack.fxml"));
        ModpackController modpackController = getModpackController(modpackRoot);
        modpackController.setLocalModGroup(localModGroup);
        modpackController.setLocationButtonVisible(false);
        return modpackRoot;
    }

    private void updateModsets() {
        Platform.runLater(() -> {
            LauncherController.getInstance().setProgress(0);
            LauncherController.getInstance().setProgressText("Updating Modsets");
        });

        List<Repository> repositories = new LinkedList<>();
        HashMap<Repository, FileindexEntry> rootFileindexEntries = new HashMap<>();
        try {
            for (String repositoryIdentifier : Settings.getInstance().getServerInfo().getRepositoryIdentifiers()) {
                Repository repository = LauncherUtils.getRepository(repositoryIdentifier);
                FileindexEntry rootFileindexEntry = LauncherUtils.getFileIndexEntry(repositoryIdentifier);

                repositories.add(repository);
                rootFileindexEntries.put(repository, rootFileindexEntry);
            }
        } catch (Exception e) {
            log.error("{}", e);
            System.exit(1);
        }

        List<ModGroup> modGroups = new LinkedList<>();
        HashMap<ModGroup, FileindexEntry> limitedFileIndexEntries = new HashMap<>();
        for (Repository repository : repositories) {
            modGroups.addAll(repository.getModGroups());
            FileindexEntry rootFileindexEntry = rootFileindexEntries.get(repository);
            FileindexEntry limitedFileindexEntry = FileSyncHelper.limitFileindexToModgroups(rootFileindexEntry, modGroups);
            for (ModGroup modGroup : modGroups) {
                limitedFileIndexEntries.put(modGroup, limitedFileindexEntry);
            }
        }

        int n = modGroups.size();
        int i = 0;
        for (ModGroup modGroup : modGroups) {
            log.info("{}", "ModsController: checking modGroup '" + modGroup.getName() + "'");

            LocalModGroup localModGroup = new LocalModGroup(modGroup);
            Path location = Settings.getInstance().getModsetLocation(localModGroup);
            localModGroup.setLocation(location);

            ArrayList<FileindexWithSyncEntry.SyncStatus> statusList = new ArrayList<>(modGroup.getMods().size());
            for (Mod mod : modGroup.getMods()) {
                FileindexEntry limitedFileindexEntry = limitedFileIndexEntries.get(modGroup);
                FileindexWithSyncEntry.SyncStatus modStatus = ModStatusHelper.checkStatusForMod(limitedFileindexEntry, mod, Settings.getInstance().getSearchDirectories());
                statusList.add(modStatus);
            }
            FileindexWithSyncEntry.SyncStatus modsGroupStatus = ModStatusHelper.combineStatus(statusList);
            localModGroup.setSyncStatus(modsGroupStatus);
            localModGroups.put(localModGroup.getUuid(), localModGroup);

            int progress = i++ / n;
            Platform.runLater(() -> {
                LauncherController.getInstance().setProgress(progress);
                LauncherController.getInstance().setProgressText("Updating " + modGroup.getName());
            });
        }

        Platform.runLater(() -> {
            updateModgroupDisplays();
            LauncherController.getInstance().setProgress(0);
            LauncherController.getInstance().setProgressText("Everything up-to-date");
        });
    }

    private void updateModgroupDisplays() {
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
