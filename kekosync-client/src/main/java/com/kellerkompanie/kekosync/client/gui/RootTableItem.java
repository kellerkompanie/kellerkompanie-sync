package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;

import java.nio.file.Path;

public class RootTableItem extends CustomTableItem {

    public RootTableItem(CheckedState checked, Type type) {
        super(checked, type);
    }

    @Override
    public FileindexWithSyncEntry.SyncStatus getStatus() {
        return FileindexWithSyncEntry.SyncStatus.LOCAL_INSYNC;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public void setLocation(Path path) {

    }
}
