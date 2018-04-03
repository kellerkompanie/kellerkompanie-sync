package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.core.entities.Mod;

public class ModTableItem extends CustomTableItem {

    private Mod mod;

    public ModTableItem(Mod mod) {
        super(CheckedState.UNCHECKED, Type.MOD_GROUP);
        this.mod = mod;
    }

    @Override
    public Status getStatus() {
        return Status.INCOMPLETE;
    }

    @Override
    public String getName() {
        return mod.getName();
    }

}
