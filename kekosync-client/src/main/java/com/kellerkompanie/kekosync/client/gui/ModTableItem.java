package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.settings.Settings;
import com.kellerkompanie.kekosync.core.entities.Mod;
import com.kellerkompanie.kekosync.core.helper.FileLocationHelper;
import com.kellerkompanie.kekosync.core.helper.FileindexWithSyncEntry;
import lombok.Getter;

import java.nio.file.Path;

public class ModTableItem extends CustomTableItem {

    @Getter
    private Mod mod;
    private Path modLocation;

    public ModTableItem(Mod mod) {
        super();
        this.mod = mod;
        setType(Type.MOD);
    }

    @Override
    public String getName() {
        return mod.getName();
    }

    @Override
    public String getLocation() {
        if(modLocation == null)
            modLocation = FileLocationHelper.getModLocalRootpath(mod, Settings.getInstance().getSearchDirectories());

        if(modLocation != null)
            return modLocation.toString();
        else
            return null;
    }

    @Override
    public void setLocation(Path searchDirectory) {
        this.modLocation = searchDirectory;
    }

}
