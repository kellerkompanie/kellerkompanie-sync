package com.kellerkompanie.kekosync.core.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class FileindexWithSyncEntry extends FileindexEntry {
    public enum SyncStatus {LOCAL_MISSING, LOCAL_WITHCHANGES, LOCAL_INSYNC, REMOTE_MISSING, UNKNOWN};
    @Getter @Setter private SyncStatus syncStatus;

    public FileindexWithSyncEntry(String name, long size, boolean directory, String UUID, String hash, List<FileindexEntry> children) {
        super(name, size, directory, UUID, hash, children);
        this.syncStatus = SyncStatus.UNKNOWN;
    }

    public FileindexWithSyncEntry(String name, long size, boolean directory, String UUID, String hash, List<FileindexEntry> children, SyncStatus syncStatus) {
        super(name, size, directory, UUID, hash, children);
        this.syncStatus = syncStatus;
    }
}
