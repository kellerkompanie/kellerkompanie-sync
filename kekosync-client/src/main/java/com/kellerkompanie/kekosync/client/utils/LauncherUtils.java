package com.kellerkompanie.kekosync.client.utils;

import com.google.gson.Gson;
import com.kellerkompanie.kekosync.core.helper.FileindexEntry;
import com.kellerkompanie.kekosync.core.helper.HttpHelper;
import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.core.constants.Filenames;

import java.util.LinkedList;

public class LauncherUtils {

    public static FileindexEntry getFileIndexEntry(String repositoryIdentifier) throws Exception {
        String indexJsonString = HttpHelper.readUrl(LauncherUtils.getRepositoryURL() + repositoryIdentifier + "/" + Filenames.FILENAME_INDEXFILE);
        return new Gson().fromJson(indexJsonString, FileindexEntry.class);
    }

    public static LinkedList<String> getModsToStart() {
        // TODO FIXME
        //TreeTableView treeTableView = ModsController.getInstance().getModsTreeTableView();
        LinkedList<String> modsToStart = new LinkedList<String>();

        /*if (treeTableView.getRoot() == null)
            return new LinkedList<>();

        for (Object modGroupObj : treeTableView.getRoot().getChildren()) {
            CheckBoxTreeItem<CustomTableItem> modGroupTreeItem = (CheckBoxTreeItem<CustomTableItem>) modGroupObj;
            for (Object modObj : modGroupTreeItem.getChildren()) {
                CheckBoxTreeItem<CustomTableItem> modTreeItem = (CheckBoxTreeItem<CustomTableItem>) modObj;

                CustomTableItem customTableItem = modTreeItem.getValue();
                if (customTableItem.getChecked()) {
                    String folderPath = customTableItem.getLocation();
                    String name = customTableItem.getName();
                    if (name.startsWith("@"))
                        modsToStart.add(folderPath + File.separator + name);
                }
            }
        }*/

        return modsToStart;
    }

    public static String getRepositoryURL() {
        return Settings.REPOSITORY_URL.endsWith("/") ? Settings.REPOSITORY_URL : Settings.REPOSITORY_URL + "/";
    }

    public static String getRepoURL(String repositryIdentifier) {
        return getRepositoryURL() + repositryIdentifier;
    }




}
