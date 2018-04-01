package com.kellerkompanie.kekosync.server;

import com.salesforce.zsync.ZsyncMake;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;

import static com.kellerkompanie.kekosync.server.constants.FileMatcher.sourceFileMatcher;
import static com.kellerkompanie.kekosync.server.constants.FileMatcher.zsyncFileMatcher;

/**
 * @author Schwaggot
 */
@Slf4j
class ZsyncGenerator {
    private static ZsyncMake zsyncMake = new ZsyncMake();

    static void processDirectory(String directoryPath) throws IOException {
        Files.walk(Paths.get(directoryPath))
                .filter(p -> sourceFileMatcher.matches(p.getFileName()))
                .distinct()
                .forEach(ZsyncGenerator::processFile);
    }

    private static void processFile(Path sourceFilePath) {
        Path zsyncFilePath = zsyncMake.make(sourceFilePath);
        log.debug("{} -> {}", sourceFilePath, zsyncFilePath);
    }

    static void cleanDirectory(String directoryPath) throws IOException {
        Files.walk(Paths.get(directoryPath))
                .filter(p -> zsyncFileMatcher.matches(p.getFileName()))
                .distinct()
                .forEach(ZsyncGenerator::deleteFile);
    }

    private static void deleteFile(Path filePath) {
        try {
            Files.delete(filePath);
            log.debug("deleted {}", filePath);
        } catch (IOException e) {
            log.error("failed to delete {}", filePath, e);
        }
    }

}
