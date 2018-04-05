package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.entities.Repository;
import com.kellerkompanie.kekosync.core.helper.FileSyncHelper;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class ModsController implements Initializable {


    @FXML
    private CheckBox expandAllCheckBox;
    @FXML
    private ListView optionalsListView;
    @FXML
    private TreeView foldersTreeView;
    @FXML
    private TreeTableView<CustomTableItem> treeTableView;

    @FXML
    private TreeTableColumn<CustomTableItem, CustomTableItem.CheckedState> checkColumn;
    @FXML
    private TreeTableColumn<CustomTableItem, String> nameColumn;
    @FXML
    private TreeTableColumn<CustomTableItem, String> locationColumn;
    @FXML
    private TreeTableColumn<CustomTableItem, CustomTableItem.Status> statusColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initalizeModsTreeTableView();
        updateModsTreeTableView();
        populateFolders();
        populateOptionals();
    }

    private void initalizeModsTreeTableView() {
        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("location"));

        statusColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));

        treeTableView.getSelectionModel().setCellSelectionEnabled(false);
        treeTableView.setShowRoot(false);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        checkColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CustomTableItem, CustomTableItem.CheckedState>, ObservableValue<CustomTableItem.CheckedState>>() {
            @Override
            public ObservableValue<CustomTableItem.CheckedState> call(TreeTableColumn.CellDataFeatures<CustomTableItem, CustomTableItem.CheckedState> param) {
                TreeItem<CustomTableItem> treeItem = param.getValue();
                CustomTableItem emp = treeItem.getValue();
                ObservableValue<CustomTableItem.CheckedState> val = new ObservableValue<CustomTableItem.CheckedState>() {
                    @Override
                    public void addListener(InvalidationListener listener) {

                    }

                    @Override
                    public void removeListener(InvalidationListener listener) {

                    }

                    @Override
                    public void addListener(ChangeListener<? super CustomTableItem.CheckedState> listener) {

                    }

                    @Override
                    public void removeListener(ChangeListener<? super CustomTableItem.CheckedState> listener) {

                    }

                    @Override
                    public CustomTableItem.CheckedState getValue() {
                        return emp.getChecked();
                    }
                };

                return val;
            }
        });

        checkColumn.setCellFactory(col -> {
            CheckBoxTreeTableCell<CustomTableItem, CustomTableItem.CheckedState> cell = new CheckBoxTreeTableCell<CustomTableItem, CustomTableItem.CheckedState>() {
                @Override
                public void updateItem(CustomTableItem.CheckedState item, boolean empty) {
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
        });

        statusColumn.setCellFactory(col -> {
            TreeTableCell<CustomTableItem, CustomTableItem.Status> cell = new TreeTableCell<CustomTableItem, CustomTableItem.Status>() {
                @Override
                public void updateItem(CustomTableItem.Status item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                        switch (item) {
                            case OK:
                                setStyle("-fx-text-fill: green");
                                break;
                            case INCOMPLETE:
                                setStyle("-fx-text-fill: orange");
                                break;
                            case MISSING:
                                setStyle("-fx-text-fill: red");
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



        expandAllCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                for (Object child : foldersTreeView.getRoot().getChildren()) {
                    TreeItem<String> treeItem = (TreeItem<String>) child;
                    treeItem.setExpanded(new_val);
                }
            }
        });
    }

    private void populateFolders() {
        // TODO update folders after changes in search directories
        Set<Path> searchDirectories = Settings.getInstance().getSearchDirectories();

        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);

        for (Path path : searchDirectories) {
            TreeItem<String> item = new TreeItem<>();
            item.setValue(path.toString());
            root.getChildren().add(item);
            populatePath(item, path);
        }

        foldersTreeView.setRoot(root);
        foldersTreeView.setShowRoot(false);
    }

    private void populateOptionals() {
        optionalsListView.getItems().add("@3denEnhanced");
        optionalsListView.getItems().add("@JSRS");
        optionalsListView.getItems().add("@Blastcore");
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
        TreeItem<CustomTableItem> rootNode = new TreeItem<>(new RootTableItem(CustomTableItem.CheckedState.CHECKED, CustomTableItem.Type.ROOT));

        for (ModGroup modGroup : modGroups) {
            System.out.println("ModsController: checking modGroup '" + modGroup.getName() + "'");

            ModGroupTableItem modGroupTableItem = new ModGroupTableItem(modGroup);
            TreeItem<CustomTableItem> modGroupTreeItem = new TreeItem<>(modGroupTableItem);

            FileindexEntry limitedFileindexEntry = FileSyncHelper.limitFileindexToModgroups(rootFileindexEntry, modGroup);

            for (Path searchDirectory : Settings.getInstance().getSearchDirectories()) {
                System.out.println("ModsController: comparing modGroup '" + modGroup.getName() + "' against directory: " + searchDirectory);

                FileindexWithSyncEntry fileindexWithSyncEntry = null;
                try {
                    fileindexWithSyncEntry = FileSyncHelper.checksyncFileindexTree(limitedFileindexEntry, searchDirectory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileindexWithSyncEntry.SyncStatus syncStatus = fileindexWithSyncEntry.getSyncStatus();
                System.out.println("ModsController: modsGroup '" + modGroup.getName() + "' syncStatus: " + syncStatus);
            }

            for (Mod mod : modGroup.getMods()) {
                ModTableItem modTableItem = new ModTableItem(mod);
                TreeItem<CustomTableItem> modTreeItem = new TreeItem<>(modTableItem);
                modGroupTreeItem.getChildren().add(modTreeItem);
                modGroupTableItem.addChild(modTableItem);
            }

            rootNode.getChildren().add(modGroupTreeItem);
        }

        rootNode.setExpanded(true);
        treeTableView.setRoot(rootNode);

        /* sort by name */
        treeTableView.getSortOrder().add(nameColumn);
        TreeTableColumn.SortType sortType = nameColumn.getSortType();
        nameColumn.setSortType(sortType);
        nameColumn.setSortable(true);
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

    private void openChooseLocationDialog() {
        List<String> choices = Settings.getInstance().getSearchDirectories()
                .stream()
                .map(Path::toString)
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, choices);

        dialog.setTitle("Change Location");
        dialog.setHeaderText("Select a folder to where to download");
        dialog.setContentText("Search directory:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            TreeItem<CustomTableItem> selectedItem = treeTableView.getSelectionModel().getSelectedItem();
            if(selectedItem != null) {
                Path path = Paths.get(result.get());
                selectedItem.getValue().setLocation(path);
                treeTableView.refresh();
            }
        }
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
        updateModsTreeTableView();
    }
}
