package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.entities.ModGroup;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ModGroupTableItem extends CustomTableItem {

    @Getter
    private ModGroup modGroup;
    private List<ModTableItem> children = new ArrayList<>();
    private Path modGroupDefaultLocation;

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
        // FIXME
        if (modGroupDefaultLocation == null) {
            String location = null;
            for(ModTableItem child : children) {
                /* if even one child has no location, the entire group has no valid notation */
                if(child.getLocation() == null)
                    break;

                /* choose first childs location as initial location */
                if(location == null) {
                    location = child.getLocation();
                    continue;
                }

                /* if even one location does not fit the others, return none */
                if(!child.getLocation().equals(location)) {
                    location = null;
                    break;
                }
            }

            if(location != null)
                modGroupDefaultLocation = Paths.get(location);
            else
                modGroupDefaultLocation = null;
        }

        if (modGroupDefaultLocation != null)
            return modGroupDefaultLocation.toString();

        return null;
    }

    @Override
    public void setLocation(Path path) {
        modGroupDefaultLocation = path;

        for (ModTableItem modTableItem : children) {
            if (modTableItem.getLocation() == null)
                modTableItem.setLocation(path);
        }
    }

    public void addChild(ModTableItem modTableItem) {
        children.add(modTableItem);
    }
}
