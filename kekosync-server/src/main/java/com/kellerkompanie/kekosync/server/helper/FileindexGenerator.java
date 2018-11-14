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
import java.util.ArrayList;

import static com.kellerkompanie.kekosync.core.helper.FileLocationHelper.getModId;
import static com.kellerkompanie.kekosync.core.helper.HashHelper.convertToHex;
import static com.kellerkompanie.kekosync.core.helper.HashHelper.generateSHA512;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileindexGenerator {
    public static FileindexEntry index(String directoryPath) {
        FileindexEntry fileindexEntry = new FileindexEntry("", 0, true, null, null, new ArrayList<>());
        fillEntry(Paths.get(directoryPath), fileindexEntry, 1);
        return fileindexEntry;
    }

    private static void fillEntry(Path path, FileindexEntry fileindexEntry, int level) {
        long size = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path entry: directoryStream) {
                if ( Files.isDirectory(entry) ) {
                    String uuid = null;
                    if ( level == 1 ) { //we are on the first level and there might be .id files there! oh joy, let's read them to annotate this entry proper!
                        uuid = getModId(entry).toString();
                    }
                    FileindexEntry newfileindexEntry = new FileindexEntry(entry.getFileName().toString(), 0, true, uuid, null, new ArrayList<>());
                    fillEntry(entry, newfileindexEntry, level+1);
                    fileindexEntry.addChild(newfileindexEntry);
                    size += newfileindexEntry.getSize();
                } else {
                    String fileHash = convertToHex(generateSHA512(entry));
                    FileindexEntry newfileindexEntry = new FileindexEntry(entry.getFileName().toString(), Files.size(entry), false, null, fileHash, new ArrayList<>());
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
