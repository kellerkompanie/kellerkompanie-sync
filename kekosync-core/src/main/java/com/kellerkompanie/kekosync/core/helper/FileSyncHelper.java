package com.kellerkompanie.kekosync.core.helper;

import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.salesforce.zsync.Zsync;
import com.salesforce.zsync.ZsyncControlFileNotFoundException;
import com.salesforce.zsync.ZsyncException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.kellerkompanie.kekosync.core.helper.HashHelper.convertToHex;
import static com.kellerkompanie.kekosync.core.helper.HashHelper.generateSHA512;

@Slf4j
public final class FileSyncHelper {
    public static FileindexEntry limitFileindexToModgroups(FileindexEntry fileindexEntry, ModGroup... modGroups) {
        FileindexEntry limitedFileindexEntry = SerializationUtils.clone(fileindexEntry);
        HashMap<UUID, Mod> modsWeWant = new HashMap<>();
        for ( ModGroup modGroup : modGroups ) {
            for ( Mod mod : modGroup.getMods() ) {
                modsWeWant.put(mod.getUuid(), mod);
            }
        }

        Iterator<FileindexEntry> fileindexEntryIterator = limitedFileindexEntry.getChildren().iterator();
        while (fileindexEntryIterator.hasNext()) {
            FileindexEntry currentFileindexEntry = fileindexEntryIterator.next();

            if ( !currentFileindexEntry.isDirectory() ) {
                continue; //we can keep it in if it's not a directory, most likely it's the .modgroups.json or the fileindex.
            }
            if ( currentFileindexEntry.getUUID() == null ) { //we should have an id for every mod.
                // this is odd and should not have happened .. let's log this! :-)
                log.debug("had a first level directory entry without uuid ... w000t? => {}", currentFileindexEntry.getName());
            } else {
                if ( !modsWeWant.containsKey(UUID.fromString(currentFileindexEntry.getUUID())) ) { //let's see if the id is in the modsWeWant-list. if not .. remove it :-)
                    fileindexEntryIterator.remove();
                }
            }
        }
        return limitedFileindexEntry;
    }

    public static void syncFileindexFile(FileindexEntry fileindexEntry, Path localpath, String remotepath) throws IOException {
        syncFileindexFile(fileindexEntry, localpath, remotepath, false);
    }

    public static void syncFileindexFile(FileindexEntry fileindexEntry, Path localpath, String remotepath, boolean pretend) throws IOException {
        //first let's find out if the file exists locally .. if not .. then we need to fully get it anyways ..
        Path localfile = localpath.resolve(fileindexEntry.getName());
        try {
            if (Files.exists(localfile)) {
                String filehash = convertToHex(generateSHA512(localfile));
                String localfilehash = fileindexEntry.getHash();
                if ((Files.size(localfile) == fileindexEntry.getSize()) && (filehash.equals(localfilehash))) {
                    System.out.println(localfile.toString() + " - file is locally the same.");
                } else {
                    System.out.println(localfile.toString() + " - file needs to be synced.");
                    //yeah! we need real syncing!
                    if (!pretend) {
                        Zsync zsync = new Zsync();
                        URI zsyncFileBaseURL = URI.create(remotepath + fileindexEntry.getName() + ".zsync");
                        Zsync.Options options = new Zsync.Options();
                        options.setOutputFile(localfile);
                        zsync.zsync(zsyncFileBaseURL, options);
                    }
                }
            } else {
                System.out.println(localfile.toString() + " - file does not exist locally. Will download traditionally.");

                if (!pretend) HttpHelper.downloadFile(remotepath + fileindexEntry.getName(), localfile, fileindexEntry.getSize());
            }
        } catch (ZsyncControlFileNotFoundException e) {
            log.debug("zsync file for {} not found, falling back to traditional full download, {}", localfile, e);
            HttpHelper.downloadFile(remotepath + fileindexEntry.getName(), localfile, fileindexEntry.getSize());
        } catch (RuntimeException | ZsyncException e) {
            log.warn("error during sync with file {}, {}", localfile, e);
        }
    }

