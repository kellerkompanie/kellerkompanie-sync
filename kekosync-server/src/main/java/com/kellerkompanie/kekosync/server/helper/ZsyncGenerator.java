package com.kellerkompanie.kekosync.server.helper;

import com.salesforce.zsync.ZsyncMake;
import com.salesforce.zsync.internal.ControlFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;

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
        if (Files.exists(getPathOfZsync(sourceFilePath))) return;

        ZsyncMake.Options options = new ZsyncMake.Options();
        options.setBlockSize(8192); //trying to stay compatible with lé arma3sync.
        Path zsyncFilePath = zsyncMake.writeToFile(sourceFilePath, options).getOutputFile();
        log.info("{} -> {}", sourceFilePath, zsyncFilePath);
    }

    public static void cleanDirectory(String directoryPath) throws IOException {
        Files.walk(Paths.get(directoryPath))
                .filter(p -> zsyncFileMatcher.matches(p.getFileName()))
                .distinct()
                .forEach(ZsyncGenerator::deleteFile);
    }

    private static void deleteFile(Path filePath) {
        try {
            InputStream inputStream = Files.newInputStream(filePath);
            ControlFile zsyncFile = ControlFile.read(inputStream);
            inputStream.close();

            long zsyncMTime = zsyncFile.getHeader().getMtime().getTime();
            long zsyncSize = zsyncFile.getHeader().getLength();
            Path originalFile = getPathOfOriginal(filePath);
            long originalMTime = (Files.getLastModifiedTime(originalFile).toMillis() / 1000) * 1000;  // zsync files are ignoring millisecond precision
            long originalSize = Files.size(originalFile);

            // only delete if zsync is outdated
            if ((zsyncMTime < originalMTime) || (zsyncSize != originalSize)) {
                Files.delete(filePath);
                log.info("deleted {}", filePath);
            }
        } catch (IOException e) {
            log.error("failed to delete {}", filePath, e);
        }
    }

    private static Path getPathOfOriginal(Path zsyncFilepath) {
        String fileName = zsyncFilepath.getFileName().toString();
        String originalFileName = fileName.substring(0, fileName.lastIndexOf("."));
        return zsyncFilepath.resolveSibling(originalFileName);
    }

    private static Path getPathOfZsync(Path sourceFilePath) {
        return sourceFilePath.resolveSibling(sourceFilePath.getFileName() + ".zsync");
    }

}
