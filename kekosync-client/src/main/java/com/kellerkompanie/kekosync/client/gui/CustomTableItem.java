package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

public abstract class CustomTableItem {

    private boolean checked;

    private boolean indeterminate;

    @Getter
    @Setter
    private FileindexWithSyncEntry.SyncStatus status = FileindexWithSyncEntry.SyncStatus.UNKNOWN;

    @Getter
    @Setter
    private Type type;

    enum Type {
        MOD_GROUP, MOD, ROOT
    }

    public abstract String getName();
    public abstract String getLocation();
    public abstract void setLocation(Path path);

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    public boolean getIndeterminate() {
        return indeterminate;
    }
}
