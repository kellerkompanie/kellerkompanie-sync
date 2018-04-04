package com.kellerkompanie.kekosync.core.helper;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FileindexWithSyncEntry implements Serializable {
    public enum SyncStatus {LOCAL_MISSING, LOCAL_WITHCHANGES, LOCAL_INSYNC, REMOTE_MISSING, UNKNOWN};

    @Getter @Setter private String name;
    @Getter @Setter private long size;
    @Getter @Setter private boolean directory;
    @Getter @Setter private String UUID; //this property is filled with the content of the .id-file in case this is a primary mod-folder
    @Getter @Setter private String hash;
    @Getter @Setter private SyncStatus syncStatus;
    @Getter private List<FileindexWithSyncEntry> children = new ArrayList<>();

    public void addChild(FileindexWithSyncEntry child) { children.add(child); }

    public void removeChild(FileindexWithSyncEntry child) {
        children.remove(child);
    }

    public static FileindexWithSyncEntry fromFileindexEntry(FileindexEntry fileindexEntry, SyncStatus syncStatus) {
        return fromFileindexEntry(fileindexEntry, syncStatus, new ArrayList<>());
    }

    public static FileindexWithSyncEntry fromFileindexEntry(FileindexEntry fileindexEntry, SyncStatus syncStatus, List<FileindexWithSyncEntry> children) {
        return new FileindexWithSyncEntry(fileindexEntry.getName(), fileindexEntry.getSize(), fileindexEntry.isDirectory(), fileindexEntry.getUUID(), fileindexEntry.getHash(), syncStatus, children);
    }
}