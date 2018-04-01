package com.kellerkompanie.kekosync.server;

import com.salesforce.zsync.ZsyncMake;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author Schwaggot
 */
class ZsyncGenerator {

    private ZsyncMake zsyncMake = new ZsyncMake();
    private PathMatcher sourceFileMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{pbo,bisign,bikey,cpp,paa,dll}");
    private PathMatcher zsyncFileMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{zsync}");

    void processDirectory(String directoryPath) throws IOException {
        Files.walk(Paths.get(directoryPath))
                .filter(p -> sourceFileMatcher.matches(p.getFileName()))
                .distinct()
                .forEach(this::processFile);
    }

    private void processFile(Path sourceFilePath) {

        Path zsyncFilePath = zsyncMake.make(sourceFilePath);
        System.out.println(String.format("%s -> %s", sourceFilePath, zsyncFilePath));
    }

    void cleanDirectory(String directoryPath) throws IOException {
        Files.walk(Paths.get(directoryPath))
                .filter(p -> zsyncFileMatcher.matches(p.getFileName()))
                .distinct()
                .forEach(this::deleteFile);
    }

    private void deleteFile(Path filePath) {
        try {
            Files.delete(filePath);
            System.out.println(String.format("deleted %s", filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
