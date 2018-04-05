package com.kellerkompanie.kekosync.client.utils;

import com.google.gson.Gson;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.core.helper.HttpHelper;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.core.constants.Filenames;
import com.kellerkompanie.kekosync.core.entities.Repository;

public class LauncherUtils {

    public static Repository getRepository() throws Exception {
        String repoJsonString = HttpHelper.readUrl(Settings.REPO_URL + Filenames.FILENAME_MODGROUPS);
        return new Gson().fromJson(repoJsonString, Repository.class);
    }

    public static FileindexEntry getFileIndexEntry() throws Exception {
        String indexJsonString = HttpHelper.readUrl(Settings.REPO_URL + Filenames.FILENAME_INDEXFILE);
        return new Gson().fromJson(indexJsonString, FileindexEntry.class);
    }

}
