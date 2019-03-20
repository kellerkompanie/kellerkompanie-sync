package com.kellerkompanie.kekosync.client.download;

public interface DownloadCallback {

    void onDownloadStart(DownloadTask downloadTask);
    void onDownloadProgress(DownloadTask downloadTask, double progress);
    void onDownloadFinished(DownloadTask downloadTask);

}
