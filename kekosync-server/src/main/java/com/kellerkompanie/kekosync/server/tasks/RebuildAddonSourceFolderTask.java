package com.kellerkompanie.kekosync.server.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.server.helper.AddonProvider;
import com.kellerkompanie.kekosync.server.helper.FileindexGenerator;
import com.kellerkompanie.kekosync.server.helper.UUIDGenerator;
import com.kellerkompanie.kekosync.server.helper.ZsyncGenerator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dth
 * @author Schwaggot
 * <p>
 * RebuildAddonSourceFolderTask
 * represents the task of rebuilding a source folder containing addons, following a step by step list of instructions to
 * generate or refresh a new or existing addon source folder.
 * This class will delegate where possible to avoid a god complex.
 * <p>
 * step 1) check for .id files for every addon in the addon source folder and generate if missing
 * step 2) cleanup-zsync: clean out preexisting .zsync files
 * step 3) generate-zsync: generate new .zsync files
 * step 4) create file index
 */
@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
public class RebuildAddonSourceFolderTask {
    @Getter
    private String addonSourceFolder;

    public boolean execute() {
        log.info("building repository " + addonSourceFolder + " ...");

        log.info("\t(1) checking for .id files ...");
        if (!checkIdFileExistence()) return false;
        /*log.info("\t(2) cleaning zsync ...");
        if (!cleanupZsync()) return false;
        log.info("\t(3) regenerating zsync ...");
        if (!generateZsync()) return false;
        log.info("\t(4) generating file-index ...");
        if (!generateFileindex()) return false;*/
        log.info("done.\n");
        return true;
    }

    private boolean checkIdFileExistence() {
        List<Path> subdirectories;
        try {
            subdirectories = Files.walk(Paths.get(addonSourceFolder), 1)
                    .filter(p -> Files.isDirectory(p) && p.getFileName().toString().startsWith("@"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("couldn't check subdirectories", e);
            return false;
        }

        if (subdirectories.isEmpty()) {
            log.warn("folder {} does not contain any directories starting with @", addonSourceFolder);
            return false;
        }

        for (Path subdirectory : subdirectories) {
            String foldername = subdirectory.getFileName().toString();

            if (!subdirectory.resolve(Filenames.FILENAME_MODID).toFile().exists()) {
                String uuid = AddonProvider.getInstance().getAddonUuidByFoldername(foldername);
                if (uuid == null) {
                    uuid = UUIDGenerator.generateUUID().toString();
                    AddonProvider.getInstance().createNewAddon(uuid, foldername);
                }

                try {
                    Files.writeString(subdirectory.resolve(Filenames.FILENAME_MODID), uuid);
                } catch (IOException e) {
                    log.error("Could not write .id-file.", e);
                    return false;
                }
            } else {
                Path idFilePath = subdirectory.resolve(Filenames.FILENAME_MODID);
                String uuid;
                try {
                    uuid = Files.readString(idFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                if (!AddonProvider.getInstance().containsAddon(uuid)) {
                    AddonProvider.getInstance().createNewAddon(uuid, foldername);
                }
            }
        }
        return true;
    }

    private boolean cleanupZsync() {
        try {
            ZsyncGenerator.cleanDirectory(addonSourceFolder);
            return true;
        } catch (IOException e) {
            log.error("ran into trouble during cleanup", e);
            return false;
        }
    }

    private boolean generateZsync() {
        try {
            ZsyncGenerator.processDirectory(addonSourceFolder);
            return true;
        } catch (IOException e) {
            log.error("ran into trouble during zsync-generation", e);
            return false;
        }
    }

    private boolean generateFileindex() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileindexEntry existingFileindexEntry = null;
        Path fileindexFilePath = Paths.get(addonSourceFolder, Filenames.FILENAME_INDEXFILE);
        FileindexGenerator fileindexGenerator;
        if (Files.exists(fileindexFilePath)) {
            //Paths.get(serverRepository.getFolder(), Filenames.FILENAME_INDEXFILE).toFile().delete();
            try {
                Reader reader = Files.newBufferedReader(fileindexFilePath, StandardCharsets.UTF_8);
                existingFileindexEntry = gson.fromJson(reader, FileindexEntry.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // generate file-index generator from existing indices
            fileindexGenerator = new FileindexGenerator(existingFileindexEntry, addonSourceFolder);
        } else {
            // there is no existing index file, so create a new generator
            fileindexGenerator = new FileindexGenerator(addonSourceFolder);
        }

        FileindexEntry fileindexEntry;
        fileindexEntry = fileindexGenerator.index();

        String indexJson = gson.toJson(fileindexEntry);
        try {
            Files.writeString(fileindexFilePath, indexJson);
        } catch (IOException e) {
            log.error("Could not write index-file.", e);
            return false;
        }
        return true;
    }
}