    public static FileindexWithSyncEntry checksyncFileindexFile(FileindexEntry fileindexEntry, Path localpath, String remotepath) throws IOException {
        //first let's find out if the file exists locally .. if not .. then we need to fully get it anyways ..
        Path localfile = localpath.resolve(fileindexEntry.getName());
        if (Files.exists(localfile)) {
            String filehash = convertToHex(generateSHA512(localfile));
            String localfilehash = fileindexEntry.getHash();
            if ((Files.size(localfile) == fileindexEntry.getSize()) && (filehash.equals(localfilehash))) {
                System.out.println(localfile.toString() + " - file is locally the same.");
                return FileindexWithSyncEntry.fromFileindexEntry(fileindexEntry, FileindexWithSyncEntry.SyncStatus.LOCAL_INSYNC);
            } else {
                System.out.println(localfile.toString() + " - file needs to be synced.");
                return FileindexWithSyncEntry.fromFileindexEntry(fileindexEntry, FileindexWithSyncEntry.SyncStatus.LOCAL_WITHCHANGES);
            }
        } else {
            System.out.println(localfile.toString() + " - file does not exist locally. Will download traditionally.");

            return FileindexWithSyncEntry.fromFileindexEntry(fileindexEntry, FileindexWithSyncEntry.SyncStatus.LOCAL_MISSING);
        }
    }

    public static void syncFileindexTree(FileindexEntry fileindexEntry, Path localpath, String remotepath) throws IOException {
        syncFileindexTree(fileindexEntry, localpath, remotepath, false);
    }

    public static void syncFileindexTree(FileindexEntry fileindexEntry, Path localpath, String remotepath, boolean pretend) throws IOException {
        if ( !Files.exists(localpath) && !pretend) Files.createDirectory(localpath);
        for ( FileindexEntry currentFileindexEntry : fileindexEntry.getChildren() ) {
            if ( !currentFileindexEntry.isDirectory() ) {
                if ( currentFileindexEntry.getName().endsWith(".zsync" ) ) continue; //we don't need to sync those.
                try {
                    syncFileindexFile(currentFileindexEntry, localpath, remotepath);
                } catch (IOException e) {
                    log.error("Error while syncing file {}", localpath.getFileName(), e);
                }
            } else {
                String newremotepath = null;
                try {
                    newremotepath = remotepath + URLEncoder.encode(currentFileindexEntry.getName(),"UTF-8") + "/";
                } catch (UnsupportedEncodingException e) {
                    log.error("encoding UTF-8 is missing, trololol.", e);
                }
                syncFileindexTree(currentFileindexEntry, localpath.resolve(currentFileindexEntry.getName()), newremotepath);
            }
        }
    }

    public static FileindexWithSyncEntry checksyncFileindexTree(FileindexEntry fileindexEntry, Path localpath, String remotepath) throws IOException {
        List<FileindexWithSyncEntry> newChildren = new ArrayList<>();
        for ( FileindexEntry currentFileindexEntry : fileindexEntry.getChildren() ) {
            if ( !currentFileindexEntry.isDirectory() ) {
                if ( currentFileindexEntry.getName().endsWith(".zsync" ) ) continue; //we don't need to sync those.
                newChildren.add(checksyncFileindexFile(currentFileindexEntry, localpath, remotepath));
            } else {
                String newremotepath = null;
                try {
                    newremotepath = remotepath + URLEncoder.encode(currentFileindexEntry.getName(),"UTF-8") + "/";
                } catch (UnsupportedEncodingException e) {
                    log.error("encoding UTF-8 is missing, trololol.", e);
                }
                newChildren.add(checksyncFileindexFile(currentFileindexEntry, localpath.resolve(currentFileindexEntry.getName()), newremotepath));
            }
        }
        return FileindexWithSyncEntry.fromFileindexEntry(fileindexEntry, FileindexWithSyncEntry.SyncStatus.UNKNOWN, newChildren);
    }
}
