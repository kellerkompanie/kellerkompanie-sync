package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.entities.ModGroup;

public class ModGroupTableItem extends CustomTableItem {

    private ModGroup modGroup;

    public ModGroupTableItem(ModGroup modGroup) {
        super(CheckedState.UNCHECKED, Type.MOD_GROUP);
        this.modGroup = modGroup;
    }

    @Override
    public Status getStatus() {
        return Status.INCOMPLETE;
    }

    @Override
    public String getName() {
        return modGroup.getName();
    }
}
