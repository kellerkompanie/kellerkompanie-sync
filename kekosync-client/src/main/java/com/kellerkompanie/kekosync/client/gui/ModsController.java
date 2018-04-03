package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.entities.Repository;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class ModsController implements Initializable {


    /*TreeItem<CustomTableItem> rootNode = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.INDETERMINATE, "root", CustomTableItem.Status.INCOMPLETE));
    TreeItem<CustomTableItem> mainNode = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.CHECKED, "kellerkompanie-main", CustomTableItem.Status.OK));
    TreeItem<CustomTableItem> optionalNode = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.UNCHECKED, "kellerkompanie-optional", CustomTableItem.Status.OK));


    TreeItem<CustomTableItem> node1 = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.INDETERMINATE, "@CBA_A3", CustomTableItem.Status.OK));
    TreeItem<CustomTableItem> node2 = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.CHECKED, "@ace", CustomTableItem.Status.OK));
    TreeItem<CustomTableItem> node3 = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.CHECKED, "@acex", CustomTableItem.Status.MISSING));
    TreeItem<CustomTableItem> node4 = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.UNCHECKED, "@task_force_radio", CustomTableItem.Status.OK));
    TreeItem<CustomTableItem> node5 = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.UNCHECKED, "@3denEnhanced", CustomTableItem.Status.INCOMPLETE));
    TreeItem<CustomTableItem> node6 = new TreeItem<>(new CustomTableItem(CustomTableItem.CheckedState.CHECKED, "@AresModAchilles", CustomTableItem.Status.OK));*/

    TreeItem<CustomTableItem> rootNode = new TreeItem<>(new RootTableItem(CustomTableItem.CheckedState.CHECKED, CustomTableItem.Type.ROOT));

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
    private TreeTableColumn<CustomTableItem, CustomTableItem.Status> statusColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateModGroups();
        populateFolders();
        populateOptionals();
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
            Files.walk(path)
                    //.map(p -> p.getParent().getParent())
                    .filter(p -> p.getFileName().toString().startsWith("@"))
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

    private void populateModGroups() {
        Repository repository = null;
        try {
            repository = LauncherUtils.getRepository();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        List<ModGroup> modGroups = repository.getModGroups();

        for(ModGroup modGroup : modGroups) {
            ModGroupTableItem modGroupTableItem = new ModGroupTableItem(modGroup);
            TreeItem<CustomTableItem> modGroupTreeItem = new TreeItem<>(modGroupTableItem);

            for(Mod mod : modGroup.getMods()) {
                ModTableItem modTableItem = new ModTableItem(mod);
                TreeItem<CustomTableItem> modTreeItem = new TreeItem<>(modTableItem);
                modGroupTreeItem.getChildren().add(modTreeItem);
            }

            rootNode.getChildren().add(modGroupTreeItem);
        }

        rootNode.setExpanded(true);

        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));

        statusColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));
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

        /*nameColumn.prefWidthProperty().bind(
                treeTableView.widthProperty()
                        .subtract(checkColumn.widthProperty())
                        .subtract(statusColumn.widthProperty())
                        .subtract(2)  // a border stroke?
        );*/

        treeTableView.setRoot(rootNode);
        treeTableView.getSelectionModel().setCellSelectionEnabled(false);
        treeTableView.setShowRoot(false);
        treeTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        checkColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CustomTableItem, CustomTableItem.CheckedState>, ObservableValue<CustomTableItem.CheckedState>>() {
            @Override
            public ObservableValue<CustomTableItem.CheckedState> call(TreeTableColumn.CellDataFeatures<CustomTableItem, CustomTableItem.CheckedState> param) {
                TreeItem<CustomTableItem> treeItem = param.getValue();
                CustomTableItem emp = treeItem.getValue();
                //SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(emp.isChecked());
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

                /*booleanProp.addListener(new ChangeListener<CustomTableItem.CheckedState>() {
                    @Override
                    public void changed(ObservableValue<? extends CustomTableItem.CheckedState> observable, CustomTableItem.CheckedState oldValue, CustomTableItem.CheckedState newValue) {

                    }
                });*/
                return val;
            }
        });

        /*checkColumn.setCellFactory(p -> {
            CheckBoxTreeTableCell<CustomTableItem, Boolean> cell = new CheckBoxTreeTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });*/

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
                                setStyle("-fx-background-color: green");
                                break;
                            case UNCHECKED:
                                setStyle("-fx-background-color: red");
                                break;
                            case INDETERMINATE:
                                setStyle("-fx-background-color: orange");
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
    }
}
