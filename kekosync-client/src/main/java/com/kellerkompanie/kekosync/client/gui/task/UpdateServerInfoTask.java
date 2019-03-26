package com.kellerkompanie.kekosync.client.gui.task;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kellerkompanie.kekosync.client.download.DownloadCallback;
import com.kellerkompanie.kekosync.client.download.DownloadManager;
import com.kellerkompanie.kekosync.client.download.DownloadTask;
import com.kellerkompanie.kekosync.client.gui.LauncherController;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.client.utils.LauncherUtils;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.ServerInfo;
import javafx.application.Platform;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class UpdateServerInfoTask extends ProgressTask<ServerInfo> {

    private static final String SERVER_INFO_URL = LauncherUtils.getRepositoryURL() + Filenames.FILENAME_SERVERINFO;
    private static final File appdataPath = new File(System.getenv("APPDATA") + File.separator + "KekoSync" + File.separator + "cache");
    private static final File serverInfoFile = new File(appdataPath, "serverinfo.json");

    @Override
    public ServerInfo doInBackground() {
        DownloadTask downloadTask = new DownloadTask(SERVER_INFO_URL, serverInfoFile, new DownloadCallback() {
            @Override
            public void onDownloadStart(DownloadTask downloadTask) {
                Platform.runLater(() -> LauncherController.getInstance().setProgressText("Downloading Server Info ..."));
            }

            @Override
            public void onDownloadProgress(DownloadTask downloadTask, double progress) {
                Platform.runLater(() -> LauncherController.getInstance().setProgress(progress));
            }

            @Override
            public void onDownloadFinished(DownloadTask downloadTask) {
            }
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

        ServerInfo serverInfo = null;
        try {
            serverInfo = readServerInfoFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverInfo;
    }

    @Override
    public void onPostExecute(ServerInfo serverInfo) {
        Settings.getInstance().setServerInfo(serverInfo);

        Platform.runLater(() -> {
            LauncherController.getInstance().setProgressText("Server Info up-to-date");
            LauncherController.getInstance().setProgress(0);
        });
    }

    private ServerInfo readServerInfoFromFile() throws IOException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(serverInfoFile));
        return gson.fromJson(reader, ServerInfo.class);
    }
}
