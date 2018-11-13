package com.kellerkompanie.kekosync.server.tasks;

import com.google.gson.GsonBuilder;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.entities.Repository;
import com.kellerkompanie.kekosync.core.helper.FileLocationHelper;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.server.entities.ServerRepository;
import com.kellerkompanie.kekosync.server.helper.FileindexGenerator;
import com.kellerkompanie.kekosync.server.helper.UUIDGenerator;
import com.kellerkompanie.kekosync.server.helper.ZsyncGenerator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dth
 * @author Schwaggot
 * <p>
 * RebuildRepositoryTask
 * represents the task of rebuilding a repository, following a step by step list of instructions to
 * generate or refresh a new or existing repository.
 * This class will delegate where possible to avoid a god complex.
 * <p>
 * step 1) check for .id files for every mod in the repository and generate if missing
 * step 2) generate sample modgroup file with "all" modgroup if none exists
 * step 3) cleanup-zsync
 * clean out preexisting .zsync files
 * step 4) generate-zsync
 * generate new .zsync files
 */
@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
public class RebuildRepositoryTask {
    @Getter
    private ServerRepository serverRepository;

    public boolean execute() {
        log.debug("step1: checking for .id files");
        if (!checkModIdFileExistence()) return false;
        log.debug("step2: generating sample modgroup file if necessary");
        if (!checkModgroupFile()) return false;
        log.debug("step3: cleaning zsync");
        if (!cleanupZsync()) return false;
        log.debug("step4: regenerating zsync");
        if (!generateZsync()) return false;
        log.debug("step5: generate file-index");
        if (!generateFileindex()) return false;
        log.debug("done.");
        return true;
    }

    private boolean checkModIdFileExistence() {
        List<Path> subdirectories = null;
        try {
            subdirectories = Files.walk(Paths.get(serverRepository.getFolder()), 1)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("couldn't check subdirectories", e);
            return false;
        }
        subdirectories.remove(0); //remove repositoryPath itself from the list
        for (Path subdirectory : subdirectories) {
            if (!subdirectory.resolve(Filenames.FILENAME_MODID).toFile().exists()) {
                try {
                    Files.write(subdirectory.resolve(Filenames.FILENAME_MODID), UUIDGenerator.generateUUID().toString().getBytes("UTF-8"));
                } catch (IOException e) {
                    log.error("Could not write .id-file.", e);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkModgroupFile() {
        if (Paths.get(serverRepository.getFolder(), Filenames.FILENAME_MODGROUPS).toFile().exists()) return true;

        List<Path> subdirectories = null;
        try {
            subdirectories = Files.walk(Paths.get(serverRepository.getFolder()), 1)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("couldn't check subdirectories", e);
            return false;
        }
        subdirectories.remove(0); //remove repositoryPath itself from the list

        //we seem to have to generate an example :-(
        Set<Mod> modSet = new HashSet<>(subdirectories.size());
        for (Path subdirectory : subdirectories) {
            modSet.add(new Mod(subdirectory.getFileName().toString(), FileLocationHelper.getModId(subdirectory)));
        }
        ModGroup allModsGroup = new ModGroup("all", UUIDGenerator.generateUUID(), modSet);

        Repository repository = new Repository(serverRepository.getName(), UUIDGenerator.generateUUID(), Arrays.asList(allModsGroup), null);
        String repositoryJson = new GsonBuilder().setPrettyPrinting().create().toJson(repository);
        try {
            Files.write(Paths.get(serverRepository.getFolder()).resolve(Filenames.FILENAME_MODGROUPS), repositoryJson.getBytes("UTF-8"));
        } catch (IOException e) {
            log.error("Could not write modgroup-file.", e);
            return false;
        }
        return true;
    }

    private boolean cleanupZsync() {
        try {
            ZsyncGenerator.cleanDirectory(serverRepository.getFolder());
            return true;
        } catch (IOException e) {
            log.error("ran into trouble during cleanup", e);
            return false;
        }
    }

    private boolean generateZsync() {
        try {
            ZsyncGenerator.processDirectory(serverRepository.getFolder());
            return true;
        } catch (IOException e) {
            log.error("ran into trouble during zsync-generation", e);
            return false;
        }
    }

    private boolean generateFileindex() {
        if (Paths.get(serverRepository.getFolder(), Filenames.FILENAME_INDEXFILE).toFile().exists()) {
            Paths.get(serverRepository.getFolder(), Filenames.FILENAME_INDEXFILE).toFile().delete();
        }

        FileindexEntry fileindexEntry = null;
        try {
            fileindexEntry = FileindexGenerator.index(serverRepository.getFolder());
        } catch (IOException e) {
            log.error("ran into trouble during zsync-generation", e);
            return false;
        }

        String indexJson = new GsonBuilder().setPrettyPrinting().create().toJson(fileindexEntry);
        try {
            Files.write(Paths.get(serverRepository.getFolder()).resolve(Filenames.FILENAME_INDEXFILE), indexJson.getBytes("UTF-8"));
        } catch (IOException e) {
            log.error("Could not write modgroup-file.", e);
            return false;
        }
        return true;
    }
}
