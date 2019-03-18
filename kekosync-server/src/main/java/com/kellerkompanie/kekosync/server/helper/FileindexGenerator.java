package com.kellerkompanie.kekosync.server.helper;

import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static com.kellerkompanie.kekosync.core.helper.FileLocationHelper.getModId;
import static com.kellerkompanie.kekosync.core.helper.HashHelper.convertToHex;
import static com.kellerkompanie.kekosync.core.helper.HashHelper.generateSHA512;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileindexGenerator {

    private HashMap<String, FileindexEntry> fileindexTreeMap;
    private String directoryPath;

    public FileindexGenerator(String directoryPath) {
        if (directoryPath == null)
            throw new IllegalArgumentException("Directory path cannot be null");

        this.fileindexTreeMap = new HashMap<>();
        this.directoryPath = directoryPath;
    }

    public FileindexGenerator(FileindexEntry existingFileindexEntry, String directoryPath) {
        this(directoryPath);

        if (existingFileindexEntry == null)
            throw new IllegalArgumentException("FileindexEntry cannot be null");

        addToMap(existingFileindexEntry);
    }

    private void addToMap(FileindexEntry fileindexEntry) {
        if (fileindexEntry == null)
            throw new IllegalArgumentException("FileindexEntry cannot be null");

        String fileindexEntryTreePath = fileindexEntry.getFileindexTreePath();
        fileindexTreeMap.put(fileindexEntryTreePath, fileindexEntry);
        for (FileindexEntry child : fileindexEntry.getChildren()) {
            child.setParent(fileindexEntry);
            addToMap(child);
        }
    }

    public FileindexEntry index() {
        FileindexEntry fileindexEntry = new FileindexEntry();
        fileindexEntry.setName("");
        fileindexEntry.setSize(0);
        fileindexEntry.setDirectory(true);
        fillEntry(Paths.get(directoryPath), fileindexEntry, 1);
        return fileindexEntry;
    }

    private void fillEntry(Path path, FileindexEntry fileindexEntry, int level) {
        if (path == null)
            throw new IllegalArgumentException("path cannot be null");
        if (fileindexEntry == null)
            throw new IllegalArgumentException("FileindexEntry cannot be null");

        long size = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path entry : directoryStream) {
                // ignore ArmASync files
                if (entry.endsWith(".a3s"))
                    continue;

                if (Files.isDirectory(entry)) {
                    String uuid = null;
                    if (level == 1) { //we are on the first level and there might be .id files there! oh joy, let's read them to annotate this entry proper!
                        uuid = getModId(entry).toString();
                    }
                    long lastModified = Files.getLastModifiedTime(entry).toMillis();
                    FileindexEntry newfileindexEntry = new FileindexEntry();
                    newfileindexEntry.setName(entry.getFileName().toString());
                    newfileindexEntry.setSize(0);
                    newfileindexEntry.setDirectory(true);
                    newfileindexEntry.setUUID(uuid);
                    newfileindexEntry.setLastModified(lastModified);
                    newfileindexEntry.setParent(fileindexEntry);
                    fillEntry(entry, newfileindexEntry, level + 1);
                    fileindexEntry.addChild(newfileindexEntry);
                    size += newfileindexEntry.getSize();
                } else {
                    FileindexEntry newfileindexEntry = new FileindexEntry();
                    newfileindexEntry.setName(entry.getFileName().toString());
                    newfileindexEntry.setSize(Files.size(entry));
                    newfileindexEntry.setParent(fileindexEntry);

                    long lastModified = Files.getLastModifiedTime(entry).toMillis();
                    newfileindexEntry.setLastModified(lastModified);

                    String entryTreePath = newfileindexEntry.getFileindexTreePath();
                    FileindexEntry existingEntry = fileindexTreeMap.get(entryTreePath);

                    if(existingEntry != null) {
                        // there already exists an entry, check if update is necessary
                        boolean hasFilesizeChanged = existingEntry.getSize() != newfileindexEntry.getSize();
                        boolean wasModified = existingEntry.getLastModified() < newfileindexEntry.getLastModified();
                        if (!hasFilesizeChanged && !wasModified) {
                            // use existing entry instead of creating new one
                            fileindexEntry.addChild(existingEntry);
                            size += newfileindexEntry.getSize();
                            continue;
                        }
                    }

                    log.info("generating {}", entry);
                    String fileHash = convertToHex(generateSHA512(entry));
                    newfileindexEntry.setHash(fileHash);
                    fileindexEntry.addChild(newfileindexEntry);
                    size += newfileindexEntry.getSize();
                }
            }
        } catch (IOException ex) {
            log.error("Error while indexing", ex);
        }
        fileindexEntry.setSize(size);
    }


}
