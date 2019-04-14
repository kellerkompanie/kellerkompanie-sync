package com.kellerkompanie.kekosync.client;

import com.google.gson.Gson;
import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;
import com.kellerkompanie.kekosync.core.helper.HttpHelper;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.entities.ModGroup;
import com.kellerkompanie.kekosync.core.entities.Repository;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.util.*;

import static com.kellerkompanie.kekosync.core.helper.FileSyncHelper.*;

@Slf4j
public class ExampleRunthroughRunner {

    public static void main(String[] args) throws Exception {
        final String repoBaseURL =  "http://localhost/repo/";

        // Getting the modgroups
        String repoJsonString = HttpHelper.readUrl(repoBaseURL + Filenames.FILENAME_MODGROUPS);
        Repository repository = new Gson().fromJson(repoJsonString, Repository.class);

        log.info("{}", repository);

        String indexJsonString = HttpHelper.readUrl(repoBaseURL + Filenames.FILENAME_INDEXFILE);
        FileindexEntry rootindexEntry = new Gson().fromJson(indexJsonString, FileindexEntry.class);

        log.info("{}", "local results");
        log.info("{}", rootindexEntry);

        ModGroup allModGroup = repository.getModGroups().get(0);

        ModGroup onlyAceGroup = new ModGroup("onlyacegroup", UUID.randomUUID(), new HashSet<>());
        ModGroup moreAceGroup = new ModGroup("moreacegroup", UUID.randomUUID(), new HashSet<>());

        for (Mod mod: allModGroup.getMods()) {
//            if ( mod.getName().equals("@ace") ) onlyAceGroup.addMod(mod);
//            if ( mod.getName().equals("@ace") ) moreAceGroup.addMod(mod);
            if ( mod.getName().equals("@acex") ) moreAceGroup.addMod(mod);
        }

        FileindexEntry limitedFileindexEntry = limitFileindexToModgroups(rootindexEntry, allModGroup);

//        syncFileindexTree(limitedFileindexEntry, Paths.get("E:\\kekosync-local"), repoBaseURL);
        FileindexWithSyncEntry fileindexWithSyncEntry = checksyncFileindexTree(limitedFileindexEntry, Paths.get("E:\\kekosync-local"));


        log.info("{}", "done");


        //Zsync zsync = new Zsync();
        //URI zsyncFileBaseURL = URI.create(repoBaseURL + "/@ace/ace_advanced_ballistics.dll.zync");
        //zsync.zsync(zsyncFileBaseURL);
    }
}
