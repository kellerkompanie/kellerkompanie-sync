package com.kellerkompanie.kekosync.client.helper;

import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.helper.FileSyncHelper;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;

import java.io.IOException;
import java.nio.file.Path;

import static com.kellerkompanie.kekosync.core.helper.FileLocationHelper.getModLocalRootpath;

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

}
