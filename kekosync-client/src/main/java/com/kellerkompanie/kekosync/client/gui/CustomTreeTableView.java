package com.kellerkompanie.kekosync.client.gui;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeTableView;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class CustomTreeTableView<T> extends TreeTableView<T> {

    public List<CustomTableItem> getSelectedTableItems() {
        LinkedList<CustomTableItem> selectedMods = new LinkedList<>();
        for(Object modGroupObj : getRoot().getChildren()) {
            CheckBoxTreeItem<CustomTableItem> modGroupTreeItem = (CheckBoxTreeItem<CustomTableItem>) modGroupObj;
            CustomTableItem modGroupTableItem = modGroupTreeItem.getValue();
            if(modGroupTableItem.getChecked())
                selectedMods.add(modGroupTableItem);

            for(Object modObj : modGroupTreeItem.getChildren()) {
                CheckBoxTreeItem<CustomTableItem> modTreeItem = (CheckBoxTreeItem<CustomTableItem>) modObj;
                CustomTableItem customTableItem = modTreeItem.getValue();
                if(customTableItem.getChecked())
                    selectedMods.add(customTableItem);
            }
        }
        return selectedMods;
    }

    public List<ModGroupTableItem> getSelectedModGroups() {
        LinkedList<ModGroupTableItem> selectedMods = new LinkedList<>();
        for(Object modGroupObj : getRoot().getChildren()) {
            CheckBoxTreeItem<CustomTableItem> modGroupTreeItem = (CheckBoxTreeItem<CustomTableItem>) modGroupObj;
            ModGroupTableItem modGroupTableItem = (ModGroupTableItem) modGroupTreeItem.getValue();
            if(modGroupTableItem.getChecked()) {
                selectedMods.add(modGroupTableItem);
            }
        }
        return selectedMods;
    }

    public List<ModGroupTableItem> getIndeterminateModGroups() {
        LinkedList<ModGroupTableItem> selectedMods = new LinkedList<>();
        for(Object modGroupObj : getRoot().getChildren()) {
            CheckBoxTreeItem<CustomTableItem> modGroupTreeItem = (CheckBoxTreeItem<CustomTableItem>) modGroupObj;
            ModGroupTableItem modGroupTableItem = (ModGroupTableItem) modGroupTreeItem.getValue();
            if(modGroupTableItem.getIndeterminate()) {
                selectedMods.add(modGroupTableItem);
            }
        }
        return selectedMods;
    }

    public List<ModTableItem> getSelectedMods() {
        LinkedList<ModTableItem> selectedMods = new LinkedList<ModTableItem>();
        for(Object modGroupObj : getRoot().getChildren()) {
            CheckBoxTreeItem<CustomTableItem> modGroupTreeItem = (CheckBoxTreeItem<CustomTableItem>) modGroupObj;
            for(Object modObj : modGroupTreeItem.getChildren()) {
                CheckBoxTreeItem<CustomTableItem> modTreeItem = (CheckBoxTreeItem<CustomTableItem>) modObj;

                ModTableItem modTableItem = (ModTableItem) modTreeItem.getValue();
                if (modTableItem.getChecked()) {
                    selectedMods.add(modTableItem);
                }
            }
        }
        return selectedMods;
    }

}
