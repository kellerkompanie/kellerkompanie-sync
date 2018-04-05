package com.kellerkompanie.kekosync.core.helper;

import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.Mod;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class FileLocationHelper {
    public static Path getModLocalRootpath(Mod mod, Path... localDirectories) {
        return getModLocalRootpath(mod, Arrays.asList(localDirectories));
    }

    public static Path getModLocalRootpath(Mod mod, Iterable<Path> localDirectories) {
        for ( Path localDirectory : localDirectories ) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(localDirectory)) {
                for (Path entry : directoryStream) {
                    if (Files.isDirectory(entry)) {
                        UUID localDirModId = getModId(entry);
                        if ( localDirModId != null && localDirModId.equals(mod.getUuid()) ) return localDirectory;
                    }
                }
            } catch (IOException e) {
                log.debug("Error while traversing directories", e);
            }
        }
        return null;
    }

    public static UUID getModId(Path modsubdirectory) {
        try {
            String stringValue = new String(Files.readAllBytes(modsubdirectory.resolve(Filenames.FILENAME_MODID)), "UTF-8");
            return UUID.fromString(stringValue);
        } catch (IOException e) {
            log.debug("error while reading {}/.id", modsubdirectory.toString(), e);
            return null;
        }
    }
}
