package com.kellerkompanie.kekosync.server.helper;

import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
    private FileindexEntry existingFileindexEntry;
    private String directoryPath;

    public FileindexGenerator(FileindexEntry existingFileindexEntry, String directoryPath) {
        this.fileindexTreeMap = new HashMap<>();
        this.existingFileindexEntry = existingFileindexEntry;
        this.directoryPath = directoryPath;
        addToMap(existingFileindexEntry);
    }

    private void addToMap(FileindexEntry fileindexEntry) {
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
        long size = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path entry : directoryStream) {
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
