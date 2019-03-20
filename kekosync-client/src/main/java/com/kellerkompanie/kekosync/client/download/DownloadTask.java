package com.kellerkompanie.kekosync.client.download;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

@EqualsAndHashCode(of = {"uuid"})
@ToString
public class DownloadTask {

    private final UUID uuid;
    @Getter
    private String source;
    @Getter
    private Path destination;
    @Getter
    @Setter
    private DownloadState state;

    @Getter
    private DownloadCallback callback;

    private DownloadTask() {
        uuid = UUID.randomUUID();
        state = DownloadState.CREATED;
    }

    public DownloadTask(String source, Path destination, DownloadCallback callback) {
        this();
        this.source = source;
        this.destination = destination;
        this.callback = callback;
    }

    public DownloadTask(String source, File destination, DownloadCallback callback) {
        this(source, destination.toPath(), callback);
    }

}
