package com.kellerkompanie.kekosync.client.download;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class DownloadManager {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private BlockingQueue<DownloadTask> taskQueue = new LinkedBlockingDeque<>();
    private Runnable task = () -> {
        try {
            // take one item from the queue
            DownloadTask downloadTask = taskQueue.take();

            // download that item
            doDownload(downloadTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    public boolean queueDownloadTask(DownloadTask downloadTask) {
        synchronized (taskQueue) {
            boolean wasAdded = taskQueue.add(downloadTask);
            if (wasAdded)
                downloadTask.setState(DownloadState.QUEUED);
            return wasAdded;
        }
    }

    public void processQueue() {
        executorService.submit(task);
    }

    public void cancelQueue() {
        executorService.shutdownNow();
    }

    private void doDownload(DownloadTask downloadTask) {
        log.info("starting download of task: {}", downloadTask);

        downloadTask.setState(DownloadState.DOWNLOADING);
        downloadTask.getCallback().onDownloadStart(downloadTask);

        log.info("callback finished");

        String source = downloadTask.getSource();
        Path destination = downloadTask.getDestination();

        FileOutputStream fos;
        ReadableByteChannel rbc;
        URL url;

        try {
            url = new URL(source);
            rbc = new RBCWrapper(Channels.newChannel(url.openStream()), contentLength(url), (rbc1, progress) -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downloadTask.getCallback().onDownloadProgress(downloadTask, progress);

                if(progress >= 1.0)
                    downloadTask.getCallback().onDownloadFinished(downloadTask);
            });
            fos = new FileOutputStream(destination.toFile());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private int contentLength(URL url) {
        HttpURLConnection connection;
        int contentLength = -1;

        try {
            HttpURLConnection.setFollowRedirects(false);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");

            contentLength = connection.getContentLength();
        } catch (Exception e) {
        }

        return contentLength;
    }

    private interface RBCWrapperDelegate {
        void rbcProgressCallback(RBCWrapper rbc, double progress);
    }

    private static final class RBCWrapper implements ReadableByteChannel {
        private RBCWrapperDelegate delegate;
        private long expectedSize;
        private ReadableByteChannel rbc;
        private long readSoFar;

        RBCWrapper(ReadableByteChannel rbc, long expectedSize, RBCWrapperDelegate delegate) {
            this.delegate = delegate;
            this.expectedSize = expectedSize;
            this.rbc = rbc;
        }

        public void close() throws IOException {
            rbc.close();
        }

        public long getReadSoFar() {
            return readSoFar;
        }

        public boolean isOpen() {
            return rbc.isOpen();
        }

        public int read(ByteBuffer bb) throws IOException {
            int n;
            double progress;

            if ((n = rbc.read(bb)) > 0) {
                readSoFar += n;
                progress = expectedSize > 0 ? (double) readSoFar / (double) expectedSize : -1.0;
                delegate.rbcProgressCallback(this, progress);
            }

            return n;
        }
    }

}
