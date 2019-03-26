package com.kellerkompanie.kekosync.client.gui.task;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kellerkompanie.kekosync.client.download.DownloadCallback;
import com.kellerkompanie.kekosync.client.download.DownloadManager;
import com.kellerkompanie.kekosync.client.download.DownloadTask;
import com.kellerkompanie.kekosync.client.gui.LauncherController;
import com.kellerkompanie.kekosync.client.gui.LocalModGroup;
import com.kellerkompanie.kekosync.client.gui.ModsController;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.entities.RunningModset;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class UpdateCurrentModsetTask extends ProgressTask<RunningModset> {

    private static final UUID RUNNING_MODSET_UUID = new UUID(0, 0);
    private static final File appdataPath = new File(System.getenv("APPDATA") + File.separator + "KekoSync" + File.separator + "cache");
    private static final File currentModsetFile = new File(appdataPath, "current_modset.json");

    @Override
    public RunningModset doInBackground() {
        DownloadTask downloadTask = new DownloadTask(Settings.getInstance().getServerInfo().getInfoURL(), currentModsetFile, new DownloadCallback() {
            @Override
            public void onDownloadStart(DownloadTask downloadTask) {
                Platform.runLater(() -> LauncherController.getInstance().setProgressText("Downloading News ..."));
            }

            @Override
            public void onDownloadProgress(DownloadTask downloadTask, double progress) {
                Platform.runLater(() -> LauncherController.getInstance().setProgress(progress));
            }

            @Override
            public void onDownloadFinished(DownloadTask downloadTask) { }
        });

        DownloadManager downloadManager = new DownloadManager();
        downloadManager.queueDownloadTask(downloadTask);
        Future future = downloadManager.processQueue();

        try {
            // block until download is finished
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        downloadManager.shutdown();

        RunningModset runningModset = null;
        try {
            runningModset = readCurrentModsetFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return runningModset;
    }

    @Override
    public void onPostExecute(RunningModset runningModset) {
        HashSet<Mod> currentMods = new HashSet<>(runningModset.getMods());
        ModGroup currentlyRunningModGroup = new ModGroup("Current Server Modset", RUNNING_MODSET_UUID, currentMods);
        LocalModGroup runningLocalModGroup = new LocalModGroup(currentlyRunningModGroup);
        runningLocalModGroup.setPriority(0);

        Platform.runLater(() -> {
            LauncherController.getInstance().setProgressText("Current Modset up-to-date");
            LauncherController.getInstance().setProgress(0);

            ModsController.getInstance().updateRunningModset(runningLocalModGroup);
        });
    }

    private RunningModset readCurrentModsetFromFile() throws IOException {
        Gson gson = new Gson();
        log.info("{}", currentModsetFile);
        JsonReader reader = new JsonReader(new FileReader(currentModsetFile));
        return gson.fromJson(reader, RunningModset.class);
    }
}
