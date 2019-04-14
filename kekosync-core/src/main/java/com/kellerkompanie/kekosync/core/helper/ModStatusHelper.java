package com.kellerkompanie.kekosync.core.helper;

import com.kellerkompanie.kekosync.core.entities.Mod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.kellerkompanie.kekosync.core.helper.FileLocationHelper.getModLocalRootpath;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ModStatusHelper {
    public static FileindexWithSyncEntry.SyncStatus checkStatusForMod(FileindexEntry rootFileindexEntry, Mod mod, Iterable<Path> localDirectories) {
        Path modPath = getModLocalRootpath(mod, localDirectories);
        if ( modPath == null ) return FileindexWithSyncEntry.SyncStatus.UNKNOWN;
        FileindexEntry limitedFileindexEntry = FileSyncHelper.limitFileindexToMods(rootFileindexEntry, mod);

        try {
            return FileSyncHelper.checksyncFileindexTree(limitedFileindexEntry, modPath).getSyncStatus();
        } catch (IOException e) {
            return FileindexWithSyncEntry.SyncStatus.UNKNOWN;
        }
    }

    public static FileindexWithSyncEntry.SyncStatus combineStatus(List<FileindexWithSyncEntry> fileindexWithSyncEntries) {
        List<FileindexWithSyncEntry.SyncStatus> syncStatusList = fileindexWithSyncEntries.stream()
                .map(FileindexWithSyncEntry::getSyncStatus)
                .collect(Collectors.toList());

        return combineStatus(syncStatusList);
    }

    public static FileindexWithSyncEntry.SyncStatus combineStatus(Iterable<FileindexWithSyncEntry.SyncStatus> syncStatuses) {
        FileindexWithSyncEntry.SyncStatus combinedSyncStatus = FileindexWithSyncEntry.SyncStatus.LOCAL_INSYNC;
        for ( FileindexWithSyncEntry.SyncStatus syncStatus : syncStatuses ) {
            if ( combinedSyncStatus.equals(FileindexWithSyncEntry.SyncStatus.LOCAL_INSYNC) && syncStatus.equals(FileindexWithSyncEntry.SyncStatus.LOCAL_WITHCHANGES) )
                combinedSyncStatus = FileindexWithSyncEntry.SyncStatus.LOCAL_WITHCHANGES;
            if ( !combinedSyncStatus.equals(FileindexWithSyncEntry.SyncStatus.UNKNOWN) && syncStatus.equals(FileindexWithSyncEntry.SyncStatus.LOCAL_MISSING) )
                combinedSyncStatus = FileindexWithSyncEntry.SyncStatus.LOCAL_MISSING;
            if ( syncStatus.equals(FileindexWithSyncEntry.SyncStatus.UNKNOWN) )
                combinedSyncStatus = FileindexWithSyncEntry.SyncStatus.UNKNOWN;
        }
        return combinedSyncStatus;
    }
}
