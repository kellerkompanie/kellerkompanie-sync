package com.kellerkompanie.kekosync.client.gui;

public class RootTableItem extends CustomTableItem {

    public RootTableItem(CheckedState checked, Type type) {
        super(checked, type);
    }

    @Override
    public Status getStatus() {
        return Status.OK;
    }

    @Override
    public String getName() {
        return null;
    }
}
