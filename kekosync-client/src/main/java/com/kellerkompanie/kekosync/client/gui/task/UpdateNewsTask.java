package com.kellerkompanie.kekosync.client.gui.task;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.kellerkompanie.kekosync.client.download.DownloadCallback;
import com.kellerkompanie.kekosync.client.download.DownloadManager;
import com.kellerkompanie.kekosync.client.download.DownloadTask;
import com.kellerkompanie.kekosync.client.gui.LauncherController;
import com.kellerkompanie.kekosync.client.gui.NewsController;
import com.kellerkompanie.kekosync.core.entities.News;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
public class UpdateNewsTask extends ProgressTask<List<News>> {

    private static final String NEWS_URL = "http://server.kellerkompanie.com/news.json";
    private static final File newsPath = new File(System.getenv("APPDATA") + File.separator + "KekoSync" + File.separator + "cache");
    private static final File newsFile = new File(newsPath, File.separator + "news.json");

    @Override
    public List<News> doInBackground() {
        DownloadTask downloadTask = new DownloadTask(NEWS_URL, newsFile, new DownloadCallback() {
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

        List<News> newsList = null;
        try {
            newsList = readNewsFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newsList;
    }

    @Override
    public void onPostExecute(List<News> newsList) {
        Platform.runLater(() -> {
            try {
                NewsController.getInstance().updateNews(newsList);
                LauncherController.getInstance().setProgressText("News up-to-date");
                LauncherController.getInstance().setProgress(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private List<News> readNewsFromFile() throws IOException {
        Type type = new TypeToken<List<News>>() {
        }.getType();
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(newsFile));
        return gson.fromJson(reader, type);
    }
}
