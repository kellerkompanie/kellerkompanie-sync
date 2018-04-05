package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.entities.ModGroup;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModGroupTableItem extends CustomTableItem {

    private ModGroup modGroup;
    private List<ModTableItem> children = new ArrayList<>();

    public ModGroupTableItem(ModGroup modGroup) {
        super();
        this.modGroup = modGroup;
        setType(Type.MOD_GROUP);
    }

    @Override
    public String getName() {
        return modGroup.getName();
    }

    @Override
    public String getLocation() {
        // TODO implement
        return null;
    }

    @Override
    public void setLocation(Path path) {
        for (ModTableItem modTableItem : children) {
            modTableItem.setLocation(path);
        }
    }

    public void addChild(ModTableItem modTableItem) {
        children.add(modTableItem);
    }
}
