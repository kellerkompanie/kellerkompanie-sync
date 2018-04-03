package com.kellerkompanie.kekosync.client.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public abstract class CustomTableItem {
    @Getter
    @Setter
    private CustomTableItem.CheckedState checked;

    @Getter
    @Setter
    private Type type;

    enum Type {
        MOD_GROUP, MOD, ROOT
    }

    enum Status {
        OK, INCOMPLETE, MISSING
    }

    enum CheckedState {
        CHECKED, UNCHECKED, INDETERMINATE
    }

    public abstract Status getStatus();
    public abstract String getName();
}
