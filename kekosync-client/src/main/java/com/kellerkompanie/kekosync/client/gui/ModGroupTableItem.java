package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModGroupTableItem extends CustomTableItem {

    private ModGroup modGroup;
    private List<ModTableItem> children = new ArrayList<>();

    public ModGroupTableItem(ModGroup modGroup) {
        super(CheckedState.UNCHECKED, Type.MOD_GROUP);
        this.modGroup = modGroup;
    }

    @Override
    public FileindexWithSyncEntry.SyncStatus getStatus() {
        return FileindexWithSyncEntry.SyncStatus.UNKNOWN;
    }

    @Override
    public String getName() {
        return modGroup.getName();
    }

    @Override
    public String getLocation() {
        // TODO implement
        return null;
    }

    @Override
    public void setLocation(Path path) {
        for(ModTableItem modTableItem : children) {
            modTableItem.setLocation(path);
        }
    }

    public void addChild(ModTableItem modTableItem) {
        children.add(modTableItem);
    }
}
