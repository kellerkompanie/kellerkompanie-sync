package com.kellerkompanie.kekosync.server.helper;

import com.kellerkompanie.kekosync.core.entities.FileindexEntry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static com.kellerkompanie.kekosync.server.constants.FileMatcher.zsyncFileMatcher;

@Slf4j
@NoArgsConstructor
public final class FileindexGenerator {
    public static FileindexEntry index(String directoryPath) throws IOException {
        FileindexEntry fileindexEntry = new FileindexEntry("", 0, true, new ArrayList<>());
        fillEntry(Paths.get(directoryPath), fileindexEntry);
        return fileindexEntry;
    }

    private static void fillEntry(Path path, FileindexEntry fileindexEntry) {
        long size = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path entry: directoryStream) {
                if ( Files.isDirectory(entry) ) {
                    FileindexEntry newfileindexEntry = new FileindexEntry(entry.getFileName().toString(), 0, true, new ArrayList<>());
                    fillEntry(entry, newfileindexEntry);
                    fileindexEntry.addChild(newfileindexEntry);
                    size += newfileindexEntry.getSize();
                } else {
                    FileindexEntry newfileindexEntry = new FileindexEntry(entry.getFileName().toString(), Files.size(entry), false, new ArrayList<>());
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
