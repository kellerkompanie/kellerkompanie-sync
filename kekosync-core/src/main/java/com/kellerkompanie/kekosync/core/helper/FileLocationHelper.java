package com.kellerkompanie.kekosync.core.helper;

import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.Mod;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
public class FileLocationHelper {
    public static Path getModPath(Mod mod, Path... localDirectories) {
        for ( Path localDirectory : localDirectories ) {
            UUID localDirModId = getModId(localDirectory);
            if ( localDirModId != null && localDirModId.equals(mod.getUuid()) ) return localDirectory;
        }
        return null;
    }

    public static UUID getModId(Path modsubdirectory) {
        try {
            String stringValue = new String(Files.readAllBytes(modsubdirectory.resolve(Filenames.FILENAME_MODID)), "UTF-8");
            return UUID.fromString(stringValue);
        } catch (IOException e) {
            log.error("error while reading {}/.id", modsubdirectory.toString(), e);
            return null;
        }
    }
}
