package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@AllArgsConstructor
public abstract class CustomTableItem {
    @Getter
    @Setter
    private CustomTableItem.CheckedState checked;

    @Getter
    @Setter
    private Type type;

    @Getter
    @Setter
    private FileindexWithSyncEntry.SyncStatus status;

    enum Type {
        MOD_GROUP, MOD, ROOT
    }

    enum CheckedState {
        CHECKED, UNCHECKED, INDETERMINATE
    }

    public abstract String getName();
    public abstract String getLocation();
    public abstract void setLocation(Path path);
}
