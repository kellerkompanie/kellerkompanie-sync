package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.entities.Repository;
import com.kellerkompanie.kekosync.core.helper.*;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.kellerkompanie.kekosync.core.helper.FileSyncHelper.limitFileindexToModgroups;

@Slf4j
public class ModsController implements Initializable {


    private static ModsController instance;
    @FXML
    private CheckBox expandAllCheckBox;
    @FXML
    private CheckBox expandAllModsCheckBox;
    @FXML
    private ListView optionalsListView;
    @FXML
    private TreeView searchDirectoriesTreeView;
    @FXML
    private CustomTreeTableView<CustomTableItem> treeTableView;
    @FXML
    private TreeTableColumn<CustomTableItem, Boolean> checkColumn;
    @FXML
    private TreeTableColumn<CustomTableItem, String> nameColumn;
    @FXML
    private TreeTableColumn<CustomTableItem, String> locationColumn;
    @FXML
    private TreeTableColumn<CustomTableItem, FileindexWithSyncEntry.SyncStatus> statusColumn;

    public static ModsController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        initalizeModsTreeTableView();
    }

    private void initalizeModsTreeTableView() {
        treeTableView.setRowFactory(item -> new CheckBoxTreeTableRow<>());

        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("location"));

        statusColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));

        treeTableView.getSelectionModel().setCellSelectionEnabled(false);
        treeTableView.setShowRoot(false);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);


        checkColumn.setCellFactory(p -> new DefaultTreeTableCell<>());

        /*checkColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CustomTableItem, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<CustomTableItem, Boolean> param) {
                TreeItem<CustomTableItem> treeItem = param.getValue();
                CustomTableItem emp = treeItem.getValue();
                SimpleBooleanProperty sbp = new SimpleBooleanProperty();
                return sbp;
            }
        });*/

        /*checkColumn.setCellFactory(col -> {
            CheckBoxTreeTableCell<CustomTableItem, Boolean> cell = new CheckBoxTreeTableCell<CustomTableItem, Boolean>() {
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setStyle("-fx-background-color: transparent");
                    } else {
                        switch (item) {
                            case CHECKED:
                                //setStyle("-fx-background-color: green");
                                break;
                            case UNCHECKED:
                                //setStyle("-fx-background-color: red");
                                break;
                            case INDETERMINATE:
                                //setStyle("-fx-background-color: orange");
                                break;
                        }
                    }
                }
            };

            cell.setAlignment(Pos.CENTER);

            return cell;
        });*/

        statusColumn.setCellFactory(col -> {
            TreeTableCell<CustomTableItem, FileindexWithSyncEntry.SyncStatus> cell = new TreeTableCell<CustomTableItem, FileindexWithSyncEntry.SyncStatus>() {
                @Override
                public void updateItem(FileindexWithSyncEntry.SyncStatus item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                        switch (item) {
                            case LOCAL_MISSING:
                                setText("UPDATE");
                                setStyle("-fx-font-weight: bold; -fx-text-fill: darkred");
                                break;
                            case LOCAL_WITHCHANGES:
                                setText("UPDATE");
                                setStyle("-fx-font-weight: bold; -fx-text-fill: orange");
                                break;
                            case LOCAL_INSYNC:
                                setText("OK");
                                setStyle("-fx-font-weight: bold; -fx-text-fill: green");
                                break;
                            case REMOTE_MISSING:
                                setText("REMOTE MISSING");
                                setStyle("-fx-font-weight: bold; -fx-text-fill: firebrick");
                                break;
                            case UNKNOWN:
                                setText("MISSING");
                                setStyle("-fx-font-weight: bold; -fx-text-fill: firebrick");
                                break;
                        }
                    }
                }
            };

            cell.setAlignment(Pos.CENTER);

            return cell;
        });

        treeTableView.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) treeTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> header.setReordering(false));
        });

        treeTableView.setEditable(true);

        expandAllCheckBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            for (Object child : searchDirectoriesTreeView.getRoot().getChildren()) {
                TreeItem<String> treeItem = (TreeItem<String>) child;
                treeItem.setExpanded(new_val);
            }
        });

        expandAllModsCheckBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            for (Object child : treeTableView.getRoot().getChildren()) {
                CheckBoxTreeItem<CustomTableItem> treeItem = (CheckBoxTreeItem<CustomTableItem>) child;
                treeItem.setExpanded(new_val);
            }
        });
    }

    void update() {
        if (isServerReachable()) {
            Dialog<Void> loadingDialog = new Dialog<>();
            loadingDialog.initModality(Modality.WINDOW_MODAL);
            Stage stage = (Stage) treeTableView.getScene().getWindow();
            loadingDialog.initOwner(stage);
            loadingDialog.initStyle(StageStyle.TRANSPARENT);
            Label loader = new Label("Refreshing");
            loader.setContentDisplay(ContentDisplay.LEFT);
            loader.setGraphic(new ProgressIndicator());
            loadingDialog.getDialogPane().setGraphic(loader);
            DropShadow ds = new DropShadow();
            ds.setOffsetX(1.3);
            ds.setOffsetY(1.3);
            ds.setColor(Color.DARKGRAY);
            loadingDialog.getDialogPane().setEffect(ds);

            Task<Boolean> task = new Task<Boolean>() {
                @Override
                public Boolean call() {
                    updateModsTreeTableView();
                    updateSearchDirectoriesTreeView();
                    updateCurrentlyRunningModpack();
                    return true;
                }
            };

            task.setOnRunning((e) -> loadingDialog.show());
            task.setOnSucceeded((e) -> {
                // work around for modal dialog still being shown after calling close()
                // see https://stackoverflow.com/a/37138609
                loadingDialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
                loadingDialog.close();
            });
            task.setOnFailed((e) -> {

            });
            new Thread(task).start();
        } else {
            log.warn("Server is not reachable");

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server Connection");
            alert.setHeaderText("Connection Error");
            alert.setContentText("Cannot connect to server, are you online? Is the server running?");
            alert.showAndWait();
        }
    }

    private boolean isServerReachable() {
        try {
            // TODO optimize: just check, do not download entire file
            HttpHelper.readUrl(LauncherUtils.getRepoURL() + Filenames.FILENAME_MODGROUPS);
            return true;
        } catch (ConnectException e) {
            log.error("Connection to server could not be established", e);
            return false;
        } catch (Exception e) {
            log.error("Something went horribly wrong while trying to connect to the server", e);
            return false;
        }
    }

    private void updateSearchDirectoriesTreeView() {
        Set<Path> searchDirectories = Settings.getInstance().getSearchDirectories();

        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);

        for (Path path : searchDirectories) {
            TreeItem<String> item = new TreeItem<>();
            item.setValue(path.toString());
            root.getChildren().add(item);
            populatePath(item, path);
        }

        Platform.runLater(() -> {
            searchDirectoriesTreeView.setRoot(root);
            searchDirectoriesTreeView.setShowRoot(false);
        });
    }

    private void updateCurrentlyRunningModpack() {
        String currentModpack = null;
        try {
            currentModpack = HttpHelper.readUrl("http://server.kellerkompanie.com/info.php");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String mods[] = currentModpack.split("\n");
        Arrays.sort(mods);

        Platform.runLater(() -> {
            optionalsListView.getItems().clear();
            for (String mod : mods) {
                mod = mod.trim();
                if (!mod.isEmpty() && !mod.startsWith("<"))
                    optionalsListView.getItems().add(mod);
            }
        });
    }

    private void populatePath(TreeItem<String> item, Path path) {
        try {
            Files.walk(path, 1)
                    .filter(p -> Files.isDirectory(p) && !path.equals(p))
                    .distinct()
                    .forEach(p -> {
                        TreeItem<String> child = new TreeItem<>();
                        child.setValue(p.getFileName().toString());
                        item.getChildren().add(child);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateModsTreeTableView() {
        Repository repository = null;
        FileindexEntry rootFileindexEntry = null;
        try {
            repository = LauncherUtils.getRepository();
            rootFileindexEntry = LauncherUtils.getFileIndexEntry();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        List<ModGroup> modGroups = repository.getModGroups();
        FileindexEntry limitedFileindexEntry = FileSyncHelper.limitFileindexToModgroups(rootFileindexEntry, modGroups);

        TreeItem<CustomTableItem> rootNode = new TreeItem<>(new RootTableItem());

        for (ModGroup modGroup : modGroups) {
            System.out.println("ModsController: checking modGroup '" + modGroup.getName() + "'");

            ModGroupTableItem modGroupTableItem = new ModGroupTableItem(modGroup);
            CheckBoxTreeItem<CustomTableItem> modGroupTreeItem = new CheckBoxTreeItem<>(modGroupTableItem);

            modGroupTreeItem.selectedProperty().addListener((observable, oldValue, newValue) -> modGroupTableItem.setChecked(newValue));
            modGroupTreeItem.indeterminateProperty().addListener((observable, oldValue, newValue) -> modGroupTableItem.setIndeterminate(newValue));

            /*for (Path searchDirectory : Settings.getInstance().getSearchDirectories()) {
                System.out.println("ModsController: comparing modGroup '" + modGroup.getName() + "' against directory: " + searchDirectory);

                FileindexWithSyncEntry fileindexWithSyncEntry = null;
                try {
                    fileindexWithSyncEntry = FileSyncHelper.checksyncFileindexTree(limitedFileindexEntry, searchDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileindexWithSyncEntry.SyncStatus syncStatus = fileindexWithSyncEntry.getSyncStatus();
                System.out.println("ModsController: modsGroup '" + modGroup.getName() + "' syncStatus: " + syncStatus);
                modGroupTableItem.setStatus(syncStatus);

                for(FileindexWithSyncEntry child : fileindexWithSyncEntry.getChildren()) {
                    System.out.println("ModsController: '" + child.getName() + "' syncStatus: " + syncStatus);
                }
            }*/

            ArrayList<FileindexWithSyncEntry.SyncStatus> statusList = new ArrayList<>(modGroup.getMods().size());
            for (Mod mod : modGroup.getMods()) {
                ModTableItem modTableItem = new ModTableItem(mod);
                CheckBoxTreeItem<CustomTableItem> modTreeItem = new CheckBoxTreeItem<>(modTableItem);
                modGroupTreeItem.getChildren().add(modTreeItem);
                modGroupTableItem.addChild(modTableItem);

                modTreeItem.selectedProperty().addListener((observable, oldValue, newValue) -> modTableItem.setChecked(newValue));
                modTreeItem.indeterminateProperty().addListener((observable, oldValue, newValue) -> modTableItem.setIndeterminate(newValue));

                FileindexWithSyncEntry.SyncStatus modStatus = ModStatusHelper.checkStatusForMod(limitedFileindexEntry, mod, Settings.getInstance().getSearchDirectories());
                modTableItem.setStatus(modStatus);
                statusList.add(modStatus);
            }
            FileindexWithSyncEntry.SyncStatus modsGroupStatus = ModStatusHelper.combineStatus(statusList);
            modGroupTableItem.setStatus(modsGroupStatus);

            rootNode.getChildren().add(modGroupTreeItem);
        }

        Platform.runLater(() -> {
            rootNode.setExpanded(true);
            treeTableView.setRoot(rootNode);

            /* sort by name */
            treeTableView.getSortOrder().add(nameColumn);
            TreeTableColumn.SortType sortType = nameColumn.getSortType();
            nameColumn.setSortType(sortType);
            nameColumn.setSortable(true);

            if (expandAllModsCheckBox.isSelected()) {
                for (Object child : treeTableView.getRoot().getChildren()) {
                    CheckBoxTreeItem<CustomTableItem> treeItem = (CheckBoxTreeItem<CustomTableItem>) child;
                    treeItem.setExpanded(true);
                }
            }
        });
    }

    private void createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem item1 = new MenuItem("Synchronize");
        item1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                TreeItem<CustomTableItem> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
                System.out.println(selectedItem);
            }
        });
        MenuItem item2 = new MenuItem("Change Location");
        item2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                TreeItem<CustomTableItem> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
                System.out.println(selectedItem);
            }
        });

        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(item1, item2);
        treeTableView.setContextMenu(contextMenu);
    }


    public void handleChangeLocationAction(ActionEvent actionEvent) {
        TreeItem<CustomTableItem> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            openChooseLocationDialog();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Change Location");
            alert.setHeaderText("Change Location");
            alert.setContentText("You have to choose a Modgroup or Mod on the left!");

            alert.showAndWait();
        }
    }

    public void handleRefreshAction(ActionEvent actionEvent) {
        update();
    }

    public void handleDownloadAction(ActionEvent actionEvent) {

        List<CustomTableItem> selectedItems = treeTableView.getSelectedTableItems();
        if (selectedItems.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Locations Missing");
            alert.setHeaderText("Locations Missing");
            alert.setContentText("In order to proceed with the download, select all Mods and Modgroups you want to download");

            alert.showAndWait();

            log.info("no mods or modgroups selected, aborting download");
            return;
        }

        if (!checkMissingLocations()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Locations Missing");
            alert.setHeaderText("Locations Missing");
            alert.setContentText("In order to proceed with the download, locations must be set for all Mods and Modgroups");

            alert.showAndWait();

            log.info("some locations are missing, aborting download");
            return;
        }

        for (Object modGroupObj : treeTableView.getRoot().getChildren()) {
            CheckBoxTreeItem<CustomTableItem> modGroupTreeItem = (CheckBoxTreeItem<CustomTableItem>) modGroupObj;
            ModGroupTableItem modGroupTableItem = (ModGroupTableItem) modGroupTreeItem.getValue();
            ModGroup modGroup = modGroupTableItem.getModGroup();

            boolean modGroupChecked = modGroupTableItem.getChecked();
            boolean modGroupIndeterminate = modGroupTableItem.getIndeterminate();

            FileindexEntry rootIndexEntry = null;
            try {
                rootIndexEntry = LauncherUtils.getFileIndexEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (modGroupChecked) {
                FileindexEntry limitedFileindexEntry = limitFileindexToModgroups(rootIndexEntry, modGroup);
                try {
                    FileSyncHelper.syncFileindexTree(limitedFileindexEntry, Paths.get(modGroupTableItem.getLocation()), LauncherUtils.getRepoURL());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (modGroupIndeterminate) {
                for (Object modObj : modGroupTreeItem.getChildren()) {
                    CheckBoxTreeItem<CustomTableItem> modTreeItem = (CheckBoxTreeItem<CustomTableItem>) modObj;

                    ModTableItem modTableItem = (ModTableItem) modTreeItem.getValue();
                    if (modTableItem.getChecked()) {
                        ModGroup tempModGroup = new ModGroup("tempModGroup", UUID.randomUUID(), new HashSet<>());
                        tempModGroup.addMod(modTableItem.getMod());
                        FileindexEntry limitedFileIndexEntry = limitFileindexToModgroups(rootIndexEntry, tempModGroup);
                        try {
                            FileSyncHelper.syncFileindexTree(limitedFileIndexEntry, Paths.get(modTableItem.getLocation()), LauncherUtils.getRepoURL());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if there are locations set for all selected items,
     * if there are locations missing, open selection dialog to choose the locations
     *
     * @return true if all locations are satisfied afterwards, false if locations are still missing
     */
    private boolean checkMissingLocations() {
        List<CustomTableItem> selectedTableItems = treeTableView.getSelectedTableItems();
        for (CustomTableItem selectedTableItem : selectedTableItems) {
            if (selectedTableItem.getLocation() == null) {
                switch (selectedTableItem.getType()) {
                    case MOD_GROUP:
                        boolean modGroupLocationSet = openChooseModGroupLocationDialog(selectedTableItem);
                        if (!modGroupLocationSet)
                            return false;
                        break;
                    case MOD:
                        boolean modLocationSet = openChooseModLocationDialog(selectedTableItem);
                        if (!modLocationSet)
                            return false;
                        break;
                    case ROOT:
                    default:
                        break;
                }
            }
        }

        return true;
    }

    private boolean openChooseModGroupLocationDialog(CustomTableItem customTableItem) {
        List<String> choices = Settings.getInstance().getSearchDirectories()
                .stream()
                .map(Path::toString)
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, choices);

        dialog.setTitle("Change Location");
        dialog.setHeaderText("Select a folder to where to download Modgroup: " + customTableItem.getName());
        dialog.setContentText("Search directory:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            Path path = Paths.get(result.get());
            customTableItem.setLocation(path);
            treeTableView.refresh();
            return true;
        }

        return false;
    }

    private boolean openChooseModLocationDialog(CustomTableItem customTableItem) {
        List<String> choices = Settings.getInstance().getSearchDirectories()
                .stream()
                .map(Path::toString)
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, choices);

        dialog.setTitle("Change Mod Location");
        dialog.setHeaderText("Select a folder to where to download mod: " + customTableItem.getName());
        dialog.setContentText("Search directory:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            Path path = Paths.get(result.get());
            customTableItem.setLocation(path);
            treeTableView.refresh();
            return true;
        }

        return false;
    }

    private boolean openChooseLocationDialog() {
        TreeItem<CustomTableItem> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
        CustomTableItem selectedTableItem = selectedItem.getValue();

        if (selectedTableItem != null) {
            switch (selectedTableItem.getType()) {
                case MOD_GROUP:
                    return openChooseModGroupLocationDialog(selectedTableItem);
                case MOD:
                    return openChooseModLocationDialog(selectedTableItem);
            }
        }
        return false;
    }

    public TreeTableView getModsTreeTableView() {
        return treeTableView;
    }
}
