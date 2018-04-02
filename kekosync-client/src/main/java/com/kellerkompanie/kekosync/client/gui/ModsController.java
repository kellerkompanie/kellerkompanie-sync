package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.settings.Settings;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.ResourceBundle;
import java.util.Set;

public class ModsController implements Initializable {

    TreeItem<ModItem> rootNode = new TreeItem<>(new ModItem(true, "root", ModItem.Status.INCOMPLETE));
    TreeItem<ModItem> mainNode = new TreeItem<>(new ModItem(true, "kellerkompanie-main", ModItem.Status.OK));
    TreeItem<ModItem> optionalNode = new TreeItem<>(new ModItem(true, "kellerkompanie-optional", ModItem.Status.OK));


    TreeItem<ModItem> node1 = new TreeItem<>(new ModItem(true, "@CBA_A3", ModItem.Status.OK));
    TreeItem<ModItem> node2 = new TreeItem<>(new ModItem(false, "@ace", ModItem.Status.OK));
    TreeItem<ModItem> node3 = new TreeItem<>(new ModItem(true, "@acex", ModItem.Status.MISSING));
    TreeItem<ModItem> node4 = new TreeItem<>(new ModItem(true, "@task_force_radio", ModItem.Status.OK));
    TreeItem<ModItem> node5 = new TreeItem<>(new ModItem(true, "@3denEnhanced", ModItem.Status.INCOMPLETE));
    TreeItem<ModItem> node6 = new TreeItem<>(new ModItem(true, "@AresModAchilles", ModItem.Status.OK));

    @FXML
    private TreeView optionalsTreeView;
    @FXML
    private TreeView foldersTreeView;
    @FXML
    private TreeTableView<ModItem> treeTableView;

    @FXML
    private TreeTableColumn<ModItem, Boolean> checkColumn;
    @FXML
    private TreeTableColumn<ModItem, String> nameColumn;
    @FXML
    private TreeTableColumn<ModItem, ModItem.Status> statusColumn;

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
        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);

        TreeItem<String> itemEden = new TreeItem<>();
        itemEden.setValue("@3denEnhanced");
        root.getChildren().add(itemEden);

        TreeItem<String> itemJSRS = new TreeItem<>();
        itemJSRS.setValue("@JSRS");
        root.getChildren().add(itemJSRS);

        TreeItem<String> itemBlastcore = new TreeItem<>();
        itemBlastcore.setValue("@Blastcore");
        root.getChildren().add(itemBlastcore);

        optionalsTreeView.setRoot(root);
        optionalsTreeView.setShowRoot(false);
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
        mainNode.getChildren().addAll(node1, node2, node3, node4);
        optionalNode.getChildren().addAll(node5, node6);

        rootNode.getChildren().addAll(mainNode, optionalNode);
        rootNode.setExpanded(true);

        nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));

        statusColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));
        statusColumn.setCellFactory(col -> {
            TreeTableCell<ModItem, ModItem.Status> cell = new TreeTableCell<ModItem, ModItem.Status>() {
                @Override
                public void updateItem(ModItem.Status item, boolean empty) {
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
                        ;
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

        checkColumn.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<ModItem, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TreeTableColumn.CellDataFeatures<ModItem, Boolean> param) {
                TreeItem<ModItem> treeItem = param.getValue();
                ModItem emp = treeItem.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(emp.isChecked());

                booleanProp.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                    }
                });
                return booleanProp;
            }
        });

        checkColumn.setCellFactory(p -> {
            CheckBoxTreeTableCell<ModItem, Boolean> cell = new CheckBoxTreeTableCell<>();

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
