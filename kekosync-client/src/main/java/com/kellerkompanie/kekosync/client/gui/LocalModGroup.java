package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class LocalModGroup extends ModGroup {
    @Getter
    @Setter
    private Path location;
    @Getter
    @Setter
    private FileindexWithSyncEntry.SyncStatus syncStatus = FileindexWithSyncEntry.SyncStatus.UNKNOWN;
    @Getter
    @Setter
    private int priority = 9;

    public LocalModGroup(ModGroup modGroup) {
        super(modGroup.getName(), modGroup.getUuid(), modGroup.getMods());
        assignPriority();
    }

    public LocalModGroup(ModGroup modGroup, Path location) {
        this(modGroup);
        this.location = location;
    }

    /**
     * Priority is used to sort entries in the GUI, e.g., currently running modpack should be first.
     * The lower the priority value, the higher up the item will be.
     */
    private void assignPriority() {
        if(getName().contains("Minimal"))
            setPriority(1);
        else if(getName().contains("Main"))
            setPriority(2);
        else if(getName().contains("Maps"))
            setPriority(3);
        else if(getName().contains("Optional"))
            setPriority(4);
    }

}
