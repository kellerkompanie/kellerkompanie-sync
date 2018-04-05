package com.kellerkompanie.kekosync.server.helper;

import com.salesforce.zsync.ZsyncMake;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;

import static com.kellerkompanie.kekosync.server.constants.FileMatcher.sourceFileMatcher;
import static com.kellerkompanie.kekosync.server.constants.FileMatcher.zsyncFileMatcher;

/**
 * @author Schwaggot
 * @author dth
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZsyncGenerator {
    private static ZsyncMake zsyncMake = new ZsyncMake();

    public static void processDirectory(String directoryPath) throws IOException {
        Files.walk(Paths.get(directoryPath))
                .filter(p -> sourceFileMatcher.matches(p.getFileName()))
                .distinct()
                .forEach(ZsyncGenerator::processFile);
    }

    private static void processFile(Path sourceFilePath) {
        //Path zsyncFilePath = zsyncMake.make(sourceFilePath);
        ZsyncMake.Options options = new ZsyncMake.Options();
        options.setBlockSize(8192); //trying to stay compatible with lé arma3sync.
        Path zsyncFilePath = zsyncMake.writeToFile(sourceFilePath, options).getOutputFile();
        log.debug("{} -> {}", sourceFilePath, zsyncFilePath);
    }

    public static void cleanDirectory(String directoryPath) throws IOException {
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
