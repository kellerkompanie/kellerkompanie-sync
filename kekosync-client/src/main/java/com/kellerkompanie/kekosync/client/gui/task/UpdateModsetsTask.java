package com.kellerkompanie.kekosync.client.gui.task;

import com.kellerkompanie.kekosync.client.gui.LauncherController;
import com.kellerkompanie.kekosync.client.gui.LocalModGroup;
import com.kellerkompanie.kekosync.client.gui.ModsController;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.entities.Repository;
import com.kellerkompanie.kekosync.core.helper.FileSyncHelper;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;
import com.kellerkompanie.kekosync.core.helper.ModStatusHelper;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class UpdateModsetsTask extends ProgressTask<Object> {

    private double progress = 0;

    @Override
    public Object doInBackground() {
        Platform.runLater(() -> {
            LauncherController.getInstance().setProgress(progress);
            LauncherController.getInstance().setProgressText("Updating Modsets ...");
        });

        List<Repository> repositories = new LinkedList<>();
        HashMap<Repository, FileindexEntry> rootFileindexEntries = new HashMap<>();
        try {
            for (String repositoryIdentifier : Settings.getInstance().getServerInfo().getRepositoryIdentifiers()) {
                Repository repository = LauncherUtils.getRepository(repositoryIdentifier);
                FileindexEntry rootFileindexEntry = LauncherUtils.getFileIndexEntry(repositoryIdentifier);

                repositories.add(repository);
                rootFileindexEntries.put(repository, rootFileindexEntry);
            }
        } catch (Exception e) {
            log.error("{}", e);
            System.exit(1);
        }

        List<ModGroup> modGroups = new LinkedList<>();
        HashMap<ModGroup, FileindexEntry> limitedFileIndexEntries = new HashMap<>();
        for (Repository repository : repositories) {
            modGroups.addAll(repository.getModGroups());
            FileindexEntry rootFileindexEntry = rootFileindexEntries.get(repository);
            FileindexEntry limitedFileindexEntry = FileSyncHelper.limitFileindexToModgroups(rootFileindexEntry, modGroups);
            for (ModGroup modGroup : modGroups) {
                limitedFileIndexEntries.put(modGroup, limitedFileindexEntry);
            }
        }

        for (ModGroup modGroup : modGroups) {
            log.info("{}", "ModsController: checking modGroup '" + modGroup.getName() + "'");

            progress = 0;
            Platform.runLater(() -> {
                LauncherController.getInstance().setProgress(progress);
                LauncherController.getInstance().setProgressText("Updating " + modGroup.getName() + " ...");
            });

            LocalModGroup localModGroup = new LocalModGroup(modGroup);
            Path location = Settings.getInstance().getModsetLocation(localModGroup);
            localModGroup.setLocation(location);

            ArrayList<FileindexWithSyncEntry.SyncStatus> statusList = new ArrayList<>(modGroup.getMods().size());
            double percentage = 1.0 /  (double) modGroup.getMods().size();
            for (Mod mod : modGroup.getMods()) {
                log.info("checking {}", mod.getName());

                FileindexEntry limitedFileindexEntry = limitedFileIndexEntries.get(modGroup);
                FileindexWithSyncEntry.SyncStatus modStatus = ModStatusHelper.checkStatusForMod(limitedFileindexEntry, mod, Settings.getInstance().getSearchDirectories());
                statusList.add(modStatus);
                progress += percentage;
                Platform.runLater(() -> LauncherController.getInstance().setProgress(progress));
            }

            FileindexWithSyncEntry.SyncStatus modsGroupStatus = ModStatusHelper.combineStatus(statusList);
            localModGroup.setSyncStatus(modsGroupStatus);
            ModsController.getInstance().addLocalModGroup(localModGroup);
        }

        return null;
    }

    @Override
    public void onPostExecute(Object o) {
        Platform.runLater(() -> {
            ModsController.getInstance().updateModgroupDisplays();
            LauncherController.getInstance().setProgress(0);
            LauncherController.getInstance().setProgressText("Everything up-to-date");
        });
    }
}
